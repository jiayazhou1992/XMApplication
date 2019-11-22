package com.xiaomawang.commonlib.ui.web;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomawang.commonlib.ui.web.bean.ArgJson;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.io.Serializable;
import java.util.Map;

public abstract class JSInterface implements Serializable {
    public static final String TAG = "JSInterface";

    protected WebView mWebView;
    protected WebContract.View view;
    protected Handler uiHandler;

    protected Gson gson;

    public void attachView(WebView webView, WebContract.View view){
        this.mWebView = webView;
        this.view = view;
        this.uiHandler = new Handler(Looper.getMainLooper());
        if (gson == null) {
            gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        }
    }

    public void detachView(){
        this.mWebView = null;
        this.view = null;
        this.uiHandler.removeCallbacks(null);
        this.uiHandler = null;
    }

    ArgJson getArgJson(String argJsonStr){
        Log.i(TAG, " arg -> " + argJsonStr);
        ArgJson argJson = null;
        if (!StringUtils.isEmpty(argJsonStr)){
            try {
                argJson = gson.fromJson(argJsonStr, ArgJson.class);
            }catch (Exception e){

            }
        }
        return argJson;
    }

    /**
     * 加载数据
     * @param argJsonStr
     */
    @JavascriptInterface
    public abstract void postMessage(String argJsonStr);

    /**
     * 加载框
     * @param loading
     */
    public abstract void loading(final boolean loading);

    /**
     * 加载框
     * @param argJsonStr
     */
    public abstract void loading(final String argJsonStr);

    /**
     * 保存H5的图片到相册(自定义路径)
     * @param imgPath 不能为空
     */
    public abstract void savePicToDevice(String imgPath);

    /**
     * 打开图库或者拍照
     * @param type 选择的图片所属媒体
     * @param count 表示要选几张图片 不能为0
     */
    public abstract void photo(String type, int count);

    /**
     * 分享到微信朋友圈或者微信好友
     * @param title
     * @param url
     * @param img
     * @param desc
     */
    public abstract void share(String title, String url, String img, String desc);

    public abstract void share(String argJsonStr);

    /**
     * 打电话
     * @param phoneNumber
     */
    public abstract void callPhone(final String phoneNumber);

    /**
     * 获取用户信息
     */
    public abstract String getUserInfo();

    /**
     * 获取位置信息
     * @return
     */
    public abstract String getLocationInfo();

    /**
     * 隐藏标题栏
     * @param hide true 隐藏 false 显示
     */
    public abstract void hideTitleBar(boolean hide);

    public abstract void hideTitleBar(String argJson);

    /**
     * 设置是否是表单
     * @param formData
     */
    public abstract void setFormData(boolean formData);

    public abstract void setFormData(String argJson);

    public abstract void setShare(String argJsonStr);

    /**
     * 跳转
     * @param argJsonStr
     */
    @JavascriptInterface
    public abstract void jumpTo(String argJsonStr);

    public abstract void jumpTo(String pagePath, Map<String, Object> pageArgs);

    /**
     * 关闭界面
     */
    @JavascriptInterface
    public void close(){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loading(false);
                view.closeView();
            }
        });
    }

    /**
     * 回收
     */
    public void recycle(){
        detachView();
    }
}
