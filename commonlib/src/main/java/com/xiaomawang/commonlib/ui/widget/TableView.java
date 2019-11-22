package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;

public class TableView extends LinearLayout {

    private View contentView;

    private TextView tv_lable;

    private View v_indicator;

    private int selectedColor;

    private int unSelectedColor = Color.WHITE;

    private int selected_type;//选中样式

    private int max_textsize;

    private int min_textsize;

    private boolean isSelect;

    private int pos;

    public TableView(Context context) {
        super(context);
        init(null);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_table_view,this);
        tv_lable = contentView.findViewById(R.id.tv_lable);
        v_indicator = contentView.findViewById(R.id.v_indicator);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.TableView);
            ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.TableView_tableColors);
            String table = typedArray.getString(R.styleable.TableView_table);
            selected_type = typedArray.getInt(R.styleable.TableView_selected_type,0);

            typedArray.recycle();

            if (colorStateList!=null) {
                tv_lable.setTextColor(colorStateList);
            }
            tv_lable.setText(table);

            int[] state = new int[]{android.R.attr.state_selected};
            selectedColor = colorStateList == null? Color.BLUE : colorStateList.getColorForState(state, Color.BLUE);

            if (selected_type == 1){
                tv_lable.getPaint().setFakeBoldText(true);
            }

        }

        max_textsize = 21;
        min_textsize = 15;

    }

    @Override
    public boolean isSelected() {
        return isSelect;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelect = selected;
        if (selected_type == 0) {
            tv_lable.setSelected(selected);
        }else if (selected_type == 1){
            if (selected){
                tv_lable.setTextSize(TypedValue.COMPLEX_UNIT_DIP,max_textsize);
            }else {
                tv_lable.setTextSize(TypedValue.COMPLEX_UNIT_DIP,min_textsize);
            }
        }

        /*if (selected){
            v_indicator.setBackgroundColor(selectedColor);
        }else {
            v_indicator.setBackgroundColor(unSelectedColor);
        }*/
    }

    public void setSelected_type(int selected_type) {
        this.selected_type = selected_type;
        if (selected_type == 1){
            tv_lable.getPaint().setFakeBoldText(true);
        }else {
            tv_lable.getPaint().setFakeBoldText(false);
        }
    }

    public void setTableStr(CharSequence tableStr){
        tv_lable.setText(tableStr);
    }

    public void setColorStateList(@ColorRes int colorStateListId){

        ColorStateList colorStateList = getContext().getResources().getColorStateList(colorStateListId);
        tv_lable.setTextColor(colorStateList);

        int[] state = new int[]{android.R.attr.state_selected};
        selectedColor = colorStateList.getColorForState(state, Color.BLUE);
    }

    public int getContentWidth(){

        return getMeasuredWidth();
    }

    public int getCenterX(){
        return (getLeft() + getRight())/2;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
