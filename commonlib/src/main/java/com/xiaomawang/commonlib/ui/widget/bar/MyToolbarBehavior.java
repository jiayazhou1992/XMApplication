package com.xiaomawang.commonlib.ui.widget.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.xiaomawang.commonlib.ui.widget.behavior.HeaderBehavior;


public class MyToolbarBehavior extends HeaderBehavior<MyToolbar> {
    private static final String TAG = "MyToolbarBehavior";

    public MyToolbarBehavior() {
    }

    public MyToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setHeaderTopBottomOffset(CoordinatorLayout parent, MyToolbar header, int newOffset, int minOffset, int maxOffset) {
        Log.i(TAG, "setHeaderTopBottomOffset");
        return super.setHeaderTopBottomOffset(parent, header, newOffset, minOffset, maxOffset);
    }

    @Override
    protected void headerTopBottomOffsetConsumed(MyToolbar header, int consumed) {

    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, MyToolbar child, MotionEvent ev) {
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    protected boolean canDragView(MyToolbar view) {
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull MyToolbar child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return true;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull MyToolbar child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.i(TAG, "onNestedPreScroll");
        consumed[1] = dy;
    }
}
