package com.xiaomawang.commonlib.utils.dev.app;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.database.Cursor;
import android.net.Uri;

import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;


/**
 * detail: 创建删除快捷图标工具类
 * Created by Ttt
 * 需要权限:
 * com.android.launcher.permission.INSTALL_SHORTCUT
 * com.android.launcher.permission.UNINSTALL_SHORTCUT
 */
public final class ShortCutUtils {

    private ShortCutUtils() {
    }

    // 日志TAG
    private static final String TAG = ShortCutUtils.class.getSimpleName();

    /**
     * 检测是否存在桌面快捷方式
     * @param context
     * @param name
     * @return 是否存在桌面图标
     */
    public static boolean hasShortcut(Context context, String name) {
        boolean isInstallShortcut = false;
        Cursor cursor = null;
        try {
            ContentResolver cr = context.getContentResolver();
            String AUTHORITY = "com.android.launcher.settings";
            Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
            cursor = cr.query(CONTENT_URI, new String[]{"title", "iconResource"}, "title=?", new String[]{ name }, null);
            if (cursor != null && cursor.getCount() > 0) {
                isInstallShortcut = true;
            }
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "hasShortcut");
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                }
            }
        }
        return isInstallShortcut;
    }

    /**
     * 为程序创建桌面快捷方式
     * @param context
     * @param clasName
     * @param name
     * @param res
     */
    public static void addShortcut(Context context, String clasName, String name, int res) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name); // 快捷方式的名称
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        // 设置 快捷方式跳转页面
        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(context, clasName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        // 快捷方式的图标
        ShortcutIconResource iconRes = ShortcutIconResource.fromContext(context, res);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }

    /**
     * 删除程序的快捷方式
     * @param context
     * @param name
     */
    public static void delShortcut(Context context, String clasName, String name) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        String appClass = context.getPackageName() + "." + clasName;
        ComponentName comp = new ComponentName(context.getPackageName(), appClass);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
        context.sendBroadcast(shortcut);
    }
}