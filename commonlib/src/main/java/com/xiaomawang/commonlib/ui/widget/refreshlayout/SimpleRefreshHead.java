package com.xiaomawang.commonlib.ui.widget.refreshlayout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by jiayazhou on 2017/11/29.
 */

public class SimpleRefreshHead implements RefreshHead{

    private LinearLayout layout;
    private TextView textView;
    private boolean isBottleneck = false;
    private boolean isLive = false;

    public SimpleRefreshHead(Context context){
        //layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_loadmore,null);
        //layout.setPadding(0,context.getResources().getDimensionPixelSize(R.dimen.dim40),0,context.getResources().getDimensionPixelSize(R.dimen.dim40));
    }

    @Override
    public View getHead() {
        return layout;
    }

    @Override
    public int getHeight() {
        return layout.getMeasuredHeight();
    }

    @Override
    public int getWidth() {
        return layout.getMeasuredWidth();
    }

    @Override
    public void refresh() {

    }

    @Override
    public void refreshComplete() {

    }

    @Override
    public void scrollListener(int dy) {
        //int alpha = Math.abs(dy)/getHight()*255;
        //layout.getBackground().setAlpha(alpha);
    }

    @Override
    public int pullDown(int dy) {

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
