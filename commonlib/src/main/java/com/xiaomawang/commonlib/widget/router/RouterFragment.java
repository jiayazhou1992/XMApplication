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
    private List<String> tags = new ArrayList<>();

    private Map<Integer, Router.CallBack> mCallbacks = new HashMap<>();
    private Map<String, FragmentGoOptions> optionsMap = new HashMap<>();

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
        return new RouterFragment();
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

    public boolean haveTags(){
        return tags.size() > 0;
    }

    @UiThread
    public void startFragmentForResult(String path, boolean replace, boolean anim, int requestCode, Bundle data, Router.CallBack callback){
        isJumping = true;
        mHandler.sendEmptyMessageDelayed(MSG_JUMP_OVER, 300);

        //TODO 关闭键盘
        KeyBoardUtils.closeKeyboard(mActivity);

        if (callback != null) {
            if (mCallbacks.containsKey(requestCode)){
                throw new IllegalArgumentException("requestCode had callback");
            }
            mCallbacks.put(requestCode, callback);
        }

        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();

        //隐藏or删除当前fragment
        String currentFragmentTag = tags.size() > 0 ? tags.get(tags.size()-1) : "";
        if (replace && !StringUtils.isEmpty(currentFragmentTag)){
            //删除上一个fragment
            fragmentManager.popBackStackImmediate();
            optionsMap.remove(currentFragmentTag);
            tags.remove(currentFragmentTag);
        }else {
            //隐藏上一个fragment
            XMFragment currentFragment = (XMFragment) fragmentManager.findFragmentByTag(currentFragmentTag);
            if (currentFragment != null) {
                currentFragment.setUserVisibleHint(false);
            }
        }

        //添加目标fragment
        XMFragment fragment = (XMFragment) ARouter.getInstance().build(path).with(data).navigation();
        //如果已存在，path加后缀用来区分
        if ((fragmentManager.findFragmentByTag(path) != null)){
            path = String.format(path + "_r%4.0f", (Math.random()*10000));
            if (fragment != null) {
                fragment.setFragmentTag(path);
                Log.i(TAG, "go --> " + "pagePath = " + path + " class = " + fragment.getClass().getName());
            }else {
                Log.e(TAG, "go --> " + "pagePath = " + path + " class = null");
            }
        }

        if (fragment==null){
            throw new IllegalArgumentException("找不到 path = " + path + " 的fragment");
        }

        optionsMap.put(path, new FragmentGoOptions(requestCode, currentFragmentTag, anim));

        if (!fragment.isAdded()) {
            tags.add(path);

            if (anim) {
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fragment_open_enter_anim, R.anim.fragment_close_exit_anim, R.anim.fragment_close_enter_anim, R.anim.fragment_open_exit_anim)
                        .add(R.id.fragment_content, fragment, path)
                        .addToBackStack(path)
                        .commitAllowingStateLoss();
            }else {
                fragmentManager.beginTransaction().add(R.id.fragment_content, fragment, path)
                        .addToBackStack(path)
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

        if (tags.size()>1){
            // 将要被弹出的fragment 的参数
            FragmentGoOptions fragmentGoOptions = optionsMap.get(tags.get(tags.size()-1));
            String from_tag = "";
            int requestCode = -1;
            if (fragmentGoOptions != null) {
                from_tag = fragmentGoOptions.formTag;
                requestCode = fragmentGoOptions.requestCode;
                optionsMap.remove(tags.get(tags.size()-1));
            }

            fragmentManager.popBackStack();
            tags.remove(tags.size()-1);

            // 找到上一个fragment
            XMFragment currentFragment = (XMFragment) fragmentManager.findFragmentByTag(from_tag);
            if (currentFragment == null) {
                // 如果使用 from tag 没找到的话，就这样
                String currentFragmentTag = tags.size() > 0 ? tags.get(tags.size() - 1) : "";
                currentFragment = (XMFragment) fragmentManager.findFragmentByTag(currentFragmentTag);
            }
            if (currentFragment != null){
                currentFragment.setUserVisibleHint(true);
            }

            mHandler.removeCallbacks(delayedRunnable);
            Intent data = new Intent();
            if (result != null) {
                data.putExtras(result);
            }
            onPageBack(requestCode, noData ? 0 : FRAGMENT_OK, data, fragmentGoOptions.anim);

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
        if (callback != null) {
            mCallbacks.put(requestCode, callback);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        onPageBack(requestCode, resultCode, data, false);

        if (!(mActivity instanceof RouteActivity)) {
            // 让activity里的fragment回调onActivityResult
            // RouteActivity 里已经处理过了
            onPageResult(requestCode, resultCode, data);
        }
    }

    /**
     * 回调fragment的onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onPageResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onPageResult");

        if (data != null) {
            // TODO 传给首层fragment

            Bundle bundle = data.getExtras();
            String currentFragmentTag = bundle == null ? null : bundle.getString(FRAGMENT_FROM, null);
            XMFragment currentFragment = (XMFragment) mActivity.getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
            if (currentFragment == null) {
                Log.e(TAG, "没有找到currentFragment1 onFragmentResult");
                String currentFragmentId = tags.size() > 0 ? tags.get(tags.size() - 1) : "";
                currentFragment = (XMFragment) mActivity.getSupportFragmentManager().findFragmentByTag(currentFragmentId);

                if (currentFragment != null){
                    Log.e(TAG, "找到 onActivityResult");
                    currentFragment.onActivityResult(requestCode, resultCode, data);
                }else {
                    Log.e(TAG, "没有找到currentFragment2 onFragmentResult");
                }
            }else {
                Log.e(TAG, "找到 onFragmentResult");
//                currentFragment.onFragmentResult(data.getExtras(), requestCode, resultCode);
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }
        }else {
            Log.e(TAG, "onActivityResult no data 可能是图片选择 或其他吧");
            String currentFragmentId = tags.size() > 0 ? tags.get(tags.size() - 1) : "";
            XMFragment currentFragment = (XMFragment) mActivity.getSupportFragmentManager().findFragmentByTag(currentFragmentId);

            if (currentFragment != null){
                Log.e(TAG, "找到 onActivityResult");
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }else {
                Log.e(TAG, "没有找到currentFragment2 onFragmentResult");
            }
        }
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     * @param delay 延时回调
     */
    public void onPageBack(final int requestCode, final int resultCode, final Intent data, boolean delay){
        final Router.CallBack callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);

        if (delay) { // fragment 动画有点卡顿，尝试延迟回调
            delayedRunnable = new Runnable() {
                @Override
                public void run() {
                    //TODO 关闭键盘
                    KeyBoardUtils.closeKeyboard(mActivity);

                    if (callback != null) {
                        callback.onPageBack(requestCode, resultCode, data);
                    } else {
                        Log.i(TAG, " requestCode = " + requestCode + " 没有 callback");
                    }
                }
            };
            mHandler.postDelayed(delayedRunnable, 200);
        }else {
            if (callback != null) {
                callback.onPageBack(requestCode, resultCode, data);
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

        if (tags.size()>0){
            String tag = tags.get(tags.size()-1);
            Log.i(TAG, tag);
            XMFragment fragment = (XMFragment) mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            return fragment.onBackPressed();
        }
        return false;
    }

    /**
     * go 前参数
     */
    private static class FragmentGoOptions{
        int requestCode = -1;
        String formTag = "";
        boolean anim;

        private FragmentGoOptions(int requestCode, String formTag, boolean anim) {
            this.requestCode = requestCode;
            this.formTag = formTag;
            this.anim = anim;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public String getFormTag() {
            return formTag;
        }

        public void setFormTag(String formTag) {
            this.formTag = formTag;
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
        super.onDestroyView();
    }
}
