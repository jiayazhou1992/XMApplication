package com.xiaomawang.family;

import android.app.Activity;

import com.xiaomawang.commonlib.base.XMApplication;
import com.xiaomawang.commonlib.widget.router.Router;

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
    }

    @Override
    public void showAd(Activity baseActivity) {

    }

    @Override
    public void showGestureLock(Activity baseActivity) {

    }
}
