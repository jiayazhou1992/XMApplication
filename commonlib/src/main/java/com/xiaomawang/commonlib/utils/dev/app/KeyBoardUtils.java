package com.xiaomawang.commonlib.utils.dev.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xiaomawang.commonlib.utils.dev.DevUtils;
import com.xiaomawang.commonlib.utils.dev.LogPrintUtils;

import java.lang.reflect.Field;

/**
 * detail: 软键盘相关辅助类
 * Created by Ttt
 */
public final class KeyBoardUtils {

	private KeyBoardUtils() {
	}

	// 日志TAG
	private static final String TAG = KeyBoardUtils.class.getSimpleName();

	/** 默认延迟时间 */
	private static int DELAY_MILLIS = 300;
	/** 键盘显示 */
	public static final int KEYBOARD_DISPLAY = 930;
	/** 键盘隐藏 */
	public static final int KEYBOARD_HIDE = 931;


	/**
	 * 避免输入法面板遮挡 manifest.xml 中 activity 中设置
	 * android:windowSoftInputMode="adjustPan"
	 * android:windowSoftInputMode="adjustUnspecified|stateHidden"
	 */

	// == ----------------------------------------- ==

	/**
	 * 设置延迟时间
	 * @param delayMillis
	 */
	public static void setDelayMillis(int delayMillis) {
		DELAY_MILLIS = delayMillis;
	}

	// ================
	// == 打开软键盘 ==
	// ================
	
