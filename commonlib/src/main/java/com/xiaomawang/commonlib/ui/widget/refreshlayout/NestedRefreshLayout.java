package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.view.NestedScrollingChild;

import com.xiaomawang.commonlib.ui.widget.behavior.ViewOffsetHelper;

/**
 * Created by jiayazhou on 2017/10/17.
 */

public class NestedRefreshLayout extends RelativeLayout {
    private static final String TAG = "NestedRefreshLayout";

    private ViewOffsetHelper viewOffsetHelper;

    private int TouchSlop;
    private float mPrevX, mPrevY;
    private float mLastX, mLastY;
    private boolean moving;

    private View targetView;
    private RefreshHeadInterface refreshHead;

    private int[] rangTop = new int[2];

    private ValueAnimator scrollBack;

    private OnUpdataListener onUpdataListener;


    public NestedRefreshLayout(Context context) {
        super(context);
        init(null);
    }

    public NestedRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public NestedRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        TouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        rangTop[0] = 0;
        rangTop[1] = 300;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof NestedScrollingChild) {
                targetView = getChildAt(i);
                break;
            }
        }
        if (targetView == null) {
            ViewGroup viewGroup = (ViewGroup) getChildAt(0);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof NestedScrollingChild) {
                    targetView = viewGroup.getChildAt(i);
                    break;
                }
            }
        }
        viewOffsetHelper = new ViewOffsetHelper(targetView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (viewOffsetHelper != null) {
            viewOffsetHelper.onViewLayout();
        }
    }

    public void setRefreshHead(final RefreshHeadInterface refreshHead) {
        if (refreshHead == null) return;
        this.refreshHead = refreshHead;
        this.rangTop[0] = refreshHead.getRang()[0];
        this.rangTop[1] = refreshHead.getRang()[1];
        post(new Runnable() {
            @Override
            public void run() {
                addView(refreshHead.getHead(), 0);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.i(TAG, "dispatchTouchEvent");

        boolean holder = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevY = MotionEvent.obtain(event).getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float eventY = MotionEvent.obtain(event).getY();
                float yDiff = Math.abs(eventY - mPrevY);

                if (yDiff > TouchSlop || moving) {
                    moving = true;
                    if (!targetView.canScrollVertically(-1)){//不能下拉
//                        Log.i(TAG, "scroll 1");
                        int dy = (int) (eventY - mLastY);
                        int usr = pullDown(dy);
                        holder = viewOffsetHelper.getTopAndBottomOffset() != 0;
                    }else if (!targetView.canScrollVertically(1)){//不能上滑
//                        Log.i(TAG, "scroll 2");
                        holder =  false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (viewOffsetHelper.getTopAndBottomOffset() != 0 && moving) {
                    holder =  true;
                }
                moving = false;
//                刷新
                refresh();
                break;
        }
        mLastX = event.getX();
        mLastY = event.getY();

        if (holder) {
            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onInterceptTouchEvent");
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mPrevX = MotionEvent.obtain(event).getX();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                float eventX = event.getX();
//                float xDiff = Math.abs(eventX - mPrevX);
//
//                if (xDiff > TouchSlop) {
//                    return false;
//                }
//        }
//        return super.onInterceptTouchEvent(event);
//    }

    private int pullDown(int dy) {
        if (scrollBack != null) {
            scrollBack.cancel();
            scrollBack = null;
        }
        int nowOffset = viewOffsetHelper.getTopAndBottomOffset();
        int offset = nowOffset + dy;
        if (offset > rangTop[1]){
            offset = rangTop[1];
        }
        if (offset < rangTop[0]) {
            offset = rangTop[0];
        }
        viewOffsetHelper.setTopAndBottomOffset(offset);
        if (refreshHead != null) {
            refreshHead.pullDown(offset);
        }
        return offset - nowOffset;
    }

    private void refresh() {
        if (refreshHead != null) {
            if (viewOffsetHelper.getTopAndBottomOffset() == rangTop[1]) {
                if (onUpdataListener != null) {
                    onUpdataListener.onUpdata();
                    setRefreshing(true);
                } else {
                    setRefreshing(false);
                }
            } else {
                setRefreshing(false);
            }
        }
    }

    private void animatorScrollBack(){
        if (viewOffsetHelper.getTopAndBottomOffset() != 0) {
            scrollBack = new ValueAnimator();
            scrollBack.setDuration(80);
            scrollBack.setIntValues(viewOffsetHelper.getTopAndBottomOffset(), 0);
            scrollBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    viewOffsetHelper.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                }
            });
            scrollBack.start();
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing) {
            viewOffsetHelper.setTopAndBottomOffset(rangTop[1]);
            if (refreshHead != null) {
                refreshHead.refresh();
            }
        } else {
            animatorScrollBack();
            if (refreshHead != null) {
                refreshHead.refreshComplete();
            }
        }
    }

    public void setOnUpdataListener(OnUpdataListener onUpdataListener) {
        this.onUpdataListener = onUpdataListener;
    }

    public interface OnUpdataListener{
        void onUpdata();
    }
}
