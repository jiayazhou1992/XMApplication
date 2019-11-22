package com.xiaomawang.commonlib.ui.widget.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;

public class MyAppBarLayout extends FrameLayout {

    private int toolbarIndex = -1;

    public MyAppBarLayout(Context context) {
        super(context);
    }

    public MyAppBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAppBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyAppBarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof MyToolbar) {
                toolbarIndex = i;
                break;
            }
        }
        if (toolbarIndex > -1) {
            getChildAt(toolbarIndex).bringToFront();
            toolbarIndex = getChildCount();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //myToolbar
        MyAppBarLayoutParams params = (MyAppBarLayoutParams) getChildAt(toolbarIndex).getLayoutParams();
    }

    public void scroll(){

    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MyAppBarLayoutParams;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyAppBarLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MyAppBarLayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MyAppBarLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class MyAppBarLayoutParams extends FrameLayout.LayoutParams{

        int scrollFlag;

        public MyAppBarLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AppBarLayout_Layout);
            this.scrollFlag = a.getInt(R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);

            a.recycle();
        }

        public MyAppBarLayoutParams(int w, int h) {
            super(w, h);
        }

        public MyAppBarLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public MyAppBarLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public MyAppBarLayoutParams(LayoutParams source) {
            super(source);
        }
    }
}
