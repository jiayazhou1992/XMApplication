package com.xiaomawang.commonlib.utils.dev.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

/**
 * detail: 亮度相关工具类
 * Created by Ttt
 */
public final class BrightnessUtils {

    private BrightnessUtils() {
    }

    // 日志TAG
    private static final String TAG = BrightnessUtils.class.getSimpleName();

    /**
     * 判断是否开启自动调节亮度
     * @return true : 是, false : 否
     */
    public static boolean isAutoBrightnessEnabled() {
        try {
            int mode = Settings.System.getInt(DevUtils.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "isAutoBrightnessEnabled");
            return false;
        }
    }

    /**
     * 设置是否开启自动调节亮度
     * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
     * @param enabled true : 打开, false : 关闭
     * @return true : 成功, false : 失败
     */
    public static boolean setAutoBrightnessEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(DevUtils.getContext())) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + DevUtils.getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DevUtils.getContext().startActivity(intent);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "setAutoBrightnessEnabled");
            }
            return false;
        }
        try {
            return Settings.System.putInt(DevUtils.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    enabled ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "setAutoBrightnessEnabled");
        }
        return false;
    }

    /**
     * 获取屏幕亮度
     * @return 屏幕亮度 0-255
     */
    public static int getBrightness() {
        try {
            return Settings.System.getInt(DevUtils.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getBrightness");
            return 0;
        }
    }

    /**
     * 设置屏幕亮度
     * <uses-permission android:name="android.permission.WRITE_SETTINGS" />
     * @param brightness 亮度值
     */
    public static boolean setBrightness(@IntRange(from = 0, to = 255) final int brightness) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(DevUtils.getContext())) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + DevUtils.getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DevUtils.getContext().startActivity(intent);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "setBrightness");
            }
            return false;
        }
        try {
            ContentResolver resolver = DevUtils.getContext().getContentResolver();
            boolean result = Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), null);
            return result;
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "setBrightness");
        }
        return false;
    }

    /**
     * 设置窗口亮度
     * @param window 窗口
     * @param brightness 亮度值
     */
    public static void setWindowBrightness(@NonNull final Window window, @IntRange(from = 0, to = 255) final int brightness) {
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255f;
        window.setAttributes(lp);
    }

    /**
     * 获取窗口亮度
     * @param window 窗口
     * @return 屏幕亮度 0-255
     */
    public static int getWindowBrightness(final Window window) {
        if (window == null) return -1;
        WindowManager.LayoutParams lp = window.getAttributes();
        float brightness = lp.screenBrightness;
        if (brightness < 0) return getBrightness();
        return (int) (brightness * 255);
    }

    /**
     * 设置window透明度
     * @param window
     * @param alpha
     */
    public static void setWindowAlpha(@NonNull final Window window, @FloatRange(from = 0.0f, to = 1.0f) final float alpha){
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        window.setAttributes(lp);
    }
}
