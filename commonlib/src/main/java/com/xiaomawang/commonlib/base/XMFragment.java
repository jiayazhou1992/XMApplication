package com.xiaomawang.commonlib.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.ui.widget.PageStatusManager;
import com.xiaomawang.commonlib.ui.widget.SwipeBackLayout;
import com.xiaomawang.commonlib.ui.widget.TitleManager;
import com.xiaomawang.commonlib.widget.router.Router;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class XMFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    //openForResult
    public static final int FRAGMENT_OK = -1;
    public static final String FRAGMENT_REQUEST = "requestCode";
    public static final String FRAGMENT_RESULT = "resultCode";
    public static final String FRAGMENT_FROM = "from_tag";


    private Runnable delayedRunnable;//延时线程

    protected XMActivity mActivity;
    protected Unbinder unbinder;
    protected View rootView;

    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;

    protected TitleManager mTitleManager;
    protected PageStatusManager pageStatusManager;


    private String fragment_tag;

    public String getFragmentTag() {
        return fragment_tag;
    }

    public void setFragmentTag(String fragment_tag) {
        this.fragment_tag = fragment_tag;
    }

    /**
     * 可不可以侧滑返回
     * @return
     */
    protected boolean isSwipeBack(){
        return false;
    }

    //初始/重置
    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {   //setUserVisibleHint()有可能在fragment的生命周期外被调用
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
            isFragmentVisible = true;
            return;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    /**
     * 此方法可以得到上下文对象
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (XMActivity) getActivity();
        preSetup();
        initData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        initVariable();
    }

    /**
     * 返回一个需要展示的View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (!isSwipeBack()){
            view = inflater.inflate(getContentId(), container, false);
        }else {
            view = new SwipeBackLayout(getContext()).attachFragment(this, inflater.inflate(getContentId(), container, false));
        }
        view.setClickable(true);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //如果setUserVisibleHint()在rootView创建前调用时，那么就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }else {
                    onFragmentVisibleChange(true);
                }
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? rootView : view, savedInstanceState);
    }

    /**
     * 当Activity初始化之后可以在这里进行一些数据的初始化操作
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initToolBar();
        initView();
        initViewModel();

        delayedRunnable = new Runnable() {
            @Override
            public void run() {
                setViewDataAfterAnima(); //放在动画之后，防止切换卡顿
            }
        };
        rootView.postDelayed(delayedRunnable,150);
    }

    /**
     * 预设
     */
    protected abstract void preSetup();

    /**
     * 获取内容布局
     */
    protected abstract int getContentId();

    /**
     * 初始化必要数据
     */
    protected abstract void initData();

    /**
     * 初始bar
     */
    protected void initToolBar(){
        mTitleManager = new TitleManager(mActivity,rootView);
        pageStatusManager = new PageStatusManager(rootView);
    }

    /**
     *
     */
    protected abstract void initView();

    protected abstract void initViewModel();

    //放在动画之后，防止切换卡顿
    protected void setViewDataAfterAnima(){

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    protected void onFragmentFirstVisible() {
        Log.i(TAG,"--->" + "第一次可见");
    }

    protected void onFragmentVisibleChange(final boolean isVisible) {
        Log.i(TAG,this.getClass().getSimpleName() + " --->" + (isVisible?"可见":"不可见"));
        // 延时
        delayedRunnable = new Runnable() {
            @Override
            public void run() {
                onDelayedFragmentVisibleChange(isVisible);
            }
        };
        rootView.postDelayed(delayedRunnable, 150);
    }

    protected void onDelayedFragmentVisibleChange(boolean isVisible){}

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    public void fragmentBack(){
        if (isAdded()) {
            Router.with(mActivity).pageBack();
        }
    }

    public void fragmentBack(Bundle bundle){
        if (isAdded()) {
            Router.with(mActivity).pageBack(bundle);
        }
    }

    public boolean onBackPressed(){
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        rootView.removeCallbacks(delayedRunnable);
        delayedRunnable = null;
        if (mTitleManager != null) {
            mTitleManager.recycle();
        }
        if (pageStatusManager != null) {
            pageStatusManager.recycle();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        initVariable();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public <T extends View> T findView(@IdRes int viewId){
        return rootView.findViewById(viewId);
    }

    /**
     * 获取宿主Activity
     */
    public XMActivity getHoldingActivity() {
        return mActivity;
    }
}
