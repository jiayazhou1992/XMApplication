package com.xiaomawang.commonlib.ui.widget.recyclerview.dragsortrecyclerView;

import android.graphics.Canvas;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


import com.xiaomawang.commonlib.ui.widget.recyclerview.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by jiayazhou on 2017/10/28.
 */

public class ItemTouchHelperImpl extends ItemTouchHelper.Callback {
    private static final String TAG = "ItemTouchHelperImpl";

    public enum FLAG{

        FLAG_SWIPE,
        FLAG_DRAG,
        FLAG_NONE,
        FLAG_ALL
    }


    private List tList;
    private RecyclerView recyclerView;
    private OnSortedCutedListener onSortedCutedListener;
    private OnChidDrawCallback chidDrawCallback;
    private int swipeFlag = ItemTouchHelper.LEFT;       //滑动方向
    private int dragFlag = 0;                           //拖动方向
    private boolean isSetDrag = false;                  //是否设置过drag方向
    private FLAG flag = FLAG.FLAG_ALL;                       //动作开关
    private boolean allowDiffHolderAlternate = false;   //是否允许不同Type的Holder进行交换位置(正常情况下不允许,除非有特殊需要)
    private boolean allowHeadHolderAlternate = false;   //是否允许与头部进行交互(正常情况下不允许,除非有特殊需要)
    private boolean allowTailHolderAlternate = false;   //是否允许与尾巴进行交互(正常情况下不允许,除非有特殊需要)
    private int headCount = 0;                          //头部控件数量
    private int tailCount = 0;                          //尾巴控件数量
    private int headOffset = 0;                         //头部偏移量
    private int tailOffset = 0;                         //尾部偏移量



    public ItemTouchHelperImpl(){
        this(0, FLAG.FLAG_ALL);
    }

    public ItemTouchHelperImpl(FLAG flag){
        this(0,flag);
    }

    /**
     * @param headCount 头部控件的数量,没有头部就传0
     */
    public ItemTouchHelperImpl(int headCount,FLAG flag) {
        this(0,0,false,0,0,false,false,flag);
    }

    /**
     * @param headCount                头部的数量
     * @param headOffset               头部偏移量(item代替头部的个数)
     * @param allowHeadHolderAlternate 是否允许与头部进行交互
     * @param tailCount                尾巴的数量
     * @param tailOffset               尾巴偏移量(item代替尾巴的个数)
     * @param allowTailHolderAlternate 是否允许与尾巴进行交互
     * @param allowDiffHolderAlternate 是否允许不同类型的holder进行交互(默认关闭)
     */
    public ItemTouchHelperImpl(int headCount, int headOffset, boolean allowHeadHolderAlternate, int tailCount, int tailOffset, boolean allowTailHolderAlternate, boolean allowDiffHolderAlternate,FLAG flag) {
        this.headCount = headCount;
        this.tailCount = tailCount;
        this.headOffset = headOffset;
        this.tailOffset = tailOffset;
        this.allowDiffHolderAlternate = allowDiffHolderAlternate;
        this.allowHeadHolderAlternate = allowHeadHolderAlternate;
        this.allowTailHolderAlternate = allowTailHolderAlternate;
        this.flag = flag;
    }


    /**
     * @param tList 必须与adapter的data引用相同
     */
    /*public ItemTouchHelperImpl(List tList) {
        this.tList = tList;
    }*/

    /**
     * 设置数据
     */
    /*public void setData(List tList) {
        this.tList = tList;
    }*/

