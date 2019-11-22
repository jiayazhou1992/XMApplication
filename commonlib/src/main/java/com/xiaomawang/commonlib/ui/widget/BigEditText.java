package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

import java.lang.reflect.Field;

/**
 * Created by jiayazhou on 2018/6/4.
 */
public class BigEditText extends AppCompatEditText {
    private static final String TAG = "BigEditText";

    private Paint mPaint;
    private int maxLength = 100;

    public BigEditText(Context context) {
        super(context);
        init();
    }

    public BigEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BigEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#D92E2C"));
        mPaint.setTextSize(SizeUtils.spConvertPx(12));
        int paddingBottom = getPaddingBottom();
        paddingBottom += SizeUtils.dipConvertPx(12);
        setPadding(getLeft(), getTop(), getRight(), paddingBottom);
        InputFilter[] filters = getText().getFilters();
        if (filters != null && filters.length > 0) {
            for (InputFilter filter : filters) {
                if (filter instanceof InputFilter.LengthFilter){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        maxLength = ((InputFilter.LengthFilter) filter).getMax();
                    }else {
                        Class c = filter.getClass();
                        Field[] f = c.getDeclaredFields();
                        for (Field field : f){
                            if (field.getName().equals("mMax")) {
                                field.setAccessible(true);
                                try {
                                    maxLength = (Integer) field.get(filter);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = getText().length();
        if (maxLength == length){
            mPaint.setColor(Color.parseColor("#D92E2C"));
        }else {
            mPaint.setColor(ResourceUtils.getColor(R.color.black_tran40));
        }
        String text = length + "/" + maxLength;
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, getWidth() - rect.width() - 50, getHeight() - rect.height(), mPaint);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
