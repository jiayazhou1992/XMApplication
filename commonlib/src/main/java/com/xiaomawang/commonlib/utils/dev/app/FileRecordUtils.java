package com.xiaomawang.commonlib.utils.dev.app;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * detail: App 文件记录工具类
 * Created by Ttt
 */
public final class FileRecordUtils {

    private FileRecordUtils(){
    }

    // 日志TAG
    private static final String TAG = FileRecordUtils.class.getSimpleName();

    // ===================  配置信息  =======================

    /** App 版本(如1.0.01) 显示给用户看的 */
    static String APP_VERSION_NAME = "";

    /** android:versionCode——整数值,代表应用程序代码的相对版本,也就是版本更新过多少次。(不显示给用户看) */
    static String APP_VERSION_CODE = "";

    /** 设备信息 */
    static String DEVICE_INFO_STR = null;

    /** 用来存储设备信息 */
    static HashMap<String, String> DEVICE_INFO_MAPS = new HashMap<String, String>();

    /** 换行字符串 */
    static final String NEW_LINE_STR = System.getProperty("line.separator");

    /** 换行字符串 - 两行 */
    static final String NEW_LINE_STR_X2 = NEW_LINE_STR + NEW_LINE_STR;

    // ================== App、设备信息处理  ===================

    /**
     * 获取 App 版本信息
     */
    static String[] getAppVersion() {
        String[] aVersion = null;
        try {
            PackageManager pm = DevUtils.getContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(DevUtils.getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                // --
                aVersion = new String[]{versionName,versionCode};
            }
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getAppVersion");
        }
        return aVersion;
    }

    /**
     * 获取设备信息
     * @param dInfoMaps 传入设备信息传出HashMap
     */
    static void getDeviceInfo(HashMap<String, String> dInfoMaps) {
        // 获取设备信息类的所有申明的字段,即包括public、private和proteced, 但是不包括父类的申明字段。
        Field[] fields = Build.class.getDeclaredFields();
        // 遍历字段
        for (Field field : fields) {
            try {
                // 取消java的权限控制检查
                field.setAccessible(true);
                // 获取类型对应字段的数据,并保存
                dInfoMaps.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                LogPrintUtils.eTag(TAG, e, "getDeviceInfo");
            }
        }
    }

