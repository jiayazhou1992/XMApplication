package com.xiaomawang.commonlib.ui.widget.recyclerview;


import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装了头部\尾部\自定义类型
 * 兼容横\纵\网格\瀑布流
 * 只需继承这个类,实现Holder类即可
 *
 */
public abstract class BaseRecyclerAdapter<T,VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 1100;//头部类型   //可能有若干个

    public static final int TYPE_NORMAL = 1200;//正常类型   //现在可通过getItemType方法自行拓展

    public static final int TYPE_LOADMORE = 1300;//到底加载

    public static final int TYPE_FOOTER = 1400;//尾部类型   //可能有若干个

    public static final int TYPE_NODATA = 1500;//无数据类型

    protected boolean loadMore = false;//是否需要到底加载

    protected LoadMoreLayout loadMoreView;//加载更多控件

    protected List<T> mDatas = new ArrayList<>();

    protected SparseArray<View> mHeaderViews = new SparseArray<>();//头部

    protected View mFooterView;//尾部

    protected OnItemClickListener mClickListener;

    protected OnItemLongClickListener mLongClickListener;

    private int extraItemCount = 0;//除了正常数据外还有几条额外的Item(头部\尾部\加载更多\)


    public void setOnItemClickListener(OnItemClickListener li) {
        mClickListener = li;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener li) {
        mLongClickListener = li;
    }

    public void addHeaderView(View headerView) {
        addHeaderView(headerView, true);
    }

    public void addHeaderView(View headerView, boolean notify) {
        mHeaderViews.put(mHeaderViews.size(), headerView);
        if (notify) {
            notifyDataSetChanged();//这样写不太合理，先这样吧
        }
    }

    public void addFooterView(View footerView) {
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    /**
     * 同时添加header和foot防止多次刷新导致崩溃
     */
    public void addHeaderAndFooter(View headerView, View footerView) {
        mHeaderViews.put(mHeaderViews.size(), headerView);
        mFooterView = footerView;
        notifyDataSetChanged();
    }

    /**
     * 这个方法在设置加载更多监听器的时候会调用
     * 为了防止数据先于监听器设置,在开启到底加载后,要刷新一遍适配器
     *
     * @param loadMore
     */
    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
        notifyDataSetChanged();
    }

    public View getHeaderView(int key) {
        return mHeaderViews.get(key);
    }

    /**
     * 往集合的指定位置中添加一条数据,并且刷新
     * @param location
     * @param data
     */
    public void add(int location, T data) {
        if (data == null) return;
        int itemCount = mDatas.size() - location -1;
        mDatas.add(location, data);
        notifyItemInserted(location);
        notifyItemRangeChanged(location + 1, itemCount);
    }

    public void add(T data) {
        if (data == null) return;
        int start = mHeaderViews.size() + mDatas.size();
        mDatas.add(data);
        notifyItemRangeChanged(start, 1);
    }

    public void addDatas(List<T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        int start = mHeaderViews.size() + mDatas.size();
        mDatas.addAll(datas);
        notifyItemRangeChanged(start, datas.size());
    }

    public void addDatas2(List<? extends T> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        int start = mHeaderViews.size() + mDatas.size();
        mDatas.addAll(datas);
        notifyItemRangeChanged(start, datas.size());
    }

    public void setDatas(List<T> datas) {
        int oldCount = mDatas.size();
        int newCount = datas == null ? 0 : datas.size();
        mDatas.clear();
        if (datas != null) {
            mDatas.addAll(datas);
        }
        if (mHeaderViews.size() > 0){
            if (oldCount > newCount){
                notifyItemRangeChanged(mHeaderViews.size(), newCount);
                notifyItemRangeRemoved(newCount + mHeaderViews.size(), oldCount - newCount);
            }else if (oldCount < newCount){
                notifyItemRangeChanged(mHeaderViews.size(), oldCount);
                notifyItemRangeInserted(oldCount + mHeaderViews.size(), newCount - oldCount);
            }else {
                notifyItemRangeChanged(mHeaderViews.size(), mDatas.size());
            }
        }else {
            notifyDataSetChanged();
        }
    }

    public void setDatas2(List<? extends T> datas) {
        int oldCount = mDatas.size();
        int newCount = datas == null ? 0 : datas.size();
        mDatas.clear();
        if (datas != null) {
            mDatas.addAll(datas);
        }
        if (mHeaderViews.size() > 0){
            if (oldCount > newCount){
                notifyItemRangeChanged(mHeaderViews.size(), mDatas.size());
                notifyItemRangeRemoved(newCount + mHeaderViews.size(), oldCount - newCount);
            }else if (oldCount < newCount){
                notifyItemRangeChanged(mHeaderViews.size(), newCount);
                notifyItemRangeInserted(oldCount + mHeaderViews.size(), newCount - oldCount);
            }else {
                notifyItemRangeChanged(mHeaderViews.size(), mDatas.size());
            }
        }else {
            notifyDataSetChanged();
        }
    }

    public void setDataDiff(List<T> datas){
        if (datas == null){
            mDatas = new ArrayList<>();
            return;
        }
        //mDatas.clear();
        mDatas = new ArrayList<>(datas);
    }

    public void remove(int position){
        if (position >= mDatas.size()) return;
        mDatas.remove(position);
        notifyItemRemoved(position + mHeaderViews.size());
        notifyItemRangeChanged(position  + mHeaderViews.size(), mDatas.size() - 1 - position);
    }


    public List<T> getDatas() {
        return mDatas;
    }

    /**
     * 清空数据
     */
    public void clearDatas() {
        hideLoadMore();
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * 清除头部和尾部view
     */
    public void clearHeadOrFootView() {
        mHeaderViews.clear();
        mFooterView = null;
        notifyDataSetChanged();
    }

    /**
     * 清除底部view
     */
    public void removeFooterView(boolean notify) {
        mFooterView = null;
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 清除头部view
     */
    public void removeHeaderView(boolean notify) {
        mHeaderViews.clear();
        if (notify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 在外部拿到适配器对象后提供这个方法来获取适配器的数据
     *
     * @param position
     * @return
     */
    public T getItemData(int position) {
        if (mDatas == null || mDatas.size() == 0 || position >= mDatas.size()) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        extraItemCount = 0;//额外的Item数量    头部 尾部  到底加载  都要算进去
        extraItemCount += mHeaderViews.size();//添加了头部
        if (mFooterView != null){
            extraItemCount++;//添加了尾部
        }
        if (loadMore){
            extraItemCount++;//添加了到底加载
        }
        return mDatas.size() + extraItemCount;
    }

    /**
     * 获取真正的itemCount
     *
     * @return
     */
    public int getRealItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        //这个方法还不够完善,需要改进
        if (loadMore && position + 1 == getItemCount()) {//需要到底加载的情况下
            return TYPE_LOADMORE;
        }
        if (mFooterView != null) {//需要尾部的话,进行尾巴Position判断
            if (loadMore && position + 2 == getItemCount()) {//有到底加载的情况下倒数第二个是尾巴
                return TYPE_FOOTER;
            } else if (position + 1 == getItemCount()) {//最后一个是尾巴
                return TYPE_FOOTER;
            }
        }
        if (mHeaderViews.get(position) != null) {//需要头部的话
            return TYPE_HEADER + position;
        }
        return getItemType(position);//正常/计算类型(无需给出真实的position)
    }

    /**
     * 这个方法供子类重写,用来计算Item的类型
     */
    public int getItemType(int position) {
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        DevLogger.iTag("adapter", " parent instanceof " + (parent instanceof RecyclerView));
        if (viewType >= TYPE_HEADER && viewType < TYPE_HEADER + mHeaderViews.size()) { //1100-1199属于头部
            HeaderLayout headerLayout = new HeaderLayout(parent.getContext());
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerLayout.setLayoutParams(params);
            View headerView = mHeaderViews.get(viewType - TYPE_HEADER);
            if (headerView.getParent() != null){
                final ViewGroup parentView = ((ViewGroup)headerView.getParent());
                parentView.removeView(headerView);
                parentView.post(new Runnable() {
                    @Override
                    public void run() {
                        parentView.requestLayout();
                    }
                });
            }
            headerLayout.addView(headerView);
            return new HeadHolder(headerLayout);

        } else if (viewType == TYPE_FOOTER) {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            frameLayout.setLayoutParams(params);
            if (mFooterView.getParent() != null){
                final ViewGroup parentView = ((ViewGroup)mFooterView.getParent());
                parentView.removeView(mFooterView);
                parentView.post(new Runnable() {
                    @Override
                    public void run() {
                        parentView.requestLayout();
                    }
                });
            }
            frameLayout.addView(mFooterView);
            return new FootHolder(frameLayout);

        } else if (viewType == TYPE_LOADMORE) {//这里直接写死了加载更多的布局,也可以将这部用抽象方法开放出来给子类自定义
            loadMoreView = new LoadMoreLayout(parent.getContext());
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadMoreView.setLayoutParams(params);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loadmore, parent, false);
            if (view.getParent() != null){
                ((ViewGroup)view.getParent()).removeView(view);
            }
            loadMoreView.addView(view);
            return new LoadMoreHoler(loadMoreView);

        }
        return onCreate(parent, viewType);//用来创建不同类型的Holder
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            int pos = getRealPosition(holder);
            T data = (pos > -1 && pos < mDatas.size()) ? mDatas.get(pos) : null;
            int itemViewType = getItemViewType(position);//获取到Item的类型

            if (!onBindPayloads((VH) holder, pos, itemViewType, data,payloads)){
                onBindViewHolder(holder,position);
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);//获取到Item的类型

        if (itemViewType >= TYPE_HEADER && itemViewType < TYPE_HEADER + mHeaderViews.size()) {
            //复用
            HeadHolder headHolder = (HeadHolder) viewHolder;
            HeaderLayout headerLayout = (HeaderLayout) headHolder.itemView;
            if (headerLayout.getChildCount() == 0){
                View headerView = mHeaderViews.get(position);
                if (headerView.getParent() != null){
                    ((ViewGroup)headerView.getParent()).removeView(headerView);
                }
                headerLayout.addView(headerView);
            }else {
                View headerView = mHeaderViews.get(position);
                if (headerView != headerLayout.getChildAt(0)) {
                    headerLayout.removeViewAt(0);
                    if (headerView.getParent() != null) {
                        ((ViewGroup) headerView.getParent()).removeView(headerView);
                    }
                    headerLayout.addView(headerView);
                }
            }
            return;
        } else if (itemViewType == TYPE_FOOTER) {
            //复用
            FootHolder footHolder = (FootHolder) viewHolder;
            FrameLayout frameLayout = (FrameLayout) footHolder.itemView;
            if (frameLayout.getChildCount() == 0){
                if (mFooterView.getParent() != null){
                    ((ViewGroup)mFooterView.getParent()).removeView(mFooterView);
                }
                frameLayout.addView(mFooterView);
            }
            return;
        } else if (itemViewType == TYPE_LOADMORE) {//加载更多
            //加载更多布局的绑定(暂时不需要)
            return;
        } else {//正常类型,自行拓展的类型都在这里获取到Holder
            final int pos = getRealPosition(viewHolder);
            final T data = mDatas.get(pos);

            onBind((VH) viewHolder, pos, itemViewType, data);

            if (mClickListener != null) {//设置点击事件要放到绑定完控件之后
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int click_pos = getRealPosition(viewHolder);
                        mClickListener.onItemClick(click_pos, mDatas.get(click_pos));
                    }
                });
            }

            if (mLongClickListener != null) {//设置Item的长按事件
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int click_pos = getRealPosition(viewHolder);
                        mLongClickListener.onItemLongClick(v.getContext(), click_pos, mDatas.get(click_pos));
                        return true;
                    }
                });
            }
        }
    }

    /**
     * 在这里解决有Head的时候GridLayout中头部Item所占的列数
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {//获取不同类型的Item所占的列数
                    int itemViewType = getItemViewType(position);

                    if (itemViewType >= TYPE_HEADER && itemViewType < TYPE_HEADER + mHeaderViews.size()) {
                        return gridManager.getSpanCount();
                    } else if (itemViewType == TYPE_LOADMORE) {
                        return gridManager.getSpanCount();
                    } else if (itemViewType == TYPE_FOOTER) {
                        return gridManager.getSpanCount();
                    } else if (itemViewType == TYPE_NODATA) {
                        return gridManager.getSpanCount();
                    } else {
                        return getNormalSpan(position);//获取普通Item的所占格子数量
                    }
                }
            });
        }
    }

    /**
     * 获取普通Item的所占格子数量
     */
    protected int getNormalSpan(int position) {
        return 1;
    }

    /**
     * 在这里解决瀑布流的时候头部是否占满整行
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            //p.setFullSpan(holder.getLayoutPosition() == 0);//这样会导致一个bug,当瀑布流Item超出屏幕宽度的时候,会导致其他Item也为full
            p.setFullSpan(holder.getLayoutPosition() == 0 ? true : false);
        }
    }

    /**
     * 获取真实的position
     * @param holder
     * @return
     */
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return position - mHeaderViews.size();
    }

    /**
     * @return 获取真实的position（mDatas中的位置）
     */
    public int getRealPosition(int position) {
        return position - mHeaderViews.size();
    }

    /**
     * 获取item所在的位置（包括headerView）
     * @param data_pos
     * @return
     */
    public int getViewItemPostion(int data_pos){
        return data_pos + mHeaderViews.size();
    }

    /**
     * 抽象方法创建正常的ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    /**
     * 抽象方法绑定ViewHolder
     * @param viewHolder
     * @param realPosition in datas
     * @param data
     */
    public abstract void onBind(VH viewHolder, int realPosition, int itemViewType, T data);

    public boolean onBindPayloads(VH viewHolder, int realPosition, int itemViewType, T data, List<Object> payloads){
        return false;
    }

    /**
     * 隐藏到底加载
     */
    public void hideLoadMore() {
        if (loadMoreView != null) {
            loadMoreView.setLoading(false);
            loadMoreView.setNoMore(false);

            loadMoreView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示到底加载
     */
    public void showLoadMore() {
        if (loadMoreView != null) {
            loadMoreView.setLoading(true);
            loadMoreView.setNoMore(false);

            loadMoreView.setVisibility(View.VISIBLE);
            loadMoreView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            TextView textView = loadMoreView.findViewById(R.id.tv_status_msg);
            textView.setText("加载中...");
        }
    }

    /**
     * 没有更多
     */
    public void noMore(){
        if (loadMoreView != null){
            loadMoreView.setLoading(false);
            loadMoreView.setNoMore(true);

            loadMoreView.setVisibility(View.VISIBLE);
            loadMoreView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            TextView textView = loadMoreView.findViewById(R.id.tv_status_msg);
            textView.setText("没有更多了");
        }
    }

    public void noMore(String msg){
        if (loadMoreView != null){
            loadMoreView.setLoading(false);
            loadMoreView.setNoMore(true);

            loadMoreView.setVisibility(View.VISIBLE);
            loadMoreView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            TextView textView = loadMoreView.findViewById(R.id.tv_status_msg);
            textView.setText(msg);
        }
    }



    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(Context context, int position, T data);
    }

    /**
     * 头部控件的Holder(临时)
     */
    protected class HeadHolder extends RecyclerView.ViewHolder {
        public HeadHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 头部控件的Holder(临时)
     */
    protected class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 加载更多的Holder
     */
    protected class LoadMoreHoler extends RecyclerView.ViewHolder {
        public LoadMoreHoler(View itemView) {
            super(itemView);
            itemView.setVisibility(View.INVISIBLE);
        }
    }
}
