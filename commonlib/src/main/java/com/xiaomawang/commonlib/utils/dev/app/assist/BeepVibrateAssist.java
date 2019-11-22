package com.xiaomawang.commonlib.utils.dev.app.assist;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import androidx.annotation.RawRes;

import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.Closeable;


/**
 * detail: 播放“bee”的声音, 并且震动 辅助类
 * Created by Ttt
 */
public final class BeepVibrateAssist implements Closeable {

    // 日志TAG
    private static final String TAG = BeepVibrateAssist.class.getSimpleName();
    // Context
    private final Context context;
    // 播放资源对象
    private MediaPlayer mediaPlayer = null;
    // 是否需要震动
    private boolean vibrate = true;
    // 震动时间
    private long vibrateDuration = 200L;

    /**
     * 构造函数
     * @param context
     */
    public BeepVibrateAssist(Context context) {
        this.context = context;
    }

    /**
     * 构造函数
     * @param context
     * @param rawId
     */
    public BeepVibrateAssist(Context context, @RawRes int rawId) {
        this.context = context;
        this.mediaPlayer = buildMediaPlayer(context, rawId);
    }

    /**
     * 构造函数
     * @param context
     * @param path 只支持本地资源
     */
    public BeepVibrateAssist(Context context, String path) {
        this.context = context;
        this.mediaPlayer = buildMediaPlayer(path);
    }

    // == 内部判断方法 ==

    /**
     * 检查是否允许播放声音
     * @return true: 允许, false: 不允许
     */
    private boolean shouldBeep() {
        boolean shouldPlayBeep = true;
        if (shouldPlayBeep) {
            try {
                // RINGER_MODE_NORMAL(普通)、RINGER_MODE_SILENT(静音)、RINGER_MODE_VIBRATE(震动)
                AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                    shouldPlayBeep = false; // 进入只有属于, 静音、震动，才不播放
                }
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "shouldBeep");
            }
        }
        return shouldPlayBeep;
    }

    /**
     * 内部检查更新
     */
    private synchronized void update() {
        if (shouldBeep() && mediaPlayer != null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            try {
                ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
            } catch (Exception e){
                LogPrintUtils.eTag(TAG, e, "update");
            }
        }
    }

    // == 对外公开方法 ==

    /**
     * 判断是否允许播放声音
     * @return true: 允许, false: 不允许
     */
    public boolean isPlayBeep() {
        return shouldBeep();
    }

    /**
     * 获取是否允许震动
     * @return true: 允许, false: 不允许
     */
    public boolean isVibrate() {
        return vibrate;
    }

    /**
     * 设置是否允许震动
     * @param vibrate
     * @return {@link BeepVibrateAssist}
     */
    public BeepVibrateAssist setVibrate(boolean vibrate) {
        setVibrate(vibrate, 200l);
        return this;
    }

    /**
     * 设置是否允许震动
     * @param vibrate
     * @param vibrateDuration 震动时间
     * @return {@link BeepVibrateAssist}
     */
    public BeepVibrateAssist setVibrate(boolean vibrate, long vibrateDuration) {
        this.vibrate = vibrate;
        return this;
    }

    /**
     * 设置播放资源对象
     * @param mediaPlayer
     * @return {@link BeepVibrateAssist}
     */
    public BeepVibrateAssist setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        // 进行更新
        update();
        return this;
    }

    /**
     * 进行播放声音, 并且震动
     */
    public synchronized void playBeepSoundAndVibrate() {
        // 判断是否允许播放
        if (shouldBeep() && mediaPlayer != null) {
            try {
                // 播放
                mediaPlayer.start();
            } catch (Exception e){
            }
        }
        // 判断是否允许震动
        if (vibrate) {
            try {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibrateDuration);
            } catch (Exception e){
            }
        }
    }

    /**
     * 关闭震动、提示声, 并释放资源
     */
    @Override
    public synchronized void close() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // == 创建 MediaPlayer 处理 ==

    /**
     * 创建 MediaPlayer 对象
     * @param context
     * @param rawId 响声资源id
     * @return {@link MediaPlayer}
     */
    public static final MediaPlayer buildMediaPlayer(Context context, @RawRes int rawId) {
        return buildMediaPlayer(context, rawId, 0.1f);
    }

    /**
     * 创建 MediaPlayer 对象
     * @param context
     * @param rawId 响声资源id
     * @param beepVolume 音量
     * @return {@link MediaPlayer}
     */
    public static final MediaPlayer buildMediaPlayer(Context context, @RawRes int rawId, float beepVolume) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogPrintUtils.dTag(TAG, "buildMediaPlayer - onCompletion");
//                if (mediaPlayer != null){
//                    mediaPlayer.seekTo(0);
//                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
                LogPrintUtils.dTag(TAG, "buildMediaPlayer - onError => what: " + what + ", extra: " + extra);
                // 播放异常, 直接不处理
                return true;
            }
        });
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(rawId);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            } finally {
                file.close();
            }
            mediaPlayer.setVolume(beepVolume, beepVolume);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "buildMediaPlayer");
            mediaPlayer.release();
            return null;
        }
    }

    // =

    /**
     * 创建 MediaPlayer 对象
     * @param path 响声资源路径(只支持本地资源)
     * @return {@link MediaPlayer}
     */
    public static final MediaPlayer buildMediaPlayer(String path) {
        return buildMediaPlayer(path, 0.1f);
    }

    /**
     * 创建 MediaPlayer 对象
     * @param path 响声资源路径(只支持本地资源)
     * @param beepVolume 音量
     * @return {@link MediaPlayer}
     */
    public static final MediaPlayer buildMediaPlayer(String path, float beepVolume) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogPrintUtils.dTag(TAG, "buildMediaPlayer - onCompletion");
//                if (mediaPlayer != null){
//                    mediaPlayer.seekTo(0);
//                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
                LogPrintUtils.dTag(TAG, "buildMediaPlayer - onError => what: " + what + ", extra: " + extra);
                // 播放异常, 直接不处理
                return true;
            }
        });
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.setVolume(beepVolume, beepVolume);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "buildMediaPlayer");
            mediaPlayer.release();
            return null;
        }
    }
}
