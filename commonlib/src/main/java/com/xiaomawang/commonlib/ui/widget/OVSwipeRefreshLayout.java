package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OVSwipeRefreshLayout extends SwipeRefreshLayout {

    private int TouchSlop;

    public OVSwipeRefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public OVSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        TouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private boolean hScroll;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean handler = super.onInterceptTouchEvent(ev);
        if (hScroll){
            handler = false;
        }

        int action = ev.getAction();
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int dealtX = 0;
        int dealtY = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                hScroll = false;
                dealtX = 0;
                dealtY = 0;
                lastX = ev.getRawX();
                lastY = ev.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:

                if (!hScroll) {
                    dealtX += Math.abs(x - lastX);
                    dealtY += Math.abs(y - lastY);

                    // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                    if ((dealtX > TouchSlop || dealtY > TouchSlop) && dealtX >= dealtY) {
                        hScroll = true;
                        handler = false;
                    }else {
                        handler = super.onInterceptTouchEvent(ev);
                    }
                }else {
                    handler = false;
                }
                lastX = x;
                lastY = y;
                break;

        }
        return handler;
    }
}
