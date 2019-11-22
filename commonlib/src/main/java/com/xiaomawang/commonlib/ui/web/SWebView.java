package com.xiaomawang.commonlib.ui.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.utils.dev.app.SDCardUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.lang.ref.WeakReference;

public class SWebView extends WebView {
    private static final String TAG = "SWebView";
    private static final int MSG_TIMEOUT = 408;
    private static final int OPEN_VIEW = 409;

    private long timeOut = 2 * 60 * 1000;
    private long delay = 3 * 1000l;

    //加载错误
    private boolean loadError;

    private SWebChromeClient sWebChromeClient;
    private SWebChromeClient.OpenFileChooserCallBack mOpenFileChooserCallBack;
    private OnLoadListenerCallBack mOnLoadListenerCallBack;

    //Handler
    private SHandler sHandler;

    private static class SHandler extends Handler {

        WeakReference<Context> mContextReference;
        WeakReference<OnLoadListenerCallBack> mLoadListenerCallBackWeakReference;

        SHandler(Context context) {
            super(Looper.getMainLooper());
            mContextReference = new WeakReference<>(context);
        }

        public void setLoadListenerCallBack(OnLoadListenerCallBack mLoadListenerCallBack) {
            this.mLoadListenerCallBackWeakReference = new WeakReference<>(mLoadListenerCallBack);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == SWebView.MSG_TIMEOUT){
                if (mLoadListenerCallBackWeakReference.get() != null) {
                    Log.i(TAG, "onLoadTimeOut");
                    mLoadListenerCallBackWeakReference.get().onLoadTimeOut();
                }
            }else if (msg.what == SWebView.OPEN_VIEW){
                Log.i(TAG, "open_view");
                if (msg.obj instanceof String) {
                    String scheme = (String) msg.obj;
                    if (!StringUtils.isEmpty(scheme)){
                        if (mContextReference.get() != null) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
                                mContextReference.get().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                //ToastUtils.showShort(mContextReference.get(), "没有找到应用程序");
                            }
                        }
                    }
                }
            }
        }
    }



    public SWebView(Context context) {
        super(context);

        init();
    }

    public SWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    public SWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);

        init();
    }

    /**
     * 初始化
     */
    public void init(){
        initSetting();
        initWebChromeClient();
        initWebViewClient();

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                getContext().startActivity(i);
            }
        });

        setHorizontalScrollBarEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //setWebContentsDebuggingEnabled(true);
        }

        sHandler = new SHandler(getContext());
    }

    /**
     * 初始化settings
     */
    public void initSetting(){
        WebSettings settings = getSettings();

        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + ";shebaowa");

        //settings.setTextSize(WebSettings.TextSize.NORMAL);
        //settings.setDefaultFontSize(35);

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        settings.setJavaScriptEnabled(true);

        //支持插件
        //settings.setPluginsEnabled(true);

        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        settings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        settings.setCacheMode(WebSettings.LOAD_DEFAULT); //关闭webview中缓存
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(SDCardUtils.getDiskCacheDir());
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式

        /*settings.setLoadWithOverviewMode(true);
        settings.setSafeBrowsingEnabled(true);
        settings.setSupportMultipleWindows(true);*/
        settings.setSaveFormData(true);

        //启用数据库    
        settings.setDatabaseEnabled(true);
        //设置定位的数据库路径    
        String dir = this.getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);
        //启用地理定位  
        settings.setGeolocationEnabled(true);
        //开启DomStorage缓存  
        settings.setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT>15)
            settings.setAllowUniversalAccessFromFileURLs(true);
        // 如果不使用该句代码，在点击超链地址后，会跳出程序，而弹出浏览器访问网页。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//使用https访问 加载图片
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //settings.setBlockNetworkImage(false);
    }

    /**
     * 初始化WebViewClient
     */
    private void initWebViewClient(){
        setWebViewClient(new SWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                loadError = false;
                sHandler.removeMessages(MSG_TIMEOUT);
                sHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, timeOut);

                if (mOnLoadListenerCallBack != null){
                    Log.i(TAG, "onLoadStart");
                    mOnLoadListenerCallBack.onLoadStart(url);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mOnLoadListenerCallBack != null){
                    Log.i(TAG, "onPageFinished");
                    mOnLoadListenerCallBack.onLoadPageFinished();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "shouldOverrideUrlLoading1");

                if (mOnLoadListenerCallBack != null){
                    if (!mOnLoadListenerCallBack.shouldOverrideUrlLoading(view, url)){
                        return super.shouldOverrideUrlLoading(view, url);
                    }else {
                        return true;
                    }
                }else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.i(TAG, "shouldOverrideUrlLoading2");
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    return;
                }
                loadError = true;
            }

            // 在Android6以上的机器上，网页中的任意一个资源获取不到（比如字体），网页就很可能显示自定义的错误界面
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.isForMainFrame()) { // 解决方案
                        loadError = true;
                    }
                }else {
                    loadError = true;
                }
                super.onReceivedError(view, request, error);
            }
        });
    }

    /**
     * 初始化WebChromeClient
     */
    private void initWebChromeClient(){
        sWebChromeClient = new SWebChromeClient(getContext(), mOpenFileChooserCallBack){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (mOnLoadListenerCallBack != null){
                    Log.i(TAG, "onReceivedTitle");
                    mOnLoadListenerCallBack.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (mOnLoadListenerCallBack != null){
                    Log.i(TAG,"onLoadProgress");
                    mOnLoadListenerCallBack.onLoadProgress(newProgress);
                }

                if (newProgress >= 100){

                    sHandler.removeMessages(MSG_TIMEOUT);

                    if (loadError){
                        if (mOnLoadListenerCallBack != null){
                            Log.i(TAG, "onLoadError");
                            mOnLoadListenerCallBack.onLoadError();
                        }
                    }else {
                        if (mOnLoadListenerCallBack != null){
                            Log.i(TAG, "onLoadSuccess");
                            mOnLoadListenerCallBack.onLoadSuccess();
                        }
                    }
                }

            }
        };

        setWebChromeClient(sWebChromeClient);
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public void setOpenFileChooserCallBack(SWebChromeClient.OpenFileChooserCallBack mOpenFileChooserCallBack) {
        this.mOpenFileChooserCallBack = mOpenFileChooserCallBack;
        sWebChromeClient.setOpenFileChooserCallBack(this.mOpenFileChooserCallBack);
    }

    public void setOnLoadListenerCallBack(OnLoadListenerCallBack mOnLoadListenerCallBack) {
        this.mOnLoadListenerCallBack = mOnLoadListenerCallBack;
        sHandler.setLoadListenerCallBack(this.mOnLoadListenerCallBack);
    }

    public void openView(String scheme){
        if (sHandler.hasMessages(OPEN_VIEW)) {
            sHandler.removeMessages(OPEN_VIEW);
        }
        if (!StringUtils.isEmpty(scheme)) {
            Message message = sHandler.obtainMessage(OPEN_VIEW, scheme);
            sHandler.sendMessageDelayed(message, delay);
        }
    }

    public void evaluateJavascript(String script, ValueCallback<String> resultCallback){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.evaluateJavascript(script, resultCallback);
        } else {
            loadUrl(script);
        }
    }

    /**
     * 接口
     */
    public interface OnLoadListenerCallBack{

        /**
         * 开始加载
         */
        void onLoadStart(String url);

        /**
         *
         * @param title
         */
        void onReceivedTitle(String title);

        /**
         * 加载进度
         * @param progress
         */
        void onLoadProgress(int progress);

        /**
         *
         * @param view
         * @param url
         */
        boolean shouldOverrideUrlLoading(WebView view, String url);

        /**
         * 加载失败
         */
        void onLoadError();

        /**
         * 加载成功
         */
        void onLoadSuccess();


        void onLoadPageFinished();

        /**
         * 超时
         */
        void onLoadTimeOut();
    }
}