	/**
	 * 打开软键盘
	 * @param mEditText 输入框
	 */
	public static void openKeyboard(EditText mEditText) {
		if (mEditText != null) {
			try {
				InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "openKeyboard");
			}
		}
	}

	/**
	 * 打开软键盘
	 * @param mEditText
	 * @param vHandler
	 */
	public static void openKeyboard(final EditText mEditText, Handler vHandler){
		openKeyboard(mEditText, vHandler, DELAY_MILLIS);
	}

	/**
	 * 打开软键盘
	 * @param mEditText
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void openKeyboard(final EditText mEditText, Handler vHandler, int delayMillis){
		if (vHandler != null && mEditText != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					try {
						mEditText.requestFocus();
						mEditText.setSelection(mEditText.getText().toString().length());
					} catch (Exception e){
					}
					openKeyboard(mEditText);
				}
			}, delayMillis);
		}
	}

	// -

	/**
	 * 打开软键盘
	 */
	public static void openKeyboard() {
		try {
			InputMethodManager imm = (InputMethodManager) DevUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "openKeyboard");
		}
	}

	/**
	 * 打开软键盘
	 * @param vHandler
	 */
	public static void openKeyboard(Handler vHandler){
		openKeyboard(vHandler, DELAY_MILLIS);
	}

	/**
	 * 打开软键盘
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void openKeyboard(Handler vHandler, int delayMillis){
		if (vHandler != null && DevUtils.getContext() != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					openKeyboard();
				}
			}, delayMillis);
		}
	}

	// ================
	// == 关闭软键盘 ==
	// ================

	/**
	 * 关闭软键盘
	 * @param mEditText 输入框
	 */
	public static void closeKeyboard(EditText mEditText) {
		if (mEditText != null) {
			try {
				InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "closeKeyboard");
			}
		}
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeKeyboard() {
		if (DevUtils.getContext() != null) {
			try {
				InputMethodManager imm = (InputMethodManager) DevUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "closeKeyboard");
			}
		}
	}

	/**
	 * 关闭软键盘
	 * @param mActivity 当前页面
	 */
	public static void closeKeyboard(Activity mActivity) {
		if (mActivity != null) {
			try {
				InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mActivity.getWindow().peekDecorView().getWindowToken(), 0);
				//imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "closeKeyboard");
			}
		}
	}

	/**
	 * 关闭dialog中打开的键盘
	 * @param mDialog
	 */
	public static void closeKeyboard(Dialog mDialog) {
		if (mDialog != null) {
			try {
				InputMethodManager imm = (InputMethodManager) mDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDialog.getWindow().peekDecorView().getWindowToken(), 0);
				//imm.hideSoftInputFromWindow(mDialog.getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {
				LogPrintUtils.eTag(TAG, e, "closeKeyboard");
			}
		}
	}

	// ==

	/**
	 * 关闭软键盘 - 特殊处理
	 * @param mEditText
	 * @param mDialog
	 */
	public static void closeKeyBoardSpecial(EditText mEditText, Dialog mDialog){
		try {
			// 关闭输入法
			closeKeyboard();
			// 关闭输入法
			closeKeyboard(mEditText);
			// 关闭输入法
			closeKeyboard(mDialog);
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "closeKeyBoardSpecial");
		}
	}

	/**
	 * 关闭软键盘 - 特殊处理
	 * @param mEditText
	 * @param mDialog
	 * @param vHandler
	 */
	public static void closeKeyBoardSpecial(final EditText mEditText, final Dialog mDialog, Handler vHandler){
		closeKeyBoardSpecial(mEditText, mDialog, vHandler, DELAY_MILLIS);
	}

	/**
	 * 关闭软键盘 - 特殊处理(两个都关闭)
	 * @param mEditText
	 * @param mDialog
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void closeKeyBoardSpecial(final EditText mEditText, final Dialog mDialog, Handler vHandler, int delayMillis){
		if (vHandler != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeKeyBoardSpecial(mEditText, mDialog);
				}
			}, delayMillis);
		}
	}

	// -

	/**
	 * 关闭软键盘
	 * @param mEditText
	 * @param vHandler
	 */
	public static void closeKeyboard(final EditText mEditText, Handler vHandler){
		closeKeyboard(mEditText, vHandler, DELAY_MILLIS);
	}

	/**
	 * 关闭软键盘
	 * @param mEditText
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void closeKeyboard(final EditText mEditText, Handler vHandler, int delayMillis){
		if (vHandler != null && mEditText != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeKeyboard(mEditText);
				}
			}, delayMillis);
		}
	}

	/**
	 * 关闭软键盘
	 * @param vHandler
	 */
	public static void closeKeyboard(Handler vHandler){
		closeKeyboard(vHandler, DELAY_MILLIS);
	}

	/**
	 * 关闭软键盘
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void closeKeyboard(Handler vHandler, int delayMillis){
		if (vHandler != null && DevUtils.getContext() != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeKeyboard();
				}
			}, delayMillis);
		}
	}

	/**
	 * 关闭软键盘
	 * @param mActivity
	 * @param vHandler
	 */
	public static void closeKeyboard(final Activity mActivity, Handler vHandler){
		closeKeyboard(mActivity, vHandler, DELAY_MILLIS);
	}

	/**
	 * 关闭软键盘
	 * @param mActivity
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void closeKeyboard(final Activity mActivity, Handler vHandler, int delayMillis){
		if (vHandler != null && mActivity != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeKeyboard(mActivity);
				}
			}, delayMillis);
		}
	}

	/**
	 * 关闭软键盘
	 * @param mDialog
	 * @param vHandler
	 */
	public static void closeKeyboard(final Dialog mDialog, Handler vHandler){
		closeKeyboard(mDialog, vHandler, DELAY_MILLIS);
	}

	/**
	 * 关闭软键盘
	 * @param mDialog
	 * @param vHandler
	 * @param delayMillis
	 */
	public static void closeKeyboard(final Dialog mDialog, Handler vHandler, int delayMillis){
		if (vHandler != null && mDialog != null){
			// 延迟打开
			vHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					closeKeyboard(mDialog);
				}
			}, delayMillis);
		}
	}
	
	// == ----------------------------------------- ==

	// 下面暂时无法使用，缺少判断键盘是否显示，否则和自动切换无区别
	// InputMethodManager.isActive()   (无法获取)
	// Activity.getWindow().getAttributes().softInputMode  (有些版本可以，不适用)
	// ==----==

	/**
	 * 自动切换键盘状态，如果键盘显示了则隐藏，隐藏着显示
	 */
	public static void toggleKeyboard() {
		// 程序启动后，自动弹出软键盘，可以通过设置一个时间函数来实现，不能再onCreate里写
		try {
			InputMethodManager imm = (InputMethodManager) DevUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		} catch (Exception e) {
			LogPrintUtils.eTag(TAG, e, "toggleKeyboard");
		}
	}

	// ==========  点击非EditText 则隐藏输入法  ===============

	/**
	 * 某个View里面的子View的View判断
	 * @param view
	 */
	public static void judgeView(View view, final Activity activity) {
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					closeKeyboard(activity);
					return false;
				}
			});
		}
		// --
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				judgeView(innerView, activity);
			}
		}
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
			int[] l = {0, 0};
			v.getLocationInWindow(l);
			int left = l[0];   //得到输入框在屏幕中上下左右的位置
			int top = l[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击位置如果是EditText的区域，忽略它，不收起键盘。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略
		return false;
	}

	// ==========  输入法隐藏显示  ===============

	/**
	 * 判断软键盘是否可见
	 * @param activity
	 * @return true : 可见, false : 不可见
	 */
	public static boolean isSoftInputVisible(final Activity activity) {
		int minHeightOfSoftInput = ScreenUtils.getScreenHeight() / 3;
		return isSoftInputVisible(activity, minHeightOfSoftInput);
	}

	/**
	 * 判断软键盘是否可见
	 * @param activity
	 * @param minHeightOfSoftInput 软键盘最小高度
	 * @return true : 可见, false : 不可见
	 */
	public static boolean isSoftInputVisible(final Activity activity, final int minHeightOfSoftInput) {
		return getContentViewInvisibleHeight(activity) >= minHeightOfSoftInput;
	}

	/**
	 * 计算View的宽度高度
	 * @param activity
	 * @return
	 */
	private static int getContentViewInvisibleHeight(final Activity activity) {
		try {
			final View contentView = activity.findViewById(android.R.id.content);
			Rect rect = new Rect();
			contentView.getWindowVisibleDisplayFrame(rect);
			return contentView.getRootView().getHeight() - rect.height();
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "getContentViewInvisibleHeight");
			return 0;
		}
	}

	/**
	 * 注册软键盘改变监听器
	 * @param activity
	 * @param listener listener
	 */
	public static void registerSoftInputChangedListener(final Activity activity, final OnSoftInputChangedListener listener) {
		try {
			// 获取根View
			final View contentView = activity.findViewById(android.R.id.content);
			// 添加事件
			contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (listener != null) {
						// 获取高度
						int height = getContentViewInvisibleHeight(activity);
						// 判断是否相同
						listener.onSoftInputChanged(height >= 200, height);
					}
				}
			});
		} catch (Exception e){
			LogPrintUtils.eTag(TAG, e, "registerSoftInputChangedListener");
		}
	}

	/**
	 * 注册软键盘改变监听器
	 * @param activity
	 * @param listener listener
	 */
	private static boolean keyBoardVisible;

	public static void registerSoftInputChangedListener2(final Activity activity, final OnSoftInputChangedListener listener) {
		final View decorView = activity.getWindow().getDecorView();
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (listener != null){
					try {
						Rect rect = new Rect();
						decorView.getWindowVisibleDisplayFrame(rect);
						// 计算出可见屏幕的高度
						int displayHight = rect.bottom - rect.top;
						// 获取屏幕整体的高度
						int hight = decorView.getHeight();
						// 获取键盘高度
						int keyboardHeight = hight - displayHight;
						// 计算一定比例
						boolean visible = (double) displayHight / hight < 0.8;
						// 判断是否显示
						if (visible != keyBoardVisible) {
							keyBoardVisible = visible;
							listener.onSoftInputChanged(visible, keyboardHeight);
						}
					} catch (Exception e){
						LogPrintUtils.eTag(TAG, e, "registerSoftInputChangedListener2");
					}
				}
			}
		});
	}

	public static void registerSoftInputChangedListener3(final View decorView, final OnSoftInputChangedListener listener) {
		decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (listener != null){
					try {
						Rect rect = new Rect();
						decorView.getWindowVisibleDisplayFrame(rect);
						// 计算出可见屏幕的高度
						int displayHight = rect.bottom - rect.top;
						// 获取屏幕整体的高度
						int hight = decorView.getHeight();
						// 获取键盘高度
						int keyboardHeight = hight - displayHight;
						// 计算一定比例
						boolean visible = (double) displayHight / hight < 0.8;
						// 判断是否显示
						if (visible != keyBoardVisible) {
							keyBoardVisible = visible;
							listener.onSoftInputChanged(visible, keyboardHeight);
						}
					} catch (Exception e){
						LogPrintUtils.eTag(TAG, e, "registerSoftInputChangedListener2");
					}
				}
			}
		});
	}

	/** 输入法弹出、隐藏改变事件 */
	public interface OnSoftInputChangedListener {

		/**
		 * 输入法弹出、隐藏改变通知
		 * @param visible
		 * @param height
		 */
		void onSoftInputChanged(boolean visible, int height);
	}

	// ==

	/**
	 * 修复软键盘内存泄漏 在 Activity.onDestroy() 中使用
	 * @param context
	 */
	public static void fixSoftInputLeaks(final Context context) {
		if (context == null) return;
		try {
			InputMethodManager imm = (InputMethodManager) DevUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			String[] strArr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
			for (int i = 0; i < 3; i++) {
				try {
					Field declaredField = imm.getClass().getDeclaredField(strArr[i]);
					if (declaredField == null) continue;
					if (!declaredField.isAccessible()) {
						declaredField.setAccessible(true);
					}
					Object obj = declaredField.get(imm);
					if (obj == null || !(obj instanceof View)) continue;
					View view = (View) obj;
					if (view.getContext() == context) {
						declaredField.set(imm, null);
					} else {
						return;
					}
				} catch (Throwable th) {
				}
			}
		} catch (Exception e){
		}
	}
}
