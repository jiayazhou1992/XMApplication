package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.ViewDragHelper;

import com.xiaomawang.commonlib.base.XMFragment;

import java.lang.ref.WeakReference;

public class SwipeBackLayout extends FrameLayout {
    private static final String TAG = "SwipeBackLayout";

    private ViewDragHelper mHelper;
    private WeakReference<AppCompatActivity> mActivity;
    private WeakReference<XMFragment> mFragment;
    private View mView;


    public SwipeBackLayout(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        initViewDragHelper();
    }

    /**
     * 绑定 Activity
     * @param activity 容器 Activity
     */
    public void attachActivity(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        View content = decor.getChildAt(0);
        decor.removeView(content);
        mView = content;
        addView(content);
        decor.addView(this);
    }

    /**
     * 绑定 fragment 在onCreateView之后使用
     * @param fragment 容器 frament
     */
    public SwipeBackLayout attachFragment(XMFragment fragment, View content) {
        mFragment = new WeakReference<>(fragment);
        mView = content;
        addView(content);
        return this;
    }

    /**
     * 初始化方法
     */
    private void initViewDragHelper() {
        mHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                // 默认不扑获 View
                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                invalidate();
                // 拖动限制（大于左边界）
                left = Math.max(0, left);
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                //左右侧滑时，限制不能上下拖动。
                return 0;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                // 拖动距离大于屏幕的一半右移，拖动距离小于屏幕的一半左移
                int left = releasedChild.getLeft();
                if (left > getWidth()*1f/3f) {
                    mHelper.settleCapturedViewAt(getWidth(), 0);
                } else {
                    mHelper.settleCapturedViewAt(0, 0);
                }
                invalidate();
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                // 移动子 View
                mHelper.captureChildView(mView, pointerId);
            }
        });
        // 跟踪左边界拖动
        mHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 拦截代理
        return mHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Touch Event 代理
        mHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        //Log.i(TAG,"computeScroll");
        // 子 View 需要更新状态
        if (mHelper.continueSettling(true)) {
            invalidate();
        }else {
            //Log.i(TAG,"computeScroll continueSettling false left = " + mView.getLeft());
            if (mView.getLeft() > getWidth()*9f/10f){
                if (mActivity != null && mActivity.get() != null) {
                    mActivity.get().finish();
                    mActivity.get().overridePendingTransition(0, 0);
                }
                if (mFragment != null && mFragment.get() != null) {
                    mFragment.get().fragmentBack();
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int alpha = (int) ((1f - mView.getLeft()*1f/getWidth()) * 255);
        canvas.drawARGB(alpha, 0,0,0);
    }

}
