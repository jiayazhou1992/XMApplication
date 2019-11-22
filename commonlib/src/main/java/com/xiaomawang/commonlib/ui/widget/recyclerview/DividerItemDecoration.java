package com.xiaomawang.commonlib.ui.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * RecyclerView带的Item装饰类(带分割线,支持线性布局)
 * Created by hsh on 2016/5/14.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;//画笔
    private Drawable mDivider;//分割线
    private ArrayList<Integer> ignoreItemPositions = new ArrayList<>();//存放要忽略的Item的数组
    private int mDividerHeight = 2;//分割线高度，默认为1px
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL  或者GridLayout
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private int[] offsetXY = new int[]{0,0};

    /**
     * 是否要忽视最后一个加载更多
     */
    private  boolean isLoadMore = true;


    public DividerItemDecoration(Context context, int orientation, int[] ignoreItemPositions) {
        setOrientation(orientation);
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }

    public DividerItemDecoration(Context context, int orientation, int drawableId, int[] ignoreItemPositions, int[] offsetXY) {
        setOrientation(orientation);
        this.offsetXY[0] = offsetXY[0];
        this.offsetXY[1] = offsetXY[1];
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }

    public DividerItemDecoration(Context context, int orientation, int drawableId, int[] ignoreItemPositions) {
        setOrientation(orientation);
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = mDivider.getIntrinsicHeight();
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }

    public DividerItemDecoration(Context context, int orientation, int dividerHeight, int dividerColor, int[] ignoreItemPositions) {
        setOrientation(orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }
    /**
     * 第四个构造参数 是否忽略 加载更多diveder
     * @param context
     * @param orientation
     * @param dividerHeight
     * @param dividerColor
     * @param ignoreItemPositions
     * @param isLoadMore
     */
    public DividerItemDecoration(Context context, int orientation, int dividerHeight, int dividerColor, int[] ignoreItemPositions, boolean isLoadMore) {
        this.isLoadMore  =isLoadMore;
        setOrientation(orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }

    /**
     * 获得要忽略的Item(被忽略的Item不会进行分割线绘制和间距计算)
     */
    private void getIgnoreItemPositions(int[] ignoreItemPositions) {
        if (ignoreItemPositions != null) {//将要忽略的放到集合中去
            for (int i = 0; i < ignoreItemPositions.length; i++) {
                this.ignoreItemPositions.add(ignoreItemPositions[i]);
            }
        }
    }

    /**
     * 设置orientation
     * @param orientation
     */
    private void setOrientation(int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;
    }


    //偏移量计算(这里没有获取到position,偏移量暂时无法计算)
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (!ignoreItemPositions.contains(childAdapterPosition)) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, childAdapterPosition == 0 ? mDividerHeight : 0, 0, mDividerHeight);
            }else {
                outRect.set(childAdapterPosition == 0 ? mDividerHeight : 0, 0, mDividerHeight, 0);
            }
        }
    }


    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }


    //绘制横向 item 分割线,固定分割线的top和bottom,计算每一个Item的左和右
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();//布局是Horizontal的!需要绘制的是纵向线条!!!所以锁定上边和下面
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            if (ignoreItemPositions.contains(i)) {
                continue;
            }
            if (isLoadMore&&i ==childSize-1){//加载更多的时候 不绘制画面
                return;
            }
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {//使用图片的时候用这个方法绘制
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {//使用画笔的时候用这个方法绘制
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    //绘制纵向 item 分割线,固定分割线的左和右,计算每一个Item的top和bottom
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();//布局是Vertical的!需要绘制的是横向线条!!!所以锁定左边和右边
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            if (ignoreItemPositions.contains(i)) {
                continue;
            }
            if (isLoadMore&&i ==childSize-1){//加载更多的时候 不绘制画面
                return;
            }
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {//使用图片的时候用这个方法绘制
                mDivider.setBounds(left + offsetXY[0], top, right - offsetXY[1], bottom);
                mDivider.draw(canvas);
            } else {
            }
            if (mPaint != null) {//使用画笔的时候用这个方法绘制
                canvas.drawRect(left + offsetXY[0], top, right - offsetXY[1], bottom, mPaint);
            } else {
            }
        }
    }

    public void  setShowLastPosition(boolean isLoadMore){
        this.isLoadMore  =isLoadMore;
    }
}
