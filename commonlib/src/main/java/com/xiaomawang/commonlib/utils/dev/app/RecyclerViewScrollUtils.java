package com.xiaomawang.commonlib.utils.dev.app;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;

import java.lang.ref.WeakReference;

public class RecyclerViewScrollUtils {
    private static final String TAG = "ScrollUtils";

    private WeakReference<RecyclerView> mRecyclerView;
    private boolean mShouldScroll;
    private boolean mShouldScrollMiddle;
    private int mToPosition;
    private int mToMiddlePosition;
    private int lastTopPosition = -1;
    private int lastBottomPosition = -1;

    //指定（位置）滑动状态
    private boolean checkStatus;


    public RecyclerViewScrollUtils(RecyclerView recyclerView) {
        this(recyclerView, null);
    }

    public RecyclerViewScrollUtils(RecyclerView recyclerView, OnPositionListener onPositionListener) {
        this(recyclerView, onPositionListener, null);
    }

    public RecyclerViewScrollUtils(RecyclerView recyclerView, final OnPositionListener onPositionListener, final OnSmartPositionListener onSmartPositionListener) {
        this.mRecyclerView = new WeakReference<>(recyclerView);

        mRecyclerView.get().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //Log.i(TAG, "onScrollStateChanged");
                /*if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRecyclerView, mToPosition);
                }*/
                if (onSmartPositionListener != null && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    onSmartPositionListener.onSmartPosition(mRecyclerView.get(), getLastTopPosition(), getLastBottomPosition());
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.i(TAG, "onScrolled");
                if (mShouldScroll) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mToPosition, checkStatus);
                    return;
                }
                if (mShouldScrollMiddle) {
                    mShouldScrollMiddle = false;
                    moveToMiddle(mToMiddlePosition);
                    return;
                }

                if (onPositionListener != null) {
                    int oldTop = lastTopPosition;
                    int oldBottom = lastBottomPosition;

                    View firstView = mRecyclerView.get().getChildAt(0);
                    int firstItem = mRecyclerView.get().getChildLayoutPosition(firstView);
                    if (firstView.getTop() <= 0 && firstView.getTop() >= -50 && lastTopPosition != firstItem) {
                        lastTopPosition = firstItem;
                        onPositionListener.onTopPosition(mRecyclerView.get(), lastTopPosition, checkStatus);
                    }

                    View bottomView = mRecyclerView.get().getChildAt(mRecyclerView.get().getChildCount() - 1);
                    int bottomItem = mRecyclerView.get().getChildLayoutPosition(bottomView);
                    int height = mRecyclerView.get().getMeasuredHeight();
                    if (bottomView.getTop() <= height && bottomView.getTop() >= height - 50 && lastBottomPosition != bottomItem) {
                        lastBottomPosition = bottomItem;
                        onPositionListener.onBottomPosition(mRecyclerView.get(), lastBottomPosition);
                    }

                    if ((oldTop != firstItem) || (oldBottom != bottomItem)){
                        onPositionListener.onPosition(mRecyclerView.get(), firstItem, bottomItem);
                    }
                }
                //完成指定滑动，重制状态
                checkStatus = false;
            }
        });
    }

    /**
     * recyclerView滑动到指定位置
     */
    public void smoothMoveToPosition(final int position) {
        smoothMoveToPosition(position, false);
    }

    public void smoothMoveToPosition(final int position, boolean checkStatus) {
        if (position == -1 || position >= mRecyclerView.get().getAdapter().getItemCount()) return;

        this.checkStatus = checkStatus;

        // 第一个可见位置
        int firstItem = mRecyclerView.get().getChildLayoutPosition(mRecyclerView.get().getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.get().getChildLayoutPosition(mRecyclerView.get().getChildAt(mRecyclerView.get().getChildCount() - 1));

        if (position <= firstItem) {
            DevLogger.iTag(TAG,"第一种可能:跳转位置在第一个可见位置之前");

            //mRecyclerView.smoothScrollToPosition(position);
            mRecyclerView.get().scrollToPosition(position);
        } else if (position <= lastItem) {
            DevLogger.iTag(TAG,"第二种可能:跳转位置在第一个可见位置之后");

            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.get().getChildCount()) {
                int top = mRecyclerView.get().getChildAt(movePosition).getTop();
                //mRecyclerView.smoothScrollBy(0, top);
                mRecyclerView.get().scrollBy(0,top);
            }
        } else {
            DevLogger.iTag(TAG,"第三种可能:跳转位置在最后可见项之后");

            mToPosition = position;
            mShouldScroll = true;
            //mRecyclerView.smoothScrollToPosition(position);
            mRecyclerView.get().scrollToPosition(position);
        }
    }

    public void moveToMiddle(int position) {
        if (position == -1 || position >= mRecyclerView.get().getAdapter().getItemCount()) return;
        // 第一个可见位置
        int firstItem = mRecyclerView.get().getChildLayoutPosition(mRecyclerView.get().getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.get().getChildLayoutPosition(mRecyclerView.get().getChildAt(mRecyclerView.get().getChildCount() - 1));

        if (position < firstItem) {
            DevLogger.iTag(TAG,"2 第一种可能:跳转位置在第一个可见位置之前");

            mShouldScrollMiddle = true;
            mToMiddlePosition = position;
            mRecyclerView.get().scrollToPosition(position);

        } else if (position == firstItem || position <= lastItem) {
            DevLogger.iTag(TAG,"2 第二种可能:跳转位置在第一个可见位置之后");

            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.get().getChildCount()) {
                int top = mRecyclerView.get().getChildAt(movePosition).getTop();
                int bottom = mRecyclerView.get().getChildAt(movePosition).getBottom();
                int height = mRecyclerView.get().getHeight();
                int toTop = (height - (bottom - top)) / 2;
                mRecyclerView.get().scrollBy(0,top - toTop);
            }

        } else {
            DevLogger.iTag(TAG,"2 第三种可能:跳转位置在最后可见项之后");

            mToMiddlePosition = position;
            mShouldScrollMiddle = true;

            mRecyclerView.get().scrollToPosition(position);
        }
    }

    public int getLastTopPosition() {
        return lastTopPosition;
    }

    public int getLastBottomPosition() {
        return lastBottomPosition;
    }

    /**
     * 只要滑动就调用
     */
    public interface OnPositionListener{
        void onTopPosition(RecyclerView recyclerView, int position, boolean checkStatus);
        void onBottomPosition(RecyclerView recyclerView, int bottomPosition);
        void onPosition(RecyclerView recyclerView, int topPosition, int bottomPosition);
    }

    /**
     * 只有滑动开始，惯性滑动，滑动结束才调用
     */
    public interface OnSmartPositionListener{
        void onSmartPosition(RecyclerView recyclerView, int topPosition, int bottomPosition);
    }
}
