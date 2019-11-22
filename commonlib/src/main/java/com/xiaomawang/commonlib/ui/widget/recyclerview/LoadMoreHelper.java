package com.xiaomawang.commonlib.ui.widget.recyclerview;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LoadMoreHelper {

    public static void initLoader(RecyclerView recyclerView, final OnLoadMoreListener onLoadMoreListener) {
        if (recyclerView.getAdapter() instanceof BaseRecyclerAdapter){
            ((BaseRecyclerAdapter) recyclerView.getAdapter()).setLoadMore(true);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 滑倒底部
                if (!recyclerView.canScrollVertically(1) && onLoadMoreListener!=null && newState == RecyclerView.SCROLL_STATE_IDLE){
                    // 最后一个可见位置
                    View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);

                    if (bottomView instanceof LoadMoreLayout){
                        LoadMoreLayout loadMoreLayout = (LoadMoreLayout) bottomView;
                        if (!loadMoreLayout.isLoading() && !loadMoreLayout.isNoMore()){
                            if (recyclerView.getAdapter() instanceof BaseRecyclerAdapter){
                                ((BaseRecyclerAdapter) recyclerView.getAdapter()).showLoadMore();
                            }
                            onLoadMoreListener.onLoadMore();
                        }
                    }else {
                        if (recyclerView.getAdapter() instanceof BaseRecyclerAdapter){
                            ((BaseRecyclerAdapter) recyclerView.getAdapter()).showLoadMore();
                        }
                        onLoadMoreListener.onLoadMore();
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });
    }

    /**
     * 上啦加载监听
     */
    public interface OnLoadMoreListener{
        void onLoadMore();
    }
}
