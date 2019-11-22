package com.xiaomawang.commonlib.ui.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.LayoutRes;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.base.XMApplication;
import com.xiaomawang.commonlib.ui.web.bean.MessageFromJS;
import com.xiaomawang.commonlib.ui.web.bean.MessageToJS;
import com.xiaomawang.commonlib.ui.widget.SwipeBackLayout;

import butterknife.ButterKnife;

public abstract class BaseWebJsPage extends BaseWebFragment implements WebContract.WebJsView{
    private static final String TAG = "BaseWebJsPage";

    protected ProgressBar progressBar_status;

    protected Gson gson;

    @Override
    protected int getContentId() {
        return R.layout.fragment_web_page;
    }

    @LayoutRes
    protected int getBottomLayout(){
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (!isSwipeBack()){
            view = inflater.inflate(getContentId(), container, false);
        }else {
            View contentView = inflater.inflate(getContentId(), container, false);
            if (getBottomLayout() != 0) {
                inflater.inflate(getBottomLayout(), (ViewGroup) contentView, true);
            }
            view = new SwipeBackLayout(getContext()).attachFragment(this, contentView);
        }
        view.setClickable(true);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        webOptions = ((XMApplication)mActivity.getApplication()).getWebOptions();
    }

    @Override
    protected void initView() {
        super.initView();
        progressBar_status = findView(R.id.progressBar_status);
    }

    @Override
    protected void setData() {
        gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        mUrl = initPageUrl();

        super.setData();
    }

    protected String initPageUrl(){
        String url = "";
        // 设置url
        Route pagePath = getClass().getAnnotation(Route.class);
        if (pagePath != null) {
            url = "file:///" + pagePath.path();
        }
        return url;
    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    public void onLoadProgress(int progress) {

    }

    @Override
    public void onLoadPageFinished() {
        // 加载js
        loadJs();
    }

    @Override
    public void handleMessage(MessageFromJS jsMessage) {

    }

    @Override
    public void sendDataToJs(MessageToJS jsMessage) {
        String js = String.format("javascript:ANJSBridge._handleMessageFromNative(%s)", gson.toJson(jsMessage));
        mWebView.evaluateJavascript(js, null);
    }

    /**
     * 加载 js
     */
    private void loadJs(){
        String js = ";(function() {\n" +
                "        if (window.ANJSBridge) { return }\n" +
                "        var responseCallbacks = {}\n" +
                "        var uniqueId = 1\n" +
                "        function invoke(handlerName, data, responseCallback) {\n" +
                "            var message = { handlerName:handlerName, data:data }\n" +
                "            if (responseCallback) {\n" +
                "                var callbackId = 'cb_'+(uniqueId++)+'_'+new Date().getTime()\n" +
                "                responseCallbacks[callbackId] = responseCallback\n" +
                "                message['callbackId'] = callbackId\n" +
                "            }\n" +
                "            native.postMessage(JSON.stringify(message))\n" +
                "        }\n" +
                "        function _handleMessageFromNative(messageJSON) {\n" +
                "            setTimeout(function _timeoutDispatchMessageFromNative() {\n" +
                "                var message = messageJSON\n" +
                "                if (message.responseId) {\n" +
                "                    var responseCallback = responseCallbacks[message.responseId]\n" +
                "                    if (!responseCallback) { return; }\n" +
                "                    responseCallback(message.responseData)\n" +
                "                    delete responseCallbacks[message.responseId]\n" +
                "                } else if (message.handlerName) {\n" +
                "                    var handler = eval('window.' + message.handlerName)\n" +
                "                    try {\n" +
                "                        handler(message.data)\n" +
                "                    } catch(exception) {\n" +
                "                        if (typeof console != 'undefined') {\n" +
                "                            console.log(\"ANJSBridge: WARNING: javascript handler threw.\", message, exception)\n" +
                "                        }\n" +
                "                    }\n" +
                "                } else if (typeof console != 'undefined') {\n" +
                "                    console.log(\"ANJSBridge: WARNING: javascript handler unkown callbacks\")\n" +
                "                }\n" +
                "            })\n" +
                "        }\n" +
                "        window.ANJSBridge = {\n" +
                "            invoke: invoke,\n" +
                "            _handleMessageFromNative: _handleMessageFromNative\n" +
                "        }\n" +
                "        var doc = document\n" +
                "        var readyEvent = doc.createEvent('Events')\n" +
                "        readyEvent.initEvent('ANJSBridgeReady')\n" +
                "        readyEvent.bridge = ANJSBridge\n" +
                "        doc.dispatchEvent(readyEvent)\n" +
                "    })();";

        mWebView.evaluateJavascript(js, null);
    }
}
