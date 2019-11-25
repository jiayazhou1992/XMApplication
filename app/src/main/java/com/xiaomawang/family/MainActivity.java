package com.xiaomawang.family;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.base.XMActivity;
import com.xiaomawang.commonlib.widget.router.Router;

public class MainActivity extends XMActivity {

    @Override
    protected void preSetup() {

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initView() {
        TextView text = findViewById(R.id.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("test","yazhou");
                Router.with(MainActivity.this).setData(bundle).pageGo("/test/fragment");
            }
        });
    }

    @Override
    protected void initViewModel() {

    }
}
