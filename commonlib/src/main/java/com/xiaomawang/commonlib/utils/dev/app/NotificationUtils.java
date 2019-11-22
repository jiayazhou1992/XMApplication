package com.xiaomawang.commonlib.utils.dev.app;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.xiaomawang.commonlib.utils.dev.DevUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * detail: 通知栏管理类
 * Created by Ttt
 * https://blog.csdn.net/hss01248/article/details/55096553
 * https://www.jianshu.com/p/cf5f6c30019d
 */
public final class NotificationUtils {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";


    private NotificationUtils(){
    }

    // 通知栏管理类
    private static NotificationManager mNotificationManager = null;


    /**
     * 权限检查
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            boolean isOpened = manager.areNotificationsEnabled();
            return isOpened;
        }

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 打开设置
     * @param context
     */
    public static void openNotificationSet(final Context context){
        DialogUtils.createAlertDialog(context, "", "检测到您没有打开通知权限，是否去打开", "取消", "确定", new DialogUtils.DialogListener() {
            @Override
            public void onRightButton(DialogInterface dialog) {
                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", AppUtils.getAppPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    localIntent.setAction(Intent.ACTION_VIEW);

                    localIntent.setClassName("com.android.settings",
                            "com.android.settings.InstalledAppDetails");

                    localIntent.putExtra("com.android.settings.ApplicationPkgName",
                            AppUtils.getAppPackageName());
                }
                context.startActivity(localIntent);
            }
        }).show();
    }

    /**
     * 获取通知栏管理类
     * @return
     */
    public static NotificationManager getNotificationManager() {
        if (mNotificationManager == null){
            mNotificationManager = (NotificationManager) DevUtils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    /**
     * 移除通知 - 移除所有通知(只是针对当前Context下的Notification)
     */
    public static void cancelAll(){
        if (getNotificationManager() != null){
            mNotificationManager.cancelAll();
        }
    }

    /**
     * 移除通知 - 移除标记为id的通知 (只是针对当前Context下的所有Notification)
     * @param args
     */
    public static void cancel(final int... args){
        if (getNotificationManager() != null && args != null){
            for (int id : args){
                mNotificationManager.cancel(id);
            }
        }
    }

    /**
     * 移除通知 - 移除标记为id的通知 (只是针对当前Context下的所有Notification)
     * @param tag
     * @param id
     */
    public static void cancel(final String tag, final int id){
        if (getNotificationManager() != null && tag != null){
            mNotificationManager.cancel(tag, id);
        }
    }

    /**
     * 进行通知
     * @param id
     * @param notification
     * @return
     */
    public static boolean notify(final int id, final Notification notification){
        if (getNotificationManager() != null && notification != null){
            mNotificationManager.notify(id, notification);
            return true;
        }
        return false;
    }

    /**
     * 进行通知
     * @param tag
     * @param id
     * @param notification
     * @return
     */
    public static boolean notify(final String tag, final int id, final Notification notification){
        if (getNotificationManager() != null && tag != null && notification != null){
            mNotificationManager.notify(tag, id, notification);
            return true;
        }
        return false;
    }

    // == 封装外部调用 ==

//    // 使用自定义View
//    RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.xxx);
//    // 设置View
//    Notification.builder.setContent(remoteViews);

    /**
     * 获取跳转id
     * @param intent
     * @param id
     * @return
     */
    public static PendingIntent createPendingIntent(Intent intent, int id){
        /* 跳转Intent */
        PendingIntent pIntent = PendingIntent.getActivity(DevUtils.getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    /**
     * 创建通知栏对象
     * @param icon
     * @param title
     * @param msg
     * @return
     */
    public static Notification createNotification(int icon, String title, String msg) {
        return createNotification(null, icon, title, title, msg, true, VibratePattern.obtain(0, 100, 300), LightPattern.obtain(Color.WHITE, 1000, 1000));
    }

    /**
     * 创建通知栏对象
     * @param icon
     * @param title
     * @param msg
     * @param vibratePattern
     * @param lightPattern
     * @return
     */
    public static Notification createNotification(int icon, String title, String msg, VibratePattern vibratePattern, LightPattern lightPattern) {
        return createNotification(null, icon, title, title, msg, true, vibratePattern, lightPattern);
    }

    /**
     * 创建通知栏对象
     * @param pendingIntent
     * @param icon
     * @param ticker
     * @param title
     * @param msg
     * @param isAutoCancel
     * @param vibratePattern
     * @param lightPattern
     * @return
     */
    public static Notification createNotification(PendingIntent pendingIntent, int icon, String ticker, String title, String msg, boolean isAutoCancel, VibratePattern vibratePattern, LightPattern lightPattern) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Notification.Builder builder = new Notification.Builder(DevUtils.getContext());
            // 点击通知执行intent
            builder.setContentIntent(pendingIntent);
            // 设置图标
            builder.setSmallIcon(icon);
            // 设置图标
            builder.setLargeIcon(BitmapFactory.decodeResource(DevUtils.getContext().getResources(), icon));
            // 指定通知的ticker内容，通知被创建的时候，在状态栏一闪而过，属于瞬时提示信息。
            builder.setTicker(ticker);
            // 设置标题
            builder.setContentTitle(title);
            // 设置内容
            builder.setContentText(msg);
            // 设置消息提醒，震动 | 声音
            builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
            // 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失
            builder.setAutoCancel(isAutoCancel);
            // 设置时间
            builder.setWhen(System.currentTimeMillis());
            // 设置震动参数
            if (vibratePattern != null && !vibratePattern.isEmpty()){
                builder.setVibrate(vibratePattern.vibrates);
            }
            // 设置 led 灯参数
            if (lightPattern != null){
                builder.setLights(lightPattern.argb, lightPattern.durationMS, lightPattern.startOffMS);
            }
            // = 初始化 Notification 对象 =
            Notification baseNF;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                baseNF = builder.getNotification();
            } else {
                baseNF = builder.build();
            }
            return baseNF;
        } else {
//            // https://www.cnblogs.com/Arture/p/5523695.html
//            // -- 初始化通知信息实体类 --
//            Notification notification = new Notification();
//            // 设置图标
//            notification.icon = icon;
//            // 设置图标
//            notification.largeIcon = BitmapFactory.decodeResource(DevUtils.getContext().getResources(), icon);
//            // 指定通知的ticker内容，通知被创建的时候，在状态栏一闪而过，属于瞬时提示信息。
//            notification.tickerText = title;
//            // 设置时间
//            notification.when = System.currentTimeMillis();
//            // 设置消息提醒，震动 | 声音
//            notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
//            // 点击了此通知则取消该通知栏
//            if (isAutoCancel) {
//                notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            }
//            // 设置震动参数
//            if (vibratePattern != null && !vibratePattern.isEmpty()){
//                notification.vibrate = vibratePattern.vibrates;
//            }
//            // 设置 led 灯参数
//            if (lightPattern != null){
//                try {
//                    notification.ledARGB = lightPattern.argb; // 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
//                    notification.ledOffMS = lightPattern.startOffMS; // 指定 LED 灯暗去的时长，也是以毫秒为单位
//                    notification.ledOnMS = lightPattern.durationMS; // 指定 LED 灯亮起的时长，以毫秒为单位
//                    notification.flags = Notification.FLAG_SHOW_LIGHTS;
//                } catch (Exception e){
//                }
//            }
//            // 设置标题内容等 - 已经移除, 现在都是支持 4.0以上, 不需要兼容处理
//            notification.setLatestEventInfo(DevUtils.getContext(), title, msg, pendingIntent);
//            return notification;
        }
        return null;
    }

    /**
     * detail: 设置通知栏 Led 灯参数实体类
     * Created by Ttt
     */
    public static class LightPattern {
        /**
         * 手机处于锁屏状态时， LED灯就会不停地闪烁， 提醒用户去查看手机,下面是绿色的灯光一 闪一闪的效果
         */
        private int argb = 0; // 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
        private int startOffMS = 0; // 指定 LED 灯暗去的时长，也是以毫秒为单位
        private int durationMS = 0; // 指定 LED 灯亮起的时长，以毫秒为单位

        private LightPattern(int argb, int startOffMS, int durationMS) {
            this.argb = argb;
            this.startOffMS = startOffMS;
            this.durationMS = durationMS;
        }

        /**
         * 获取 Led 配置参数
         * @param argb 颜色
         * @param startOffMS 开始时间
         * @param durationMS 持续时间
         * @return
         */
        public static LightPattern obtain(int argb, int startOffMS, int durationMS) {
            return new LightPattern(argb, startOffMS, durationMS);
        }
    }

    /**
     * detail: 设置通知栏 震动参数实体类
     * Created by Ttt
     */
    public static class VibratePattern {
        /**
         * vibrate 属性是一个长整型的数组，用于设置手机静止和震动的时长，以毫秒为单位。
         * 参数中下标为0的值表示手机静止的时长，下标为1的值表示手机震动的时长， 下标为2的值又表示手机静止的时长，以此类推。
         */
        // long[] vibrates = { 0, 1000, 1000, 1000 };
        private long[] vibrates = null;

        private VibratePattern(long[] vibrates) {
            this.vibrates = vibrates;
        }

        /**
         * 判断是否为null
         * @return
         */
        public boolean isEmpty(){
            if (vibrates != null){
                if (vibrates.length != 0){
                    return false;
                }
            }
            return true;
        }

        /**
         * 获取 震动时间 配置参数
         * @param args
         * @return
         */
        public static VibratePattern obtain(long... args) {
            return new VibratePattern(args);
        }
    }

}
