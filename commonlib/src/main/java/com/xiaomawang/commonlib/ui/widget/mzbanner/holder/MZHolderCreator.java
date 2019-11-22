package com.xiaomawang.commonlib.ui.widget.mzbanner.holder;

public interface MZHolderCreator<VH extends MZViewHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
    public VH createViewHolder();
}
