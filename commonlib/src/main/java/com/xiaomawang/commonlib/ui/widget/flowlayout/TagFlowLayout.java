package com.xiaomawang.commonlib.ui.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.util.Pools;

import com.xiaomawang.commonlib.R;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhy on 15/9/10.
 */
public class TagFlowLayout extends FlowLayout implements TagAdapter.OnDataChangedListener {
    private static final String TAG = "TagFlowLayout";
    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";

    private Pools.Pool<TagView> tagViewPool = new Pools.SynchronizedPool(20);
    private Pools.Pool<View> viewPool = new Pools.SynchronizedPool(20);

    private TagAdapter mTagAdapter;

    private boolean itemTouchCheckable;//是否可以选择
    private boolean cancelSelectable;//是否可以取消选中
    private int mSelectedMax = -1;//-1为不限制数量

    private Set<Integer> mSelectedViewPosition = new HashSet<Integer>();

    private OnSelectListener mOnSelectListener;
    private OnTagClickListener mOnTagClickListener;

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        itemTouchCheckable = ta.getBoolean(R.styleable.TagFlowLayout_itemTouchCheckable, true);
        cancelSelectable = ta.getBoolean(R.styleable.TagFlowLayout_cancelSelectable, true);
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1);
        ta.recycle();
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() == View.GONE) {
                continue;
            }
            if (tagView.getTagView().getVisibility() == View.GONE) {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void removeAllViews() {
        for (int i = 0; i < getChildCount(); i++) {
            TagView child = (TagView) getChildAt(i);
            tagViewPool.release(child);
            viewPool.release(child.getChildAt(0));
            child.removeAllViews();
        }
        super.removeAllViews();
    }

    public void setAdapter(TagAdapter adapter) {
        mTagAdapter = adapter;
        mTagAdapter.setOnDataChangedListener(this);
        mSelectedViewPosition.clear();
        changeAdapter();
    }

    @Override
    public void onChanged() {
        mSelectedViewPosition.clear();
        changeAdapter();
    }

    private void changeAdapter() {
        removeAllViews();
        TagAdapter adapter = mTagAdapter;
        TagView tagViewContainer = null;
        HashSet preCheckedList = mTagAdapter.getPreCheckedList();
        for (int i = 0; i < adapter.getCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));
            if (tagView == null) {
                tagView = adapter.getView2(this, i, adapter.getItem(i), viewPool.acquire());
            }
            tagView.setDuplicateParentStateEnabled(true);//复制父布局的sate

            tagViewContainer = tagViewPool.acquire();
            if (tagViewContainer == null){
                tagViewContainer = new TagView(getContext());

                //如果都是新的view替换一下layoutParams
                if (tagView.getLayoutParams() != null) {
                    tagViewContainer.setLayoutParams(tagView.getLayoutParams());

                } else {
                    ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(dip2px(getContext(), 5), dip2px(getContext(), 5), dip2px(getContext(), 5), dip2px(getContext(), 5));
                    tagViewContainer.setLayoutParams(lp);
                }
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                tagView.setLayoutParams(lp);
            }
            tagViewContainer.setChecked(false);
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);

            if (preCheckedList.contains(i)) {
                setChildChecked(i, tagViewContainer);
            }

            if (mTagAdapter.setSelected(i, adapter.getItem(i))) {
                setChildChecked(i, tagViewContainer);
            }
            tagView.setClickable(false);
            final TagView finalTagViewContainer = tagViewContainer;
            final int position = i;
            tagViewContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doSelect(finalTagViewContainer, position);
                    if (mOnTagClickListener != null) {
                        mOnTagClickListener.onTagClick(finalTagViewContainer, position, TagFlowLayout.this);
                    }
                }
            });
            tagViewContainer.setClickable(itemTouchCheckable);
        }
        mSelectedViewPosition.addAll(preCheckedList);
    }

    public void setMaxSelectCount(int count) {
        if (mSelectedViewPosition.size() > count) {
            Log.w(TAG, "you has already select more than " + count + " views , so it will be clear .");
            mSelectedViewPosition.clear();
        }
        mSelectedMax = count;
    }

    public Set<Integer> getSelectedList() {
        return new HashSet<Integer>(mSelectedViewPosition);
    }

    private void setChildChecked(int position, TagView view) {
        view.setChecked(true);
        mTagAdapter.onSelected(position, view.getTagView());
    }

    private void setChildUnChecked(int position, TagView view) {
        view.setChecked(false);
        mTagAdapter.unSelected(position, view.getTagView());
    }

    private void doSelect(TagView child, int position) {
        if (!child.isChecked()) {
            //处理max_select=1的情况
            if (mSelectedMax == 1 && mSelectedViewPosition.size() == 1) {
                Iterator<Integer> iterator = mSelectedViewPosition.iterator();
                Integer preIndex = iterator.next();
                TagView pre = (TagView) getChildAt(preIndex);
                setChildUnChecked(preIndex, pre);
                setChildChecked(position, child);

                mSelectedViewPosition.remove(preIndex);
                mSelectedViewPosition.add(position);
            } else {
                if (mSelectedMax > 0 && mSelectedViewPosition.size() >= mSelectedMax) {
                    return;
                }
                setChildChecked(position, child);
                mSelectedViewPosition.add(position);
            }

            if (mOnSelectListener != null) {
                mOnSelectListener.onSelected(new HashSet<>(mSelectedViewPosition));
            }
        } else {
            if (cancelSelectable) {
                setChildUnChecked(position, child);
                mSelectedViewPosition.remove(position);
            }
        }
    }

    public TagAdapter getAdapter() {
        return mTagAdapter;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState());

        String selectPos = "";
        if (mSelectedViewPosition.size() > 0) {
            for (int key : mSelectedViewPosition) {
                selectPos += key + "|";
            }
            selectPos = selectPos.substring(0, selectPos.length() - 1);
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            String mSelectPos = bundle.getString(KEY_CHOOSE_POS);
            if (!TextUtils.isEmpty(mSelectPos)) {
                String[] split = mSelectPos.split("\\|");
                for (String pos : split) {
                    int index = Integer.parseInt(pos);
                    mSelectedViewPosition.add(index);

                    TagView tagView = (TagView) getChildAt(index);
                    if (tagView != null) {
                        setChildChecked(index, tagView);
                    }
                }

            }
            super.onRestoreInstanceState(bundle.getParcelable(KEY_DEFAULT));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*while (tagViewPool.acquire() != null){
            tagViewPool.acquire();
        }
        while (viewPool.acquire() != null) {
            viewPool.acquire();
        }*/
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    public interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet);
    }

    public interface OnTagClickListener {
        boolean onTagClick(View view, int position, FlowLayout parent);
    }
}
