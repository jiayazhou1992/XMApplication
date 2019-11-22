package com.xiaomawang.commonlib.utils.dev.common.assist;

import android.os.SystemClock;

/**
 * detail: 时间堵塞保留
 * Created by 氢一
 * Update to Ttt
 */
public class TimeKeeper {

    // 预计堵塞时间
    private long keepTimeMillis;
    // 开始计时时间
    private long startMillis;

    /**
     * 构造函数
     * @param keepTimeMillis
     */
    public TimeKeeper(long keepTimeMillis) {
        this.keepTimeMillis = keepTimeMillis;
    }

    /**
     * 获取预计堵塞时间
     * @return
     */
    public long getKeepTimeMillis() {
        return keepTimeMillis;
    }

    /**
     * 设置预计堵塞时间
     * @param keepTimeMillis
     * @return
     */
    public TimeKeeper setKeepTimeMillis(long keepTimeMillis) {
        this.keepTimeMillis = keepTimeMillis;
        return this;
    }

    /**
     * 开始计时
     * @return
     */
    public TimeKeeper startNow() {
        startMillis = SystemClock.elapsedRealtime();
        return this;
    }

    /**
     * 设置等待一段时间后, 通知方法
     * @param endCallback
     * @return
     */
    public TimeKeeper waitForEnd(OnEndCallback endCallback) {
        long costMillis = SystemClock.elapsedRealtime() - startMillis;
        long leftMillis = keepTimeMillis - costMillis;
        if (leftMillis > 0) {
            SystemClock.sleep(leftMillis);
            endCallback.onEnd(costMillis, leftMillis);
        } else {
            endCallback.onEnd(costMillis, leftMillis);
        }
        return this;
    }

    /**
     * 结束通知回调
     */
    public interface OnEndCallback {

        /**
         * 结束触发通知方法
         * @param costTime 使用 -> 花费时间
         * @param leftTime 堵塞时间
         */
        void onEnd(long costTime, long leftTime);
    }
}
