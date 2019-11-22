package com.xiaomawang.commonlib.widget.router;

import android.os.Bundle;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.base.XMActivity;


/**
 * Created by Administrator on 2018/2/27 0027.
 */
public class RouteActivity extends XMActivity {

    @Override
    protected void preSetup() {

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_route;
    }

    @Override
    protected void initSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            toFragment();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setView() {

    }

    @Override
    protected void setData() {

    }

    /**
     * 跳转目标页面
     * */
    protected void toFragment(){

    }

    @Override
    public void onBackPressed() {
        if (!Router.getRouterFragment(this).onBackPressed()){
            Router.with(this).pageBack();
        }
    }
}
