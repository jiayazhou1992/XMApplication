package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BottomNavLayout extends LinearLayout {

    private int count;
    private OnNavItemClickListener onNavItemClickListener;

    public BottomNavLayout(Context context) {
        super(context);
    }

    public BottomNavLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomNavLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomNavLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof BottomNavItem){
                ((BottomNavItem) child).setPos(count);
                count++;
            }
        }
    }

    public BottomNavItem getBottomNavItem(int pos){
        BottomNavItem bottomNavItem = null;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof BottomNavItem){
                if (((BottomNavItem) child).getPos() == pos){
                    bottomNavItem = (BottomNavItem) child;
                    break;
                }
            }
        }
        return bottomNavItem;
    }

    public void select(int pos){
        BottomNavItem curSeleBottomNavItem = null;
        BottomNavItem targetBottomNavItem = null;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof BottomNavItem){
                if (((BottomNavItem) child).getPos() == pos){
                    targetBottomNavItem = (BottomNavItem) child;
                }

                if (child.isSelected()){
                    curSeleBottomNavItem = (BottomNavItem) child;
                }
            }
        }

        if (targetBottomNavItem == null || targetBottomNavItem.equals(curSeleBottomNavItem)){

            return;
        }

        if (onNavItemClickListener!=null){
            boolean hodler = onNavItemClickListener.onPreNavItemClick(targetBottomNavItem, pos);
            if (!hodler){
                targetBottomNavItem.setSelected(true);
                if (curSeleBottomNavItem != null) {
                    curSeleBottomNavItem.setSelected(false);
                }
                onNavItemClickListener.onNavItemClick(targetBottomNavItem, pos);
            }
        }else {
            targetBottomNavItem.setSelected(true);
            curSeleBottomNavItem.setSelected(false);
        }
    }

    public void setOnNavItemClickListener(OnNavItemClickListener onNavItemClickListener) {
        this.onNavItemClickListener = onNavItemClickListener;
    }

    public interface OnNavItemClickListener{
        boolean onPreNavItemClick(BottomNavItem bottomNavItem, int pos);
        void onNavItemClick(BottomNavItem bottomNavItem, int pos);
    }
}
