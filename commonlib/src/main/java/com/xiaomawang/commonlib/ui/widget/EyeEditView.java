package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

/**
 * Created by jiayazhou on 2018/6/4.
 */
public class EyeEditView extends AppCompatEditText {

    private boolean eye = true;//是否需要眼睛

    private Drawable mToggleDrawable;
    private boolean isSee = false;

    public EyeEditView(Context context) {
        super(context);
        init(context,null);
    }

    public EyeEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public EyeEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }



    /**
     * 初始化右边小眼睛的控件
     */
    private void init(Context context, AttributeSet attrs) {

        if (eye) {
            //获取EditText的DrawableRight,主要是通过xml或者外部设置右边的按钮，如果没有设置就采用默认的
            mToggleDrawable = getCompoundDrawables()[2];
            if (mToggleDrawable == null) {
                mToggleDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_password_hidden);
                setCompoundDrawablePadding(SizeUtils.dipConvertPx(10));
            }
            mToggleDrawable.setBounds(0, 0, mToggleDrawable.getIntrinsicWidth(), mToggleDrawable.getIntrinsicHeight());
            setToggleIconVisible(true);
        }
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
                boolean touchable = event.getX() > (getWidth()
                        - getPaddingRight() - mToggleDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if (!isSee) {
                        //显示密码明文
                        mToggleDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_password_shown);

                        setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        postInvalidate();
                        CharSequence charSequence = getText();
                        //为了保证体验效果，需要保持输入焦点在文本最后一位
                        if (charSequence != null) {
                            Spannable spanText = (Spannable) charSequence;
                            Selection.setSelection(spanText, charSequence.length());
                        }

                    }else {
                        //隐藏密码明文
                        mToggleDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_password_hidden);

                        setTransformationMethod(PasswordTransformationMethod.getInstance());
                        postInvalidate();
                        setSelection(getText().length());
                    }

                    isSee = !isSee;
                    mToggleDrawable.setBounds(0, 0, mToggleDrawable.getIntrinsicWidth(), mToggleDrawable.getIntrinsicHeight());
                    setToggleIconVisible(true);
                }
            }else if(event.getAction() == MotionEvent.ACTION_UP){

            }

        }

        return super.onTouchEvent(event);
    }

    /**是否需要眼睛
     * @param eye
     */
    public void showEye(boolean eye) {
        this.eye = eye;
        if (eye){
            mToggleDrawable = getCompoundDrawables()[2];
            if (mToggleDrawable == null) {
                mToggleDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_password_hidden);
                setCompoundDrawablePadding(SizeUtils.dipConvertPx(10));
            }
            mToggleDrawable.setBounds(0, 0, mToggleDrawable.getIntrinsicWidth(), mToggleDrawable.getIntrinsicHeight());
            setToggleIconVisible(true);
        }else {
            setToggleIconVisible(false);
        }
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    public void setToggleIconVisible(boolean visible) {
        Drawable right = visible ? mToggleDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

}
