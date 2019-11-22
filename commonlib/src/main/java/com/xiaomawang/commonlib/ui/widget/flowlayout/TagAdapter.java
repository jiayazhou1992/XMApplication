package com.xiaomawang.commonlib.ui.widget.flowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TagAdapter<T> {
    private static final String TAG = "TagAdapter";

    private List<T> mTagDatas = new ArrayList<>();
    private OnDataChangedListener mOnDataChangedListener;

    private HashSet<Integer> mCheckedPosList = new HashSet<>();

    public TagAdapter() {
    }

    public TagAdapter(List<T> datas) {
        mTagDatas = datas;
    }

    @Deprecated
    public TagAdapter(T[] datas) {
        mTagDatas = new ArrayList<>(Arrays.asList(datas));
    }

    interface OnDataChangedListener {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener) {
        mOnDataChangedListener = listener;
    }

    public void setSelectedList(int... poses) {
        Set<Integer> set = new HashSet<>();
        for (int pos : poses) {
            set.add(pos);
        }
        setSelectedList(set);
    }

    public void setSelectedList(Set<Integer> set) {
        mCheckedPosList.clear();
        if (set != null) {
            mCheckedPosList.addAll(set);
        }
        notifyDataChanged();
    }

    public void setPreCheckedList(int... poses) {
        HashSet<Integer> set = new HashSet<>();
        for (int pos : poses) {
            set.add(pos);
        }
        setPreCheckedList(set);
    }

    public void setPreCheckedList(HashSet<Integer> mCheckedPosList) {
        if (this.mCheckedPosList != null) {
            this.mCheckedPosList.clear();
        }
        this.mCheckedPosList.addAll(mCheckedPosList);
    }

    public HashSet<Integer> getPreCheckedList() {
        return mCheckedPosList;
    }

    public void setDatas(List<T> datas){
        if (mTagDatas != null) {
            mTagDatas.clear();
            mTagDatas.addAll(datas);
        }else {
            mTagDatas = datas;
        }
        notifyDataChanged();
    }

    public List<T> getDatas(){
        return mTagDatas;
    }

    public List<T> getCheckedDatas(){
        List<T> tList = new ArrayList<>();
        for (Integer integer : mCheckedPosList){
            tList.add(mTagDatas.get(integer));
        }
        return tList;
    }

    public int getCount() {
        return mTagDatas == null ? 0 : mTagDatas.size();
    }

    public T getItem(int position) {
        return mTagDatas.get(position);
    }

    @Deprecated
    public abstract View getView(FlowLayout parent, int position, T t);

    public View getView2(FlowLayout parent, int position, T t, View cycleView){
        return cycleView;
    }

    public void onSelected(int position, View view){
        mCheckedPosList.add(position);
    }

    public void unSelected(int position, View view){
        mCheckedPosList.remove(position);
    }

    public boolean setSelected(int position, T t) {
        return false;
    }

    public void notifyDataChanged() {
        if (mOnDataChangedListener != null)
            mOnDataChangedListener.onChanged();
    }
}
