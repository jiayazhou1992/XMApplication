package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

public class NestedWebView2 extends WebView implements GestureDetector.OnGestureListener, NestedScrollingChild2 {


    private final int[] mOffsetInWindow = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private int mMaybeLastScrollY = 0;

    private GestureDetector mGestureDetector;
    private NestedScrollingChildHelper mChildHelper;

    private Scroller mScroller;

    private Handler mHandler = new Handler();
    private final long delayed = 10L;
    private Runnable mFilingRun = new Runnable() {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset()){
                int currY=mScroller.getCurrY();
                int distanceY = currY - mMaybeLastScrollY;

                Log.i("scroller"," dy " + distanceY);

                boolean apply = nestScroll(distanceY, ViewCompat.TYPE_NON_TOUCH);

                if (apply) {
                    mMaybeLastScrollY = currY;
                    mHandler.postDelayed(this, delayed);
                }else {
                    mScroller.abortAnimation();
                }
            }
        }
    };


    public NestedWebView2(Context context) {
        super(context);
        init();
    }

    public NestedWebView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedWebView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NestedWebView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public NestedWebView2(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    private void init(){
        mGestureDetector = new GestureDetector(getContext(),this);
        mChildHelper = new NestedScrollingChildHelper(this);
        mScroller = new Scroller(getContext());

        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onDetachedFromWindow() {

        mHandler.removeCallbacks(null);

        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //super.onTouchEvent(e);

        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }

        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        nestScroll((int) distanceY, ViewCompat.TYPE_TOUCH);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //super.onTouchEvent(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        int maxScrollY = computeVerticalScrollRange() - computeVerticalScrollExtent() -1;

        Log.i("scroller","vy " + velocityY + " my " + maxScrollY + " sy " + getScrollY());

        mMaybeLastScrollY = getScrollY();

        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);

        mScroller.fling(0, mMaybeLastScrollY, // start
                0, (int) - velocityY, // velocities
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE); // overscroll

        mHandler.post(mFilingRun);

        return true;
    }


    private boolean nestScroll(int distanceY, int type){
        Log.i("scroller"," distanceY " + distanceY + " offW " + mOffsetInWindow[1]);

        distanceY = distanceY - mOffsetInWindow[1];

        //Log.i("scroller"," distanceY " + distanceY + " mScrollConsumed " + mScrollConsumed[1]);

        mScrollConsumed[1] = 10;

        dispatchNestedPreScroll(0,distanceY,mScrollConsumed,mOffsetInWindow,type);

        //Log.i("scroller"," mScrollConsumed " + mScrollConsumed[1]);

        int maxScrollY = computeVerticalScrollRange() - computeVerticalScrollExtent() -1;

        int dy = distanceY - mScrollConsumed[1];
        int sy = getScrollY();
        int ssy = sy + dy;
        ssy = Math.max(0,ssy);
        ssy = Math.min(maxScrollY,ssy);

        dy = ssy - sy;
        if (dy != 0) {
            scrollBy(0, dy);
        }

        if (mScrollConsumed[1] == 0 && dy != 0 && Math.abs(dy) < Math.abs(distanceY)){
            int laveY = distanceY - dy;
            dispatchNestedPreScroll(0,laveY,mScrollConsumed,mOffsetInWindow,type);
        }

        return dy != 0 || mScrollConsumed[1] != 0;
    }


    // NestedScrollingChild
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        //DevLogger.iTag(TAG,"startNestedScroll");
        return mChildHelper.startNestedScroll(axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent(ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean startNestedScroll(int i, int i1) {
        return mChildHelper.startNestedScroll(i,i1);
    }

    @Override
    public void stopNestedScroll(int i) {
        mChildHelper.stopNestedScroll(i);
    }

    @Override
    public boolean hasNestedScrollingParent(int i) {
        return mChildHelper.hasNestedScrollingParent(i);
    }

    @Override
    public boolean dispatchNestedScroll(int i, int i1, int i2, int i3, @Nullable int[] ints, int i4) {
        return mChildHelper.dispatchNestedScroll(i,i1,i2,i3,ints,i4);
    }

    @Override
    public boolean dispatchNestedPreScroll(int i, int i1, @Nullable int[] ints, @Nullable int[] ints1, int i2) {
        return mChildHelper.dispatchNestedPreScroll(i,i1,ints,ints1,i2);
    }
}
