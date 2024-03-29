package com.xiaomawang.commonlib.utils.dev.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;

import com.xiaomawang.commonlib.utils.dev.DevUtils;

import java.lang.reflect.Method;


/**
 * detail: 状态栏相关工具类
 * Created by Ttt
 */
public final class BarUtils {

    private BarUtils() {
    }

    private static final int DEFAULT_ALPHA = 112;
    private static final String TAG_COLOR = "TAG_COLOR";
    private static final String TAG_ALPHA = "TAG_ALPHA";
    private static final int TAG_OFFSET = -123;

    /**
     * 获取状态栏高度
     * @return the status bar's height
     */
    public static int getStatusBarHeight() {
        Resources resources = DevUtils.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 设置状态栏是否显示
     * @param activity
     * @param isVisible True to set status bar visible, false otherwise.
     */
    public static void setStatusBarVisibility(@NonNull final Activity activity, final boolean isVisible) {
        setStatusBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * 设置状态栏是否显示
     * @param window    The window.
     * @param isVisible True to set status bar visible, false otherwise.
     */
    public static void setStatusBarVisibility(@NonNull final Window window, final boolean isVisible) {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 判断状态栏是否显示
     * @param activity The activity.
     * @return true : yes, false : no
     */
    public static boolean isStatusBarVisible(@NonNull final Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        return (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
    }

    /**
     * 设置状态是否高亮模式
     * @param activity The activity.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    public static void setStatusBarLightMode(@NonNull final Activity activity, final boolean isLightMode) {
        setStatusBarLightMode(activity.getWindow(), isLightMode);
    }

    /**
     * 设置状态是否高亮模式
     * @param window The window.
     * @param isLightMode True to set status bar light mode, false otherwise.
     */
    public static void setStatusBarLightMode(@NonNull final Window window, final boolean isLightMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (isLightMode) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    /**
     * 添加状态栏同等高度到View的顶部
     * @param view The view.
     */
    public static void addMarginTopEqualStatusBarHeight(@NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Object haveSetOffset = view.getTag(TAG_OFFSET);
        if (haveSetOffset != null && (Boolean) haveSetOffset) return;
        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setTag(TAG_OFFSET, true);
    }

    /**
     * 减去状态栏同等高度
     * @param view The view.
     */
    public static void subtractMarginTopEqualStatusBarHeight(@NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Object haveSetOffset = view.getTag(TAG_OFFSET);
        if (haveSetOffset == null || !(Boolean) haveSetOffset) return;
        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin - getStatusBarHeight(), layoutParams.rightMargin, layoutParams.bottomMargin);
        view.setTag(TAG_OFFSET, false);
    }

    /**
     * 设置状态栏颜色
     * @param activity The activity.
     * @param color The status bar's color.
     */
    public static void setStatusBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        setStatusBarColor(activity, color, DEFAULT_ALPHA, false);
    }

    /**
     * 设置状态栏颜色
     * @param activity The activity.
     * @param color The status bar's color.
     * @param alpha The status bar's alpha which isn't the same as alpha in the color.
     */
    public static void setStatusBarColor(@NonNull final Activity activity, @ColorInt final int color, @IntRange(from = 0, to = 255) final int alpha) {
        setStatusBarColor(activity, color, alpha, false);
    }

    /**
     * 设置状态栏颜色
     * @param activity The activity.
     * @param color The status bar's color.
     * @param alpha The status bar's alpha which isn't the same as alpha in the color.
     * @param isDecor True to add fake status bar in DecorView,
     *                 false to add fake status bar in ContentView.
     */
    public static void setStatusBarColor(@NonNull final Activity activity, @ColorInt final int color, @IntRange(from = 0, to = 255) final int alpha, final boolean isDecor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        hideAlphaView(activity);
        transparentStatusBar(activity);
        addStatusBarColor(activity, color, alpha, isDecor);
    }

    /**
     * 设置状态栏颜色
     * @param fakeStatusBar The fake status bar view.
     * @param color The status bar's color.
     */
    public static void setStatusBarColor(@NonNull final View fakeStatusBar, @ColorInt final int color) {
        setStatusBarColor(fakeStatusBar, color, DEFAULT_ALPHA);
    }

    /**
     * 设置状态栏颜色
     * @param fakeStatusBar The fake status bar view.
     * @param color The status bar's color.
     * @param alpha The status bar's alpha which isn't the same as alpha in the color.
     */
    public static void setStatusBarColor(@NonNull final View fakeStatusBar, @ColorInt final int color, @IntRange(from = 0, to = 255) final int alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        fakeStatusBar.setVisibility(View.VISIBLE);
        transparentStatusBar((Activity) fakeStatusBar.getContext());
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = BarUtils.getStatusBarHeight();
        fakeStatusBar.setBackgroundColor(getStatusBarColor(color, alpha));
    }

    /**
     * 设置状态栏透明度
     * @param activity The activity.
     */
    public static void setStatusBarAlpha(@NonNull final Activity activity) {
        setStatusBarAlpha(activity, DEFAULT_ALPHA, false);
    }

    /**
     * 设置状态栏透明度
     * @param activity The activity.
     * @param alpha    The status bar's alpha.
     */
    public static void setStatusBarAlpha(@NonNull final Activity activity, @IntRange(from = 0, to = 255) final int alpha) {
        setStatusBarAlpha(activity, alpha, false);
    }

    /**
     * 设置状态栏透明度
     * @param activity The activity.
     * @param alpha The status bar's alpha.
     * @param isDecor True to add fake status bar in DecorView,
     *                 false to add fake status bar in ContentView.
     */
    public static void setStatusBarAlpha(@NonNull final Activity activity, @IntRange(from = 0, to = 255) final int alpha, final boolean isDecor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        hideColorView(activity);
        transparentStatusBar(activity);
        addStatusBarAlpha(activity, alpha, isDecor);
    }

    /**
     * 设置状态栏透明度
     * @param fakeStatusBar The fake status bar view.
     */
    public static void setStatusBarAlpha(@NonNull final View fakeStatusBar) {
        setStatusBarAlpha(fakeStatusBar, DEFAULT_ALPHA);
    }

    /**
     * 设置状态栏透明度
     * @param fakeStatusBar The fake status bar view.
     * @param alpha The status bar's alpha.
     */
    public static void setStatusBarAlpha(@NonNull final View fakeStatusBar, @IntRange(from = 0, to = 255) final int alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        fakeStatusBar.setVisibility(View.VISIBLE);
        transparentStatusBar((Activity) fakeStatusBar.getContext());
        ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = BarUtils.getStatusBarHeight();
        fakeStatusBar.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }

    /**
     * 设置状态栏的颜色
     * DrawLayout must add android:fitsSystemWindows="true"
     * @param activity The activity.
     * @param drawer The DrawLayout.
     * @param fakeStatusBar The fake status bar view.
     * @param color The status bar's color.
     * @param isTop True to set DrawerLayout at the top layer, false otherwise.
     */
    public static void setStatusBarColor4Drawer(@NonNull final Activity activity, @NonNull final DrawerLayout drawer, @NonNull final View fakeStatusBar, @ColorInt final int color, final boolean isTop) {
        setStatusBarColor4Drawer(activity, drawer, fakeStatusBar, color, DEFAULT_ALPHA, isTop);
    }

    /**
     * 设置状态栏的颜色
     * DrawLayout must add android:fitsSystemWindows="true"
     * @param activity The activity.
     * @param drawer The DrawLayout.
     * @param fakeStatusBar The fake status bar view.
     * @param color  The status bar's color.
     * @param alpha  The status bar's alpha which isn't the same as alpha in the color.
     * @param isTop  True to set DrawerLayout at the top layer, false otherwise.
     */
    public static void setStatusBarColor4Drawer(@NonNull final Activity activity, @NonNull final DrawerLayout drawer, @NonNull final View fakeStatusBar, @ColorInt final int color,
                                                @IntRange(from = 0, to = 255) final int alpha, final boolean isTop) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        drawer.setFitsSystemWindows(false);
        transparentStatusBar(activity);
        setStatusBarColor(fakeStatusBar, color, isTop ? alpha : 0);
        for (int i = 0, len = drawer.getChildCount(); i < len; i++) {
            drawer.getChildAt(i).setFitsSystemWindows(false);
        }
        if (isTop) {
            hideAlphaView(activity);
        } else {
            addStatusBarAlpha(activity, alpha, false);
        }
    }

    /**
     * 设置状态栏透明度
     * DrawLayout must add android:fitsSystemWindows="true"
     * @param activity The activity.
     * @param drawer drawerLayout
     * @param fakeStatusBar The fake status bar view.
     * @param isTop True to set DrawerLayout at the top layer, false otherwise.
     */
    public static void setStatusBarAlpha4Drawer(@NonNull final Activity activity, @NonNull final DrawerLayout drawer, @NonNull final View fakeStatusBar, final boolean isTop) {
        setStatusBarAlpha4Drawer(activity, drawer, fakeStatusBar, DEFAULT_ALPHA, isTop);
    }

    /**
     * 设置状态栏透明度
     * DrawLayout must add android:fitsSystemWindows="true"
     * @param activity The activity.
     * @param drawer drawerLayout
     * @param fakeStatusBar The fake status bar view.
     * @param alpha The status bar's alpha.
     * @param isTop True to set DrawerLayout at the top layer, false otherwise.
     */
    public static void setStatusBarAlpha4Drawer(@NonNull final Activity activity, @NonNull final DrawerLayout drawer, @NonNull final View fakeStatusBar,
                                                @IntRange(from = 0, to = 255) final int alpha, final boolean isTop) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        drawer.setFitsSystemWindows(false);
        transparentStatusBar(activity);
        setStatusBarAlpha(fakeStatusBar, isTop ? alpha : 0);
        for (int i = 0, len = drawer.getChildCount(); i < len; i++) {
            drawer.getChildAt(i).setFitsSystemWindows(false);
        }
        if (isTop) {
            hideAlphaView(activity);
        } else {
            addStatusBarAlpha(activity, alpha, false);
        }
    }

    /**
     * 设置状态栏颜色
     * @param activity
     * @param color
     * @param alpha
     * @param isDecor
     */
    private static void addStatusBarColor(final Activity activity, final int color, final int alpha, boolean isDecor) {
        ViewGroup parent = isDecor ? (ViewGroup) activity.getWindow().getDecorView() : (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeStatusBarView = parent.findViewWithTag(TAG_COLOR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(getStatusBarColor(color, alpha));
        } else {
            parent.addView(createColorStatusBarView(parent.getContext(), color, alpha));
        }
    }

    /**
     * 设置状态栏透明度
     * @param activity
     * @param alpha
     * @param isDecor
     */
    private static void addStatusBarAlpha(final Activity activity, final int alpha, boolean isDecor) {
        ViewGroup parent = isDecor ? (ViewGroup) activity.getWindow().getDecorView() : (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeStatusBarView = parent.findViewWithTag(TAG_ALPHA);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        } else {
            parent.addView(createAlphaStatusBarView(parent.getContext(), alpha));
        }
    }

    /**
     * 隐藏颜色View
     * @param activity
     */
    private static void hideColorView(final Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_COLOR);
        if (fakeStatusBarView == null) return;
        fakeStatusBarView.setVisibility(View.GONE);
    }

    /**
     * 隐藏透明度
     * @param activity
     */
    private static void hideAlphaView(final Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag(TAG_ALPHA);
        if (fakeStatusBarView == null) return;
        fakeStatusBarView.setVisibility(View.GONE);
    }

    /**
     * 获取状态栏颜色 = (传入颜色等，进行生成)
     * @param color
     * @param alpha
     * @return
     */
    private static int getStatusBarColor(final int color, final int alpha) {
        if (alpha == 0) return color;
        float a = 1 - alpha / 255f;
        int red = (color >> 16) & 0xff;
        int green = (color >> 8) & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return Color.argb(255, red, green, blue);
    }

    /**
     * 创建对应颜色的状态栏View
     * @param context
     * @param color
     * @param alpha
     * @return
     */
    private static View createColorStatusBarView(final Context context, final int color, final int alpha) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight()));
        statusBarView.setBackgroundColor(getStatusBarColor(color, alpha));
        statusBarView.setTag(TAG_COLOR);
        return statusBarView;
    }

    /**
     * 创建对应透明度的状态栏View
     * @param context
     * @param alpha
     * @return
     */
    private static View createAlphaStatusBarView(final Context context, final int alpha) {
        View statusBarView = new View(context);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight()));
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setTag(TAG_ALPHA);
        return statusBarView;
    }

    /**
     * 设置透明的状态栏
     * @param activity
     */
    public static void transparentStatusBar(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void transparentStatusBar(final Activity activity, boolean full, boolean dark) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option;
            if (full) {
                option = (dark ? View.SYSTEM_UI_FLAG_LAYOUT_STABLE : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            }else {
                option = (dark ? View.SYSTEM_UI_FLAG_LAYOUT_STABLE : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    // ======================
    // ===== action bar =====
    // ======================

    /**
     * 返回 ActionBase 高度
     * @return the action bar's height
     */
    public static int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (DevUtils.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, DevUtils.getContext().getResources().getDisplayMetrics());
        }
        return 0;
    }

    // ========================
    // === notification bar ===
    // ========================

    /**
     * 设置通知栏是否显示
     * <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    public static void setNotificationBarVisibility(final boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "expand" : "expandNotificationsPanel";
        } else {
            methodName = (Build.VERSION.SDK_INT <= 16) ? "collapse" : "collapsePanels";
        }
        invokePanels(methodName);
    }

    /**
     * 反射调用面板
     * @param methodName
     */
    private static void invokePanels(final String methodName) {
        try {
            @SuppressLint("WrongConstant")
            Object service = DevUtils.getContext().getSystemService("statusbar");
            @SuppressLint("PrivateApi")
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusBarManager.getMethod(methodName);
            expand.invoke(service);
        } catch (Exception ignore) {
        }
    }

    // ======================
    // === navigation bar ===
    // ======================

    /**
     * 获取 NavigationView 高度
     * @return the navigation bar's height
     */
    public static int getNavBarHeight() {
        Resources res = DevUtils.getContext().getResources();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * 设置导航栏是否可见(图标显示)
     * @param activity
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    public static void setNavBarVisibility(@NonNull final Activity activity, boolean isVisible) {
        setNavBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * 设置导航栏是否可见(图标显示)
     * @param window    The window.
     * @param isVisible True to set notification bar visible, false otherwise.
     */
    public static void setNavBarVisibility(@NonNull final Window window, boolean isVisible) {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            View decorView = window.getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int visibility = decorView.getSystemUiVisibility();
                decorView.setSystemUiVisibility(visibility & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    /**
     * 设置沉浸模式
     * @param activity The activity.
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setNavBarImmersive(@NonNull final Activity activity) {
        setNavBarImmersive(activity.getWindow());
    }

    /**
     * 设置顶部沉浸模式
     * @param window The window.
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setNavBarImmersive(@NonNull final Window window) {
        View decorView = window.getDecorView();
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    /**
     * 判断顶部状态栏(图标)是否显示
     * @param activity The activity.
     * @return true : yes, false : no
     */
    public static boolean isNavBarVisible(@NonNull final Activity activity) {
        return isNavBarVisible(activity.getWindow());
    }

    /**
     * 判断顶部状态栏(图标)是否显示
     * @param window The window.
     * @return true : yes, false : no
     */
    public static boolean isNavBarVisible(@NonNull final Window window) {
        boolean isNoLimits = (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0;
        if (isNoLimits) return false;
        View decorView = window.getDecorView();
        int visibility = decorView.getSystemUiVisibility();
        return (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
    }

    // ==

    /**
     * 设置是否显示状态栏图标等
     * @param activity
     * @param isVisible
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setNavBar(Activity activity, boolean isVisible){
        setNavBar(activity.getWindow(), isVisible);
    }

    /**
     * 设置是否显示状态栏图标等
     * @param window The window.
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setNavBar(@NonNull final Window window, boolean isVisible) {
        if (isVisible){
            // 显示状态栏
            WindowManager.LayoutParams params = window.getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            // 隐藏状态栏
            WindowManager.LayoutParams params = window.getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