    /**
     * 处理设备信息
     * @param eHint 错误提示,如获取设备信息失败
     */
    static String handleDeviceInfo(String eHint) {
        try {
            // 如果不为null,则直接返回之前的信息
            if(!TextUtils.isEmpty(DEVICE_INFO_STR)) {
                return DEVICE_INFO_STR;
            }
            // 初始化StringBuilder,拼接字符串
            StringBuilder sBuilder = new StringBuilder();
            // 获取设备信息
            Iterator<Map.Entry<String, String>> mapIter = DEVICE_INFO_MAPS.entrySet().iterator();
            // 遍历设备信息
            while (mapIter.hasNext()) {
                // 获取对应的key-value
                Map.Entry<String, String> rnEntry = (Map.Entry<String, String>) mapIter.next();
                String rnKey = (String) rnEntry.getKey(); // key
                String rnValue = (String) rnEntry.getValue(); // value
                // 保存设备信息
                sBuilder.append(rnKey);
                sBuilder.append(" = ");
                sBuilder.append(rnValue);
                sBuilder.append(NEW_LINE_STR);
            }
            // 保存设备信息
            DEVICE_INFO_STR = sBuilder.toString();
            // 返回设备信息
            return DEVICE_INFO_STR;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "handleDeviceInfo");
        }
        return eHint;
    }

    // ==================  时间格式化  ===================

    /** 日期格式类型 */
    static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前日期的字符串
     * @return 字符串
     */
    @SuppressLint("SimpleDateFormat")
    static String getDateNow() {
        try {
            Calendar cld = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat(yyyyMMddHHmmss);
            return df.format(cld.getTime());
        } catch (Exception e) {
        }
        return null;
    }


    // ==================  文件操作  ===================

    /**
     * 判断某个文件夹是否创建,未创建则创建(不能加入文件名)
     * @param fPath 文件夹路径
     */
    static File createFile(String fPath) {
        try {
            File file = new File(fPath);
            // 当这个文件夹不存在的时候则创建文件夹
            if(!file.exists()) {
                // 允许创建多级目录
                file.mkdirs();
            }
            return file;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "createFile");
        }
        return null;
    }

    /**
     * 保存文件
     * @param txt 保存内容
     * @param fUrl 保存路径(包含文件名.后缀)
     * @return 是否保存成功
     */
    static boolean saveFile(String txt, String fUrl) {
        try {
            // 保存内容到一个文件
            FileOutputStream fos = new FileOutputStream(fUrl);
            fos.write(txt.getBytes());
            fos.close();
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "saveFile");
            return false;
        }
        return true;
    }


    // ==================  异常信息处理  ===================

    /**
     * 获取错误信息(无换行)
     * @param eHint 获取失败提示
     * @param ex 错误信息
     * @return
     */
    static String getThrowableMsg(String eHint, Throwable ex) {
        PrintWriter printWriter = null;
        try {
            if(ex != null) {
                // 初始化Writer,PrintWriter打印流
                Writer writer = new StringWriter();
                printWriter = new PrintWriter(writer);
                // 写入错误栈信息
                ex.printStackTrace(printWriter);
                // 关闭流
                printWriter.close();
                return writer.toString();
            }
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getThrowableMsg");
        } finally {
            if(printWriter != null) {
                printWriter.close();
            }
        }
        return eHint;
    }

    /**
     * 获取错误信息(有换行)
     * @param eHint 获取失败提示
     * @param ex 错误信息
     * @return
     */
    static String getThrowableNewLinesMsg(String eHint, Throwable ex) {
        PrintWriter printWriter = null;
        try {
            if(ex != null) {
                // 初始化Writer,PrintWriter打印流
                Writer writer = new StringWriter();
                printWriter = new PrintWriter(writer);
                // 获取错误栈信息
                StackTraceElement[] stElement = ex.getStackTrace();
                // 标题,提示属于什么异常
                printWriter.append(ex.toString());
                printWriter.append(NEW_LINE_STR);
                // 遍历错误栈信息,并且进行换行,缩进
                for(StackTraceElement st : stElement) {
                    printWriter.append("\tat ");
                    printWriter.append(st.toString());
                    printWriter.append(NEW_LINE_STR);
                }
                // 关闭流
                printWriter.close();
                return writer.toString();
            }
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getThrowableNewLinesMsg");
        } finally {
            if(printWriter != null) {
                try {
                    printWriter.close();
                } catch (Exception e) {
                }
            }
        }
        return eHint;
    }

    // ==================  对外公开方法  ===================

    /**
     * App 初始化调用方法
     */
    public static void appInit() {
        // 如果版本信息为null，才进行处理
        if (TextUtils.isEmpty(APP_VERSION_CODE) || TextUtils.isEmpty(APP_VERSION_NAME)) {
            // 获取 App 版本信息
            String[] aVersion = getAppVersion();
            // 保存 App 版本信息
            APP_VERSION_NAME = aVersion[0];
            APP_VERSION_CODE = aVersion[1];
        }
        // 判断是否存在设备信息
        if (DEVICE_INFO_MAPS.size() == 0) {
            // 获取设备信息
            getDeviceInfo(DEVICE_INFO_MAPS);
            // 转换字符串
            handleDeviceInfo("");
        }
    }

    // ========= 保存错误日志信息 ==========

    /**
     * 保存 App 错误日志
     * @param ex 错误信息
     * @param fPath 保存路径
     * @param fName 文件名(含后缀)
     * @param isNewLines 是否换行
     * @param printDevice 是否打印设备信息
     * @param eHint 错误提示(无设备信息、失败信息获取失败)
     * @return
     */
    public static boolean saveErrorLog(Throwable ex, String fPath, String fName, boolean isNewLines, boolean printDevice, String... eHint) {
        return saveErrorLog(ex, null, null, fPath, fName, isNewLines, printDevice, eHint);
    }

    /**
     * 保存 App 错误日志
     * @param ex 错误信息
     * @param head 顶部标题
     * @param bottom 底部内容
     * @param fPath 保存路径
     * @param fName 文件名(含后缀)
     * @param isNewLines 是否换行
     * @param printDevice 是否打印设备信息
     * @param eHint 错误提示(无设备信息、失败信息获取失败)
     * @return
     */
    public static boolean saveErrorLog(Throwable ex, String head, String bottom, String fPath, String fName, boolean isNewLines, boolean printDevice, String... eHint) {
        // 处理可变参数(错误提示)
        eHint = handleVariable(2, eHint);
        // 日志拼接
        StringBuilder sBuilder = new StringBuilder();
        // 防止文件夹不存在
        createFile(fPath);
        // 设备信息
        String dInfo = handleDeviceInfo(eHint[0]);
        // 如果存在顶部内容,则进行添加
        if(!TextUtils.isEmpty(head)) {
            sBuilder.append(head);
            sBuilder.append(NEW_LINE_STR_X2);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
        }
        // ============
        // 保存 App 信息
        sBuilder.append("date: " + getDateNow());
        sBuilder.append(NEW_LINE_STR);
        sBuilder.append("versionName: " + APP_VERSION_NAME);
        sBuilder.append(NEW_LINE_STR);
        sBuilder.append("versionCode: " + APP_VERSION_CODE);
        sBuilder.append(NEW_LINE_STR_X2);
        sBuilder.append("============================");
        sBuilder.append(NEW_LINE_STR_X2);
        // 如果需要打印设备信息
        if (printDevice) {
            // 保存设备信息
            sBuilder.append(dInfo);
            sBuilder.append(NEW_LINE_STR);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
        }
        // ============
        // 错误信息
        String eMsg = null;
        // 是否换行
        if(isNewLines) {
            eMsg = getThrowableNewLinesMsg(eHint[1], ex);
        } else {
            eMsg = getThrowableMsg(eHint[1], ex);
        }
        // 保存异常信息
        sBuilder.append(eMsg);
        // 如果存在顶部内容,则进行添加
        if(!TextUtils.isEmpty(bottom)) {
            sBuilder.append(NEW_LINE_STR);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
            sBuilder.append(bottom);
        }
        // 保存日志到文件
        return saveFile(sBuilder.toString(), fPath + File.separator + fName);
    }

    /**
     * 保存 App 日志
     * @param log 日志信息
     * @param fPath 保存路径
     * @param fName 文件名(含后缀)
     * @param printDevice 是否打印设备信息
     * @param eHint 错误提示(无设备信息、失败信息获取失败)
     * @return
     */
    public static boolean saveLog(String log, String fPath, String fName, boolean printDevice, String... eHint){
        return saveLog(log, null, null, fPath, fName, printDevice, eHint);
    }

    /**
     * 保存 App 日志
     * @param log 日志信息
     * @param head 顶部标题
     * @param bottom 底部内容
     * @param fPath 保存路径
     * @param fName 文件名(含后缀)
     * @param printDevice 是否打印设备信息
     * @param eHint 错误提示(无设备信息、失败信息获取失败)
     * @return
     */
    public static boolean saveLog(String log, String head, String bottom, String fPath, String fName, boolean printDevice, String... eHint){
        // 处理可变参数(错误提示)
        eHint = handleVariable(2, eHint);
        // 日志拼接
        StringBuilder sBuilder = new StringBuilder();
        // 防止文件夹不存在
        createFile(fPath);
        // 设备信息
        String dInfo = handleDeviceInfo(eHint[0]);
        // 如果存在顶部内容,则进行添加
        if(!TextUtils.isEmpty(head)) {
            sBuilder.append(head);
            sBuilder.append(NEW_LINE_STR_X2);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
        }
        // ============
        // 保存 App 信息
        sBuilder.append("date: " + getDateNow());
        sBuilder.append(NEW_LINE_STR);
        sBuilder.append("versionName: " + APP_VERSION_NAME);
        sBuilder.append(NEW_LINE_STR);
        sBuilder.append("versionCode: " + APP_VERSION_CODE);
        sBuilder.append(NEW_LINE_STR_X2);
        sBuilder.append("============================");
        sBuilder.append(NEW_LINE_STR_X2);
        // 如果需要打印设备信息
        if (printDevice) {
            // 保存设备信息
            sBuilder.append(dInfo);
            sBuilder.append(NEW_LINE_STR);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
        }
        // ============
        // 保存日志信息
        sBuilder.append(log);
        // 如果存在顶部内容,则进行添加
        if(!TextUtils.isEmpty(bottom)) {
            sBuilder.append(NEW_LINE_STR);
            sBuilder.append("============================");
            sBuilder.append(NEW_LINE_STR_X2);
            sBuilder.append(bottom);
        }
        // 保存日志到文件
        return saveFile(sBuilder.toString(), fPath + File.separator + fName);
    }

    // ==

    /**
     * 处理可变参数
     * @param length 保留长度
     * @param vArrays 可变参数数组
     * @return
     */
    public static String[] handleVariable(int length, String[] vArrays) {
        // 处理后的数据,
        String[] hArrays = new String[length];
        // 是否统一处理
        boolean isUnifiedHandler = true;
        try {
            if(vArrays != null) {
                // 获取可变参数数组长度
                int vLength = vArrays.length;
                // 如果长度超出预留长度
                if(vLength >= length) {
                    for(int i = 0;i < length;i++) {
                        if(vArrays[i] == null) {
                            hArrays[i] = "";
                        } else {
                            hArrays[i] = vArrays[i];
                        }
                    }
                    // 但可变参数长度,超过预留长度时,已经处理完毕,不需要再次处理,节省遍历资源
                    isUnifiedHandler = false;
                } else {
                    for(int i = 0;i < vLength;i++) {
                        if(vArrays[i] == null) {
                            hArrays[i] = "";
                        } else {
                            hArrays[i] = vArrays[i];
                        }
                    }
                }
            }
            if(isUnifiedHandler) {
                // 统一处理,如果数据未null,则设置为“”,防止拼接出现   "null"
                for(int i = 0;i < length;i++) {
                    if(hArrays[i] == null) {
                        hArrays[i] = "";
                    }
                }
            }
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "handleVariable");
        }
        return hArrays;
    }
}
