package com.xiaomawang.family;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.xiaomawang.commonlib.base.XMFragment;

@Route(path = "/test/fragment")
public class BlankFragment extends XMFragment {

    private BlankViewModel mViewModel;

    @Autowired(name = "test")
    String name;

    @Override
    protected void preSetup() {

    }

    @Override
    protected int getContentId() {
        return R.layout.blank_fragment;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        mViewModel = ViewModelProviders.of(this).get(BlankViewModel.class);
    }

    @Override
    protected void setView() {

    }

    @Override
    protected void setData() {
        Log.i("Test", "name is " + name);
    }

}
