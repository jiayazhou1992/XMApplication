package com.xiaomawang.commonlib.utils.dev.app;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.IOException;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

/**
 * detail: 手电筒工具类
 * Created by Ttt
 */
public final class FlashlightUtils {

    private FlashlightUtils() {
    }

    // 日志TAG
    private final String TAG = FileRecordUtils.class.getSimpleName();

    /**
     * 获取 FlashlightUtils 实例 ,单例模式
     * @return
     */
    public static FlashlightUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static final class LazyHolder {
        private static final FlashlightUtils INSTANCE = new FlashlightUtils();
    }

    // =

    private Camera mCamera;

    /**
     * 注册摄像头
     * @return
     */
    public boolean register() {
        try {
            mCamera = Camera.open(0);
        } catch (Throwable t) {
            return false;
        }
        if (mCamera == null) {
            return false;
        }
        try {
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
            return true;
        } catch (IOException e) {
            LogPrintUtils.eTag(TAG, e, "register");
            return false;
        }
    }

    /**
     * 注销摄像头
     */
    public void unregister() {
        if (mCamera == null) return;
        mCamera.stopPreview();
        mCamera.release();
    }

    /**
     * 打开闪光灯
     */
    public void setFlashlightOn() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (!FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 关闭闪光灯
     */
    public void setFlashlightOff() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
            parameters.setFlashMode(FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 是否打开闪光灯
     * @return
     */
    public boolean isFlashlightOn() {
        if (mCamera == null) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        return FLASH_MODE_TORCH.equals(parameters.getFlashMode());
    }

    /**
     * 是否支持手机闪光灯
     * @return
     */
    public static boolean isFlashlightEnable() {
        return DevUtils.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    // =

    /**
     * 打开闪光灯
     * @param camera
     */
    public void setFlashlightOn(Camera camera) {
        if (camera != null) {
            try {
                Camera.Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameter);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "setFlashlightOn");
            }
        }
    }

    /**
     * 关闭闪光灯
     * @param camera
     */
    public void setFlashlightOff(Camera camera) {
        if (camera != null) {
            try {
                Camera.Parameters parameter = camera.getParameters();
                parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameter);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "setFlashlightOff");
            }
        }
    }

    /**
     * 是否打开了摄像头
     * @param camera
     * @return
     */
    public boolean isFlashlightOn(Camera camera) {
        if (camera == null) {
            return false;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            return FLASH_MODE_TORCH.equals(parameters.getFlashMode());
        } catch (Exception e){
            LogPrintUtils.eTag(TAG, e, "isFlashlightOn");
        }
        return false;
    }
}
