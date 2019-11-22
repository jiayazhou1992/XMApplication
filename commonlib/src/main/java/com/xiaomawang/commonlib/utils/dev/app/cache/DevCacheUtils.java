package com.xiaomawang.commonlib.utils.dev.app.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.ByteArrayOutputStream;

/**
 * detail: 缓存检查(时间)工具类
 * Created by 杨福海(michael) www.yangfuhai.com
 * Update to Ttt
 */
final class DevCacheUtils {

    private DevCacheUtils(){
    }

    // 日志Tag
    private static final String TAG = DevCacheUtils.class.getSimpleName();

    /**
     * 判断缓存的 String 数据是否到期
     * @param str
     * @return true: 到期了, false: 还没有到期
     */
    public static boolean isDue(String str) {
        return isDue(str.getBytes());
    }

    /**
     * 判断缓存的 byte 数据是否到期
     * @param data
     * @return true: 到期了, false: 还没有到期
     */
    public static boolean isDue(byte[] data) {
        // 获取时间数据信息
        String[] strs = getDateInfoFromDate(data);
        // 判断是否过期
        if (strs != null && strs.length == 2) {
            // 保存的时间
            String saveTimeStr = strs[0];
            // 判断是否0开头,是的话裁剪
            while (saveTimeStr.startsWith("0")) {
                saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
            }
            // 转换时间
            long saveTime = Long.valueOf(saveTimeStr); // 保存时间
            long deleteAfter = Long.valueOf(strs[1]); // 过期时间
            // 判断当前时间是否大于 保存时间 + 过期时间
            if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                return true;
            }
        }
        return false;
    }

    // -

    /**
     * 保存数据, 创建时间信息
     * @param second
     * @param strInfo
     * @return
     */
    public static String newStringWithDateInfo(int second, String strInfo) {
        return createDateInfo(second) + strInfo;
    }

    /**
     * 保存数据, 创建时间信息
     * @param second
     * @param data
     * @return
     */
    public static byte[] newByteArrayWithDateInfo(int second, byte[] data) {
        if (data != null){
            try {
                byte[] dataArys = createDateInfo(second).getBytes();
                byte[] retData = new byte[dataArys.length + data.length];
                System.arraycopy(dataArys, 0, retData, 0, dataArys.length);
                System.arraycopy(data, 0, retData, dataArys.length, data.length);
                return retData;
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "newByteArrayWithDateInfo");
            }
        }
        return null;
    }

    private static final char mSeparator = ' ';

    /**
     * 创建时间信息
     * @param second
     * @return
     */
    private static String createDateInfo(int second) {
        String currentTime = System.currentTimeMillis() + "";
        while (currentTime.length() < 13) {
            currentTime = "0" + currentTime;
        }
        return currentTime + "-" + second + mSeparator;
    }

    /**
     * 清空时间信息
     * @param strInfo
     * @return
     */
    public static String clearDateInfo(String strInfo) {
        if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
            strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1, strInfo.length());
        }
        return strInfo;
    }

    /**
     * 清空时间信息
     * @param data
     * @return
     */
    public static byte[] clearDateInfo(byte[] data) {
        if (hasDateInfo(data)) {
            try {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "clearDateInfo");
            }
        }
        return data;
    }

    /**
     * 检验时间信息
     * @param data
     * @return
     */
    private static boolean hasDateInfo(byte[] data) {
        return data != null && data.length > 15 && data[13] == '-' && indexOf(data, mSeparator) > 14;
    }

    /**
     * 获取时间信息 - 保存时间、过期时间
     * @param data
     * @return
     */
    private static String[] getDateInfoFromDate(byte[] data) {
        if (hasDateInfo(data)) {
            try {
                // 保存时间
                String saveDate = new String(copyOfRange(data, 0, 13));
                // 过期时间
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                // 返回数据
                return new String[]{saveDate, deleteAfter};
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "getDateInfoFromDate");
            }
        }
        return null;
    }

    private static int indexOf(byte[] data, char c) {
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static byte[] copyOfRange(byte[] original, int from, int to) throws Exception {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);

        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Bitmap → byte[]
     * @param bm
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "bitmap2Bytes");
        }
        return null;
    }

    /**
     * byte[] → Bitmap
     * @param bytes
     * @return
     */
    public static Bitmap bytes2Bimap(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "bytes2Bimap");
            }
        }
        return null;
    }

    /**
     * Drawable → Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        try {
            // 取 drawable 的长宽
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            // 取 drawable 的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            // 建立对应 bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            // 建立对应 bitmap 的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            // 把 drawable 内容画到画布中
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "drawable2Bitmap");
        }
        return null;
    }

    /**
     * Bitmap → Drawable
     * @param bm
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Drawable bitmap2Drawable(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        try {
            BitmapDrawable bd = new BitmapDrawable(bm);
            bd.setTargetDensity(bm.getDensity());
            return new BitmapDrawable(bm);
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "bitmap2Drawable");
        }
        return null;
    }
}
