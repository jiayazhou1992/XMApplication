package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

public class BottomNavItem extends RelativeLayout {

    private View contentView;

    private TextView tv_lable;

    private ImageView iv_icon;

    private TextView tv_red;

    private String lable = "";
    private int pos = -1;

    public BottomNavItem(Context context) {
        super(context);
        init(null);
    }

    public BottomNavItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BottomNavItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BottomNavItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        setClickable(true);

        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottomnavitem,this);
        iv_icon = contentView.findViewById(R.id.iv_icon);
        tv_lable = contentView.findViewById(R.id.tv_lable);
        tv_red = contentView.findViewById(R.id.tv_red);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.BottomNavItem);
            int lablecolor = typedArray.getResourceId(R.styleable.BottomNavItem_lableColors,android.R.color.black);
            int icons = typedArray.getResourceId(R.styleable.BottomNavItem_icons,0);
            int textSize = typedArray.getDimensionPixelSize(R.styleable.BottomNavItem_labelSize, SizeUtils.spConvertPx2(14));
            lable = typedArray.getString(R.styleable.BottomNavItem_lableStr);
            typedArray.recycle();

            tv_lable.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tv_lable.setTextColor(getContext().getResources().getColorStateList(lablecolor));
            if (icons!=0) {
                iv_icon.setImageResource(icons);
            }
            tv_lable.setText(lable);
        }
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return this.pos;
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            this.playSoundEffect(0);
        }
        boolean selected = isSelected();
        if (!selected){
            ViewParent parent = getParent();
            if (parent instanceof BottomNavLayout){
                ((BottomNavLayout) parent).select(pos);
            }
        }
        return true;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tv_lable.setSelected(selected);
        iv_icon.setSelected(selected);
    }

    public void setRedCount(int count){
        if (count>0){
            tv_red.setVisibility(VISIBLE);
            tv_red.setBackgroundResource(R.drawable.bg_red_cir);
            tv_red.setTextSize(SizeUtils.dipConvertPx(10));
            tv_red.setText(count+"");
        }else if (count == 0){
            tv_red.setVisibility(VISIBLE);
            tv_red.setBackgroundResource(R.drawable.bg_red_small_cir);
            tv_red.setTextSize(SizeUtils.dipConvertPx(1));
            tv_red.setText("");
        }else {
            tv_red.setVisibility(GONE);
        }
    }

    public CharSequence getLableName(){
        return tv_lable.getText();
    }
}
