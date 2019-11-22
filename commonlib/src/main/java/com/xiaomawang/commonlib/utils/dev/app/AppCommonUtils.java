package com.xiaomawang.commonlib.utils.dev.app;

import android.os.Build;

import java.util.Random;
import java.util.UUID;

/**
 * detail: App通用工具类
 * Created by Ttt
 */
public final class AppCommonUtils {

    private AppCommonUtils(){
    }

    /**
     * 获取设备唯一id
     * @return
     */
    public static String getUUID(){
        return PhoneUtils.getUUID();
    }

    /**
     * 获取随机数 唯一id
     * @return
     */
    public static String getRandomUUID(){
        // 获取随机数
        String random1 = (900000 + new Random().nextInt(10000)) + "";
        // 获取随机数
        String random2 = (900000 + new Random().nextInt(10000)) + "";
        // 获取当前时间
        String cTime = Long.toString(System.currentTimeMillis()) + random1 + random2;
        // 生成唯一随机uuid  cTime.hashCode(), random1.hashCode() | random2.hashCode()
        UUID randomUUID = new UUID(cTime.hashCode(), ((long) random1.hashCode() << 32) | random2.hashCode());
        // 获取uid
        return randomUUID.toString();
    }

    // == 版本判断处理 ==

    /**
     * 是否在2.2版本及以上
     * @return 是否在2.2版本及以上
     */
    public static boolean isFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 是否在2.3版本及以上
     * @return 是否在2.3版本及以上
     */
    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 是否在2.3.3版本及以上
     * @return 是否在2.3.3版本及以上
     */
    public static boolean isGingerbreadMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    /**
     * 是否在3.0版本及以上
     * @return 是否在3.0版本及以上
     */
    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 是否在3.1版本及以上
     * @return 是否在3.1版本及以上
     */
    public static boolean isHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * 是否在4.0版本及以上
     * @return 是否在4.0版本及以上
     */
    public static boolean isIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 是否在4.0.3版本及以上
     * @return 是否在4.0.3版本及以上
     */
    public static boolean isIceCreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * 是否在4.1版本及以上
     * @return 是否在4.1版本及以上
     */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 是否在4.4.2版本及以上
     * @return 是否在4.4.2版本及以上
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 是否在5.0.1版本及以上
     * @return 是否在5.0.1版本及以上
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否在6.0版本及以上
     * @return 是否在6.0版本及以上
     */
    public static boolean isM(){
        return Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M;
    }

    /**
     * 是否在7.0版本及以上
     * @return 是否在7.0版本及以上
     */
    public static boolean isN(){
        return Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N;
    }

    /**
     * 是否在7.1.1版本及以上
     * @return 是否在7.1.1版本及以上
     */
    public static boolean isN_MR1(){
        return Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N_MR1;
    }

    /**
     * 是否在8.0版本及以上
     * @return 是否在8.0版本及以上
     */
    public static boolean isO(){
        return Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O;
    }

    /**
     * 转换SDK版本 (convertSDKVersion(14) => Android 4.0.0-2)
     * @param sdkVersion
     * @return
     */
    public static String convertSDKVersion(int sdkVersion){
        // https://www.cnblogs.com/maogefff/p/7819076.html
        switch (sdkVersion){
            case 1:
                return "Android 1.0";
            case 2:
                return "Android 1.1";
            case 3:
                return "Android 1.5";
            case 4:
                return "Android 1.6";
            case 5:
                return "Android 2.0";
            case 6:
                return "Android 2.0.1";
            case 7:
                return "Android 2.1.x";
            case 8:
                return "Android 2.2.x";
            case 9:
                return "Android 2.3.0-2";
            case 10:
                return "Android 2.3.3-4";
            case 11:
                return "Android 3.0.x";
            case 12:
                return "Android 3.1.x";
            case 13:
                return "Android 3.2";
            case 14:
                return "Android 4.0.0-2";
            case 15:
                return "Android 4.0.3-4";
            case 16:
                return "Android 4.1.x";
            case 17:
                return "Android 4.2.x";
            case 18:
                return "Android 4.3";
            case 19:
                return "Android 4.4";
            case 20:
                return "Android 4.4W";
            case 21:
                return "Android 5.0";
            case 22:
                return "Android 5.1";
            case 23:
                return "Android 6.0";
            case 24:
                return "Android 7.0";
            case 25:
                return "Android 7.1.1";
            case 26:
                return "Android 8.0";
            case 27:
                return "Android 8.1";
            case 28:
                return "Android 9.0";
        }
        return "unknown";
    }
}