    /**
     * 获取活动的标识
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (this.recyclerView == null) {
            this.recyclerView = recyclerView;
            tList = ((BaseRecyclerAdapter) recyclerView.getAdapter()).getDatas();
        }
        if (headCount != 0 && (viewHolder.getAdapterPosition() < headCount)) {
            //屏蔽掉头部的拖拽事件
            return 0;
        }
        //注意:tList.size()里面涵盖了用来顶替尾巴的Item(偏移量)
        if (tailCount != 0 && (viewHolder.getAdapterPosition() >= (tList.size() + headCount - tailOffset))) {
            //屏蔽尾巴的拖拽事件
            return 0;
        }
        // 初始默认 拖动方向过滤
        if (!isSetDrag) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                // GridView 样式四个方向都可以
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.LEFT |
                        ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT;
            } else {
                // ListView 样式不支持左右，只支持上下
                dragFlag = ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN;
            }
        }
        if (flag == FLAG.FLAG_ALL) {//全部开启了

        }
        if (flag == FLAG.FLAG_NONE) {//全部关闭了
            dragFlag = 0;
            swipeFlag = 0;
        }
        if (flag == FLAG.FLAG_DRAG) {//关闭了侧滑
            swipeFlag = 0;
        }
        if (flag == FLAG.FLAG_SWIPE) {//关闭了拖拽
            dragFlag = 0;
        }
        return makeMovementFlags(getDragFlag(), getSwipeFlag());
    }


    /**
     * 拖拽item替换的时候移动监听
     * 这里需要额外注意的是在有头部和尾巴的情况下,Item的position和list的position就对应不起来了.这种情况需要手动计算
     *
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (tList == null || tList.size() < 2) return false;
        if (viewHolder.getClass().getName() != target.getClass().getName()) {//不同类暂
            if (!allowDiffHolderAlternate) {//如果不允许不同类型交互的话就直接返回

                return false;
            }
        } else {

        }

        // 获取原来的位置 注意 adapter 可能添加头部，尾部的情况
        int fromDataPosition = ((BaseRecyclerAdapter) recyclerView.getAdapter()).getRealPosition(viewHolder.getAdapterPosition());
        int fromItemPosition = viewHolder.getAdapterPosition();
        // 得到目标的位置
        int targetDataPosition = ((BaseRecyclerAdapter) recyclerView.getAdapter()).getRealPosition(target.getAdapterPosition());
        int targetItemPosition = target.getAdapterPosition();
        if (headCount > 0 && !allowHeadHolderAlternate) {//有头部且不允许和头部进行交互的情况下
            if (target.getAdapterPosition() < headCount) {
                //屏蔽掉头部的交互事件
                return false;
            }
        }
        //注意:tList.size()里面涵盖了用来顶替尾巴的Item(偏移量)
        if (tailCount > 0 && !allowTailHolderAlternate) {//有尾巴且不允许和尾巴进行交互的情况下
            if (target.getAdapterPosition() >= (tList.size() + headCount - tailOffset)) {
                //屏蔽尾巴的交互事件
                return false;
            }
        }

        Object o = tList.get(fromDataPosition);
        tList.remove(fromDataPosition);
        tList.add(targetDataPosition, o);
        //Collections.swap(tList, fromDataPosition, targetDataPosition);
        recyclerView.getAdapter().notifyItemMoved(fromItemPosition, targetItemPosition);
        if (onSortedCutedListener != null) {
            onSortedCutedListener.onSorted(viewHolder, target, tList.get(fromDataPosition), tList.get(targetDataPosition), targetDataPosition, fromDataPosition);
        }
        return true;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

    }

    /*默认侧滑完成的回调*/
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (tList == null || tList.size() < 1) return;

        int realPostion = ((BaseRecyclerAdapter) recyclerView.getAdapter()).getRealPosition(viewHolder.getAdapterPosition());

