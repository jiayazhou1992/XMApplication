package com.xiaomawang.commonlib.utils.dev.common;

import android.content.Context;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * detail: 类工具
 * Created by Ttt
 */
public final class ClassUtils {

    private ClassUtils(){
    }

    // 日志TAG
    private static final String TAG = ClassUtils.class.getSimpleName();

    /**
     * 判断类是否是基础数据类型
     * 目前支持11种
     * @param clazz
     * @return
     */
    public static boolean isBaseDataType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Boolean.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(Float.class)
                || clazz.equals(Double.class) || clazz.equals(Byte.class) || clazz.equals(Character.class)
                || clazz.equals(Short.class) || clazz.equals(Date.class) || clazz.equals(byte[].class)
                || clazz.equals(Byte[].class);
    }

    /**
     * 根据类获取对象：不再必须一个无参构造
     * @param claxx
     * @return
     */
    public static <T> T newInstance(Class<T> claxx){
        try {
            Constructor<?>[] cons = claxx.getDeclaredConstructors();
            for (Constructor<?> c : cons) {
                Class[] cls = c.getParameterTypes();
                if (cls.length == 0) {
                    c.setAccessible(true);
                    return (T) c.newInstance();
                } else {
                    Object[] objs = new Object[cls.length];
                    for (int i = 0; i < cls.length; i++) {
                        objs[i] = getDefaultPrimiticeValue(cls[i]);
                    }
                    c.setAccessible(true);
                    return (T) c.newInstance(objs);
                }
            }
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "newInstance");
        }
        return null;
    }

    /**
     * 判断 Class 是否为原始类型(boolean、char、byte、short、int、long、float、double)
     * @param clazz
     * @return
     */
    public static Object getDefaultPrimiticeValue(Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz == boolean.class ? false : 0;
        }
        return null;
    }

    /**
     * 判断是否集合类型
     * @param claxx
     * @return
     */
    public static boolean isCollection(Class claxx) {
        return Collection.class.isAssignableFrom(claxx);
    }

    /**
     * 是否数组类型
     * @param claxx
     * @return
     */
    public static boolean isArray(Class claxx) {
        return claxx.isArray();
    }

    /**
     * 通过指定包名，扫描包下面包含的所有的ClassName
     *
     * @param context     U know
     * @param packageName 包名
     * @return 所有class的集合
     */
    public static Set<String> getFileNameByPackageName(Context context, final String packageName) throws IOException {
        final Set<String> classNames = new HashSet<>();

        List<DexFile> dexFiles = getDexFiles(context);
        for (final DexFile dexfile : dexFiles) {
            Enumeration<String> dexEntries = dexfile.entries();
            while (dexEntries.hasMoreElements()) {
                String className = dexEntries.nextElement();
                if (className.startsWith(packageName)) {
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }

    /** 通过BaseDexClassLoader反射获取app所有的DexFile
     * @param context
     * @return
     * @throws IOException
     */
    private static List<DexFile> getDexFiles(Context context) throws IOException {
        List<DexFile> dexFiles = new ArrayList<>();
        BaseDexClassLoader loader = (BaseDexClassLoader) context.getClassLoader();
        try {
            Field pathListField = field("dalvik.system.BaseDexClassLoader","pathList");
            Object list = pathListField.get(loader);
            Field dexElementsField = field("dalvik.system.DexPathList","dexElements");
            Object[] dexElements = (Object[]) dexElementsField.get(list);
            Field dexFilefield = field("dalvik.system.DexPathList$Element","dexFile");
            for(Object dex:dexElements){
                DexFile dexFile = (DexFile) dexFilefield.get(dex);
                dexFiles.add(dexFile);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return dexFiles;
    }

    private static Field field(String clazz, String fieldName) throws ClassNotFoundException, NoSuchFieldException {
        Class cls = Class.forName(clazz);
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

}
