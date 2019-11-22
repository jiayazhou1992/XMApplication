package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ScrollerCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jiayazhou on 2017/10/17.
 */

public class RefreshLayout extends ViewGroup implements NestedScrollingParent {

    private static final String TAG = "RefreshLayout";

    public static final int SUCCESS = 0;
    public static final int NODATA = 1;
    public static final int NONET = 2;

    //private NestedScrollingParentHelper nestedScrollingParentHelper;

    private View mTarget;

    private RefreshHead refreshHead;
    private RefreshFoot refreshFoot;

    private TextView emptyView;

    private ScrollerCompat scrollerCompat;

    private int width;
    private int height;

    private float mPrevX;
    private int TouchSlop;
    //private int bigScrollY=150;//最大滑动()

    private Long time;
    private Long dTime;
    private boolean isCover = true;
    private boolean doing = false; //正在刷新

    private OnRefreshAndLoadingListener onRefreshAndLoadingListener;
    private OnScrollListener onScrollListener;

    public RefreshLayout(Context context) {
        super(context);
        initView();
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    /**添加view*/
    private void initView() {
        //nestedScrollingParentHelper=new NestedScrollingParentHelper(this);
        emptyView = new TextView(getContext());
        emptyView.setText("");

        TouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        scrollerCompat = ScrollerCompat.create(getContext());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
    private void ensureTarget() {
        if (this.mTarget == null) {
            for(int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                if (!child.equals(this.mCircleView)) {
                    this.mTarget = child;
                    break;
                }
            }
        }

    }*/

    /**初始head*/
    public void setHead(@NonNull final RefreshHead refreshHead) {
        this.refreshHead = refreshHead;
        post(new Runnable() {
            @Override
            public void run() {
                addView(refreshHead.getHead());
            }
        });


    }

    /**初始foot*/
    public void setFoot(@NonNull final RefreshFoot refreshFoot) {
        this.refreshFoot = refreshFoot;
        post(new Runnable() {
            @Override
            public void run() {
                addView(refreshFoot.getFoot());
            }
        });

    }

    /*横向冲突*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (xDiff > TouchSlop) {
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        if (mTarget == null) {
            mTarget = getChildAt(0);
            addView(emptyView);
            initScrollListener();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = measureWidth(widthMeasureSpec);
        height = measureHeight(heightMeasureSpec);
        Log.i(TAG, "w==" + width + "h==" + height);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout");
        if (refreshHead != null) {
            refreshHead.getHead().layout((width - refreshHead.getWidth()) / 2 + getPaddingLeft(),
                    -refreshHead.getHeight(),
                    (width - refreshHead.getWidth()) / 2 + refreshHead.getWidth() + getPaddingLeft(), 0);
            Log.i(TAG, "onLayout1");
        }

        mTarget.layout(getPaddingLeft(),
                getPaddingTop(),
                width - getPaddingRight(),
                height - getPaddingBottom());

        if (refreshFoot != null) {
            refreshFoot.getFoot().layout((width - refreshFoot.getWidth()) / 2 + getPaddingLeft(),
                    height,
                    (width - refreshFoot.getWidth()) / 2 + refreshFoot.getWidth() + getPaddingLeft(),
                    refreshFoot.getHight() + height);
            Log.i(TAG, "onLayout2");
        }

        emptyView.layout((int) (width - emptyView.getMeasuredWidth()) / 2,
                (int) (height - getPaddingTop() - getPaddingBottom() - emptyView.getMeasuredHeight()) / 2,
                (int) (width - emptyView.getMeasuredWidth()) / 2 + emptyView.getMeasuredWidth() + getPaddingLeft(),
                (int) (height - getPaddingTop() - getPaddingBottom() - emptyView.getMeasuredHeight()) / 2 + emptyView.getMeasuredHeight());
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        //nestedScrollingParentHelper.onNestedScrollAccepted(child,target,axes);
        //Log.i(TAG, "onNestedScrollAccepted");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        //Log.e(TAG, "onNestedScroll");
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        int dy2 = Math.abs(dy) <= 10 ? dy : (dy / Math.abs(dy)) * 15;

        if (scrollerCompat.computeScrollOffset()) return;//||doing 正在刷新
        //Log.e(TAG,"dy=="+dy);
        if (dy < 0) {
            if (!canChildScrollUp() && refreshHead != null) {
                if (refreshHead.getFixType() == 0 && getScrollY() >= -refreshHead.getHeight()) {
                    if (getScrollY() + dy2 <= -refreshHead.getHeight()) {
                        dy2 = -getScrollY() - refreshHead.getHeight();
                    }
                    scrollBy(0, dy2);
                    consumed[1] = dy;
                }
                if (refreshHead.getFixType() == 1) {
                    consumed[1] = refreshHead.pullDown(dy);
                }
            } else if (!canChildScrollDown() && refreshFoot != null) {
                if (refreshFoot.getFixType() == 0 && getScrollY() > 0) {
                    if (getScrollY() + dy2 <= 0) {
                        dy2 = -getScrollY();
                    }
                    scrollBy(0, dy2);
                    consumed[1] = dy;
                }
                if (refreshFoot.getFixType() == 1 && refreshFoot.isLive()) {
                    consumed[1] = refreshFoot.pullUp(dy);
                }
            }
        } else if (dy > 0) {
            if (!canChildScrollUp() && refreshHead != null) {
                if (refreshHead.getFixType() == 0 && getScrollY() < 0) {
                    if (getScrollY() + dy2 >= 0) {
                        dy2 = -getScrollY();
                    }
                    scrollBy(0, dy2);
                    consumed[1] = dy;
                }
                if (refreshHead.getFixType() == 1 && refreshHead.isLive()) {
                    consumed[1] = refreshHead.pullDown(dy);
                }
            } else if (!canChildScrollDown() && refreshFoot != null) {
                if (refreshFoot.getFixType() == 0 && getScrollY() <= refreshFoot.getHight()) {
                    if (getScrollY() + dy2 >= refreshFoot.getHight()) {
                        dy2 = refreshFoot.getHight() - getScrollY();
                    }
                    scrollBy(0, dy2);
                    consumed[1] = dy;
                }
                if (refreshFoot.getFixType() == 1) {
                    consumed[1] = refreshFoot.pullUp(dy);
                }
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "velocityY==" + velocityY + consumed);
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //velocityY=velocityY-100;
        Log.e(TAG, "velocityY==" + velocityY);
        //fling(velocityX);
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
        //nestedScrollingParentHelper.onStopNestedScroll(child);
        Log.e(TAG, "stop");

        if (refreshFoot != null && refreshFoot.getFixType() == 0) {
            if (getScrollY() >= refreshFoot.getHight()) {
                Log.i(TAG, "head-----------------" + refreshFoot.getHight());
                scrollBack(refreshFoot.getHight() - getScrollY());
                if (onRefreshAndLoadingListener != null) {
                    onRefreshAndLoadingListener.onLoading();
                    refreshFoot.load();
                    doing = true;
                }
                Log.i(TAG, "load");
                return;
            } else {
                scrollBack(-getScrollY());
            }
        } else if (refreshFoot != null && refreshFoot.getFixType() == 1) {
            if (refreshFoot.isBottleneck()) {
                refreshFoot.load();
                if (onRefreshAndLoadingListener != null) {
                    onRefreshAndLoadingListener.onLoading();
                    doing = true;
                }
                return;
            } else {
                refreshFoot.loadComplete();
            }

        }

        if (refreshHead != null && refreshHead.getFixType() == 0) {
            if (getScrollY() <= -refreshHead.getHeight()) {
                Log.i(TAG, "head-----------------1");
                scrollBack(-refreshHead.getHeight() - getScrollY());
                if (onRefreshAndLoadingListener != null) {
                    onRefreshAndLoadingListener.onRefresh();
                    refreshHead.refresh();
                    doing = true;
                }
                Log.i(TAG, "refrsh");
                return;
            } else {
                scrollBack(-getScrollY());
            }
        } else if (refreshHead != null && refreshHead.getFixType() == 1) {
            if (refreshHead.isBottleneck()) {
                refreshHead.refresh();
                if (onRefreshAndLoadingListener != null) {
                    onRefreshAndLoadingListener.onRefresh();
                    doing = true;
                }
                return;
            } else {
                refreshHead.refreshComplete();
            }

        }

    }

    @Override
    public int getNestedScrollAxes() {
        Log.i(TAG, "getNestedScrollAxes");
        return super.getNestedScrollAxes();
    }

    /*设置下拉上拉接口*/
    public void setOnRefreshAndLoadingListener(OnRefreshAndLoadingListener onRefreshAndLoadingListener) {
        this.onRefreshAndLoadingListener = onRefreshAndLoadingListener;
    }

    /*设置滑动监听*/
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /*完成*/
    public void done(@NonNull int state) {
        switch (state) {
            case SUCCESS:
                emptyView.setVisibility(GONE);
                break;
            case NODATA:
                emptyView.setVisibility(VISIBLE);
                emptyView.setText("暂时没有您想看的东西");
                break;
            case NONET:
                emptyView.setVisibility(VISIBLE);
                emptyView.setText("网络出错");
                break;
        }
        if (refreshFoot != null)
            refreshFoot.loadComplete();
        if (refreshHead != null)
            refreshHead.refreshComplete();
        scrollBack(-getScrollY());
        doing = false;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (refreshHead!=null)
            refreshHead.scrollListener(y);
        if (refreshFoot!=null)
            refreshFoot.scrollLisenter(y);
    }

    /*回弹*/
    private void scrollBack(int dy) {
        //if (!scrollerCompat.computeScrollOffset())return;
        scrollerCompat.startScroll(0, getScrollY(), 0, dy);
        invalidate();
    }

    /*下拉上拉接口*/
    public interface OnRefreshAndLoadingListener {
        //刷新回调
        void onRefresh();

        //加载回调
        void onLoading();
    }

    /*滑动监听*/
    public interface OnScrollListener {
        void onScroll(int dy, int dx);
    }

    private OnScrollBottomListenr listenr;
    public interface OnScrollBottomListenr{
        void onScrollBottom();
    }

    public void setListenr(OnScrollBottomListenr listenr) {
        this.listenr = listenr;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scrollerCompat.computeScrollOffset()) {
            scrollTo(0, scrollerCompat.getCurrY());
            postInvalidate();
        } else {

        }
    }

    /*滑动监听*/
    private void initScrollListener(){
        RecyclerView recyclerView= (RecyclerView) mTarget;
        if (Build.VERSION.SDK_INT>=23) {
            /*recyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                }
            });*/
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Log.i(TAG, "recyclerview--dy=" + dy);
                //super.onScrolled(recyclerView, dx, dy);
                if (onScrollListener!=null){
                    onScrollListener.onScroll(dy,dx);
                }
                if (listenr!=null&&!canChildScrollDown()&&dy>0){
                    listenr.onScrollBottom();
                }
            }
        });

    }

    /*判断能否下滑*/
    public boolean canChildScrollUp() {

        if (Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {

                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    /*判断能否上滑*/
    public boolean canChildScrollDown() {

        if (Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                int count = absListView.getCount();
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < (count - 1) || absListView.getChildAt(count - 1)
                        .getBottom() < absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mTarget, 1) || mTarget.getScrollY() < mTarget.getHeight();
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }

    /*测量*/
    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

}
