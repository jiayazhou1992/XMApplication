package com.xiaomawang.commonlib.widget.picselect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaomawang.commonlib.utils.dev.app.IntentUtils;
import com.xiaomawang.commonlib.utils.dev.app.PathUtils;
import com.xiaomawang.commonlib.utils.dev.app.PermissionUtils;
import com.xiaomawang.commonlib.utils.dev.app.UriUtils;
import com.xiaomawang.commonlib.utils.dev.app.image.BitmapExtendUtils;
import com.xiaomawang.commonlib.utils.dev.app.image.BitmapUtils;
import com.xiaomawang.commonlib.utils.dev.app.toast.ToastUtils;
import com.xiaomawang.commonlib.utils.dev.common.DateUtils;
import com.xiaomawang.commonlib.utils.dev.common.FileUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;
import com.xiaomawang.commonlib.widget.router.Router;

import java.io.File;
import java.io.FileNotFoundException;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class PicSelectInterfaceImpl implements PicSelectInterface{

    private static final String TAG = "PicSelectInterfaceImpl";

    private String file_path;

    private TypeDialog typeDialog;

    @Override
    public void showDialog(Context context, final CallBack callBack) {
        typeDialog = new TypeDialog(context);
        typeDialog.setOnClickListener(new TypeDialog.OnClickListener() {
            @Override
            public void onClick(int type) {
                callBack.openType(type);
            }
        });
        typeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callBack.openType(-1);
            }
        });
        typeDialog.show();

    }

    @Override
    public void showDialog(final AppCompatActivity context, final String outPutDir, final String authorities, final boolean crop, final int w, final int h, final int aspectX, final int aspectY, final CallBack callBack) {
        typeDialog = new TypeDialog(context);
        typeDialog.setOnClickListener(new TypeDialog.OnClickListener() {
            @Override
            public void onClick(int type) {
                if (type == 0){
                    openCamera(context, authorities, outPutDir, crop, w, h, aspectX, aspectY, callBack);
                }else if (type == 1){
                    openAlbum(context, outPutDir, authorities, crop, w, h, aspectX, aspectY, callBack);
                }else {

                }
            }
        });
        typeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                callBack.openType(-1);
            }
        });
        typeDialog.show();
    }

    @Override
    public void openCamera(final AppCompatActivity context, final String authorities, final String out, final boolean crop, final int w, final int h, final int aspectX, final int aspectY, final CallBack callBack) {
        file_path = null;

        PermissionUtils.permission(Manifest.permission.CAMERA).callBack(new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted(PermissionUtils permissionUtils) {

                file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                File file = new File(file_path);

                if (FileUtils.createFileByDeleteOldFile(file_path)) {

                    Intent intent = IntentUtils.getCaptureIntent(file, authorities, false);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,2 * 1024 * 1024);

                    Router.with(context).setRequestCode(REQUEST_CODE_CAMERA).pageGo(intent, new Router.CallBack() {
                        @Override
                        public void onPageBack(int requestCode, int resultCode, Intent data) {
                            if (resultCode == Activity.RESULT_OK){
                                dealResult(context, requestCode, resultCode, data, out, authorities, crop, w, h, aspectX, aspectY, callBack);
                            }else {
                                callBack.openType(-1);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDenied(PermissionUtils permissionUtils) {

            }
        }).request();
    }

    @Override
    public void openCameraForVideo(final AppCompatActivity context, final String authorities, final String out) {
        file_path = null;

        PermissionUtils.permission(Manifest.permission.CAMERA).callBack(new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted(PermissionUtils permissionUtils) {

                file_path = out + File.separator + DateUtils.getDateNow() + ".map4";
                File file = new File(file_path);

                if (FileUtils.createFileByDeleteOldFile(file_path)) {

                    Intent intent = IntentUtils.getVideoIntent(file, authorities, false);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);

                    context.startActivityForResult(intent, REQUEST_CODE_VIDEO);
                }
            }

            @Override
            public void onDenied(PermissionUtils permissionUtils) {

            }
        }).request();
    }

    @Override
    public void openAlbum(final AppCompatActivity context) {
        file_path = null;

        PermissionUtils.permission(Manifest.permission.READ_EXTERNAL_STORAGE).callBack(new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted(PermissionUtils permissionUtils) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                context.startActivityForResult(openAlbumIntent, REQUEST_CODE_ALBUM);//打开相册
            }

            @Override
            public void onDenied(PermissionUtils permissionUtils) {

            }
        }).request();

    }

    @Override
    public void openAlbum(final AppCompatActivity context, final String outPutPath, final String authorities, final boolean crop, final int w, final int h, final int aspectX, final int aspectY, final CallBack callBack) {
        file_path = null;

        PermissionUtils.permission(Manifest.permission.READ_EXTERNAL_STORAGE).callBack(new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted(PermissionUtils permissionUtils) {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                Router.with(context).setRequestCode(REQUEST_CODE_ALBUM).pageGo(openAlbumIntent, new Router.CallBack() {
                    @Override
                    public void onPageBack(int requestCode, int resultCode, Intent data) {
                        if (resultCode == Activity.RESULT_OK){
                            dealResult(context, requestCode, resultCode, data, outPutPath, authorities, crop, w, h, aspectX, aspectY, callBack);
                        }else {
                            callBack.openType(-1);
                        }
                    }
                });//打开相册
            }

            @Override
            public void onDenied(PermissionUtils permissionUtils) {

            }
        }).request();
    }

    @Override
    public void openCrop(AppCompatActivity context, String dataPath, String outPath, String authorities, int w, int h, int aspectX, int aspectY, CallBack callBack) {
        file_path = null;

        File dataFile = FileUtils.getFile(dataPath);
        if (!FileUtils.isFileExists(dataFile)){
            return;
        }
        //file_path = outPath + File.separator + DateUtils.getDateNow() + "_crop.jpg";
        file_path = PicSelectHelper.getCropPath(dataPath);
        File outFile = FileUtils.getFile(file_path);
        FileUtils.createFileByDeleteOldFile(outFile);

        Uri dataUri = null;
        Uri outUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以上通过fileProvider访问
            dataUri = UriUtils.getUriForFile(dataFile, authorities);
            //outUri = UriUtils.getUriForFile(outFile, authorities);
            outUri = Uri.fromFile(outFile);
        }else {
            dataUri = Uri.fromFile(dataFile);
            outUri = Uri.fromFile(outFile);
        }

        openCrop(context, dataUri, outUri, outPath, authorities, w, h, aspectX, aspectY, callBack);
    }

    @Override
    public void openCrop(final AppCompatActivity context, Uri dataUri, Uri outUri, final String outPutPath, final String authorities, int w, int h, int aspectX, int aspectY, final CallBack callBack) {
        if (w == 0 && h == 0) {
            w = h = 480;
        }
        if (aspectX == 0 && aspectY == 0) {
            aspectX = aspectY = 1;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        // 照片URL地址
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
        //是否缩放
        intent.putExtra("scale", true);
        // 不启用人脸识别
        intent.putExtra("noFaceDetection", false);
        intent.putExtra("return-data", false);

        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setDataAndType(dataUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);

        // 输出格式
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", "JPEG");

        final int finalW = w;
        final int finalH = h;
        final int finalAspectX = aspectX;
        final int finalAspectY = aspectY;
        Router.with(context).setRequestCode(REQUEST_CODE_CROP).pageGo(intent, new Router.CallBack() {
            @Override
            public void onPageBack(int requestCode, int resultCode, Intent data) {
                if (resultCode == Activity.RESULT_OK){
                    dealResult(context, requestCode, resultCode, data, outPutPath, authorities, false, finalW, finalH, finalAspectX, finalAspectY, callBack);
                }
            }
        });
    }

    @Override
    public void dealResult(Context context, int requestCode, int resultCode, Intent data, String out, CallBack callBack) {

        boolean crop = false;
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_CODE_CROP || requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_VIDEO)){

            if (requestCode == REQUEST_CODE_CROP){
                crop = true;

                bitmap = BitmapUtils.getSDCardBitmapStream(file_path);
            }else if (requestCode == REQUEST_CODE_CAMERA){
                if (data != null){
                    try {
                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else {
                    bitmap = BitmapUtils.getSDCardBitmapStream(file_path);
                }
            }else if (requestCode == REQUEST_CODE_ALBUM){
                try {
                    file_path = PathUtils.getFilePathByUri(context, data.getData());

                    if (StringUtils.isEmpty(file_path)){
                        file_path = PathUtils.getHWImagePathFromURI(context, data.getData());
                    }
                    if (StringUtils.isEmpty(file_path)){
                        file_path = PathUtils.getXMImagePathFromURI(context, data);
                    }

                    bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data.getData()));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == REQUEST_CODE_VIDEO){
                if (callBack != null){
                    callBack.result(requestCode, bitmap, file_path, crop);
                }

                return; // 录像的话下面就不走了
            }

            if (bitmap == null || StringUtils.isEmpty(file_path)) {
                ToastUtils.showShort(context, "获取图片失败");
                return;
            }


            if (bitmap.getWidth() < 400 || bitmap.getHeight() < 400){
                ToastUtils.showShort(context, "图片尺寸过小");
                return;
            }

            // 旋转过来
            bitmap = BitmapExtendUtils.rotate(bitmap, BitmapExtendUtils.getImageDegree(file_path));

            // 检查size 并缩放
            if (bitmap.getWidth() > 1080 || bitmap.getHeight() > 1920){

                float scale = 1.0f;
                if (bitmap.getWidth() > 1080){
                    scale = 1080 * 1.0f / bitmap.getWidth();
                }else if (bitmap.getHeight() > 1920){
                    scale = 1920 * 1.0f / bitmap.getHeight();
                }

                bitmap = BitmapExtendUtils.scale(bitmap, scale);

                if (!TextUtils.equals(FileUtils.getDirName(file_path),out + File.separator)){
                    file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                }

                BitmapUtils.saveBitmapToSDCardJPEG(bitmap, file_path, 100);
            }else {
                if (!TextUtils.equals(FileUtils.getDirName(file_path),out + File.separator)){
                    file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                    BitmapUtils.saveBitmapToSDCardJPEG(bitmap, file_path, 100);
                }
            }

            if (callBack != null){
                callBack.result(requestCode, bitmap, file_path, crop);
            }
        }
    }

    @Override
    public void dealResult(AppCompatActivity context, int requestCode, int resultCode, Intent data, String out, String authorities, boolean willCrop, int w, int h, int aspectX, int aspectY, CallBack callBack) {
        boolean crop = false;
        Bitmap bitmap = null;

        if (resultCode == Activity.RESULT_OK && (requestCode == REQUEST_CODE_CROP || requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_VIDEO)){

            if (requestCode == REQUEST_CODE_CROP){
                crop = true;
                bitmap = BitmapUtils.getSDCardBitmapStream(file_path);
                // 裁剪完成，删除原图（本应用内的）
                FileUtils.deleteFile(PicSelectHelper.originalPath(file_path));
            }else if (requestCode == REQUEST_CODE_CAMERA){
                if (data != null){
                    try {
                        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else {
                    bitmap = BitmapUtils.getSDCardBitmapStream(file_path);
                }
            }else if (requestCode == REQUEST_CODE_ALBUM){
                try {
                    file_path = PathUtils.getFilePathByUri(context, data.getData());

                    if (StringUtils.isEmpty(file_path)){
                        file_path = PathUtils.getHWImagePathFromURI(context, data.getData());
                    }
                    if (StringUtils.isEmpty(file_path)){
                        file_path = PathUtils.getXMImagePathFromURI(context, data);
                    }

                    bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data.getData()));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if (requestCode == REQUEST_CODE_VIDEO){
                if (callBack != null){
                    callBack.result(requestCode, bitmap, file_path, crop);
                }

                return; // 录像的话下面就不走了
            }

            if (bitmap == null || StringUtils.isEmpty(file_path)) {
                ToastUtils.showShort(context, "获取图片失败");
                if (callBack != null) {
                    callBack.result(0, null, "", false);
                }
                return;
            }

            if (FileUtils.getFileLength(file_path) >= 10 * 1024 * 1024){
                ToastUtils.showShort(context, "图片过大");
                if (callBack != null) {
                    callBack.result(0, null, "", false);
                }
                return;
            }

            // 裁剪
            if (!crop && willCrop){
                if (!TextUtils.equals(FileUtils.getDirName(file_path),out + File.separator)){// 如果不是本应用数据，复制到本应用文件夹下
                    file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                    BitmapUtils.saveBitmapToSDCardJPEG(bitmap, file_path, 100);
                }
                openCrop(context, file_path, out, authorities, w, h, aspectX, aspectY,callBack);
                return;
            }

            if (bitmap.getWidth() < 40 || bitmap.getHeight() < 40){
                ToastUtils.showShort(context, "图片尺寸过小");
                if (callBack != null) {
                    callBack.result(0, null, "", false);
                }
                return;
            }

            // 旋转过来
            bitmap = BitmapExtendUtils.rotate(bitmap, BitmapExtendUtils.getImageDegree(file_path));

            // 检查size 并缩放
            if (bitmap.getWidth() > 1080 || bitmap.getHeight() > 1920){

                float scale = 1.0f;
                if (bitmap.getWidth() > 1080){
                    scale = 1080 * 1.0f / bitmap.getWidth();
                }else if (bitmap.getHeight() > 1920){
                    scale = 1920 * 1.0f / bitmap.getHeight();
                }

                bitmap = BitmapExtendUtils.scale(bitmap, scale);

                if (!TextUtils.equals(FileUtils.getDirName(file_path),out + File.separator)){
                    file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                }

                BitmapUtils.saveBitmapToSDCardJPEG(bitmap, file_path, 100);
            }else {
                if (!TextUtils.equals(FileUtils.getDirName(file_path),out + File.separator)){
                    file_path = out + File.separator + DateUtils.getDateNow() + ".jpg";
                    BitmapUtils.saveBitmapToSDCardJPEG(bitmap, file_path, 100);
                }
            }

            if (callBack != null) {
                callBack.result(requestCode, bitmap, file_path, crop);
            }
        }
    }
}
