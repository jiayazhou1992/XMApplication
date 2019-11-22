package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class FullScreenLinearLayout extends LinearLayout {

    private int[] mInsets = new int[4];

    public FullScreenLinearLayout(Context context) {
        super(context);
    }

    public FullScreenLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public final int[] getInsets() {
        return mInsets;
    }



    @Override
    protected final boolean fitSystemWindows(Rect insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mInsets[0] = insets.left;
            mInsets[1] = insets.top;
            mInsets[2] = insets.right;
            return super.fitSystemWindows(insets);
        } else {
            return super.fitSystemWindows(insets);
        }
    }

}
