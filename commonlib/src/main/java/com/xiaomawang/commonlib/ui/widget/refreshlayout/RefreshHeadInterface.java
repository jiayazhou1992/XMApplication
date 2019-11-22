package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.view.View;

/**
 * Created by jiayazhou on 2017/11/9.
 */

public interface RefreshHeadInterface {

    View getHead();

    int[] getRang();

    void pullDown(int offset);

    void refresh();

    void refreshComplete();

}
