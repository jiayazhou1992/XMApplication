package com.xiaomawang.commonlib.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;
import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;
import com.xiaomawang.commonlib.utils.dev.app.logger.DevLogger;

import java.util.ArrayList;
import java.util.List;


public class SlidingTabIndicatorLayout extends LinearLayout {
    private static final String TAG = "SlidingTabIndicator";

    private int tableView_count = -1;

    private int selectedIndicatorHeight;

    private int selectedIndicatorWidth;

    private Paint selectedIndicatorPaint;

    private ColorDrawable defaultSelectionIndicator;

    private ColorDrawable tabSelectedIndicator;

    private int indicator_color = Color.RED;

    private int selectedPosition = -1;

    private float selectionOffset;

    private int layoutDirection = -1;

    private int indicatorLeft = -1;

    private int indicatorRight = -1;

    private ValueAnimator indicatorAnimator;

    private TableView selectedTab;

    private List<TableView> tableViews = new ArrayList<>();

    private OnTabSelectChangedListener onTabSelectChangedListener;




    public SlidingTabIndicatorLayout(Context context) {
        super(context);
        init(null);
    }

    public SlidingTabIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SlidingTabIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlidingTabIndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM);
        this.setWillNotDraw(false);
        defaultSelectionIndicator = new ColorDrawable(indicator_color);
        selectedIndicatorPaint = new Paint();
        selectedIndicatorPaint.setColor(indicator_color);
        selectedIndicatorWidth = SizeUtils.dipConvertPx(30);
        selectedIndicatorHeight = SizeUtils.dipConvertPx(4);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.SlidingTabIndicatorLayout);
            indicator_color = typedArray.getColor(R.styleable.SlidingTabIndicatorLayout_SlidingTabIndicatorLayout_indicator_color,ResourceUtils.getColor(R.color.theme_color1));
            int indicator_width = typedArray.getDimensionPixelSize(R.styleable.SlidingTabIndicatorLayout_SlidingTabIndicatorLayout_indicator_width,SizeUtils.dipConvertPx(30));
            int indicator_height = typedArray.getDimensionPixelSize(R.styleable.SlidingTabIndicatorLayout_SlidingTabIndicatorLayout_indicator_height,SizeUtils.dipConvertPx(4));

            DevLogger.iTag(TAG,"red--->"+ Color.RED+" indicator_color----->" + indicator_color);

            selectedIndicatorPaint.setColor(indicator_color);
            selectedIndicatorWidth = indicator_width;
            selectedIndicatorHeight = indicator_height;

            defaultSelectionIndicator = new ColorDrawable(indicator_color);

            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG,"child count : "+getChildCount());

        for (int i =0 ; i<getChildCount() ; i++) {
            final int finalI = i;

            final View child = getChildAt(finalI);

            if (child instanceof TableView) {

                tableView_count++;

                ((TableView) child).setPos(tableView_count);

                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (selectedTab!=null){
                            if (!selectedTab.equals(v)) {
                                selectedTab.setSelected(false);
                                if (onTabSelectChangedListener != null) {
                                    onTabSelectChangedListener.onTabUnselected(selectedTab);
                                }
                            }else {
                                if (onTabSelectChangedListener != null) {
                                    onTabSelectChangedListener.onTabReselected(selectedTab);
                                }
                            }
                        }

                        child.setSelected(true);
                        selectedTab = (TableView) child;
                        selectedPosition = selectedTab.getPos();

                        animateIndicatorToPosition(selectedPosition, 180);
                    }
                });
            }
        }

        if (tableView_count > -1){
            selectedPosition = 0;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DevLogger.iTag(TAG,"---------onAttachedToWindow");

        if (getTabView(selectedPosition)!=null){
            selectedTab = getTabView(selectedPosition);
            selectedTab.setSelected(true);
            if (onTabSelectChangedListener != null) {
                onTabSelectChangedListener.onTabSelected(selectedTab);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        DevLogger.iTag(TAG,"----changed------->"+changed);

        if (changed) {
            if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
                long duration = this.indicatorAnimator.getDuration();
                this.animateIndicatorToPosition(this.selectedPosition, Math.round((1.0F - this.indicatorAnimator.getAnimatedFraction()) * (float) duration));
            }
        }

        if (this.indicatorAnimator == null || !this.indicatorAnimator.isRunning()) {
            this.updateIndicatorPosition();
        }

    }

    private void updateIndicatorPosition() {
        View selectedTitle = this.getTabView(this.selectedPosition);
        int left;
        int right;
        if (selectedTitle instanceof TableView && selectedTitle != null && selectedTitle.getWidth() > 0) {

            int center = ((TableView) selectedTitle).getCenterX();
            //selectedIndicatorWidth = (int) (((TableView) selectedTitle).getContentWidth() * 0.6f);

            left = Math.max(0,center - selectedIndicatorWidth/2);
            right = center + selectedIndicatorWidth/2;

            //Log.i(TAG,"left : " + left + "right : " + right);
        } else {
            right = 0;
            left = 0;
        }

        this.setIndicatorPosition(left, right);
    }

    public void updateIndicator2NextByOffset(float offset){

    }

    private void setIndicatorPosition(int left, int right) {
        if (left != this.indicatorLeft || right != this.indicatorRight) {
            this.indicatorLeft = left;
            this.indicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    private void animateIndicatorToPosition(final int position, int duration) {
        if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
            this.indicatorAnimator.cancel();
        }

        View targetView = this.getTabView(position);
        if (targetView == null) {
            this.updateIndicatorPosition();
        } else {

            int center = ((TableView) targetView).getCenterX();
            //selectedIndicatorWidth = (int) (((TableView) targetView).getContentWidth() * 0.6f);

            final int targetLeft = Math.max(0,center - selectedIndicatorWidth/2);
            final int targetRight = center + selectedIndicatorWidth/2;

            final int startLeft = this.indicatorLeft;
            final int startRight = this.indicatorRight;
            if (startLeft != targetLeft || startRight != targetRight) {
                ValueAnimator animator = this.indicatorAnimator = new ValueAnimator();
                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);//
                animator.setDuration((long)duration);
                animator.setFloatValues(new float[]{0.0F, 1.0F});
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animator) {
                        float fraction = animator.getAnimatedFraction();
                        SlidingTabIndicatorLayout.this.setIndicatorPosition(lerp(startLeft, targetLeft, fraction), lerp(startRight, targetRight, fraction));
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        SlidingTabIndicatorLayout.this.selectedPosition = position;
                        SlidingTabIndicatorLayout.this.selectionOffset = 0.0F;

                        //动画走完再回调
                        if (onTabSelectChangedListener!=null){

                            onTabSelectChangedListener.onTabSelected(selectedTab);

                        }
                    }
                });
                animator.start();
            }

        }
    }

    private void calculateTabViewContentBounds(TableView tabView, RectF contentBounds) {
        int tabViewContentWidth = tabView.getContentWidth();
        if (tabViewContentWidth < SizeUtils.dipConvertPx(24)) {
            tabViewContentWidth = SizeUtils.dipConvertPx(24);
        }

        int tabViewCenter = (tabView.getLeft() + tabView.getRight()) / 2;
        int contentLeftBounds = tabViewCenter - tabViewContentWidth / 2;
        int contentRightBounds = tabViewCenter + tabViewContentWidth / 2;
        contentBounds.set((float)contentLeftBounds, 0.0F, (float)contentRightBounds, 0.0F);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int indicatorTop = this.getHeight() - selectedIndicatorHeight;
        int indicatorBottom = this.getHeight();

        if (this.indicatorLeft >= 0 && this.indicatorRight > this.indicatorLeft) {
            Drawable selectedIndicator = DrawableCompat.wrap(this.tabSelectedIndicator != null ? this.tabSelectedIndicator : this.defaultSelectionIndicator);
            selectedIndicator.setBounds(this.indicatorLeft, indicatorTop, this.indicatorRight, indicatorBottom);

            //这些代码在4。4，貌似无效
            if (this.selectedIndicatorPaint != null) {

                if (Build.VERSION.SDK_INT == 21) {
                    selectedIndicator.setColorFilter(this.selectedIndicatorPaint.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    DrawableCompat.setTint(selectedIndicator, this.selectedIndicatorPaint.getColor());
                }
            }

            selectedIndicator.draw(canvas);
        }

    }

    public void setIndicator_color(int indicator_color) {
        this.indicator_color = indicator_color;
        selectedIndicatorPaint.setColor(indicator_color);

        if (defaultSelectionIndicator!=null){
            defaultSelectionIndicator.setColor(indicator_color);
        }

        if (tabSelectedIndicator!=null){
            tabSelectedIndicator.setColor(indicator_color);
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 设置选中
     * @param pos
     * @param callBack 是否回掉
     */
    public void setSelectedTab(int pos,boolean callBack){
        if (pos<0 || pos > tableView_count){
            throw new IllegalArgumentException("pos is " + pos);
        }
        if (pos == selectedPosition){
            if (this.onTabSelectChangedListener != null){
                this.onTabSelectChangedListener.onTabReselected(selectedTab);
            }
            return;
        }

        if (selectedTab != null){
            selectedTab.setSelected(false);
        }

        selectedPosition = pos;
        selectedTab = getTabView(selectedPosition);
        selectedTab.setSelected(true);

        if (callBack) {
            animateIndicatorToPosition(pos, 180);
        }else {
            updateIndicatorPosition();
        }
    }

    public int getSelectedPosition(){
        return selectedPosition;
    }

    /**
     *
     * @param tableView
     */
    public void addTab(final TableView tableView){
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        addTab(tableView,params);
    }

    public void addTab(final TableView tableView, ViewGroup.LayoutParams params){
        addView(tableView,params);

        tableView_count++;
        tableView.setPos(tableView_count);
        tableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab!=null){
                    if (!selectedTab.equals(v)) {
                        selectedTab.setSelected(false);
                        if (onTabSelectChangedListener != null) {
                            onTabSelectChangedListener.onTabUnselected(selectedTab);
                        }
                    }else {
                        if (onTabSelectChangedListener != null) {
                            onTabSelectChangedListener.onTabReselected(selectedTab);
                        }
                    }
                }

                tableView.setSelected(true);
                selectedTab = tableView;
                selectedPosition = tableView.getPos();

                animateIndicatorToPosition(selectedPosition, 180);
            }
        });
    }

    /**
     * 清空tab
     */
    public void removeAllTab(){
        removeAllViews();
        selectedTab = null;
        selectedPosition = -1;
        tableView_count = -1;
    }

    /**
     * 获取第几个tab
     * @param pos
     * @return
     */
    private TableView getTabView(int pos){
        for (int i=0;i<getChildCount();i++){
            View view = getChildAt(i);
            if (view instanceof TableView){
                if (((TableView) view).getPos() == pos){
                    return (TableView) view;
                }
            }
        }
        return null;
    }



    public int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (float)(endValue - startValue));
    }

    public void setOnTabSelectChangedListener(OnTabSelectChangedListener onTabSelectChangedListener) {
        this.onTabSelectChangedListener = onTabSelectChangedListener;
    }

    public static class SimpleOnTabSelectChangedListener implements OnTabSelectChangedListener{

        @Override
        public void onTabSelected(TableView tableView) {

        }

        @Override
        public void onTabUnselected(TableView tableView) {

        }

        @Override
        public void onTabReselected(TableView tableView) {

        }
    }

    public interface OnTabSelectChangedListener{

        void onTabSelected(TableView tableView);

        void onTabUnselected(TableView tableView);

        void onTabReselected(TableView tableView);
    }
}
