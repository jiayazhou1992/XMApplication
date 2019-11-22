package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;

public class NestScrollViewFlipper extends ViewFlipper implements NestedScrollingChild2, NestedScrollingParent2 {
    private static final String TAG = "NestScrollViewFlipper";

    private NestedScrollingChildHelper nestedScrollingChildHelper;

    private int[] consumed = new int[2];
    private int[] offsetInWindow = new int[2];

    public NestScrollViewFlipper(Context context) {
        super(context);
        init(context);
    }

    public NestScrollViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        nestedScrollingChildHelper.setNestedScrollingEnabled(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setMinimumHeight(height);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // NestedScrollingChild

    @Override
    public boolean startNestedScroll(int i, int i1) {
        return nestedScrollingChildHelper.startNestedScroll(i, i1);
    }

    @Override
    public void stopNestedScroll(int i) {
        nestedScrollingChildHelper.stopNestedScroll(i);
    }

    @Override
    public boolean hasNestedScrollingParent(int i) {
        return nestedScrollingChildHelper.hasNestedScrollingParent(i);
    }

    @Override
    public boolean dispatchNestedScroll(int i, int i1, int i2, int i3, @Nullable int[] ints, int i4) {
        return nestedScrollingChildHelper.dispatchNestedScroll(i, i1, i2, i3, ints, i4);
    }

    @Override
    public boolean dispatchNestedPreScroll(int i, int i1, @Nullable int[] ints, @Nullable int[] ints1, int i2) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(i, i1, ints, ints1, i2);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(@NonNull View view, @NonNull View view1, int i, int i1) {
        Log.i(TAG, "onStartNestedScroll");
        startNestedScroll(i, i1);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View view, @NonNull View view1, int i, int i1) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View view, int i) {
        stopNestedScroll(i);
    }

    @Override
    public void onNestedPreScroll(@NonNull View view, int i, int i1, @NonNull int[] ints, int i2) {
        dispatchNestedPreScroll(i, i1, ints, offsetInWindow, i2);
    }

    @Override
    public void onNestedScroll(@NonNull View view, int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    public boolean canScrollDown(){
        View currentView = getCurrentView();
        return currentView.canScrollVertically(-1);
    }
}
