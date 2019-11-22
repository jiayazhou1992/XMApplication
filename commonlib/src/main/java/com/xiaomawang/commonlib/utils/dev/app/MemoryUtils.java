package com.xiaomawang.commonlib.utils.dev.app;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;

import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * detail: 获取内存信息
 * Created by Ttt
 */
public final class MemoryUtils {

    private MemoryUtils(){
    }

    // 日志TAG
    private static final String TAG = MemoryUtils.class.getSimpleName();

    /**
     * 打印内存信息
     * Print memory info. such as:
     * MemTotal:        1864292 kB
     * MemFree:          779064 kB
     * Buffers:            4540 kB
     * Cached:           185656 kB
     * SwapCached:        13160 kB
     * Active:           435588 kB
     * Inactive:         269312 kB
     * Active(anon):     386188 kB
     * Inactive(anon):   132576 kB
     * Active(file):      49400 kB
     * Inactive(file):   136736 kB
     * Unevictable:        2420 kB
     * Mlocked:               0 kB
     * HighTotal:       1437692 kB
     * HighFree:         520212 kB
     * LowTotal:         426600 kB
     * LowFree:          258852 kB
     * SwapTotal:        511996 kB
     * SwapFree:         171876 kB
     * Dirty:               412 kB
     * Writeback:             0 kB
     * AnonPages:        511924 kB
     * Mapped:           152368 kB
     * Shmem:              1636 kB
     * Slab:             109224 kB
     * SReclaimable:      75932 kB
     * SUnreclaim:        33292 kB
     * KernelStack:       13056 kB
     * PageTables:        28032 kB
     * NFS_Unstable:          0 kB
     * Bounce:                0 kB
     * WritebackTmp:          0 kB
     * CommitLimit:     1444140 kB
     * Committed_AS:   25977748 kB
     * VmallocTotal:     458752 kB
     * VmallocUsed:      123448 kB
     * VmallocChunk:     205828 kB
     */
    public static String printMemInfo() {
        try {
            FileReader fileReader = new FileReader(MEM_INFO_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
            StringBuffer stringBuffer = new StringBuffer();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                // 追加保存内容
                stringBuffer.append(str);
            }
            bufferedReader.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "printMemInfo");
        }
        return "";
    }

    /**
     * 获取内存信息
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    public static ActivityManager.MemoryInfo getMemoryInfo() {
        ActivityManager am = (ActivityManager) DevUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi;
    }

    /**
     * 打印内存信息
     * @param context
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    public static ActivityManager.MemoryInfo printMemoryInfo(Context context) {
        ActivityManager.MemoryInfo mi = getMemoryInfo();
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("_______  Memory :   ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sBuilder.append("\ntotalMem :").append(mi.totalMem);
        }
        sBuilder.append("\navailMem :").append(mi.availMem);
        sBuilder.append("\nlowMemory :").append(mi.lowMemory);
        sBuilder.append("\nthreshold :").append(mi.threshold);
        return mi;
    }

    /**
     * 获取可用内存信息
     * Get available memory info.
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    public static String getAvailMemory() {// 获取android 当前可用内存大小
        ActivityManager am = (ActivityManager) DevUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi); // mi.availMem; 当前系统的可用内存
        // 将获取的内存大小规格化
        return Formatter.formatFileSize(DevUtils.getContext(), mi.availMem);
    }

    // 内存信息文件地址
    private static final String MEM_INFO_PATH = "/proc/meminfo";
    // 获取内存总大小
    public static final String MEMTOTAL = "MemTotal";
    // 获取可用内存
    public static final String MEMAVAILABLE = "MemAvailable";


    /**
     * 获取总内存大小
     * @return
     */
    public static String getTotalMemory() {
        return getMemInfoIype(MEMTOTAL);
    }

    /**
     * 获取可用内存大小
     * @return
     */
    public static String getMemoryAvailable() {
        return getMemInfoIype(MEMAVAILABLE);
    }

    /**
     * 通过不同 type 获取对应的内存信息
     * @param type
     * @return
     */
    public static String getMemInfoIype(String type) {
        try {
            FileReader fileReader = new FileReader(MEM_INFO_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 4 * 1024);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.contains(type)) {
                    break;
                }
            }
            bufferedReader.close();
            /* \\s表示   空格,回车,换行等空白符,
            +号表示一个或多个的意思     */
            String[] array = str.split("\\s+");
            // 获取系统总内存，单位是KB，乘以1024转换为Byte
            long length = Long.valueOf(array[1]).longValue() * 1024;
            return Formatter.formatFileSize(DevUtils.getContext(), length);
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getMemInfoIype");
        }
        return "unknown";
    }

}
