package com.xiaomawang.commonlib.widget.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.base.XMFragment;
import com.xiaomawang.commonlib.utils.dev.app.KeyBoardUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaomawang.commonlib.base.XMFragment.FRAGMENT_FROM;
import static com.xiaomawang.commonlib.base.XMFragment.FRAGMENT_OK;

public class RouterFragment extends Fragment {
    private static final String TAG = "RouterFragment";

    public static final int MSG_JUMP_OVER = 1;

    private AppCompatActivity mActivity;
    private FragmentTagStack fragmentTagStack = new FragmentTagStack();
    private Map<XMFragment, FragmentOptions> optionsMap = new HashMap<>();
    private Router.CallBack activityForResultCallBack;

    // 正在跳转
    private boolean isJumping;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_JUMP_OVER) {
                isJumping = false;
            }
        }
    };
    private Runnable delayedRunnable;

    public static RouterFragment newInstance() {
        RouterFragment fragment = new RouterFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public boolean isEmptyStack(){
        return fragmentTagStack.isEmpty();
    }

    @UiThread
    public void startFragmentForResult(String path, boolean replace, boolean anim, int requestCode, Bundle data, Router.CallBack callback){
        isJumping = true;
        mHandler.sendEmptyMessageDelayed(MSG_JUMP_OVER, 300);

        //TODO 关闭键盘
        KeyBoardUtils.closeKeyboard(mActivity);

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();

        //隐藏or删除当前fragment
        String currentFragmentTag = fragmentTagStack.getTop();
        XMFragment currentFragment = (XMFragment) fragmentManager.findFragmentByTag(currentFragmentTag);
        if (replace && !StringUtils.isEmpty(currentFragmentTag)){
            //删除上一个fragment
            fragmentManager.popBackStackImmediate();
            fragmentTagStack.popStack();
            optionsMap.remove(currentFragment);
        }else {
            //隐藏上一个fragment
            if (currentFragment != null) {
                currentFragment.setUserVisibleHint(false);
            }
        }

        //添加目标fragment
        XMFragment fragment = (XMFragment) ARouter.getInstance().build(path).with(data).navigation();
        if (fragment==null){
            throw new IllegalArgumentException("找不到 path = " + path + " 的fragment");
        }
        optionsMap.put(fragment, new FragmentOptions(currentFragmentTag, requestCode, callback, anim));

        if (!fragment.isAdded()) {
            String fragmentHashCode = String.valueOf(fragment.hashCode());
            fragmentTagStack.addToStack(fragmentHashCode);
            if (anim) {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_open_enter_anim, R.anim.fragment_close_exit_anim, R.anim.fragment_close_enter_anim, R.anim.fragment_open_exit_anim)
                        .add(R.id.fragment_content, fragment, fragmentHashCode)
                        .addToBackStack(fragmentHashCode)
                        .commitAllowingStateLoss();
            }else {
                fragmentManager.beginTransaction().add(R.id.fragment_content, fragment, fragmentHashCode)
                        .addToBackStack(fragmentHashCode)
                        .commitAllowingStateLoss();
            }
        }else {
            Log.e("fragmentGo","这里不是重新打开的路子,但是可以重新设置bunle，不过暂时没写");
        }
    }

    @UiThread
    public void fragmentBack(Bundle result) {
        final boolean noData = (result == null);
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();

        if (fragmentTagStack.isMore()){
            // 将要被弹出的fragment 的参数
            Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTagStack.getTop());
            FragmentOptions fragmentOptions = optionsMap.get(currentFragment);
            optionsMap.remove(currentFragment);
            fragmentManager.popBackStack();
            fragmentTagStack.popStack();

            // 找到上一个fragment
            XMFragment preFragment = (XMFragment) fragmentManager.findFragmentByTag(fragmentOptions.formTag);
            if (preFragment != null){
                preFragment.setUserVisibleHint(true);
            }

            mHandler.removeCallbacks(delayedRunnable);
            Intent data = new Intent();
            if (result != null) {
                data.putExtras(result);
            }
            onPageBack(fragmentOptions.requestCode, noData ? 0 : FRAGMENT_OK, data, fragmentOptions.anim, fragmentOptions.callBack);

        }else {
            // 清除延时
            mHandler.removeCallbacks(delayedRunnable);
            delayedRunnable = null;

            if (result != null) {
                Intent intent = new Intent();
                intent.putExtras(result);
                mActivity.setResult(Activity.RESULT_OK, intent);
            }
            //TODO 关闭键盘
            KeyBoardUtils.closeKeyboard(mActivity);
            mActivity.finish();
        }
    }

    @UiThread
    public void startActivityForResult(Intent intent, int requestCode, Router.CallBack callback) {
        Log.i(TAG, "startActivityForResult");
        activityForResultCallBack = callback;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onPageBack(requestCode, resultCode, data, false, activityForResultCallBack);
        activityForResultCallBack = null;
    }

    public void onTopFragmentResult(int requestCode, int resultCode, Intent data) {
        Fragment currentFragment = mActivity.getSupportFragmentManager().findFragmentByTag(fragmentTagStack.getTop());
        currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     * @param delay 延时回调
     */
    public void onPageBack(final int requestCode, final int resultCode, final Intent data, boolean delay,final Router.CallBack callBack){
        if (delay) { // fragment 动画有点卡顿，尝试延迟回调
            delayedRunnable = new Runnable() {
                @Override
                public void run() {
                    //TODO 关闭键盘
                    KeyBoardUtils.closeKeyboard(mActivity);

                    if (callBack != null) {
                        callBack.onPageBack(requestCode, resultCode, data);
                    } else {
                        Log.i(TAG, " requestCode = " + requestCode + " 没有 callback");
                    }
                }
            };
            mHandler.postDelayed(delayedRunnable, 200);
        }else {
            if (callBack != null) {
                callBack.onPageBack(requestCode, resultCode, data);
            } else {
                Log.i(TAG, " requestCode = " + requestCode + " 没有 callback");
            }
        }
    }

    /**
     * 返回键
     */
    @UiThread
    public boolean onBackPressed(){
        if (isJumping) return true;

        if (!fragmentTagStack.isEmpty()){
            XMFragment fragment = (XMFragment) mActivity.getSupportFragmentManager().findFragmentByTag(fragmentTagStack.getTop());
            return fragment.onBackPressed();
        }
        return false;
    }

    /**
     * fragment参数
     */
    private static class FragmentOptions{
        String formTag;
        int requestCode;
        Router.CallBack callBack;
        boolean anim;

        private FragmentOptions(String formTag, int requestCode, Router.CallBack callBack, boolean anim) {
            this.requestCode = requestCode;
            this.formTag = formTag;
            this.anim = anim;
            this.callBack = callBack;
        }
    }

    /**
     *
     */
    private static class FragmentTagStack{
        List<String> tags;

        public FragmentTagStack() {
            this.tags = new ArrayList<>();
        }

        public String getTop() {
            if (isEmpty()) {
                return "";
            } else {
                return this.tags.get(this.tags.size() - 1);
            }
        }

        public void addToStack(String tag) {
            this.tags.add(tag);
        }

        public void popStack() {
            if (!isEmpty()) {
                this.tags.remove(this.tags.size() - 1);
            }
        }

        public void popStack(String tag) {
            if (!isEmpty()) {
                this.tags.remove(tag);
            }
        }

        public boolean isEmpty() {
            return tags == null || tags.isEmpty();
        }

        public boolean isMore() {
            return tags != null && tags.size() > 1;
        }

        public void clear() {
            this.tags.clear();
        }
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacks(delayedRunnable);
            mHandler.removeMessages(MSG_JUMP_OVER);
            delayedRunnable = null;
        }
        mHandler = null;
        mActivity = null;
        activityForResultCallBack = null;
        fragmentTagStack.clear();
        super.onDestroyView();
    }
}
