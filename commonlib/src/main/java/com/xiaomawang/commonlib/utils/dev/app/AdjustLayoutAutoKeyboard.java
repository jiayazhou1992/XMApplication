package com.xiaomawang.commonlib.utils.dev.app;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/** 可以修复adjustResize在透明状态栏下失效,也可以当做软键盘监听（在adjustResize下）
 * Created by jiayazhou on 2018/1/16.
 */

public class AdjustLayoutAutoKeyboard {
    private static final String TAG = "AdjustLayoutAutoKeyboar";

    public static void assistActivity(View content) {
        new AdjustLayoutAutoKeyboard(content);
    }

    public static void assistActivity(Activity content) {
        new AdjustLayoutAutoKeyboard(content);
    }

    public static void assistActivity(View content, OnLayoutCallback... onLayoutCallback) {
        new AdjustLayoutAutoKeyboard(content, onLayoutCallback);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;
    private OnLayoutCallback[] onLayoutCallbacks;

    private AdjustLayoutAutoKeyboard(View content) {
        this(content, new OnLayoutCallback[]{});
    }

    private AdjustLayoutAutoKeyboard(Activity content) {
        this(content.findViewById(android.R.id.content), new OnLayoutCallback[]{});
    }

    private AdjustLayoutAutoKeyboard(View content, OnLayoutCallback... onLayoutCallback) {
        if (content != null) {
            mChildOfContent = content;
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = mChildOfContent.getLayoutParams();

            this.onLayoutCallbacks = onLayoutCallback;
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        frameLayoutParams.height = mChildOfContent.getHeight();
        Log.i(TAG, "usableHeightNow " + usableHeightNow + " usableHeightPrevious " + usableHeightPrevious);
        if (usableHeightNow != usableHeightPrevious && usableHeightPrevious != 0) {
            //如果两次高度不一致
            //将计算的可视高度设置成视图的高度
            frameLayoutParams.height += (usableHeightNow - usableHeightPrevious);
            mChildOfContent.requestLayout();//请求重新布局
            //mChildOfContent.layout(mChildOfContent.getLeft(), mChildOfContent.getTop(), mChildOfContent.getRight(), mChildOfContent.getTop() + frameLayoutParams.height);
        }
        usableHeightPrevious = usableHeightNow;
    }

    private int computeUsableHeight() {
        //计算视图可视高度
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }

    public interface OnLayoutCallback{
        /**
         *
         * @param narrow true 键盘弹起
         */
        void layout(boolean narrow);
    }
}
