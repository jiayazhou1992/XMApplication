package com.xiaomawang.family;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaomawang.commonlib.base.XMFragment;

@Route(path = "/test/fragment2")
public class BlankFragment2 extends XMFragment {

    @Override
    protected void preSetup() {

    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_blank_fragment2;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        Button button = findView(R.id.button2);
        button.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("back", "asia");
            fragmentBack(bundle);
        });
    }

    @Override
    protected void initViewModel() {

    }
}
