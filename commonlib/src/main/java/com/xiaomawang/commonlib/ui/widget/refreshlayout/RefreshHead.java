package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.view.View;

/**
 * Created by jiayazhou on 2017/11/9.
 */

public interface RefreshHead {
    View getHead();
    int getHeight();
    int getWidth();

    /**
     * 正在刷新
     */
    void refresh();
    void refreshComplete();

    void scrollListener(int dy);

    /**
     * @param dy 有效下拉距离
     * @return
     */
    int pullDown(int dy);

    /**
     * @return 是否活动到最高限度
     */
    boolean isBottleneck();

    /**
     * @return 是否正在活动
     */
    boolean isLive();

    /**
     * @return 0：默认布局格式，不调用pulldown，1：覆盖式
     */
    int getFixType();
}
