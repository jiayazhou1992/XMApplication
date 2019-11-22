package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import static androidx.customview.widget.ViewDragHelper.INVALID_POINTER;


public class NestedWebView extends WebView implements NestedScrollingChild2 {
    private static final String TAG = "NestedWebView";

    private NestedScrollingChildHelper mChildHelper;

    private float mLastMotionY;

    private final int[] mOffsetInWindow = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;

    private boolean mChange;
    private boolean mIsBeingDragged = false;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mActivePointerId = INVALID_POINTER;

    private int mMaybeLastScrollY = 0;


    private Handler mHandler = new Handler();
    private final long delayed = 5L;
    private Runnable mFilingRun = new Runnable() {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset()){
                int currY=mScroller.getCurrY();
                int distanceY = currY - mMaybeLastScrollY;

                Log.i("scroller"," dy " + distanceY);

                boolean apply = nestScroll(distanceY, ViewCompat.TYPE_NON_TOUCH);

                if (apply && !mScroller.isFinished()) {
                    mMaybeLastScrollY = currY;
                    mHandler.postDelayed(this, delayed);
                }else {
                    mScroller.abortAnimation();
                }
            }
        }
    };




    public NestedWebView(Context context) {
        super(context);
        init();
    }

    public NestedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(null);
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = false;

        MotionEvent trackedEvent = MotionEvent.obtain(event);

        final int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();

        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;

                if (!mScroller.isFinished()){
                   mScroller.abortAnimation();
                }

                mActivePointerId = event.getPointerId(0);
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(event);


                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                result = super.onTouchEvent(event);
                mChange = false;

                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (mLastMotionY - y);
                final int fDeltaY = deltaY;

                if (!mIsBeingDragged && Math.abs(deltaY) < mTouchSlop){
                    break;
                }

                mVelocityTracker.addMovement(event);

                mIsBeingDragged = true;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mOffsetInWindow, ViewCompat.TYPE_TOUCH)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mOffsetInWindow[1]);
                    mNestedYOffset += mOffsetInWindow[1];
                }

                //int oldY = getScrollY();
                mLastMotionY = y - mOffsetInWindow[1];
                //int newScrollY = Math.max(0, oldY + deltaY);
                //deltaY -= newScrollY - oldY;

                if (dispatchNestedScroll(0, mScrollConsumed[1], 0, deltaY, mOffsetInWindow, ViewCompat.TYPE_TOUCH)) {
                    mLastMotionY -= mOffsetInWindow[1];
                    trackedEvent.offsetLocation(0, mOffsetInWindow[1]);
                    mNestedYOffset += mOffsetInWindow[1];
                }

                Log.i(TAG, " fDeltaY " + fDeltaY + " mScrollConsumed " + mScrollConsumed[1]);

                if(mScrollConsumed[1]==0 && mOffsetInWindow[1]==0) {
                    if(mChange){
                        mChange =false;
                        trackedEvent.setAction(MotionEvent.ACTION_DOWN);
                        super.onTouchEvent(trackedEvent);
                    }else {
                        result = super.onTouchEvent(trackedEvent);
                    }
                    trackedEvent.recycle();
                }else{
                    if(!mChange){
                        mChange =true;
                        super.onTouchEvent(MotionEvent.obtain(0,0, MotionEvent.ACTION_CANCEL,0,0,0));
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                if (mIsBeingDragged) {

                    mIsBeingDragged = false;

                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);

                    if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                        nestFling(initialVelocity);
                    }

                    super.onTouchEvent(MotionEvent.obtain(0,0, MotionEvent.ACTION_CANCEL,0,0,0));
                }else {
                    super.onTouchEvent(event);
                }

                break;
        }
        return result;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void nestFling(int velocityY) {

        int maxScrollY = computeVerticalScrollRange() - computeVerticalScrollExtent() -1;

        Log.i("scroller","vy " + velocityY + " my " + maxScrollY + " sy " + getScrollY());

        mMaybeLastScrollY = getScrollY();

        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);

        mScroller.fling(0, mMaybeLastScrollY, // start
                0, - velocityY, // velocities
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE);

        mHandler.post(mFilingRun);
    }

    private boolean nestScroll(int distanceY, int type){
        Log.i("scroller"," distanceY " + distanceY + " offW " + mOffsetInWindow[1]);

        mScrollConsumed[1] = 0;

        dispatchNestedPreScroll(0,distanceY,mScrollConsumed,mOffsetInWindow,type);

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

        return dy != 0 || mScrollConsumed[1] != 0;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
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
