package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.util.Pools;
import androidx.gridlayout.widget.GridLayout;

import com.xiaomawang.commonlib.R;

public class HomeTagsLayout extends GridLayout {

    private Pools.SynchronizedPool<View> itemViewPool = new Pools.SynchronizedPool<>(15);
    private int itemLayout;

    public HomeTagsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public HomeTagsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HomeTagsLayout(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HomeTagsLayout);
            itemLayout = typedArray.getResourceId(R.styleable.HomeTagsLayout_itemLayout, 0);
            typedArray.recycle();
        }
    }

    public View createItem(int index) {
        if (itemLayout != 0 && index >= 0) {
            View itemView;
            if (index < getChildCount()) {
                itemView = getChildAt(index);
            }else {
                itemView = itemViewPool.acquire();
                if (itemView == null) {
                    itemView = LayoutInflater.from(getContext()).inflate(itemLayout, this, false);
                }
                GridLayout.Spec rowSpec = GridLayout.spec(index / getColumnCount(), 1f);
                GridLayout.Spec columnSpec = GridLayout.spec(index % getColumnCount(), 1f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                params.width = 10;
                addView(itemView, params);
            }
            return itemView;
        }
        return null;
    }

    public void clearItems(int start) {
        for (int i = start; i < getChildCount(); i++) {
            itemViewPool.release(getChildAt(i));
        }
        if (start < getChildCount()) {
            removeViews(start, getChildCount() - start);
        }
    }
}
