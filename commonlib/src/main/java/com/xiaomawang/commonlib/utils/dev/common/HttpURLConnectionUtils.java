package com.xiaomawang.commonlib.utils.dev.common;

import com.xiaomawang.commonlib.utils.dev.JCLogUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * detail: HttpURLConnection 网络工具类
 * Created by Ttt
 */
public final class HttpURLConnectionUtils {

    private HttpURLConnectionUtils(){
    }

    // 日志TAG
    private static final String TAG = HttpURLConnectionUtils.class.getSimpleName();

    // 请求超时时间
    private static final int TIMEOUT_IN_MILLIONS = 5000;

    // 请求回调
    public interface CallBack {

        /**
         * 请求响应回调
         * @param result
         */
        void onResponse(String result);

        /**
         * 请求失败
         * @param e
         */
        void onFail(Exception e);
    }

    /**
     * 异步的Get请求
     * @param urlStr
     * @param callBack
     */
    public static void doGetAsyn(final String urlStr, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    request("GET", urlStr, null, null, callBack);
                } catch (Exception e) {
                    JCLogUtils.eTag(TAG, e, "doGetAsyn");
                }
            }
        }.start();
    }

    /**
     * 异步的Post请求
     * @param urlStr
     * @param params
     * @param callBack
     */
    public static void doPostAsyn(final String urlStr, final String params, final CallBack callBack) {
        new Thread() {
            public void run() {
                try {
                    request("POST", urlStr, null, params, callBack);
                } catch (Exception e) {
                    JCLogUtils.eTag(TAG, e, "doPostAsyn");
                }
            }
        }.start();
    }

    /**
     * 发送请求
     * @param method
     * @param urlStr
     * @param headers
     * @param params
     * @param callBack
     * @return
     */
    public static void request(final String method, final String urlStr, final HashMap<String, String> headers, final String params, final CallBack callBack) {
        // 获取连接对象
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream bout = null;
        try {
            // 请求路径
            URL url = new URL(urlStr);
            // 获取连接对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法
            connection.setRequestMethod(method);
            // 设置请求头信息
            if (headers != null){
                Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 获取请求时间
            JCLogUtils.dTag(TAG, "response time: " + connection.getDate());
            // 判断是否需要写入数据
            if(params != null && params.length() != 0) {
                // 允许写入
                connection.setDoInput(true);
                // 设置是否向connection输出，如果是post请求，参数要放在http正文内，因此需要设为true
                connection.setDoOutput(true);
                // Post 请求不能使用缓存
                connection.setUseCaches(false);
                // 写入数据
                OutputStream ot = connection.getOutputStream();
                ot.write(params.getBytes());
                ot.flush();
                ot.close();
            }
            // 单位是毫秒
            connection.setConnectTimeout(TIMEOUT_IN_MILLIONS); // 设置连接超时
            connection.setReadTimeout(TIMEOUT_IN_MILLIONS); // 设置读取超时
            // 获取请求状态码
            int responseCode = connection.getResponseCode();
            // 判断请求码是否是200
            if (responseCode >= 200 && responseCode < 300) {
                // 输入流
                inputStream = connection.getInputStream();
                bout = new ByteArrayOutputStream();
                // 设置缓存流大小
                byte[] buffer = new byte[1024];
                int len = 0;
                while (((len = inputStream.read(buffer)) != -1)) {
                    bout.write(buffer, 0, len);
                }
                // 获取请求结果
                String result = new String(bout.toByteArray());
                // 判断是否回调
                if (callBack != null){
                    // 请求成功, 触发回调
                    callBack.onResponse(result);
                }
            } else {
                // 响应成功,非200直接返回null
                if (callBack != null){
                    callBack.onFail(new Exception("responseCode not >= 200 or < 300 , code: " + responseCode));
                }
            }
        } catch (Exception e) {
            JCLogUtils.eTag(TAG, e, "request");
            if (callBack != null){
                callBack.onFail(e);
            }
        } finally {
            if (bout != null) {
                try {
                    bout.close();
                } catch(Exception ignore){
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch(Exception ignore){
                }
            }
            if (connection != null) {
                try {
                    // 关闭底层连接Socket
                    connection.disconnect();
                } catch(Exception ignore){
                }
            }
        }
    }

    // == 获取网络时间处理 ==

    public static final String BAIDU_URL = "https://www.baidu.com";

    /**
     * 时间回调
     */
    public interface TimeCallBack {

        /**
         * 请求相应回调
         * @param time 毫秒
         */
        void onResponse(long time);

        /**
         * 请求失败
         * @param e
         */
        void onFail(Exception e);
    }

    /**
     * 获取网络时间 - 默认使用百度链接
     * @param timeCallBack
     */
    public static void getNetTime(TimeCallBack timeCallBack){
        getNetTime(BAIDU_URL, timeCallBack);
    }

    /**
     * 获取网络时间
     * @param urlStr
     * @param timeCallBack
     */
    public static void getNetTime(final String urlStr, final TimeCallBack timeCallBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                reqNetTime(urlStr, timeCallBack);
            }
        }).start();
    }

    /**
     * 请求网络时间(内部私有)
     * @param urlStr
     * @param timeCallBack
     */
    private static void reqNetTime(String urlStr, TimeCallBack timeCallBack){
        // 获取连接对象
        HttpURLConnection connection = null;
        try {
            // 请求路径
            URL url = new URL(urlStr);
            // 获取连接对象
            connection = (HttpURLConnection) url.openConnection();
            // 获取时间
            long date = connection.getDate();
            // 获取失败, 则进行修改
            if (date <= 0){
                date = -1l;
            }
            // 触发回调
            if (timeCallBack != null){
                timeCallBack.onResponse(date);
            }
        } catch (Exception e){
            JCLogUtils.eTag(TAG, e, "getNetTime");
            // 触发回调
            if (timeCallBack != null){
                timeCallBack.onFail(e);
            }
        } finally {
            if (connection != null) {
                try {
                    // 关闭底层连接Socket
                    connection.disconnect();
                } catch(Exception ignore){
                }
            }
        }
    }
}