package com.xiaomawang.commonlib.utils.dev.common;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.io.Closeable;


/**
 * detail: 关闭工具类 - (关闭IO流等)
 * Created by Blankj
 * Update to Ttt
 */
public final class CloseUtils {

    private CloseUtils() {
    }

    // 日志TAG
    private static final String TAG = CloseUtils.class.getSimpleName();

    /**
     * 关闭 IO
     * @param closeables closeables
     */
    public static void closeIO(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    JCLogUtils.eTag(TAG, e, "closeIO");
                }
            }
        }
    }

    /**
     * 安静关闭 IO
     * @param closeables closeables
     */
    public static void closeIOQuietly(final Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception ignore) {
                    JCLogUtils.eTag(TAG, ignore, "closeIO");
                }
            }
        }
    }
}
