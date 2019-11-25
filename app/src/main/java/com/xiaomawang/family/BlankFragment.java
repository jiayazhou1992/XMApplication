package com.xiaomawang.family;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.base.XMFragment;
import com.xiaomawang.commonlib.ui.web.WebContract;
import com.xiaomawang.commonlib.utils.dev.app.toast.ToastUtils;
import com.xiaomawang.commonlib.widget.router.Router;

import butterknife.BindView;

@Route(path = "/test/fragment")
public class BlankFragment extends XMFragment {

    private BlankViewModel mViewModel;

    @BindView(R.id.button) Button button;

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

        button.setOnClickListener(v -> {
//            ARouter.getInstance().build("/main/main2").navigation(mActivity, 100);
//            Intent intent = new Intent(mActivity, Main2Activity.class);
//            Router.with(mActivity).setRequestCode(100).pageGo("/test/fragment2", new Router.CallBack() {
//                @Override
//                public void onPageBack(int requestCode, int resultCode, Intent data) {
//                    String s = data.getExtras().getString("back");
//                    ToastUtils.showShort(mActivity, s);
//                }
//            });
            Router.with(mActivity).setData(WebContract.getBundle("https://www.baidu.com", "百度",false, false, false)).pageGo("/main/web");
        });
        Log.i("Test", "name is " + name);
    }

    @Override
    protected void initViewModel() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String s = data.getExtras().getString("back");
        ToastUtils.showShort(mActivity, s);
    }
}
