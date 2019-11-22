package com.xiaomawang.commonlib.ui.widget.pickers.common;


import com.xiaomawang.commonlib.ui.widget.pickers.listeners.OnItemPickListener;
import com.xiaomawang.commonlib.ui.widget.pickers.widget.WheelView;

final public class OnItemPickedRunnable implements Runnable {
    private final WheelView wheelView;
    private OnItemPickListener onItemPickListener;

    public OnItemPickedRunnable(WheelView wheelView, OnItemPickListener onItemPickListener) {
        this.wheelView = wheelView;
        this.onItemPickListener = onItemPickListener;
    }

    @Override
    public final void run() {
        onItemPickListener.onItemPicked(wheelView.getCurrentPosition(),wheelView.getCurrentItem());
    }
}
