package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

public class ElasticHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG = "ElasticHorizontalScroll";

    private int elastic = 100;
    private Point lastPoint;
    private View inner;

    public ElasticHorizontalScrollView(Context context) {
        super(context);
    }

    public ElasticHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ElasticHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ElasticHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inner = getChildAt(0);
    }

    private boolean isTop(){
        return getScrollX() == 0;
    }

    private boolean isTopElastic(){
        return getScrollX() == -elastic;
    }

    private boolean isTopElasticRang(){
        return getScrollX() > -elastic && getScrollX() <= 0;
    }

    private boolean isBottom(){
        return getScrollX() == getScrollRange();
    }

    private boolean isBottomElastic(){
        return getScrollX() == getScrollRange() + elastic;
    }

    private boolean isBottomElasticRang(){
        return getScrollX() > (getScrollRange() + elastic) && getScrollX() <= getScrollRange();
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0, child.getWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
        }
        return scrollRange;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i(TAG, " dispatchTouchEvent ");
        if (lastPoint == null) {
            lastPoint = new Point();
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            lastPoint.x = ev.getX();
            lastPoint.y = ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i(TAG, " onTouchEvent ");

        if (inner != null){
            dragInnerView(ev);
        }

        return super.onTouchEvent(ev);
    }

    private void dragInnerView(MotionEvent event) {
        float dx = event.getX() - lastPoint.x;
        lastPoint.x = event.getX();
        if ((isTop() && dx > 0) || (isBottom() && dx < 0)) {
            inner.setTranslationX(inner.getTranslationX() + dx);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            animBack();
        }
    }

    private void animBack(){
        if (inner.getTranslationX() != 0) {
            ViewCompat.animate(inner).translationX(0).setDuration(150).start();
        }
    }

    public void scrollMiddle(View view){
        int left = view.getLeft();
        int dx = left + view.getWidth()/2 - getScrollX() - getWidth()/2;
        scrollBy(dx, 0);
    }


    private class Point{
        float x;
        float y;
    }
}
