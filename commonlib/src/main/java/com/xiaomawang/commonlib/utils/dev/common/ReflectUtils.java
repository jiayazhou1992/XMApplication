package com.xiaomawang.commonlib.utils.dev.common;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * detail: 反射相关工具类
 * Created by Ttt
 * ==
 * 有两个方法:getMethod,getDeclaredMethod。看了下说明大概的意思就是getMethod只能调用public声明的方法，而getDeclaredMethod基本可以调用任何类型声明的方法
 * 反射多用getDeclaredMethod，尽量少用getMethod。
 */
public final class ReflectUtils {

    private final Class<?> type;

    private final Object object;

    private ReflectUtils(final Class<?> type) {
        this(type, type);
    }

    private ReflectUtils(final Class<?> type, Object object) {
        this.type = type;
        this.object = object;
    }

    // =================
    // ==== reflect ====
    // =================

    /**
     * 设置要反射的类
     * @param className 完整类名
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public static ReflectUtils reflect(final String className) throws ReflectException {
        return reflect(forName(className));
    }

    /**
     * 设置要反射的类
     * @param className   完整类名
     * @param classLoader 类加载器
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public static ReflectUtils reflect(final String className, final ClassLoader classLoader) throws ReflectException {
        return reflect(forName(className, classLoader));
    }

    /**
     * 设置要反射的类
     * @param clazz 类的类型
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public static ReflectUtils reflect(final Class<?> clazz) throws ReflectException {
        return new ReflectUtils(clazz);
    }

    /**
     * 设置要反射的类
     * @param object 类对象
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public static ReflectUtils reflect(final Object object) throws ReflectException {
        return new ReflectUtils(object == null ? Object.class : object.getClass(), object);
    }

    private static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
    }

    private static Class<?> forName(String name, ClassLoader classLoader) {
        try {
            return Class.forName(name, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
    }

    // =====================
    // ==== newInstance ====
    // =====================

    /**
     * 实例化反射对象
     * @return {@link ReflectUtils}
     */
    public ReflectUtils newInstance() {
        return newInstance(new Object[0]);
    }

