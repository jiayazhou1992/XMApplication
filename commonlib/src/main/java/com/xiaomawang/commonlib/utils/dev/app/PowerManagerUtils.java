package com.xiaomawang.commonlib.utils.dev.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.xiaomawang.commonlib.utils.dev.DevUtils;


/**
 * detail: 电源管理工具类
 * Created by Ttt
 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
 */
public final class PowerManagerUtils {

    // PowerManagerUtils 实例
    private static PowerManagerUtils INSTANCE;

    /** 获取 PowerManagerUtils 实例 ,单例模式 */
    public static PowerManagerUtils getInstance() {
        if (INSTANCE == null){
            INSTANCE = new PowerManagerUtils();
        }
        return INSTANCE;
    }

    /** 电源管理类 */
    PowerManager powerManager;
    // 电源管理锁
    PowerManager.WakeLock wakeLock;

    /** 构造函数 */
    @SuppressLint("InvalidWakeLockTag")
    private PowerManagerUtils() {
        // 获取系统服务
        powerManager = (PowerManager) DevUtils.getContext().getSystemService(Context.POWER_SERVICE);
        // 电源管理锁
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "PowerManagerUtils");
    }

    /**
     * 屏幕是否打开(亮屏)
     * @return
     */
    public boolean isScreenOn() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR_MR1) {
            return false;
        } else {
            return powerManager.isScreenOn();
        }
    }

    /**
     * 唤醒屏幕/点亮亮屏
     */
    public void turnScreenOn() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    /**
     * 释放屏幕锁, 允许休眠时间自动黑屏
     */
    public void turnScreenOff() {
        if (wakeLock != null && wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取 PowerManager.WakeLock
     * @return
     */
    public PowerManager.WakeLock getWakeLock() {
        return wakeLock;
    }

    /**
     * 设置 PowerManager.WakeLock
     * @param wakeLock
     */
    public void setWakeLock(PowerManager.WakeLock wakeLock) {
        this.wakeLock = wakeLock;
    }

    /**
     * 获取 PowerManager
     * @return
     */
    public PowerManager getPowerManager() {
        return powerManager;
    }

    /**
     * 设置 PowerManager
     * @param powerManager
     */
    public void setPowerManager(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    /**
     * 设置屏幕常亮
     * @param activity
     */
    public static void setBright(Activity activity){
        if (activity != null){
            setBright(activity.getWindow());
        }
    }

    /**
     * 设置屏幕常亮
     * @param window {@link Activity#getWindow()}
     */
    public static void setBright(Window window){
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 取消屏幕常亮
     * @param window {@link Activity#getWindow()}
     */
    public static void clearBright(Window window){
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * 设置WakeLock 常亮
     * @return
     * run: {@link Activity#onResume()}
     */
    public static PowerManager.WakeLock setWakeLockToBright(){
        // onResume()
        @SuppressLint("InvalidWakeLockTag")
        PowerManager.WakeLock mWakeLock = PowerManagerUtils.getInstance().getPowerManager().newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "setWakeLockToBright");
        mWakeLock.acquire(); // 常量, 持有不黑屏

//        // onPause()
//        if (mWakeLock != null){
//            mWakeLock.release(); // 释放资源, 到休眠时间自动黑屏
//        }
        return mWakeLock;
    }
}
