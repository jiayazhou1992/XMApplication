package com.xiaomawang.family;

import android.app.Activity;

import com.xiaomawang.commonlib.base.XMActivity;
import com.xiaomawang.commonlib.base.XMApplication;
import com.xiaomawang.commonlib.ui.web.WebContract;
import com.xiaomawang.commonlib.widget.router.Router;

import java.util.Map;

public class App extends XMApplication {

    @Override
    public void onCreate() {
        super.onCreate();

//        初始化路由器
        Router.RouterOptions.Builder builder = new Router.RouterOptions.Builder();
        builder.setContainerClass(XRouterActivity.class)
//                .setWebContainerActivityClass(TWebActivity.class)
//                .setWebContainerPath(RouteConfig.WEB)
                .setScheme("pocket")
                .setHost("app")
                .setPathPrefix("/open");
        Router.init(builder.build());

        mWebOptions = new WebContract.WebOptions() {
            @Override
            public boolean isExplicitLink(String url) {
                return false;
            }

            @Override
            public Map<String, String> addExtraHeaders(String url, Map<String, String> headers) {
                return null;
            }

            @Override
            public String getExtraParams(String url) {
                return null;
            }

            @Override
            public void share(XMActivity activity, String shareTitle, String shareDesc, String shareLink) {

            }
        };
    }

    @Override
    public void showAd(Activity baseActivity) {

    }

    @Override
    public void showGestureLock(Activity baseActivity) {

    }
}
