package com.xiaomawang.commonlib.ui.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


/**
 * 解决ViewPager左右滑动和下拉刷新冲突的方案
 */
public class CustomSwipeToRefresh extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;

    private View mTarGet;

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (xDiff > mTouchSlop) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }




    public void setTarGet(View mTarGet) {
        this.mTarGet = mTarGet;
    }

    /**
     * 解决 swipelayout 下拉刷新与recyview 滑动冲突
     * recyview 必须放在子布局第一个
     */
    @Override
    public boolean canChildScrollUp() {
        View target =mTarGet!=null?mTarGet: getChildAt(0);
        if (target instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) target;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        } else{
            return ViewCompat.canScrollVertically(target, -1);
        }

    }

    /**
     * 重载onMeasure和onLayout使布局可以放置更多view，加载圆圈在最上层*/
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        for (int i = 1;i<getChildCount();i++){
            View view = getChildAt(i);
            if (!(view instanceof RecyclerView)&&!(view instanceof AbsListView) ) {
                view.measure(MeasureSpec.makeMeasureSpec(
                        getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                        MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.AT_MOST));
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 1;i<getChildCount();i++){
            View view = getChildAt(i);
            if (!(view instanceof RecyclerView)&&!(view instanceof AbsListView) ) {
                view.layout(getPaddingLeft(), getPaddingTop(), view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        }

        super.onLayout(changed, left, top, right, bottom);

    }
}