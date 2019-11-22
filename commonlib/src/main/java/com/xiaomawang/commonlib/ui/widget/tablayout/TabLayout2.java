package com.xiaomawang.commonlib.ui.widget.tablayout;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;

import com.google.android.material.animation.AnimationUtils;
import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class TabLayout2 extends LinearLayout {
    private static final String TAG = "TabLayout2";

    private int selectedTabPosition = Tab.INVALID_POSITION;
    private Tab selectedTab;

    //private static final Pools.Pool<Tab> tabPool = new Pools.SynchronizedPool(16);
    //private Pools.Pool<TabView> tabViewPool;

    private List<Tab> tabList;

    private int tab_indicator_color;
    private int tab_indicator_width;
    private int tab_indicator_height;

    private int indicatorLeft = -1;
    private int indicatorRight = -1;

    private ColorDrawable indicatorDrawable;

    private ValueAnimator indicatorAnimator;
    private long duration = 180;

    private Runnable callbackRunnable;
    private OnTabSelectChangedListener onTabSelectChangedListener;


    public TabLayout2(Context context) {
        super(context);
        init(null);
    }

    public TabLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TabLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM);
        this.setWillNotDraw(false);
        if (attrs != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.Tab);
            tab_indicator_color = typedArray.getColor(R.styleable.Tab_tab_indicator_color, ResourceUtils.getColor(R.color.red));
            tab_indicator_height = typedArray.getDimensionPixelSize(R.styleable.Tab_tab_indicator_height, 0);
            tab_indicator_width = typedArray.getDimensionPixelSize(R.styleable.Tab_tab_indicator_width, 0);
            typedArray.recycle();
        }

        indicatorDrawable = new ColorDrawable(tab_indicator_color);

        tabList = new ArrayList<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof TabView){
                TabView tabView = (TabView) view;
                Tab tab = tabView.tab;
                tab.parent = this;
                tabList.add(tab);
                tab.setPosition(tabList.size() - 1);
            }
        }
        if (tabList.size() > 0) {
            this.selectedTabPosition = 0;
            this.selectedTab = tabList.get(0);
            this.selectedTab.tabView.select(true);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
            this.indicatorAnimator.cancel();
            long duration = this.indicatorAnimator.getDuration();
            this.animateIndicatorToPosition(this.selectedTabPosition, Math.round((1.0F - this.indicatorAnimator.getAnimatedFraction()) * (float)duration));
        } else {
            this.updateIndicatorPosition();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (selectedTab != null){
            int height = getHeight();
            if (this.indicatorLeft != this.indicatorRight) {
                indicatorDrawable.setBounds(this.indicatorLeft, height - this.tab_indicator_height, this.indicatorRight, height);
                indicatorDrawable.draw(canvas);
            }
        }

    }

    private void updateIndicatorPosition() {
        int indicatorLeft = 0;
        int indicatorRight = 0;
        if (selectedTab != null){
            int centerX = selectedTab.tabView.getCenterX();
            indicatorLeft = centerX - this.tab_indicator_width / 2;
            indicatorRight = centerX + this.tab_indicator_width / 2;
        }

        setIndicatorPosition(indicatorLeft, indicatorRight);
    }

    private void animateIndicatorToPosition(final int position, long duration) {
        if (this.indicatorAnimator != null && this.indicatorAnimator.isRunning()) {
            this.indicatorAnimator.cancel();
        }

        TabView targetView = this.getTab(position).tabView;
        if (targetView != null) {
            int center = targetView.getCenterX();

            final int targetLeft = Math.max(0,center - tab_indicator_width/2);
            final int targetRight = center + tab_indicator_width/2;

            final int startLeft = this.indicatorLeft;
            final int startRight = this.indicatorRight;
            if (startLeft != targetLeft || startRight != targetRight) {
                ValueAnimator animator = this.indicatorAnimator = new ValueAnimator();
                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);//
                animator.setDuration(duration);
                animator.setFloatValues(new float[]{0.0F, 1.0F});
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animator) {
                        float fraction = animator.getAnimatedFraction();
                        setIndicatorPosition(lerp(startLeft, targetLeft, fraction), lerp(startRight, targetRight, fraction));
                    }
                });
                animator.start();
            }

        }
    }

    public int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (float)(endValue - startValue));
    }

    private void setIndicatorPosition(int left, int right) {
        if (left != this.indicatorLeft || right != this.indicatorRight) {
            this.indicatorLeft = left;
            this.indicatorRight = right;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (indicatorAnimator != null && indicatorAnimator.isRunning()) {
            indicatorAnimator.cancel();
            indicatorAnimator = null;
        }
        if (callbackRunnable != null) {
            getHandler().removeCallbacks(callbackRunnable);
            callbackRunnable = null;
        }
        super.onDetachedFromWindow();
        if (tabList != null) {
            for (Tab tab : tabList) {
                tab.reset();
            }
            tabList.clear();
        }
    }

    /*******************************************************************/

    public Tab newTab(){
        Tab tab = new Tab();
        tab.parent = this;
        return tab;
    }

    public void addTab(Tab tab){
        tab.setPosition(tabList.size());
        tabList.add(tab);

        TabView tabView = new TabView(getContext(), tab);
        addView(tabView);
    }

    private Tab getTab(int pos){
        if (tabList.size() > pos){
            return tabList.get(pos);
        }else {
            return null;
        }
    }

    public void selectTab(int pos) {
        this.selectTab(pos, true);
    }

    public void selectTab(int pos, boolean callback) {
        if (pos == this.selectedTabPosition) {
            // 复选
            if (onTabSelectChangedListener != null) {
                onTabSelectChangedListener.onTabReselected(this.selectedTab);
            }
            return;
        }
        // 取消上一个tab选中状态
        if (this.selectedTab != null) {
            this.selectedTab.tabView.select(false);
            if (onTabSelectChangedListener != null) {
                onTabSelectChangedListener.onTabUnselected(this.selectedTab);
            }
        }

        // 选中
        this.selectedTabPosition = pos;
        this.selectedTab = getTab(pos);
        this.selectedTab.tabView.select(true);

        if (callback) {
            if (callbackRunnable == null) {
                callbackRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (onTabSelectChangedListener != null) {
                            onTabSelectChangedListener.onTabSelected(TabLayout2.this.selectedTab);
                        }
                    }
                };
            }
            getHandler().postDelayed(callbackRunnable, duration);
            animateIndicatorToPosition(this.selectedTabPosition, duration);
        }else {
            updateIndicatorPosition();
        }
    }

    public int getSelectedTabPosition() {
        return selectedTabPosition;
    }


    /*** Tab ********************************************************/

    public static class Tab{
        public static final int INVALID_POSITION = -1;

        private Object tag;
        private Drawable icon;
        private CharSequence text;
        private boolean boldText;
        private int tab_selected_textColor;
        private int tab_unselected_textColor;
        private int tab_textSize;
        private int tab_selected_textSize;
        private int tab_unselected_textSize;
        private int paddingLeft = -1;
        private int paddingTop = -1;
        private int paddingRight = -1;
        private int paddingBottom = -1;
        private int position = -1;

        private View customView;
        private TabLayout2 parent;
        private TabLayout2.TabView tabView;

        public TabLayout2 getParent() {
            return parent;
        }

        public TabView getTabView() {
            return tabView;
        }

        @Nullable
        public Object getTag() {
            return this.tag;
        }

        @NonNull
        public TabLayout2.Tab setTag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return this.customView;
        }

        @NonNull
        public TabLayout2.Tab setCustomView(@Nullable View view) {
            this.customView = view;
            this.updateView();
            return this;
        }

        @NonNull
        public TabLayout2.Tab setCustomView(@LayoutRes int resId) {
            LayoutInflater inflater = LayoutInflater.from(this.tabView.getContext());
            return this.setCustomView(inflater.inflate(resId, this.tabView, false));
        }

        @Nullable
        public Drawable getIcon() {
            return this.icon;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Nullable
        public CharSequence getText() {
            return this.text;
        }

        @NonNull
        public TabLayout2.Tab setIcon(@Nullable Drawable icon) {
            this.icon = icon;
            this.updateView();
            return this;
        }

        @NonNull
        public TabLayout2.Tab setIcon(@DrawableRes int resId) {
            if (this.parent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            } else {
                return this.setIcon(AppCompatResources.getDrawable(this.parent.getContext(), resId));
            }
        }

        @NonNull
        public TabLayout2.Tab setText(@Nullable CharSequence text) {
            this.text = text;
            this.updateView();
            return this;
        }

        @NonNull
        public TabLayout2.Tab setText(@StringRes int resId) {
            if (this.parent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            } else {
                return this.setText(this.parent.getResources().getText(resId));
            }
        }

        public void setTab_textSize(int tab_textSize) {
            this.tab_textSize = tab_textSize;
        }

        public void setTab_selected_textColor(int tab_selected_textColor) {
            this.tab_selected_textColor = tab_selected_textColor;
        }

        public void setTab_unselected_textColor(int tab_unselected_textColor) {
            this.tab_unselected_textColor = tab_unselected_textColor;
        }

        public void setTab_selected_textSize(int tab_selected_textSize) {
            this.tab_selected_textSize = tab_selected_textSize;
        }

        public void setTab_unselected_textSize(int tab_unselected_textSize) {
            this.tab_unselected_textSize = tab_unselected_textSize;
        }

        public void setBoldText(boolean boldText) {
            this.boldText = boldText;
        }

        public void setPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom){
            this.paddingLeft = paddingLeft;
            this.paddingTop = paddingTop;
            this.paddingRight = paddingRight;
            this.paddingBottom = paddingBottom;
        }

        public void select() {
            if (this.parent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            } else {
                this.parent.selectTab(position);
            }
        }

        public boolean isSelected() {
            if (this.parent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            } else {
                return this.parent.getSelectedTabPosition() == this.position;
            }
        }

        private void updateView() {
            if (this.tabView != null) {
                this.tabView.update();
            }
        }

        private void reset() {
            this.parent = null;
            this.tabView = null;
            this.tag = null;
            this.icon = null;
            this.text = null;
            this.position = -1;
            this.customView = null;
        }
    }


    public static class TabView extends FrameLayout {

        private TabLayout2.Tab tab;

        private ImageView iconView;
        private TextView textView;


        public TabView(Context context, Tab tab) {
            super(context);
            init(context);
            initAttr(tab);
        }

        public TabView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
            initAttr(attrs);
        }

        public TabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
            initAttr(attrs);
        }

        private void init(Context context){
            LayoutInflater.from(context).inflate(R.layout.tab_simple_view, this);
            iconView = findViewById(R.id.iv_icon);
            textView = findViewById(R.id.tv_text);
        }

        private void initAttr(AttributeSet attrs){
            tab = new Tab();

            if (attrs != null){
                TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.Tab);

                String tab_text = typedArray.getString(R.styleable.Tab_tab_text);
                int tab_selected_textColor = typedArray.getColor(R.styleable.Tab_tab_selected_textColor, ResourceUtils.getColor(R.color.black_tran90));
                int tab_unselected_textColor = typedArray.getColor(R.styleable.Tab_tab_unselected_textColor, ResourceUtils.getColor(R.color.black_tran60));
                int tab_textSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tab_textSize, SizeUtils.dipConvertPx(14));
                int tab_selected_textSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tab_selected_textSize, SizeUtils.dipConvertPx(14));
                int tab_unselected_textSize = typedArray.getDimensionPixelSize(R.styleable.Tab_tab_unselected_textSize, SizeUtils.dipConvertPx(14));
                boolean bold = typedArray.getBoolean(R.styleable.Tab_tab_textBold, false);
                Drawable tab_icon = typedArray.getDrawable(R.styleable.Tab_tab_icon);

                typedArray.recycle();

                tab.setBoldText(bold);
                tab.setText(tab_text);
                tab.setTab_selected_textColor(tab_selected_textColor);
                tab.setTab_unselected_textColor(tab_unselected_textColor);
                tab.setTab_textSize(tab_textSize);
                tab.setTab_selected_textSize(tab_selected_textSize);
                tab.setTab_unselected_textSize(tab_unselected_textSize);
                tab.setIcon(tab_icon);
            }

            initAttr(tab);
        }

        private void initAttr(Tab tab){
            this.tab = tab;
            this.tab.tabView = this;

            if (tab.paddingLeft > -1 && tab.paddingTop > -1 && tab.paddingRight > -1 && tab.paddingBottom > -1) {
                setPadding(tab.paddingLeft, tab.paddingTop, tab.paddingRight, tab.paddingBottom);
            }

            if (tab.icon != null){
                iconView.setVisibility(VISIBLE);
                iconView.setImageDrawable(tab.icon);
            }else {
                iconView.setVisibility(GONE);
            }
            if (tab.tab_textSize > 0) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tab.tab_textSize);
            }
            if (tab.tab_unselected_textColor != 0) {
                textView.setTextColor(tab.tab_unselected_textColor);
            }
            textView.getPaint().setFakeBoldText(tab.boldText);
            textView.setText(tab.text);

            //可点击
            setClickable(true);
        }

        @Override
        public boolean performClick() {
            boolean handled = super.performClick();
            if (this.tab != null) {
                if (!handled) {
                    this.playSoundEffect(0);
                }
                if (!tab.isSelected()) {
                    select(true);
                }
                this.tab.select();
                return true;
            } else {
                return handled;
            }
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(Tab.class.getName());
        }

        @TargetApi(14)
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(Tab.class.getName());
        }

        private int getCenterX(){
            return (getLeft() + getRight()) / 2;
        }

        public void update(){
            requestLayout();
        }

        public void select(boolean select){
            if (select){
                textView.setTextColor(tab.tab_selected_textColor);
                if (tab.tab_selected_textSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tab.tab_selected_textSize);
                }
            }else {
                textView.setTextColor(tab.tab_unselected_textColor);
                if (tab.tab_unselected_textSize > 0) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tab.tab_unselected_textSize);
                }
            }
        }

        public int getPosition() {
            return tab.getPosition();
        }
    }


    //
    public void setOnTabSelectChangedListener(OnTabSelectChangedListener onTabSelectChangedListener) {
        this.onTabSelectChangedListener = onTabSelectChangedListener;
    }

    public static class SimpleOnTabSelectChangedListener implements OnTabSelectChangedListener{

        @Override
        public void onTabSelected(Tab tab) {

        }

        @Override
        public void onTabUnselected(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    }

    public interface OnTabSelectChangedListener{

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);

        void onTabReselected(Tab tab);
    }
}