        if (onSortedCutedListener != null) {

            onSortedCutedListener.onSwiped(viewHolder, tList.get(realPostion), realPostion);
        }else {
            //默认
            tList.remove(realPostion);
            recyclerView.getAdapter().notifyItemRemoved(realPostion);
            recyclerView.getAdapter().notifyItemRangeChanged(realPostion, tList.size() - 1);
        }
    }

    /*选中item回调*/
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            /*if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                Toast.makeText(recyclerView.getContext(), "拖动改变位置", Toast.LENGTH_SHORT).show();*/
            if (onSortedCutedListener != null)
                onSortedCutedListener.onSelectedChanged(viewHolder);

        }
    }

    /*恢复item回调*/
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (onSortedCutedListener != null)
            onSortedCutedListener.onClearView(recyclerView, viewHolder);

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //DevLogger.iTag(TAG,"onChildDraw dx---->"+dX+"dy----->"+dY);
        if (chidDrawCallback != null) {
            chidDrawCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //DevLogger.iTag(TAG,"onChildDrawOver dx---->"+dX+"dy----->"+dY);
        if (chidDrawCallback != null){
            chidDrawCallback.onChildDrawOver(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
        }else {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }




    /*----------------------api------------------------*/

    public boolean isAllowDiffHolderAlternate() {
        return allowDiffHolderAlternate;
    }

    public void setAllowDiffHolderAlternate(boolean allowDiffHolderAlternate) {
        this.allowDiffHolderAlternate = allowDiffHolderAlternate;
    }

    public boolean isAllowHeadHolderAlternate() {
        return allowHeadHolderAlternate;
    }

    public void setAllowHeadHolderAlternate(boolean allowHeadHolderAlternate) {
        this.allowHeadHolderAlternate = allowHeadHolderAlternate;
    }

    public boolean isAllowTailHolderAlternate() {
        return allowTailHolderAlternate;
    }

    public void setAllowTailHolderAlternate(boolean allowTailHolderAlternate) {
        this.allowTailHolderAlternate = allowTailHolderAlternate;
    }

    /** 设置移动类型
     * @param flag
     */
    public ItemTouchHelperImpl setFlag(FLAG flag) {
        this.flag = flag;
        return this;
    }

    /**
     * 设置拖拽方向
     * @param dragFlag
     * @return
     */
    public ItemTouchHelperImpl setDragFlag(int dragFlag) {
        this.dragFlag = dragFlag;
        isSetDrag = true;
        return this;
    }

    /** 设置滑动方向 默认可以左滑 ItemTouchHelper.LEFT
     * @param swipeFlag
     * @return
     */
    public ItemTouchHelperImpl setSwipeFlag(int swipeFlag) {
        this.swipeFlag = swipeFlag;
        return this;
    }

    public int getDragFlag() {
        return dragFlag;
    }

    public int getSwipeFlag() {
        return swipeFlag;
    }

    /**设置拖动排序和滑动删除监听
     * @param onSortedCutedListener
     * @return
     */
    public ItemTouchHelperImpl setOnSortedCutedListener(OnSortedCutedListener onSortedCutedListener) {
        this.onSortedCutedListener = onSortedCutedListener;
        return this;
    }

    /**
     * 设置childview绘制回掉
     * @param chidDrawCallback
     * @return
     */
    public ItemTouchHelperImpl setOnChidDrawCallback(OnChidDrawCallback chidDrawCallback) {
        this.chidDrawCallback = chidDrawCallback;
        return this;
    }

    /**
     * @param <T> 数据对象类型
     */
    public abstract static class OnSortedCutedListener<T> {
        //拖动结束
        public void onSorted(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target, T newItem, T oldItem, int newPostoin, int oldPostion){

        }

        //删除结束
        public void onSwiped(RecyclerView.ViewHolder target, T item, int postion){

        }

        //选中item后回调
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder){

        }

        //操作结束恢复view调
        public void onClearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){

        }
    }


    /**
     * 侧滑完成回调
     */
    public interface OnSwipedCallback{
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    }


    /**
     * 绘制回调
     */
    public interface OnChidDrawCallback {
        /**
         * @param c
         * @param recyclerView
         * @param viewHolder
         * @param dX
         * @param dY
         * @param actionState       拖拽或滑动
         * @param isCurrentlyActive true是用户滑动的行为，fasle是动画行为
         */
        //自定义view变化
        void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);

        void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive);
    }
}