    /**
     * 实例化反射对象
     * @param args 实例化需要的参数
     * @return {@link ReflectUtils}
     */
    public ReflectUtils newInstance(Object... args) {
        Class<?>[] types = getArgsType(args);
        try {
            Constructor<?> constructor = type().getDeclaredConstructor(types);
            return newInstance(constructor, args);
        } catch (NoSuchMethodException e) {
            List<Constructor<?>> list = new ArrayList<>();
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    list.add(constructor);
                }
            }
            if (list.isEmpty()) {
                throw new ReflectException(e);
            } else {
                sortConstructors(list);
                return newInstance(list.get(0), args);
            }
        }
    }

    private Class<?>[] getArgsType(final Object... args) {
        if (args == null) return new Class[0];
        Class<?>[] result = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            Object value = args[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }
        return result;
    }

    private void sortConstructors(List<Constructor<?>> list) {
        Collections.sort(list, new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> o1, Constructor<?> o2) {
                Class<?>[] types1 = o1.getParameterTypes();
                Class<?>[] types2 = o2.getParameterTypes();
                int len = types1.length;
                for (int i = 0; i < len; i++) {
                    if (!types1[i].equals(types2[i])) {
                        if (wrapper(types1[i]).isAssignableFrom(wrapper(types2[i]))) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
                return 0;
            }
        });
    }

    private ReflectUtils newInstance(final Constructor<?> constructor, final Object... args) {
        try {
            return new ReflectUtils(constructor.getDeclaringClass(), accessible(constructor).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    // ===============
    // ==== field ====
    // ===============

    /**
     * 设置反射的字段
     * @param name 字段名
     * @return {@link ReflectUtils}
     */
    public ReflectUtils field(final String name) {
        try {
            Field field = getField(name);
            return new ReflectUtils(field.getType(), field.get(object));
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 设置反射的字段
     * @param name  字段名
     * @param value 字段值
     * @return {@link ReflectUtils}
     */
    public ReflectUtils field(String name, Object value) {
        try {
            Field field = getField(name);
            field.set(object, unwrap(value));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private Field getField(String name) throws IllegalAccessException {
        Field field = getAccessibleField(name);
        if ((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (NoSuchFieldException ignore) {
                // runs in android will happen
            }
        }
        return field;
    }

    private Field getAccessibleField(String name) {
        Class<?> type = type();
        try {
            return accessible(type.getField(name));
        } catch (NoSuchFieldException e) {
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                } catch (NoSuchFieldException ignore) {
                }
                type = type.getSuperclass();
            } while (type != null);
            throw new ReflectException(e);
        }
    }

    private Object unwrap(Object object) {
        if (object instanceof ReflectUtils) {
            return ((ReflectUtils) object).get();
        }
        return object;
    }

    // ==

    /**
     * 获取Object 对象
     * 例: 获取父类中的变量
     * Object obj = 对象;
     * getObject(getDeclaredFieldBase(obj, "父类中变量名"), obj);
     * @param field
     * @param obj
     * @return
     */
    public static Object getObject(Field field, Object obj){
        try {
            field.setAccessible(true);
            Object out = field.get(obj);
            return out;
        } catch (Exception e){
        }
        return null;
    }

    /**
     * 设置枚举值
     * @param clas 类型
     * @param name
     * @param val
     */
    public ReflectUtils setEnumVal(Class<?> clas, String name, String val){
        try {
            return field(name, Enum.valueOf((Class<Enum>) clas, val));
        } catch (Exception e) {
        }
        return this;
    }

    /**
     * 通过反射获取全部字段
     * @param obj
     * @param name
     * @return
     * @throws Exception
     */
    public static Object getDeclaredField(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param object
     * @param fieldName
     * @return
     */
    public static Field getDeclaredFieldBase(Object object, String fieldName) {
        return getDeclaredFieldBase(object, fieldName, false);
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * @param object : 子类对象
     * @param fieldName : 父类中的属性名
     * @param isSuper 是否一直跟到最后, 如果父类还有父类，并且有相同变量名, 则设置isSuper = true，一直会跟到最后的变量
     * @return 父类中的属性对象
     */
    public static Field getDeclaredFieldBase(Object object, String fieldName, boolean isSuper){
        Field field = null ;
        Class<?> clazz = object.getClass() ;
        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                if (!isSuper){
                    return field;
                }
            } catch (Exception e) {
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了
            }
        }
        return field;
    }

    // ================
    // ==== method ====
    // ================

    /**
     * 设置反射的方法
     * @param name 方法名
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public ReflectUtils method(final String name) throws ReflectException {
        return method(name, new Object[0]);
    }

    /**
     * 设置反射的方法
     * @param name 方法名
     * @param args 方法需要的参数
     * @return {@link ReflectUtils}
     * @throws ReflectException 反射异常
     */
    public ReflectUtils method(final String name, final Object... args) throws ReflectException {
        Class<?>[] types = getArgsType(args);
        try {
            Method method = exactMethod(name, types);
            return method(method, object, args);
        } catch (NoSuchMethodException e) {
            try {
                Method method = similarMethod(name, types);
                return method(method, object, args);
            } catch (NoSuchMethodException e1) {
                throw new ReflectException(e1);
            }
        }
    }

    private ReflectUtils method(final Method method, final Object obj, final Object... args) {
        try {
            accessible(method);
            if (method.getReturnType() == void.class) {
                method.invoke(obj, args);
                return reflect(obj);
            } else {
                return reflect(method.invoke(obj, args));
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private Method exactMethod(final String name, final Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();
        try {
            return type.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ignore) {
                }
                type = type.getSuperclass();
            } while (type != null);
            throw new NoSuchMethodException();
        }
    }

    private Method similarMethod(final String name, final Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();
        List<Method> methods = new ArrayList<>();
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                methods.add(method);
            }
        }
        if (!methods.isEmpty()) {
            sortMethods(methods);
            return methods.get(0);
        }
        do {
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    methods.add(method);
                }
            }
            if (!methods.isEmpty()) {
                sortMethods(methods);
                return methods.get(0);
            }
            type = type.getSuperclass();
        } while (type != null);

        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types) + " could be found on type " + type() + ".");
    }

    private void sortMethods(final List<Method> methods) {
        Collections.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                Class<?>[] types1 = o1.getParameterTypes();
                Class<?>[] types2 = o2.getParameterTypes();
                int len = types1.length;
                for (int i = 0; i < len; i++) {
                    if (!types1[i].equals(types2[i])) {
                        if (wrapper(types1[i]).isAssignableFrom(wrapper(types2[i]))) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                }
                return 0;
            }
        });
    }

    private boolean isSimilarSignature(final Method possiblyMatchingMethod, final String desiredMethodName, final Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName) && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    private boolean match(final Class<?>[] declaredTypes, final Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class || wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i]))) {
                    continue;
                }
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) return null;
        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers()) && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }
        if (!accessible.isAccessible()) accessible.setAccessible(true);
        return accessible;
    }

    /**
     * 获取类型
     * @return
     */
    public Class<?> type() {
        return type;
    }

    private Class<?> wrapper(final Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }
        return type;
    }

    /**
     * 获取反射想要获取的
     * @param <T> 返回的范型
     * @return 反射想要获取的
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) object;
    }

    /**
     * 获取 HashCode
     * @return
     */
    @Override
    public int hashCode() {
        return object.hashCode();
    }

    /**
     * 判断反射的两个对象是否一样
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReflectUtils && object.equals(((ReflectUtils) obj).get());
    }

    /**
     * 获取反射获取的对象 toString
     * @return
     */
    @Override
    public String toString() {
        return object.toString();
    }

    // ==

    /** 内部标记 null */
    private static class NULL {
    }

    /** 定义反射异常类 */
    public static class ReflectException extends RuntimeException {

        private static final long serialVersionUID = 858774075258496016L;

        public ReflectException(String message) {
            super(message);
        }

        public ReflectException(String message, Throwable cause) {
            super(message, cause);
        }

        public ReflectException(Throwable cause) {
            super(cause);
        }
    }
}
