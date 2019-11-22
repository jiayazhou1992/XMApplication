package com.xiaomawang.commonlib.widget.picselect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 图片选择
 */
public interface PicSelectInterface {

    int REQUEST_CODE_CAMERA = 11000;
    int REQUEST_CODE_ALBUM = 11001;
    int REQUEST_CODE_CROP = 11002;
    int REQUEST_CODE_VIDEO = 11003;

    /**
     * 弹框
     * @param callBack
     */
    void showDialog(Context context, CallBack callBack);

    /**
     * 弹框
     * @param callBack
     */
    void showDialog(AppCompatActivity context, String outPutDir, String authorities, boolean crop, int w, int h, int aspectX, int aspectY, CallBack callBack);

    /**
     * 打开摄像机
     * @param context
     */
    void openCamera(AppCompatActivity context, String authorities, String out, boolean crop, int w, int h, int aspectX, int aspectY, CallBack callBack);

    /**
     * 打开摄像机
     * @param context
     */
    void openCameraForVideo(AppCompatActivity context, String authorities, String out);

    /**
     * 打开相册
     * @param context
     */
    void openAlbum(AppCompatActivity context);

    /**
     * 打开相册
     * @param context
     */
    void openAlbum(AppCompatActivity context, String outPutPath, String authorities, boolean crop, int w, int h, int aspectX, int aspectY, CallBack callBack);

    /**
     * 打开裁剪
     * @param context
     * @param dataPath 原图片
     * @param outPath 输出地
     */
    void openCrop(AppCompatActivity context, String dataPath, String outPath, String authorities, int w, int h, int aspectX, int aspectY, CallBack callBack);

    /**
     * 打开裁剪
     * @param context
     * @param dataUri 原图片
     * @param outUri 输出地
     */
    void openCrop(AppCompatActivity context, Uri dataUri, Uri outUri, String outPutPath, String authorities, int w, int h, int aspectX, int aspectY, CallBack callBack);


    /**
     * 处理返回结果
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void dealResult(Context context, int requestCode, int resultCode, Intent data, String out, CallBack callBack);

    /**
     * 处理返回结果
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void dealResult(AppCompatActivity context, int requestCode, int resultCode, Intent data, String out, String authorities, boolean willCrop, int w, int h, int aspectX, int aspectY, CallBack callBack);


    /**
     * 回调
     */
    interface CallBack{

        /**
         * 打开方式
         * @param type
         */
        void openType(int type);

        /**
         * 结果
         * @param bitmap
         * @param file_path
         */
        void result(int requestCode, Bitmap bitmap, String file_path, boolean crop);
    }
}
