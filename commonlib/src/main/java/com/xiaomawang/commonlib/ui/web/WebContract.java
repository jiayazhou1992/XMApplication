package com.xiaomawang.commonlib.ui.web;

import android.os.Bundle;

import androidx.annotation.DrawableRes;

import com.xiaomawang.commonlib.base.BaseConstans;
import com.xiaomawang.commonlib.base.XMActivity;
import com.xiaomawang.commonlib.ui.web.bean.MessageFromJS;
import com.xiaomawang.commonlib.ui.web.bean.MessageToJS;
import com.xiaomawang.commonlib.widget.picselect.PicSelectInterface;

import java.util.Map;

public class WebContract {

    public static final String Referer = "Referer";

    public static Bundle getBundle(String url, String title, boolean zoomEnable, boolean addHear, boolean addExtra){
        Bundle bundle = new Bundle();
        bundle.putString(BaseConstans.URL, url);
        bundle.putString(BaseConstans.TITLE,title);
        bundle.putBoolean(BaseConstans.ARG1, zoomEnable);
        bundle.putBoolean(BaseConstans.ARG2, addHear);
        bundle.putBoolean(BaseConstans.ARG3, addExtra);
        return bundle;
    }

    public interface View extends SWebView.OnLoadListenerCallBack, SWebChromeClient.OpenFileChooserCallBack, PicSelectInterface.CallBack{

        void loading(boolean loading);

        void hideTitleBar(boolean hide);

        void setFormData(boolean formData);

        void setShare(boolean share, String shareTitle, String shareDesc, String shareLink);

        void closeView();
    }

    public interface WebJsView extends View{
        void handleMessage(MessageFromJS jsMessage);
        void sendDataToJs(MessageToJS jsMessage);
    }


    /**
     * webPage 配置
     */
    public static abstract class WebOptions{

        // JS
        public String mScriptName = "native";
        // JS接口
        public Class<? extends JSInterfaceImpl> jsInterfaceClass;
        // 文件保存路径
        public String outPutFilePath;
        // file authorities
        public String authorities;
        // 分享icon
        @DrawableRes
        public int icon_share;
        // 错误icon
        @DrawableRes
        public int icon_error;
        public String error_desc;
        // 超时icon
        @DrawableRes
        public int icon_timeOut;
        public String timeOut_desc;

        /**
         * 是否是外链
         * @param url
         * @return
         */
        public abstract boolean isExplicitLink(String url);

        /**
         * 获取headers
         * @return
         */
        public abstract Map<String, String> addExtraHeaders(String url, Map<String, String> headers);

        /**
         * 获取url后而外参数
         * @return
         */
        public abstract String getExtraParams(String url);

        public abstract void share(XMActivity activity, String shareTitle, String shareDesc, String shareLink);

    }
}
