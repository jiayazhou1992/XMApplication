package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.KeyBoardUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

/**
 * Created by jiayazhou on 2018/6/4.
 */
public class EditTextWithClear extends AppCompatEditText {

    private Drawable clearDrawable;

    public EditTextWithClear(Context context) {
        super(context);
        init(context,null);
    }

    public EditTextWithClear(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EditTextWithClear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    /**
     * 初始化右边小眼睛的控件
     */
    private void init(Context context, AttributeSet attrs) {

        //获取EditText的DrawableRight,主要是通过xml或者外部设置右边的按钮，如果没有设置就采用默认的
        clearDrawable = getCompoundDrawables()[2];
        if (clearDrawable == null) {
            if (attrs != null){
                TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.EditTextWithClear);
                clearDrawable = typedArray.getDrawable(R.styleable.EditTextWithClear_clearIcon);
                typedArray.recycle();
            }
            if (clearDrawable == null) {
                clearDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_edit_clear);
            }
            setCompoundDrawablePadding(SizeUtils.dipConvertPx(10));
        }
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
        //setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], clearDrawable, getCompoundDrawables()[3]);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    setToggleIconVisible(false);
                }else {
                    setToggleIconVisible(true);
                }
            }
        });
    }


    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean touchable = event.getX() > (getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth()) && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if (getText().length() > 0){
                        getText().clear();
                    }
                    clearFocus();
                    KeyBoardUtils.closeKeyboard(this);
                    return false;
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){

            }

        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    public void setToggleIconVisible(boolean visible) {
        if (visible && getCompoundDrawables()[2] != null) return;//已显示

        Drawable right = visible ? clearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }
}
