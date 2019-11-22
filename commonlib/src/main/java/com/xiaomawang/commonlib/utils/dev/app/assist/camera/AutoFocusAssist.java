package com.xiaomawang.commonlib.utils.dev.app.assist.camera;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;

import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.RejectedExecutionException;


/**
 * detail: 自动获取焦点 辅助类
 * Created by Ttt
 */
public final class AutoFocusAssist implements Camera.AutoFocusCallback {

    // 日志 TAG
    private final String TAG = AutoFocusAssist.class.getSimpleName();
    // 设置对焦模式
    public static final Collection<String> FOCUS_MODES_CALLING_AF;

    static {
        // 对焦模式
        // https://blog.csdn.net/fulinwsuafcie/article/details/49558001
        FOCUS_MODES_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO); // 自动对焦
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO); // 微距
    }

    // == 变量 ==
    // 间隔获取焦点时间
    private long interval = 2000L;
    // 摄像头对象
    private final Camera camera;
    // 判断摄像头是否使用对焦
    private final boolean useAutoFocus;
    // 判断是否停止对焦
    private boolean stopped;
    // 判断是否对焦中
    private boolean focusing;
    // 对焦任务
    private AsyncTask<?, ?, ?> outstandingTask;
    // 判断是否需要自动对焦
    private boolean isAutoFocus = true;

    // == 构造函数 ==

    public AutoFocusAssist(Camera camera){
        this(camera, 2000L);
    }

    public AutoFocusAssist(Camera camera, long interval) {
        this.camera = camera;
        this.interval = interval;
        // 防止为null
        if (camera != null){
            // 获取对象对焦模式
            String currentFocusMode = camera.getParameters().getFocusMode();
            // 判断是否(使用/支持)对焦
            useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
        } else {
            // 不支持对焦
            useAutoFocus = false;
        }
        // 开始任务
        start();
    }

    /**
     * 设置对焦模式
     * @param collection
     */
    public static void setFocusModes(Collection<String> collection){
        // 清空旧的
        FOCUS_MODES_CALLING_AF.clear();
        // 防止为null
        if (collection != null){
            FOCUS_MODES_CALLING_AF.addAll(collection);
        }
    }

    /**
     * 是否允许自动对焦
     * @return true: 自动对焦, false: 非自动对焦
     */
    public boolean isAutoFocus() {
        return isAutoFocus;
    }

    /**
     * 设置是否开启自动对焦
     * @param autoFocus
     */
    public void setAutoFocus(boolean autoFocus) {
        isAutoFocus = autoFocus;
        // 判断是否开启自动对焦
        if (isAutoFocus){
            start();
        } else {
            stop();
        }
    }

    /**
     * 对焦回调 {@link Camera.AutoFocusCallback} 重写方法
     * @param success 是否对焦成功
     * @param theCamera 对焦的摄像头
     */
    @Override
    public synchronized void onAutoFocus(boolean success, Camera theCamera) {
        // 对焦结束, 设置非对焦中
        focusing = false;
        // 再次自动对焦
        autoFocusAgainLater();
    }

    // = 内部方法 =

    /**
     * 再次自动对焦
     */
    @SuppressLint("NewApi")
    private synchronized void autoFocusAgainLater() {
        // 不属于停止, 并且任务等于null, 才处理
        if (!stopped && outstandingTask == null) {
            // 初始化任务
            AutoFocusTask newTask = new AutoFocusTask();
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    // 默认使用异步任务
                    newTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    newTask.execute();
                }
                outstandingTask = newTask;
            } catch (RejectedExecutionException ree) {
                LogPrintUtils.eTag(TAG, ree, "autoFocusAgainLater");
            }
        }
    }

    /**
     * 开始对焦
     */
    public synchronized void start() {
        // 如果不使用自动对焦, 则不处理
        if (!isAutoFocus){
            return;
        }
        // 支持对焦才处理
        if (useAutoFocus) {
            // 重置任务为null
            outstandingTask = null;
            // 不属于停止 并且 非对焦中
            if (!stopped && !focusing) {
                try {
                    // 设置自动对焦回调
                    camera.autoFocus(this);
                    // 表示对焦中
                    focusing = true;
                } catch (RuntimeException re) {
                    LogPrintUtils.eTag(TAG, re,"start");
                    // Try again later to keep cycle going
                    autoFocusAgainLater();
                }
            }
        }
    }

    /**
     * 停止对焦
     */
    public synchronized void stop() {
        // 表示属于停止
        stopped = true;
        // 判断是否支持对焦
        if (useAutoFocus) {
            // 关闭任务
            cancelOutstandingTask();
            try {
                // 取消对焦
                camera.cancelAutoFocus();
            } catch (RuntimeException re) {
                LogPrintUtils.eTag(TAG, re,"stop");
            }
        }
    }

    /**
     * 取消对焦任务
     */
    private synchronized void cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask.getStatus() != AsyncTask.Status.FINISHED) {
                outstandingTask.cancel(true);
            }
            outstandingTask = null;
        }
    }

    /**
     * detail: 自动对焦任务
     * Created by Ttt
     */
    private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... voids) {
            try {
                // 堵塞时间
                Thread.sleep(interval);
            } catch (InterruptedException e) {
            }
            // 开启定时
            start();
            return null;
        }
    }
}
