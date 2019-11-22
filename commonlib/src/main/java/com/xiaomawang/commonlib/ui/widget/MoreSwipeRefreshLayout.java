package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.ui.widget.recyclerview.LoadMoreLayout;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;

public class MoreSwipeRefreshLayout extends SwipeRefreshLayout {
    private static final String TAG = "MoreSwipeRefreshLayout";

    private View mRecyclerView;

    private OnLoadMoreListener onLoadMoreListener;

    private int offset = 10;


    public MoreSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public MoreSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRecyclerView = findViewById(R.id.list);
        initScrollListener();
    }

    /**
     * 滑动监听
     **/
    private void initScrollListener(){
        RecyclerView recyclerView= (RecyclerView) mRecyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!canChildScrollDown() && onLoadMoreListener!=null && newState == RecyclerView.SCROLL_STATE_IDLE){
                    // 最后一个可见位置
                    // TODO 当recyclerView添加了分割线的时候，可能导致下拉刷新不起作用，这里补 offset
                    View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    boolean inBottom = (bottomView.getBottom() + recyclerView.getPaddingBottom() + offset) >= recyclerView.getHeight();

                    if (bottomView instanceof LoadMoreLayout){
                        LoadMoreLayout loadMoreLayout = (LoadMoreLayout) bottomView;
                        if (!loadMoreLayout.isLoading() && inBottom && !loadMoreLayout.isNoMore()){
                            onLoadMoreListener.onLoadMore();
                        }
                    }else {
                        if (inBottom){
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!canChildScrollDown()){
                    DevLogger.iTag(TAG,"canNoChildScrollDown");
                }

                if (!canChildScrollUp()){
                    DevLogger.iTag(TAG,"canNoChildScrollUp");
                }

            }
        });

    }

    /**
     * 判断能否下滑
     **/
    public boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mRecyclerView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mRecyclerView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {

                return ViewCompat.canScrollVertically(mRecyclerView, -1) || mRecyclerView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mRecyclerView, -1);
        }
    }

    /**
     * 判断能否上滑
     **/
    public boolean canChildScrollDown() {
        if (Build.VERSION.SDK_INT < 14) {
            if (mRecyclerView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mRecyclerView;
                int count = absListView.getCount();
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < (count - 1) || absListView.getChildAt(count - 1)
                        .getBottom() < absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mRecyclerView, 1) || mRecyclerView.getScrollY() < mRecyclerView.getHeight();
            }
        } else {
            return ViewCompat.canScrollVertically(mRecyclerView, 1);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    /**
     * TODO 当recyclerView添加了分割线的时候，可能导致下拉刷新不起作用，这里补 offset
     * @param onLoadMoreListener
     * @param offset
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener, int offset) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.offset = offset;
    }

    /**
     * 上啦加载监听
     */
    public interface OnLoadMoreListener{
        void onLoadMore();
    }
}
