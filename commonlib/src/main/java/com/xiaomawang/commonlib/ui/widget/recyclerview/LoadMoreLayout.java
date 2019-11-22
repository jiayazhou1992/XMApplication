package com.xiaomawang.commonlib.ui.widget.recyclerview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class LoadMoreLayout extends FrameLayout {

    private boolean loading;
    private boolean noMore;

    public LoadMoreLayout(@NonNull Context context) {
        super(context);
    }

    public LoadMoreLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadMoreLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isNoMore() {
        return noMore;
    }

    public void setNoMore(boolean noMore) {
        this.noMore = noMore;
    }
}
