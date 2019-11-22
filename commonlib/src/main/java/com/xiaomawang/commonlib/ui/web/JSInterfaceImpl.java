package com.xiaomawang.commonlib.ui.web;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.xiaomawang.commonlib.ui.web.bean.ArgJson;
import com.xiaomawang.commonlib.ui.web.bean.MessageFromJS;

import java.util.Map;


/**
 * Created by Administrator on 2016/6/12.
 */
public class JSInterfaceImpl extends JSInterface {

    @JavascriptInterface
    public void postMessage(String argJsonStr) {
        Log.i(TAG, " message -> " + argJsonStr);
        if (view instanceof WebContract.WebJsView) {
            final MessageFromJS jsMessage = gson.fromJson(argJsonStr, MessageFromJS.class);
            if (jsMessage != null) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((WebContract.WebJsView) view).handleMessage(jsMessage);
                    }
                });
            }
        }
    }

    @JavascriptInterface
    public void loading(final boolean loading){
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loading(loading);
            }
        });
    }

    @JavascriptInterface
    public void loading(String argJsonStr) {
        ArgJson argJson = getArgJson(argJsonStr);
        if (argJson != null) {
            loading(argJson.isShow());
        }
    }


    @JavascriptInterface
    public void savePicToDevice(String imgPath) {
        if (imgPath == null) {
        }
    }


    @JavascriptInterface
    public void photo(String type, int count) {
        if (type == null || count == 0) {

        }
    }

    @JavascriptInterface
    public void share(String argJsonStr) {
        final ArgJson argJson = getArgJson(argJsonStr);
        if (argJson != null){
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    share(argJson.getShareTitle(), argJson.getShareLink(), argJson.getShareImg(), argJson.getShareDesc());
                }
            });
        }
    }

    @JavascriptInterface
    public void share(String title, String url, String img, String desc) {

    }


    @JavascriptInterface
    public void callPhone(final String phoneNumber) {

    }


    @JavascriptInterface
    public String getUserInfo(){
        return "";
    }


    @JavascriptInterface
    public String getLocationInfo() {
        return null;
    }


    @JavascriptInterface
    public void hideTitleBar(final boolean hide) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                view.hideTitleBar(hide);
            }
        });
    }

    @JavascriptInterface
    public void hideTitleBar(String argJsonStr) {
        ArgJson argJson = getArgJson(argJsonStr);
        if (argJson == null){
            hideTitleBar(true);
        }else {
            hideTitleBar(argJson.isHide());
        }
    }

    @JavascriptInterface
    public void setFormData(boolean formData) {
        view.setFormData(formData);
    }

    @JavascriptInterface
    public void setFormData(String argJsonStr) {
        ArgJson argJson = getArgJson(argJsonStr);
        if (argJson == null){
            setFormData(false);
        }else {
            setFormData(argJson.isFormData());
        }
    }

    @JavascriptInterface
    public void setShare(String argJsonStr) {
        final ArgJson argJson = getArgJson(argJsonStr);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (argJson == null){
                    view.setShare(false, null, null, null);
                }else {
                    view.setShare(argJson.isShare(), argJson.getShareTitle(), argJson.getShareDesc(), argJson.getShareLink());
                }
            }
        });
    }

    @JavascriptInterface
    public void jumpTo(String argJsonStr) {
        final ArgJson argJson = getArgJson(argJsonStr);
        if (argJson != null){
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    jumpTo(argJson.getPagePath(), argJson.getPageArgs());
                }
            });
        }
    }

    public void jumpTo(String pagePath, Map<String, Object> pageArgs) {

    }

    @JavascriptInterface
    public void close() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                view.loading(false);
                view.closeView();
            }
        });
    }
}
