package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

public class SideSlipLayout extends FrameLayout implements GestureDetector.OnGestureListener {
    private static final String TAG = "SideSlipLayout";

    private GestureDetector mGestureDetector;
    private int mTouchSlop;

    private View slipView;

    private ViewPropertyAnimatorCompat animatorCompat;

    private boolean canSlip = true;

    private int slipLength;

    private OnSingleTapUpListener onSingleTapUpListener;

    private OnSlipEndListener onSlipEndListener;


    public SideSlipLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public SideSlipLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SideSlipLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SideSlipLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(AttributeSet attrs){
        slipLength = SizeUtils.dipConvertPx(-128);
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SideSlipLayout);
            slipLength = typedArray.getDimensionPixelOffset(R.styleable.SideSlipLayout_slipLength, slipLength);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        slipView = findViewWithTag("slip");
        slipView.setClickable(true);
        mGestureDetector = new GestureDetector(getContext(),this);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public void setCanSlip(boolean canSlip) {
        this.canSlip = canSlip;
    }

    public void setSlipLength(int slipLength) {
        this.slipLength = slipLength;
    }

    public void setOnSingleTapUpListener(OnSingleTapUpListener onSingleTapUpListener) {
        this.onSingleTapUpListener = onSingleTapUpListener;
    }

    public void setSlipViewListener(OnClickListener listener){
        if (slipView==null){
            slipView = findViewWithTag("slip");
        }
        if (slipView!=null){
            slipView.setOnClickListener(listener);
        }
    }

    public void setOnSlipEndListener(OnSlipEndListener onSlipEndListener) {
        this.onSlipEndListener = onSlipEndListener;
    }

    public void reMarkSlip(){
        reMarkSlip(true);
    }

    public void reMarkSlip(boolean anim){
        if (slipView!=null && slipView.getTranslationX()!=0){
            if (anim) {
                animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(0);
            }else {
                ViewCompat.setTranslationX(slipView, 0);
            }
        }
    }

    public void slipEnd(){
        slipEnd(true);
    }

    public void slipEnd(boolean anim){
        if (slipView!=null && slipView.getTranslationX() == 0){
            if (anim) {
                animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(slipLength);
            }else {
                ViewCompat.setTranslationX(slipView, slipLength);
            }
            if (onSlipEndListener!=null){
                onSlipEndListener.onSlipEnd();
            }
        }
    }

    private float downX;
    private float downY;
    private float lastX;
    private float lastY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (slipView==null){
            slipView = findViewWithTag("slip");
        }

        if (!canSlip){
            return super.dispatchTouchEvent(ev);
        }

        int action = ev.getAction();
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int dealtX = 0;
        int dealtY = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                dealtX = 0;
                dealtY = 0;
                downX = ev.getRawX();
                downY = ev.getRawY();
                // 保证子View能够接收到Action_move事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                dealtX += Math.abs(x - lastX);
                dealtY += Math.abs(y - lastY);

                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (dealtX >= dealtY) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                lastX = x;
                lastY = y;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canSlip){
            return super.onInterceptTouchEvent(ev);
        }

        if (slipView!=null && ev.getAction() == MotionEvent.ACTION_MOVE){
            if (Math.abs(ev.getRawX() - downX) > mTouchSlop || Math.abs(ev.getRawY() - downY) > mTouchSlop) {
                return true;
            }else {
                return super.onInterceptTouchEvent(ev);
            }
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!canSlip){
            return super.onTouchEvent(event);
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN){
            if (animatorCompat != null){
                animatorCompat.cancel();
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
            if (slipView != null ){
                float tranX = slipView.getTranslationX();
                if (tranX != 0 && tranX != slipLength) {
                    if (slipLength < 0) {
                        if (tranX < 0 && tranX >= slipLength * 2 / 3) {
                            animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(0);
                        } else {
                            animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(slipLength);
                            if (onSlipEndListener != null) {
                                onSlipEndListener.onSlipEnd();
                            }
                        }
                    }else {
                        if (tranX > 0 && tranX <= slipLength * 2 / 3) {
                            animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(0);
                        } else {
                            animatorCompat = ViewCompat.animate(slipView).setDuration(100).translationX(slipLength);
                            if (onSlipEndListener != null) {
                                onSlipEndListener.onSlipEnd();
                            }
                        }
                    }
                }
            }

            getParent().requestDisallowInterceptTouchEvent(false);
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        //DevLogger.iTag(TAG,"onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (onSingleTapUpListener!=null){
            onSingleTapUpListener.onSingleTapUp();
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //DevLogger.iTag(TAG,"distanceX ----- >" + distanceX);
        if (slipView!=null){
            if (Math.abs(distanceX) > Math.abs(slipLength)){
                distanceX = distanceY/distanceX * mTouchSlop;
            }
            float tranX = slipView.getTranslationX() - distanceX;
            if (slipLength < 0) {
                tranX = Math.max(tranX, slipLength);
                tranX = Math.min(tranX, 0);
            } else {
                tranX = Math.min(tranX, slipLength);
                tranX = Math.max(tranX, 0);
            }
            slipView.setTranslationX(tranX);

            if (onSlipEndListener!=null&&tranX==slipLength){
                onSlipEndListener.onSlipEnd();
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnSingleTapUpListener{
        void onSingleTapUp();
    }

    public interface OnSlipEndListener{
        void onSlipEnd();
    }
}
