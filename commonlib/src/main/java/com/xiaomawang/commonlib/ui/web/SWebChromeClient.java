package com.xiaomawang.commonlib.ui.web;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.xiaomawang.commonlib.utils.dev.app.DialogUtils;

/**
 * 选择本地图片
 */
public class SWebChromeClient extends WebChromeClient {
    private static final String TAG = "SWebChromeClient";

    private OpenFileChooserCallBack mOpenFileChooserCallBack;

    private Context mContext;


    public SWebChromeClient(Context mContext, OpenFileChooserCallBack mOpenFileChooserCallBack) {
        this.mContext = mContext;
        this.mOpenFileChooserCallBack = mOpenFileChooserCallBack;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        openFileChooser(valueCallback, "");

    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        if (mOpenFileChooserCallBack != null){
            Log.i(TAG, "openFileChooser");
            mOpenFileChooserCallBack.openFileChooserCallBack(valueCallback, acceptType);
        }else {
            Log.i(TAG, "no openFileChooser");
        }
    }

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        openFileChooser(valueCallback, acceptType);

    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

        if (mOpenFileChooserCallBack != null){
            Log.i(TAG, "openFileChooser");
            mOpenFileChooserCallBack.openFileChooser5CallBack(webView, filePathCallback, fileChooserParams);
        }else {
            Log.i(TAG, "no openFileChooser");
        }

        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

        DialogUtils.createAlertDialog(this.mContext, "", message, "确定", new DialogUtils.DialogListener() {
            @Override
            public void onRightButton(DialogInterface dialog) {

            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                super.onDismiss(dialog);

                result.confirm();
            }
        }).show();

        return true;

        //return super.onJsAlert(view,url,message,result);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin,true,false);

        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    /**
     * 回调的接口
     */
    public interface OpenFileChooserCallBack {

        void openFileChooserCallBack(ValueCallback<Uri> uploadMsg, String acceptType);

        void openFileChooser5CallBack(WebView webView, ValueCallback<Uri[]> valueCallback, android.webkit.WebChromeClient.FileChooserParams fileChooserParams);
    }

    public void setOpenFileChooserCallBack(OpenFileChooserCallBack mOpenFileChooserCallBack) {
        this.mOpenFileChooserCallBack = mOpenFileChooserCallBack;
    }
}
