package com.xiaomawang.commonlib.ui.web;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.base.BaseConstans;
import com.xiaomawang.commonlib.base.XMApplication;
import com.xiaomawang.commonlib.base.XMFragment;
import com.xiaomawang.commonlib.utils.dev.app.DialogUtils;
import com.xiaomawang.commonlib.utils.dev.app.NetWorkUtils;
import com.xiaomawang.commonlib.utils.dev.app.toast.ToastUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;
import com.xiaomawang.commonlib.utils.dev.common.validator.ValidatorUtils;
import com.xiaomawang.commonlib.widget.picselect.PicSelectHelper;

import java.io.File;
import java.util.HashMap;

public abstract class BaseWebFragment extends XMFragment implements WebContract.View{
    private static final String TAG = "BaseWebFragment";

    protected ImageView iv_close;
    protected ImageView iv_share;
    protected ProgressBar progressBar;
    protected SWebView mWebView;

    // web配置
    protected WebContract.WebOptions webOptions;
    // JS
    protected JSInterfaceImpl jsInterface;
    // 标题
    protected String mTitle;
    // 链接
    protected String mUrl;
    // referer
    protected String mReferer;
    // 是否是表单填写
    protected boolean formData;
    // 是否缩放
    protected boolean zoomEnable;
    // 是否添加header
    protected boolean addHeader;
    // 是否添加额外参数
    protected boolean addExtra;
    // headers
    protected HashMap<String, String> mHeaders = new HashMap<>();
    // 选择文件
    protected ValueCallback mFilePathCallback;
    protected ValueCallback mFilePathCallbacks;

    @Override
    protected void preSetup() {

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        mUrl = bundle == null ? "" : bundle.getString(BaseConstans.URL, "");
        mTitle = bundle == null ? "" : bundle.getString(BaseConstans.TITLE,"");
        zoomEnable = bundle == null ? false : bundle.getBoolean(BaseConstans.ARG1, false);
        addHeader = bundle == null ? true : bundle.getBoolean(BaseConstans.ARG2, true);
        addExtra = bundle == null ? false : bundle.getBoolean(BaseConstans.ARG3, false);

        webOptions = ((XMApplication)mActivity.getApplication()).getWebOptions();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        iv_close = findView(R.id.iv_close);
        iv_share = findView(R.id.iv_share);
        mTitleManager.getTv_title().setText(mTitle);
    }

    @Override
    protected void initView() {
        progressBar = findView(R.id.progressBar);
        mWebView = findView(R.id.webView);
    }

