package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaomawang.commonlib.ui.widget.NestedWebView;

/**
 * Created by jiayazhou on 2017/10/17.
 */

public class RefreshLayout2 extends LinearLayout implements NestedScrollingParent2/*, NestedScrollingChild2*/, GestureDetector.OnGestureListener {
    private static final String TAG = "RefreshLayout2";

    private GestureDetector mGestureDetector;
    private boolean passing;

    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    private Scroller mScroller;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private View targetView;
    private RefreshHead refreshHead;
    private RefreshFoot refreshFoot;

    private int[] rangTop = new int[2];
    private int[] rangBottom = new int[2];
    private int[] headTop = new int[2];
    private int[] footBottom = new int[2];

    public RefreshLayout2(Context context) {
        super(context);
        init(null);
    }

    public RefreshLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RefreshLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        mGestureDetector = new GestureDetector(getContext(), this);

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        mScroller = new Scroller(getContext());

        //setNestedScrollingEnabled(true);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        Log.i(TAG, " width " + width + " height " + height);

        int m_h = 0;

        for (int i = 0; i<getChildCount(); i++){
            View childView = getChildAt(i);

            if (childView instanceof RecyclerView || childView instanceof NestedWebView){
                targetView = childView;
                measureChild(childView,widthMeasureSpec,heightMeasureSpec);
            }else {
                measureChild(childView,widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            }

            int c_h = childView.getMeasuredHeight();//Math.min(height, childView.getMeasuredHeight());

            m_h += c_h;

            Log.i(TAG, " m_h " + m_h);

            if (i == 0 ){
                rangTop[0] = 0;
                rangTop[1] = childView.getMeasuredHeight();
            }else if (i == 2){
                rangBottom[0] = rangTop[1];
                rangBottom[1] = rangBottom[0] + childView.getMeasuredHeight();
            }
        }

        if (rangBottom[0] == rangBottom[1] && rangBottom[0] == 0){
            rangBottom[0] = rangTop[1];
            rangBottom[1] = rangBottom[0];
        }

        headTop[0] = -300;
        headTop[1] = 0;
        footBottom[0] = rangBottom[1];
        footBottom[1] = footBottom[0] + 300;

        Log.i(TAG," rang top " + rangTop[0] + " " + rangTop[1] + " rang bottom  " + rangBottom[0] + " " + rangBottom[1]);

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),m_h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int top = 0;

        for (int i = 0; i<getChildCount(); i++){
            View childView = getChildAt(i);

            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
            int cl = marginLayoutParams.leftMargin;
            int ct = marginLayoutParams.topMargin + top;
            int cr = getMeasuredWidth() - marginLayoutParams.rightMargin;
            int cb = top + childView.getMeasuredHeight();

            childView.layout(cl,ct,cr,cb);

            top = top + marginLayoutParams.topMargin + childView.getMeasuredHeight() + marginLayoutParams.bottomMargin;

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            passing = true;
        }else if (ev.getAction() == MotionEvent.ACTION_UP){
            passing = false;
            computeScroll();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    /*@Override
    public boolean startNestedScroll(int i, int i1) {
        return mNestedScrollingChildHelper.startNestedScroll(i,i1);
    }

    @Override
    public void stopNestedScroll(int i) {

    }

    @Override
    public boolean hasNestedScrollingParent(int i) {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int i, int i1, int i2, int i3, @Nullable int[] ints, int i4) {
        return false;
    }

    @Override
    public boolean dispatchNestedPreScroll(int i, int i1, @Nullable int[] ints, @Nullable int[] ints1, int i2) {
        return false;
    }*/


    @Override
    public boolean onStartNestedScroll(@NonNull View view, @NonNull View view1, int i, int i1) {
        return (i & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View view, @NonNull View view1, int i, int i1) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View view, int i, int i1, @NonNull int[] ints, int i2) {

        Log.i(TAG, "scroll to by " + i1);
        if (!mScroller.isFinished()){
            mScroller.abortAnimation();
        }

        if (!view.canScrollVertically(-1) && !view.canScrollVertically(1)) {
            int sy = getScrollY();
            int ssy = sy + i1;
            ssy = Math.min(rangBottom[1], ssy);
            ssy = Math.max(rangTop[0], ssy);

            int dsy = ssy - sy;

            Log.i(TAG, "child no scroll" + dsy);

            if (dsy != 0) {
                scrollBy(0, dsy);
                ints[1] = dsy;
            }

            return;
        }

        if (!view.canScrollVertically(-1)){

            int sy = getScrollY();

            if (sy <= headTop[1] && sy >= headTop[0] && i1 < 0){
                i1 = (int) ((1.0f / 300 * sy + 1f) * i1);
            }

            int ssy = sy + i1;
            ssy = Math.min(rangTop[1],ssy);
            ssy = Math.max(headTop[0],ssy);

            int dsy = ssy - sy;

            Log.i(TAG, "scroll to down by " + dsy);

            if (dsy != 0){
                scrollBy(0,dsy);
                ints[1] = dsy;
            }

        }else if (!view.canScrollVertically(1)){

            int sy = getScrollY();
            int ssy = sy + i1;
            ssy = Math.min(footBottom[1],ssy);
            ssy = Math.max(rangBottom[0],ssy);

            int dsy = ssy - sy;

            Log.i(TAG, "scroll to up by " + dsy);

            if (dsy != 0){
                scrollBy(0,dsy);
                ints[1] = dsy;
            }

        }else {
            Log.i(TAG, "child scroll" + i1);
        }

    }

    @Override
    public void onNestedScroll(@NonNull View view, int i, int i1, int i2, int i3, int i4) {
        Log.i(TAG, "child Nested scroll" + i + " " + i1 + " " + i2 + " " + i3 + " " + i4);
    }

    @Override
    public void onStopNestedScroll(@NonNull View view, int i) {
        Log.i(TAG, "child onStopNestedScroll " + i);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.i(TAG, "child onNestedPreFling ");
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    /**
     * 回弹
     */
    private void animatorScrollBack(){
        if (passing){
            return;
        }

        int sy = getScrollY();
        if (sy < headTop[1]){
            mScroller.startScroll(0, sy, 0,headTop[1] - sy);
        }else if (sy > footBottom[0]){
            mScroller.startScroll(0, sy, 0,footBottom[0] - sy);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }else {
            animatorScrollBack();
        }
    }

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
    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
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

    @Override
    public boolean onDown(MotionEvent e) {
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
        int[] ints = new int[2];
        onNestedPreScroll(getChildAt(1),(int) distanceX, (int) distanceY,ints, ViewCompat.TYPE_NON_TOUCH);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
