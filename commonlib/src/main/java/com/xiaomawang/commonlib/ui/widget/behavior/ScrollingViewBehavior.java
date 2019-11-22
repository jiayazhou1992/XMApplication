package com.xiaomawang.commonlib.ui.widget.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.ui.widget.bar.MyToolbar;
import com.xiaomawang.commonlib.ui.widget.bar.MyToolbarBehavior;

import java.util.List;

public class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
    private static final String TAG = "ScrollingViewBehavior";

    public ScrollingViewBehavior() {
    }

    public ScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollingViewBehavior_Layout);
        this.setOverlayTop(a.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
        a.recycle();
    }

    @Override
    public MyToolbar findFirstDependency(List<View> views) {
        int i = 0;

        for(int z = views.size(); i < z; ++i) {
            View view = (View)views.get(i);
            if (view instanceof MyToolbar) {
                return (MyToolbar) view;
            }
        }

        return null;
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof MyToolbar;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        this.offsetChildAsNeeded(child, dependency);
        return false;
    }

    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
        Log.i(TAG, "onRequestChildRectangleOnScreen");
        MyToolbar header = this.findFirstDependency(parent.getDependencies(child));
        if (header != null) {
            rectangle.offset(child.getLeft(), child.getTop());
            Rect parentRect = this.tempRect1;
            parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
            if (!parentRect.contains(rectangle)) {
                //header.setExpanded(false, !immediate);
                return true;
            }
        }

        return false;
    }

    private void offsetChildAsNeeded(View child, View dependency) {
        androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior behavior = ((androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams)dependency.getLayoutParams()).getBehavior();
        if (behavior instanceof MyToolbarBehavior) {
            ViewCompat.offsetTopAndBottom(child, dependency.getBottom() - child.getTop());
        }

    }
}
