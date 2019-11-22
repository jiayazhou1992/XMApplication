package com.xiaomawang.commonlib.ui.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;


public class SWebViewClient extends WebViewClient {

    private static final String TAG = "SWebViewClient";


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        DevLogger.iTag(TAG,"onPageStarted"+url);

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        DevLogger.iTag("shouldOverrideUrlLoading1",url);

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        DevLogger.iTag(TAG,"onPageFinished"+url);

    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        DevLogger.iTag(TAG,"onReceivedError");
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        DevLogger.iTag(TAG,"onReceivedSslError");
        handler.proceed();
    }
}