    @Override
    protected void setView() {
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeView();
            }
        });

        this.mWebView.setOnLoadListenerCallBack(this);
        this.mWebView.setOpenFileChooserCallBack(this);
        this.mWebView.setHorizontalScrollBarEnabled(false);

        try {
            jsInterface = webOptions.jsInterfaceClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        jsInterface.attachView(mWebView, this);

        this.mWebView.addJavascriptInterface(jsInterface, webOptions.mScriptName);

        pageStatusManager.setReloadClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageStatusManager.showStatus(false);
                mWebView.reload();
            }
        });
    }


    /**
     * 隐藏titlebar
     * @param hide
     */
    @Override
    public void hideTitleBar(boolean hide){
        mTitleManager.getRootLayout().setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setFormData(boolean formData) {
        this.formData = formData;
    }

    @Override
    public void setShare(boolean share, final String shareTitle, final String shareDesc, final String shareLink) {
        if (share) {
            iv_share.setVisibility(View.VISIBLE);
            iv_share.setImageResource(webOptions.icon_share);
            iv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webOptions.share(mActivity, shareTitle, shareDesc, shareLink);
                }
            });
        }else {
            iv_share.setVisibility(View.GONE);
            iv_share.setOnClickListener(null);
        }
    }

    @Override
    public void closeView() {
        fragmentBack();
    }

    @Override
    protected void setData() {
        if (!StringUtils.isEmpty(mUrl)) {
            if (ValidatorUtils.isAndroidUrl(mUrl)) {
                if (addExtra){
                    mUrl = mUrl + webOptions.getExtraParams(mUrl);
                }
                mReferer = mUrl;
                if (addHeader) {
                    mWebView.loadUrl(mUrl, webOptions.addExtraHeaders(mUrl, mHeaders));
                }else {
                    mWebView.loadUrl(mUrl);
                }
            }else {
                mWebView.loadDataWithBaseURL(null,mUrl,"text/html", "utf-8", null);
            }
        }
    }

    @Override
    public void onLoadStart(String url) {

    }

    @Override
    public void onReceivedTitle(String title) {
        if (StringUtils.isEmpty(mTitle)) {
            if (TextUtils.equals("about:blank", title)){
                mTitleManager.getTv_title().setText("");
            }else {
                mTitleManager.getTv_title().setText(title);
            }
        }
    }

    @Override
    public void onLoadProgress(int progress) {
        if (progress >= 100) {
            progressBar.setVisibility(View.GONE);

            if (!mWebView.canGoBack()){
                iv_close.setVisibility(View.GONE);
            }else {
                iv_close.setVisibility(View.VISIBLE);
            }
        } else {
            if (progressBar.getVisibility() == View.GONE)
                progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        if (StringUtils.isEmpty(url)) return false;
        mHeaders.put(WebContract.Referer, mReferer);
        mReferer = url;
        //支付宝支付，推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
//        final PayTask task = new PayTask(mActivity);
//        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
//            @Override
//            public void onPayResult(final H5PayResultModel result) {
//                // 支付结果返回
//                final String url = result.getReturnUrl();
//                if (!TextUtils.isEmpty(url)) {
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (addHeader) {
//                                view.loadUrl(url, webOptions.addExtraHeaders(url, mHeaders));
//                            }else {
//                                view.loadUrl(url, mHeaders);
//                            }
//                        }
//                    });
//                }
//            }
//        });
//        if (isIntercepted){
//            return true;
//        }

        if (ValidatorUtils.isAndroidUrl(url)) {
            if (addHeader) {
                view.loadUrl(url, webOptions.addExtraHeaders(url, mHeaders));
            }else {
                view.loadUrl(url, mHeaders);
            }
            return true;
        }else {
            if (url.startsWith("http")){// 一般情况不会出现
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                //view.loadDataWithBaseURL(null,"   已前往本地浏览器","text/html", "utf-8", null);
                return true;
            }else {
                String scheme = "tel:";
                if (url.startsWith(scheme)){
                    String phone = url.substring(scheme.length());
                    jsInterface.callPhone(phone);
                    return true;
                }else {
                    mWebView.openView(url);
                    return true;
                }
            }
        }
    }

    @Override
    public void onLoadError() {
        if (NetWorkUtils.isAvailable()){
            mTitleManager.getTv_title().setText("提示");
            pageStatusManager.showStatus(true,"           已经很努力了\n但加载不出来，请稍后再试", R.drawable.ic_pagestatus_loaderror);
        }else {
            mTitleManager.getTv_title().setText("提示");
            pageStatusManager.showStatus(true,"当前无网络，请稍后再试", R.drawable.ic_pagestatus_networkerror);
        }
    }

    @Override
    public void onLoadSuccess() {
        pageStatusManager.showStatus(false);
    }

    @Override
    public void onLoadPageFinished() {

    }

    @Override
    public void onLoadTimeOut() {

    }

    @Override
    public void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.mFilePathCallback = uploadMsg;
        if (TextUtils.equals(acceptType, "video/*")){
            openType(2);
        }else { //默认不裁剪
            PicSelectHelper.getInstance().showDialog(mActivity, webOptions.outPutFilePath, webOptions.authorities, false, 0, 0, 0, 0, this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void openFileChooser5CallBack(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        this.mFilePathCallbacks = valueCallback;
        boolean video = false;
        if (fileChooserParams.getAcceptTypes() !=null&&fileChooserParams.getAcceptTypes().length>0){
            for (String acceptType : fileChooserParams.getAcceptTypes()){
                if (TextUtils.equals(acceptType, "video/*")){
                    video = true;
                    break;
                }
            }
        }
        if (video){
            openType(2);
        }else { //默认不裁剪
            PicSelectHelper.getInstance().showDialog(mActivity, webOptions.outPutFilePath, webOptions.authorities, false, 0, 0, 0, 0, this);
        }
    }

    /**
     * 图片选择回调 -1 取消 0 拍照 1 相册 2 视频
     * @param type
     */
    @Override
    public void openType(int type) {
        if (type == -1){
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            if (mFilePathCallbacks != null) {
                mFilePathCallbacks.onReceiveValue(null);
            }
            mFilePathCallback = null;
            mFilePathCallbacks = null;
        }
    }

    /**
     * 选择照片结果
     * @param bitmap
     * @param file_path
     * @param crop
     */
    @Override
    public void result(int requestCode, Bitmap bitmap, String file_path, boolean crop) {
        if (mFilePathCallback != null) {
            if (StringUtils.isEmpty(file_path)){
                ToastUtils.showShort(mActivity, "没有拿到有效文件地址");
                mFilePathCallback.onReceiveValue(null);
            }else {
                Uri uri = Uri.fromFile(new File(file_path));
                mFilePathCallback.onReceiveValue(uri);
            }
        }
        if (mFilePathCallbacks != null) {
            if (StringUtils.isEmpty(file_path)) {
                ToastUtils.showShort(mActivity, "没有拿到有效文件地址");
                mFilePathCallbacks.onReceiveValue(null);
            }else {
                Uri uri = Uri.fromFile(new File(file_path));
                mFilePathCallbacks.onReceiveValue(new Uri[] { uri });
            }
        }
        mFilePathCallback = null;
        mFilePathCallbacks = null;
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            //mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//返回不刷新
            mWebView.openView(null);//清除打开消息
            mWebView.goBack();

            if (!mWebView.canGoBack()) {
                iv_close.setVisibility(View.GONE);
            }
            return true;
        } else {
            if (formData){
                DialogUtils.createAlertDialog(mActivity, "退出", "确认退出？", "确认", "取消", new DialogUtils.DialogListener() {
                    @Override
                    public void onRightButton(DialogInterface dialog) {
                        fragmentBack();
                    }
                }).show();
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mWebView != null){
            mWebView.clearHistory();
            mWebView.clearCache(true);
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.clearHistory();
            mWebView.clearCache(true);

            // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            //mWebView.loadUrl("about:blank");
            //mWebView.freeMemory();
            //mWebView.pauseTimers();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

        jsInterface.recycle();
        super.onDestroy();
    }
}
