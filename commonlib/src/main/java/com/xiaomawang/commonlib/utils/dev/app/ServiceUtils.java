package com.xiaomawang.commonlib.utils.dev.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * detail: 服务相关工具类
 * Created by Blankj
 * Update to Ttt
 */
public final class ServiceUtils {

    private ServiceUtils() {
    }

    // 日志TAG
    private static final String TAG = ServiceUtils.class.getSimpleName();

    /**
     * 判断服务是否运行
     * @param className 完整包名的服务类名
     * @return true : 是, false : 否
     */
    public static boolean isServiceRunning(final String className) {
        try {
            ActivityManager activityManager = (ActivityManager) DevUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) return false;
            List<RunningServiceInfo> listInfos = activityManager.getRunningServices(0x7FFFFFFF);
            if (listInfos == null || listInfos.size() == 0) return false;
            for (RunningServiceInfo rInfo : listInfos) {
                if (className.equals(rInfo.service.getClassName())) return true;
            }
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "isServiceRunning");
        }
        return false;
    }

    /**
     * 判断服务是否运行
     * @param cls The service class.
     * @return true : 是, false : 否
     */
    public static boolean isServiceRunning(final Class<?> cls) {
        return isServiceRunning(cls.getName());
    }

    /**
     * 获取所有运行的服务
     * @return 服务名集合
     */
    public static Set getAllRunningService() {
        try {
            ActivityManager activityManager = (ActivityManager) DevUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager == null) return Collections.emptySet();
            List<RunningServiceInfo> listInfos = activityManager.getRunningServices(0x7FFFFFFF);
            if (listInfos == null || listInfos.size() == 0) return null;
            Set<String> names = new HashSet<>();
            for (RunningServiceInfo rInfo : listInfos) {
                names.add(rInfo.service.getClassName());
            }
            return names;
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "getAllRunningService");
        }
        return Collections.emptySet();
    }

    /**
     * 启动服务
     * @param className 完整包名的服务类名
     */
    public static void startService(final String className) {
        try {
            startService(Class.forName(className));
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "startService");
        }
    }

    /**
     * 启动服务
     * @param cls 服务类
     */
    public static void startService(final Class<?> cls) {
        try {
            Intent intent = new Intent(DevUtils.getContext(), cls);
            DevUtils.getContext().startService(intent);
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "startService");
        }
    }

    /**
     * 停止服务
     * @param className 完整包名的服务类名
     * @return true : 停止成功, false : 停止失败
     */
    public static boolean stopService(final String className) {
        try {
            return stopService(Class.forName(className));
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "stopService");
            return false;
        }
    }

    /**
     * 停止服务
     * @param cls 服务类
     * @return true : 停止成功, false : 停止失败
     */
    public static boolean stopService(final Class<?> cls) {
        try {
            Intent intent = new Intent(DevUtils.getContext(), cls);
            return DevUtils.getContext().stopService(intent);
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "stopService");
            return false;
        }
    }

    /**
     * 绑定服务
     * @param className 完整包名的服务类名
     * @param conn 服务连接对象
     * @param flags 绑定选项
     * ====
     * {@link Context#BIND_AUTO_CREATE}
     * {@link Context#BIND_DEBUG_UNBIND}
     * {@link Context#BIND_NOT_FOREGROUND}
     * {@link Context#BIND_ABOVE_CLIENT}
     * {@link Context#BIND_ALLOW_OOM_MANAGEMENT}
     * {@link Context#BIND_WAIVE_PRIORITY}
     */
    public static void bindService(final String className, final ServiceConnection conn, final int flags) {
        try {
            bindService(Class.forName(className), conn, flags);
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "bindService");
        }
    }

    /**
     * 绑定服务
     * @param cls 服务类
     * @param conn 服务连接对象
     * @param flags 绑定选项
     * ====
     * {@link Context#BIND_AUTO_CREATE}
     * {@link Context#BIND_DEBUG_UNBIND}
     * {@link Context#BIND_NOT_FOREGROUND}
     * {@link Context#BIND_ABOVE_CLIENT}
     * {@link Context#BIND_ALLOW_OOM_MANAGEMENT}
     * {@link Context#BIND_WAIVE_PRIORITY}
     */
    public static void bindService(final Class<?> cls, final ServiceConnection conn, final int flags) {
        try {
            Intent intent = new Intent(DevUtils.getContext(), cls);
            DevUtils.getContext().bindService(intent, conn, flags);
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "bindService");
        }
    }

    /**
     * 解绑服务
     * @param conn 服务连接对象
     */
    public static void unbindService(final ServiceConnection conn) {
        try {
            DevUtils.getContext().unbindService(conn);
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "unbindService");
        }
    }
}
