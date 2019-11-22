package com.xiaomawang.commonlib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaomawang.commonlib.utils.dev.app.AppUtils;
import com.xiaomawang.commonlib.utils.dev.app.toast.ToastUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;


public class FunctionUtils {
    /**
     * h获取渠道
     * @return
     */
    public static String getChannelFrom(){
        try {
            PackageManager pm = AppUtils.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(AppUtils.getAppPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

    /**
     * 复制
     * @param copyStr
     */
    public static void copyStr(AppCompatActivity activity, String copyStr){
        if (StringUtils.isEmpty(copyStr)) return;

        ClipboardManager tvCopy = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        String text = copyStr;
        ClipData myClip = ClipData.newPlainText("text", text);
        tvCopy.setPrimaryClip(myClip);

        ToastUtils.showShort(activity, "复制成功");
    }
}
