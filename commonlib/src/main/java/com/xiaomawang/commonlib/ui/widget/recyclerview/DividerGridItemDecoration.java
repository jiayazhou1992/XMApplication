package com.xiaomawang.commonlib.ui.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

/**
 * RecyclerView带的Item装饰类(带分割线,支持GridLayout布局和StaggeredGridLayoutManager布局)
 * ---------------------瀑布流因为BUG暂时不使用------------------------------------------
 * 本方法有三个构造,分别支持默认分割线,自定义图片分割线,自定义颜色宽高分割线
 * <p/>
 * findFirstVisibleItemPosition()====================>
 * findFirstCompletelyVisibleItemPosition()==========>
 * findLastVisibleItemPosition()=====================>
 * findLastCompletelyVisibleItemPosition()===========>
 * Created by hsh on 2016/5/14.
 */
public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;//画笔
    private int ItemSpan = 0;//Item之间的间距(左右上下一样的)
    private boolean IsDrawBorder = true;//是否绘制边框,默认为要绘制,如果关闭,最外面的边框线将不会绘制
    private ArrayList<Integer> ignoreItemPositions = new ArrayList<>();//存放要忽略的Item的数组
    //private int FIRST_ITEM = 0;//第一个Item的下标
    private Drawable mHDivider;//横向分割线,横向分割线的高度=自身的高度,宽度无限
    private Drawable mVDivider;//纵向分割线,纵向分割线的宽度=自身的宽度,高度无限
    private int mHDividerHeight = 2;//横向分割线的高度，默认为2px
    private int mVDividerWidth = 2;//纵向分割线的宽度，默认为2px
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     * 第一个构造,
     * 默认分割线：默认宽高，颜色为灰色
     *
     * @param context
     * @param isDrawBorder        是否需要外边框
     * @param ignoreItemPositions 要求忽略的Item的数组,这个数组里面记载的position的Item不会进行边框绘制
     * @param ItemSpan            Item之间的距离
     */
    public DividerGridItemDecoration(Context context, boolean isDrawBorder, int[] ignoreItemPositions, int ItemSpan) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.IsDrawBorder = isDrawBorder;
        // FIRST_ITEM = hasHeadItem ? 1 : 0;
        this.ItemSpan = ItemSpan;
        mHDivider = a.getDrawable(0);
        mVDivider = a.getDrawable(0);
        a.recycle();
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item


    }

    /**
     * 第二个构造,需要指定横向分割线图片和纵向分割线图片
     * 自定义分割线
     *
     * @param context
     * @param isDrawBorder        是否需要外边框
     * @param ignoreItemPositions 要求忽略的Item的数组,这个数组里面记载的position的Item不会进行边框绘制
     * @param ItemSpan            Item之间的距离
     * @param HDrawableId         横向分割线图片
     * @param VDrawableId         纵向分割线图片
     */
    public DividerGridItemDecoration(Context context, boolean isDrawBorder, int[] ignoreItemPositions, int ItemSpan, int HDrawableId, int VDrawableId) {
        this.IsDrawBorder = isDrawBorder;
        // FIRST_ITEM = hasHeadItem ? 1 : 0;
        this.ItemSpan = ItemSpan;
        mHDivider = ContextCompat.getDrawable(context, HDrawableId);
        mVDivider = ContextCompat.getDrawable(context, VDrawableId);
        mHDividerHeight = mHDivider.getIntrinsicHeight();
        mVDividerWidth = mVDivider.getIntrinsicWidth();
        getIgnoreItemPositions(ignoreItemPositions);//获得要忽略的Item
    }

    /**
     * 第三个构造,自定横向分割线的高度和纵向分割线的宽度以及分割线的颜色
     * 自定义分割线
     *
     * @param context
     * @param IsDrawBorder        是否需要外边框
     * @param ignoreItemPositions 要求忽略的Item的数组,这个数组里面记载的position的Item不会进行边框绘制
     * @param ItemSpan            Item之间的距离
     * @param HDividerHeight      横向分割线高度
     * @param VDividerWidth       纵向分割线高度
     * @param dividerColor        分割线的颜色
     */
    public DividerGridItemDecoration(Context context, boolean IsDrawBorder, int[] ignoreItemPositions, int ItemSpan, int HDividerHeight, int VDividerWidth, int dividerColor) {
        this.IsDrawBorder = IsDrawBorder;
        this.ItemSpan = ItemSpan;
        mHDividerHeight = HDividerHeight;
        mVDividerWidth = VDividerWidth;
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

    public void refreshIgnoreItemPositions(int[] ignoreItemPositions){
        if (ignoreItemPositions != null) {//将要忽略的放到集合中去
            for (int i = 0; i < ignoreItemPositions.length; i++) {
                this.ignoreItemPositions.add(ignoreItemPositions[i]);
            }
        }
    }

    /**
     * 每个Item都会绘制右边和下面的分割线....
     * 可选的是边框线.如果ISDRAWBORDER==true的话,就绘制第一行的TOP和第一列的Left,否则最后一列的Right和最后一行的Bottom将不绘制
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();//获取子View的个数,这个方法只能后去到parent显示的View的个数,并不能获取到所有View的个数
        int totalChildCount = parent.getAdapter().getItemCount();//所有Item个数
        int spanCount = getSpanCount(parent);//列数
        //Log.e("isLastRaw", "parent.getAdapter().getItemCount()==>" + parent.getAdapter().getItemCount());
        /*for (int i = 0; i < childCount; i++) {
            //这层循环用来判断每一个Item的位置,正式情况不需要开启
            if (isFirstRaw(parent, i, spanCount, childCount)) {
                Log.e("isLastRaw", "isFirstRaw==>" + i);
            }
            if (isFirstColum(parent, i, spanCount, childCount)) {
                Log.e("isLastRaw", "isFirstColum==>" + i);
            }
            if (isLastColum(parent, i, spanCount, childCount)) {
                Log.e("isLastRaw", "isLastColum==>" + i);
            }
            if (isLastRaw(parent, i, spanCount, childCount)) {
                Log.e("isLastRaw", "isLastRaw==>" + i);
            }
        }*/
        //每次绘制之前,先找到当前显示的第一个Item的索引,然后对当前N个(parent.getChildCount())Item进行是否绘制判断
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            int firstVisibleItemPosition = ((GridLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
            //Log.e("drawLeft", "firstVisibleItemPosition===>" + firstVisibleItemPosition);
            drawBottom(c, parent, firstVisibleItemPosition, totalChildCount);//绘制每个Item的底部分割线
            drawRight(c, parent, firstVisibleItemPosition, totalChildCount);//绘每个Item的右边分割线
            drawTop(c, parent, firstVisibleItemPosition, totalChildCount);//绘制顶部分割线
            drawLeft(c, parent, firstVisibleItemPosition, totalChildCount);//绘制左边分割线
        }
    }

    /**
     * 获取列数
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    /**
     * 绘制left
     * 两种情况下需要绘制
     * 1.ItemSpan>0   默认每一个Item都会有上下左右边框,ISDRAWBORDER参数无效
     * 2.ItemSpan==0并且是第一列并且ISDRAWBORDER为true
     *
     * @param c
     * @param parent
     */
    private void drawLeft(Canvas c, RecyclerView parent, int firstVisibleItemPosition, int totalChildCount) {//绘制左边(只有第一列需要绘制)
        int childCount = parent.getChildCount();//获取子View的个数(当前可见的View的个数)
        int spanCount = getSpanCount(parent);//列数
        for (int i = 0; i < (childCount); i++) {//
            int realPosition = i + firstVisibleItemPosition;//真实的position(在所有Item中的position),区别于i,i是当前可见View中的position
            if (ignoreItemPositions.contains(realPosition) || ignoreItemPositions.contains(-(totalChildCount - realPosition))) {
                //忽略的Item不进行绘制(i+firstVisibleItemPosition是当前Item的真实position)
                //正数0表示第一个,负数-1表示倒数第一个
                continue;
            }
            if (ItemSpan > 0 || (ItemSpan == 0 && isFirstColum(parent, realPosition, spanCount, childCount) && IsDrawBorder)) {
                //两种情况下需要绘制
                final View child = parent.getChildAt(i);//这里不能用真实position,因为布局复用View个数有限,要用i
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child//拿到子View的LayoutParams
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin - mVDividerWidth;//OK
                final int right = child.getLeft() - params.leftMargin;//OK
                final int top = child.getTop() - params.topMargin - mHDividerHeight;//OK
                final int bottom = child.getBottom() + params.bottomMargin + mHDividerHeight;//OK
                if (mHDivider != null) {//如果横向分割线图片不为空就用这种方法绘制
                    mHDivider.setBounds(left, top, right, bottom);
                    mHDivider.draw(c);
                } else if (mPaint != null) {//第三种构造采用画笔的形式绘制分割线
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 绘制top
     * 两种情况下需要绘制
     * 1.ItemSpan>0   默认每一个Item都会有上下左右边框,ISDRAWBORDER参数无效
     * 2.ItemSpan==0并且是第一行并且ISDRAWBORDER为true
     *
     * @param c
     * @param parent
     */
    private void drawTop(Canvas c, RecyclerView parent, int firstVisibleItemPosition, int totalChildCount) {//绘制顶部(只有第一行需要绘制)
        int childCount = parent.getChildCount();//获取子View的个数
        int spanCount = getSpanCount(parent);//列数
        for (int i = 0; i < (childCount); i++) {//
            int realPosition = i + firstVisibleItemPosition;//真实的position(在所有Item中的position),区别于i,i是当前可见View中的position
            if (ignoreItemPositions.contains(realPosition) || ignoreItemPositions.contains(-(totalChildCount - realPosition))) {
                //忽略的Item不进行绘制(i+firstVisibleItemPosition是当前Item的真实position)
                //正数0表示第一个,负数-1表示倒数第一个
                continue;
            }
            if (ItemSpan > 0 || (ItemSpan == 0 && isFirstRaw(parent, realPosition, spanCount, childCount) && IsDrawBorder)) {
                final View child = parent.getChildAt(i);//这里不能用真实position,因为布局复用View个数有限,要用i
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child//拿到子View的LayoutParams
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;//OK
                final int right = child.getRight() + params.rightMargin + mVDividerWidth;//OK
                final int top = child.getTop() - params.topMargin - mHDividerHeight;//OK
                final int bottom = top + mHDividerHeight;//OK
                if (mHDivider != null) {//如果横向分割线图片不为空就用这种方法绘制
                    mHDivider.setBounds(left, top, right, bottom);
                    mHDivider.draw(c);
                } else if (mPaint != null) {//第三种构造采用画笔的形式绘制分割线
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 绘制Bottom
     * 三种情况下需要绘制
     * 1.ItemSpan>0   默认每一个Item都会有上下左右边框,ISDRAWBORDER参数无效
     * 2.ItemSpan==0,不是最后一行
     * 3.ItemSpan==0,最后一行但ISDRAWBORDER=true
     *
     * @param c
     * @param parent
     */
    public void drawBottom(Canvas c, RecyclerView parent, int firstVisibleItemPosition, int totalChildCount) {//绘制底部(ISDRAWBORDER为false的情况下,最后一行不绘制底部)
        int childCount = parent.getChildCount();//获取子View的个数
        int spanCount = getSpanCount(parent);//列数
        for (int i = 0; i < (childCount); i++) {//
            int realPosition = i + firstVisibleItemPosition;//真实的position(在所有Item中的position),区别于i,i是当前可见View中的position
            if (ignoreItemPositions.contains(realPosition) || ignoreItemPositions.contains(-(totalChildCount - realPosition))) {
                //忽略的Item不进行绘制(i+firstVisibleItemPosition是当前Item的真实position)
                //正数0表示第一个,负数-1表示倒数第一个
                continue;
            }
            if (ItemSpan > 0 || (ItemSpan == 0 && (!isLastRaw(parent, realPosition, spanCount, childCount))) || (ItemSpan == 0 && isLastRaw(parent, i, spanCount, childCount) && IsDrawBorder)) {
                final View child = parent.getChildAt(i);//这里不能用真实position,因为布局复用View个数有限,要用i
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child//拿到子View的LayoutParams
                        .getLayoutParams();
                final int left = child.getLeft() - params.leftMargin;//OK
                final int right = child.getRight() + params.rightMargin
                        + mVDividerWidth;//OK
                final int top = child.getBottom() + params.bottomMargin;//OK
                final int bottom = top + mHDividerHeight;//OK
                if (mHDivider != null) {//如果横向分割线图片不为空就用这种方法绘制
                    mHDivider.setBounds(left, top, right, bottom);
                    mHDivider.draw(c);
                } else if (mPaint != null) {//第三种构造采用画笔的形式绘制分割线
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 绘制Right
     * 三种情况下需要绘制
     * 1.ItemSpan>0   默认每一个Item都会有上下左右边框,ISDRAWBORDER参数无效
     * 2.ItemSpan==0,不是最后一列
     * 3.ItemSpan==0,最后一列但ISDRAWBORDER=true
     *
     * @param c
     * @param parent
     */
    public void drawRight(Canvas c, RecyclerView parent, int firstVisibleItemPosition, int totalChildCount) {//绘制右边(ISDRAWBORDER为false的情况下,最后一列不绘制右边)
        final int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);//列数
        for (int i = 0; i < (childCount); i++) {//
            int realPosition = i + firstVisibleItemPosition;//真实的position(在所有Item中的position),区别于i,i是当前可见View中的position
            if (ignoreItemPositions.contains(realPosition) || ignoreItemPositions.contains(-(totalChildCount - realPosition))) {
                //忽略的Item不进行绘制(i+firstVisibleItemPosition是当前Item的真实position)
                //正数0表示第一个,负数-1表示倒数第一个
                continue;
            }
            if (ItemSpan > 0 || (ItemSpan == 0 && (!isLastColum(parent, realPosition, spanCount, childCount))) || (ItemSpan == 0 && isLastColum(parent, i, spanCount, childCount) && IsDrawBorder)) {
                final View child = parent.getChildAt(i);//这里不能用真实position,因为布局复用View个数有限,要用i
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getTop() - params.topMargin;//OK
                final int bottom = child.getBottom() + params.bottomMargin;//OK
                final int left = child.getRight() + params.rightMargin;//OK
                final int right = left + mVDividerWidth;//OK
                if (mVDivider != null) {//如果纵向分割线图片不为空就用这种方法绘制
                    mVDivider.setBounds(left, top, right, bottom);
                    mVDivider.draw(c);
                } else if (mPaint != null) {//第三种构造采用画笔的形式绘制分割线
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 判断Item是不是在第一列
     *
     * @param parent
     * @param position    position
     * @param ColumnCount 一共有几列
     * @param childCount  一个有几个子View
     * @return
     */
    private boolean isFirstColum(RecyclerView parent, int position, int ColumnCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {//网格布局判断
            if (position % ColumnCount == 0)// 第一列(position从0开始,求于为0就是第一列)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是第一行
     *
     * @param parent
     * @param position
     * @param ColumnCount
     * @param childCount
     * @return
     */
    private boolean isFirstRaw(RecyclerView parent, int position, int ColumnCount,
                               int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (position < ColumnCount) {// position从0开始,小于列数则为第一行
                return true;
            }

        }
        return false;
    }

    /**
     * 判断Item是不是在最后一列
     *
     * @param parent
     * @param position    position
     * @param ColumnCount 一共有几列
     * @param childCount  一个有几个子View
     * @return
     */
    private boolean isLastColum(RecyclerView parent, int position, int ColumnCount,
                                int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {//网格布局判断
            if ((position + 1) % ColumnCount == 0)// 最后一列
            {
                return true;
            }
        }
        /*else if (layoutManager instanceof StaggeredGridLayoutManager) {//瀑布流布局判断
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {//瀑布流中的垂直线性
                if ((position + 1) % ColumnCount == 0)//最后一列
                {
                    return true;
                }
            } else {//瀑布流中的水平线性
                childCount = childCount - childCount % ColumnCount;
                if (position >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }*/
        return false;
    }

    /**
     * 判断是不是最后一行
     *
     * @param parent      父亲
     * @param position    要进行判断的Item的position(总position)
     * @param ColumnCount 一共有几列
     * @param childCount
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int position, int ColumnCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (childCount % ColumnCount == 0) {//如果求余==0,判断是不是第一行,不是的话就让childCount减掉  总列数个的后,position大于等于这个数的就是最后一行
                if (childCount == ColumnCount) {//这种情况下第一行就是最后一行
                    return true;
                } else {
                    childCount = childCount - ColumnCount;
                    if (position >= childCount) {
                        return true;
                    }
                }
            } else {//如果有余数,那么总数减掉这个余数,position大于等于这个结果的数就是最后一行
                childCount = childCount - childCount % ColumnCount;//如果position大于(减去掉childCount % ColumnCount的话),就是最后一行
                if (position >= childCount) {// 如果是最后一行，则不需要绘制底部
                    return true;
                }
            }

        }
        /*else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % ColumnCount;
                // 如果是最后一行，则不需要绘制底部
                if (position >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((position + 1) % ColumnCount == 0) {
                    return true;
                }
            }
        }*/
        return false;
    }

    /**
     * 获取每个Item的偏移量
     * 正常情况下,每个Item的偏移量都是一样的,等于分割线的高(宽)度+间距
     * 但是在不添加外边框的情况下,第一列没有左边框,第一行没有上边框,最后一列没有右边框,最后一行没有下边框
     * TODO 每个outRect实际上是需要相同的，因为每个item的大小的计算需要用到outRect，不要养的话会导致宽度（高度）不能平均分配，item大小不一
     * TODO 所以我们默认都只添加top，right，left就用recyclerView的paddingLeft
     * @param outRect
     * @param parent
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
        if (ignoreItemPositions.contains(itemPosition)) {//忽略的Item不进行绘制
            return;
        }
        final int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);//列数
        if (ItemSpan > 0) {//默认为添加外边框,有边距的情况下
            outRect.set(0, ItemSpan, ItemSpan, 0);
            if (isFirstRaw(parent, itemPosition, spanCount, childCount)) {//第一行
                //outRect.top = ItemSpan;
            }
            if (isFirstColum(parent, itemPosition, spanCount, childCount)) {//第一列
                //outRect.left = ItemSpan;
            }
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {//最后一行
                outRect.bottom = ItemSpan;
            }
            if (isLastColum(parent, itemPosition, spanCount, childCount)) {//最后一列
                //outRect.right = 0;
            }
        } else if (ItemSpan == 0) {//Item之间没有边距的情况下
            outRect.set(0, 0, mVDividerWidth, mHDividerHeight);//初始化right和bottom偏移量,特殊情况特殊处理
            if (!IsDrawBorder) {//不添加外边框
                if (isLastRaw(parent, itemPosition, spanCount, childCount)) {//最后一行
                    outRect.bottom = 0;
                }
                if (isLastColum(parent, itemPosition, spanCount, childCount)) {//最后一列
                    outRect.right = 0;
                }
            } else {//添加外框
                if (isFirstRaw(parent, itemPosition, spanCount, childCount)) {//第一行
                    outRect.top = mHDividerHeight;
                }
                if (isFirstColum(parent, itemPosition, spanCount, childCount)) {//第一列
                    outRect.left = mVDividerWidth;
                }
            }
        }

    }
}
