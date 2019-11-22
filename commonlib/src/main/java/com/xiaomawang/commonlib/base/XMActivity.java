package com.xiaomawang.commonlib.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaomawang.commonlib.utils.dev.app.KeyBoardUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class XMActivity extends AppCompatActivity {
     //记录处于前台的Activity
    protected static XMActivity mForegroundActivity = null;

    // 记录所有活动的Activity
    protected static final List<XMActivity> mActivities = new LinkedList<>();

    private boolean autoCloseKeyBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivities.add(this);

        // 在这里初始 presenter
        preSetup();
        setContentView(getContentId());
        initSavedInstanceState(savedInstanceState);
        initData();
        initToolBar();
        initView();
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mForegroundActivity=this;
    }

    @Override
    protected void onResume() {
        mForegroundActivity = this;
        super.onResume();

    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mActivities.remove(this);

        KeyBoardUtils.fixSoftInputLeaks(this);

        super.onDestroy();
    }

    /**
     *
     */
    protected abstract void preSetup();

    /**
     *
     */
    protected abstract int getContentId();

    /**
     *
     */
    protected void initSavedInstanceState(Bundle savedInstanceState){};

    /**
     *
     */
    protected abstract void initData();

    /**
     *
     */
    protected abstract void initToolBar();

    /**
     *
     */
    protected abstract void initView();

    /**
     *
     */
    protected abstract void initViewModel();



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<XMActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<>(mActivities);
        }
        for (XMActivity activity : copy) {
            activity.finish();
        }
    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(XMActivity except) {
        List<XMActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<>(mActivities);
        }
        for (XMActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
    }

    /**
     * 是否有启动的Activity
     */
    public static boolean hasActivity() {
        return mActivities.size() > 0;
    }

    /**
     * 获取当前处于前台的activity
     */
    public static XMActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static XMActivity getCurrentActivity() {
        List<XMActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<>(mActivities);
        }
        if (copy.size() > 0) {
            return copy.get(copy.size() - 1);
        }
        return null;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        finishAll();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_HOME:
                // 收不到

                break;
            default:

                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置是否自动关闭软键盘
     * @param autoCloseKeyBoard
     */
    public void setAutoCloseKeyBoard(boolean autoCloseKeyBoard) {
        this.autoCloseKeyBoard = autoCloseKeyBoard;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (autoCloseKeyBoard) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
                View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
                if (KeyBoardUtils.isShouldHideKeyboard(v, ev) && KeyBoardUtils.isSoftInputVisible(this)) { //判断用户点击的是否是输入框以外的区域
                    KeyBoardUtils.closeKeyboard(this);
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
