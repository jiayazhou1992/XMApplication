package com.xiaomawang.commonlib.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.ui.web.WebContract;
import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.app.AppUtils;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;
import com.xiaomawang.commonlib.utils.dev.app.logger.LogConfig;
import com.xiaomawang.commonlib.utils.dev.app.logger.LogLevel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class XMApplication extends MultiDexApplication {
    private static final String TAG = "XMApplication";

    protected boolean isDebugMode = false;

    // 正常状态
    public static final int STATE_NORMAL = 0;
    // 从后台回到前台
    public static final int STATE_BACK_TO_FRONT = 1;
    // 从前台进入后台
    public static final int STATE_FRONT_TO_BACK = 2;
    // APP状态
    public static int sAppState = STATE_NORMAL;
    // 标记程序是否已进入后台(依据onStop回调)
    private boolean flag;
    // 标记程序是否已进入后台(依据onTrimMemory回调)
    private boolean background;
    // 从前台进入后台的时间
    private static long frontToBackTime;
    // 从后台返回前台的时间
    private static long backToFrontTime;

    // webPage配置
    protected WebContract.WebOptions mWebOptions;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
}

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化工具类
        DevUtils.init(this.getApplicationContext());
        LogConfig logConfig = new LogConfig();
        logConfig.logLevel = LogLevel.DEBUG;
        logConfig.tag = "ANNiu";
        DevLogger.init(logConfig);

        isDebugMode = AppUtils.isAppDebug();

        if (isDebugMode) {
            DevUtils.openLog();
            DevUtils.openDebug();
            ARouter.openDebug();
            ARouter.openLog();
        }

        ARouter.init(this);

        if (inMainProcess(this)) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    if (background || flag) {
                        background = false;
                        flag = false;
                        sAppState = STATE_BACK_TO_FRONT;
                        backToFrontTime = System.currentTimeMillis();
                        Log.i(TAG, "onResume: STATE_BACK_TO_FRONT");

                        appBack2Front(activity);

                        if (canShowGestureLock()){
                            showGestureLock(activity);
                        }
                        if (canShowAd()) {
                            showAd(activity);
                        }
                    } else {
                        sAppState = STATE_NORMAL;
                    }
                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
//                    判断当前activity是否处于前台
                    if (!AppUtils.isAppForeground()) {
//                         从前台进入后台
                        sAppState = STATE_FRONT_TO_BACK;
                        frontToBackTime = System.currentTimeMillis();
                        flag = true;
                        Log.i(TAG, "onStop: " + "STATE_FRONT_TO_BACK");
                    } else {
//                         否则是正常状态
                        sAppState = STATE_NORMAL;
                    }
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
//         TRIM_MEMORY_UI_HIDDEN是UI不可见的回调, 通常程序进入后台后都会触发此回调,大部分手机多是回调这个参数
//         TRIM_MEMORY_BACKGROUND也是程序进入后台的回调, 不同厂商不太一样, 魅族手机就是回调这个参数
        if (level == Application.TRIM_MEMORY_UI_HIDDEN || level == TRIM_MEMORY_BACKGROUND) {
            background = true;
        } else if (level == Application.TRIM_MEMORY_COMPLETE) {
            background = !AppUtils.isAppForeground();
        }
        if (background) {
            frontToBackTime = System.currentTimeMillis();
            sAppState = STATE_FRONT_TO_BACK;
            Log.i(TAG, "onTrimMemory: TRIM_MEMORY_UI_HIDDEN || TRIM_MEMORY_BACKGROUND");
        } else {
            sAppState = STATE_NORMAL;
        }

    }

    /**
     * app 回到前台
     */
    public void appBack2Front(Activity activity) {

    }

    /**
     * 是否显示广告
     * @return
     */
    private boolean canShowAd(){
        return sAppState == STATE_BACK_TO_FRONT && (backToFrontTime - frontToBackTime) > 10 * 60 * 1000;
    }

    /**
     * @param baseActivity
     */
    public abstract void showAd(Activity baseActivity);

    /**
     * 是否显示手势锁屏
     * @return
     */
    private boolean canShowGestureLock(){
        return sAppState == STATE_BACK_TO_FRONT && (backToFrontTime - frontToBackTime) > 1 * 60 * 1000 ;
    }

    /**
     * @param baseActivity
     */
    public abstract void showGestureLock(Activity baseActivity);

    /**
     * 是否是祝进程
     * @param context
     * @return
     */
    public static boolean inMainProcess(Context context) {
        String mainProcessName = context.getApplicationInfo().processName;
        String processName = getProcessName();

        Log.i("application"," the process name : " + processName + " main process name " + mainProcessName);

        return TextUtils.equals(mainProcessName, processName);
    }

    /**
     * 获取当前进程名
     */
    public static String getProcessName() {
        BufferedReader reader = null;
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine().trim();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取webPage配置
     * @return
     */
    public WebContract.WebOptions getWebOptions() {
        return mWebOptions;
    }
}
