package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by jiayazhou on 2017/11/29.
 */

public class SimpleRefreshFoot implements RefreshFoot{

    private LinearLayout layout;
    private TextView textView;
    private boolean isBottleneck = false;
    private boolean isLive = false;

    public SimpleRefreshFoot(Context context){
        //layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_loadmore,null);
        //layout.setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.dim40),0,context.getResources().getDimensionPixelSize(R.dimen.dim40));
    }

    @Override
    public View getFoot() {
        return layout;
    }

    @Override
    public int getHight() {
        return layout.getMeasuredHeight();
    }

    @Override
    public int getWidth() {
        return layout.getMeasuredWidth();
    }

    @Override
    public void load() {

    }

    @Override
    public void loadComplete() {

    }

    @Override
    public void scrollLisenter(int dy) {

    }

    @Override
    public int pullUp(int dy) {

        return 0;
    }

    @Override
    public boolean isBottleneck() {
        return isBottleneck;
    }

    @Override
    public boolean isLive() {
        return isLive;
    }

    @Override
    public int getFixType() {
        return 0;
    }
}
