package com.xiaomawang.commonlib.utils.dev.app.image;

import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.IntRange;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

/**
 * detail: Android 自己的 RenderScript 实现图片模糊
 * Created by Ttt
 * 注：仅在 SDK >= 17 时有用
 */
public final class RSBlurUtils {

    private RSBlurUtils(){
    }

    // 日志TAG
    private static final String TAG = RSBlurUtils.class.getSimpleName();

    /**
     * RenderScript 实现图片模糊
     * @param bitmap 待模糊图片
     * @param radius 模糊度(0-25)
     * @return
     * @throws RSRuntimeException
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Bitmap bitmap, @IntRange(from = 0, to = 25) int radius) throws RSRuntimeException {
        try {
            RenderScript rs = RenderScript.create(DevUtils.getContext());
            Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(bitmap);
            rs.destroy();
            return bitmap;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "blur");
        }
        return null;
    }
}
