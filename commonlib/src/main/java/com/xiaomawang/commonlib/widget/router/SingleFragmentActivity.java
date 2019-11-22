package com.xiaomawang.commonlib.widget.router;

import android.content.Intent;
import android.os.Bundle;

import com.xiaomawang.commonlib.utils.dev.common.StringUtils;


/**
 * Created by Administrator on 2018/2/27 0027.
 */

public class SingleFragmentActivity extends RouteActivity {


    /**
     * 跳转目标页面
     * */
    @Override
    protected void toFragment(){
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String page_path = bundle == null ? "" : bundle.getString(BaseRouteConfig.PAGE_PATH, "");
            if (!StringUtils.isEmpty(page_path)){
                Router.with(this).setData(bundle).setAnim(false).pageGo(page_path);
            }
        }
    }

}
