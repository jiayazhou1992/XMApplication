package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;

public class SimpleIndicator extends View {
    private static final String TAG = "SimpleIndicator";

    private int indicatorCount = 0;

    private int indicatorMargin = 10;

    private int mSelectIndex;

    private float radius = 10;

    private Paint mPaint;

    private Rect rect;

    @ColorInt
    private int selectColor;

    @ColorInt
    private int unSelectColor;



    public SimpleIndicator(Context context) {
        super(context);
        initView(null);
    }

    public SimpleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public SimpleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SimpleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        selectColor = ResourceUtils.getColor(R.color.black_tran50);
        unSelectColor = ResourceUtils.getColor(R.color.black_tran20);

        if (attrs!=null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Indicator);
            indicatorCount = typedArray.getInt(R.styleable.Indicator_indicator_count,0);
            indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Indicator_indicator_margin,10);

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) (indicatorCount * indicatorMargin + indicatorCount * 2 * radius);
        int height = (int) (2 * radius);

        setMeasuredDimension(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (rect == null){
            rect = new Rect();
        }
        rect.set(0,0,w,h);
        DevLogger.iTag(TAG,rect.toString());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = radius;
        float cy = radius;

        for (int i = 0; i<indicatorCount; i++){
            cx = radius + i * indicatorMargin + i * 2 * radius;
            if (i == mSelectIndex){
                mPaint.setColor(selectColor);
                canvas.drawCircle(cx,cy,radius,mPaint);
            }else {
                mPaint.setColor(unSelectColor);
                canvas.drawCircle(cx,cy,radius,mPaint);
            }
        }

    }

    public void setmSelectIndex(int mSelectIndex){
        this.mSelectIndex = mSelectIndex;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
