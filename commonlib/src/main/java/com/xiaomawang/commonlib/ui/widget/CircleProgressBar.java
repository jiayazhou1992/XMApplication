package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;


/**
 * Created by jiayazhou on 2018/6/23.
 */
public class CircleProgressBar extends View {

    private static final String TAG = "CircleProgressView";

    private Paint mPaint;

    private int view_w;
    private int view_h;

    private RectF backgroudRectF;
    private RectF mRectF;

    private float centerX;
    private float centerY;

    private int mStrokeWidth;

    private int backgroudProgressColor;

    private int foregroudProgressColor;

    private int mTextColor;

    private int mTextSize;

    private int progress = 50;

    private long maxLength;

    private long currentLength;

    private String describe = "";

    public CircleProgressBar(Context context) {
        super(context);
        initView(context,null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attributeSet){

        if (attributeSet!=null){
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CircleProgressBar);
            backgroudProgressColor = a.getColor(R.styleable.CircleProgressBar_backgroudProgressColor, Color.parseColor("#BEC2C9"));
            foregroudProgressColor = a.getColor(R.styleable.CircleProgressBar_foregroudProgressColor, Color.parseColor("#26C6DA"));
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.CircleProgressBar_mStrokeWidth,SizeUtils.dipConvertPx(8));
            mTextColor = a.getColor(R.styleable.CircleProgressBar_mTextColor, Color.parseColor("#26C6DA"));
            mTextSize = a.getDimensionPixelSize(R.styleable.CircleProgressBar_mTextSize, 18);
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        backgroudRectF = new RectF();
        mRectF = new RectF();
        progress = -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        view_w = getMeasuredWidth();
        view_h = getMeasuredHeight();
        initRect(view_w,view_h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect(w,h);
    }

    private void initRect(int w,int h){
        view_w = w;
        view_h = h;
        backgroudRectF.set(0+mStrokeWidth,0+mStrokeWidth,view_w-mStrokeWidth,view_h-mStrokeWidth);
        mRectF.set(0+mStrokeWidth,0+mStrokeWidth,view_w-mStrokeWidth,view_h-mStrokeWidth);

        centerX = view_w/2;
        centerY = view_h/2;

        //Logger.t(TAG+">>w>>"+w+">>h>>"+h+">>centerX>>"+centerX+">>centerY>>"+centerY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawText(canvas);
    }


    private void drawProgress(Canvas canvas){

        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        //绘制背景进度
        mPaint.setColor(backgroudProgressColor);
        canvas.drawArc(backgroudRectF,0,360,false,mPaint);

        //绘制进度
        mPaint.setColor(foregroudProgressColor);
        canvas.drawArc(mRectF,-90,(progress == -1 ? 0 : (progress*1f/100*360)),false,mPaint);
    }

    private void drawText(Canvas canvas){

        String strPro = progress+"%";
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        if (progress == -1||progress == 100){
            float strW2 = mPaint.measureText(describe);
            float strH2 = Math.abs(fontMetrics.ascent) - fontMetrics.descent;
            canvas.drawText(describe,centerX-strW2/2,centerY+strH2/2,mPaint);
        }else {
            float strW1 = mPaint.measureText(strPro);
            float strH1 = Math.abs(fontMetrics.ascent) - fontMetrics.descent;
            canvas.drawText(strPro,centerX-strW1/2,centerY+strH1+strH1/2,mPaint);

            float strW2 = mPaint.measureText(describe);
            float strH2 = Math.abs(fontMetrics.ascent) - fontMetrics.descent;
            canvas.drawText(describe,centerX-strW2/2,centerY+strH2/6,mPaint);
        }

    }


    /*----------api---------*/

    public void setText(String text){
        describe = text;
        invalidate();
    }

    public void setMaxLength(long maxLength){
        this.maxLength = maxLength;
        progress = Math.round(currentLength*1f/this.maxLength);
        invalidate();
    }

    public void setCurrentLength(long currentLength){
        this.currentLength = currentLength;
        progress = Math.round(this.currentLength*1f/maxLength);
        invalidate();
    }

    public void setProgress(float progress){
        //Logger.t(TAG+">>嗯progress>>"+progress);
        this.progress = (int) (progress*100);

        invalidate();
    }
}
