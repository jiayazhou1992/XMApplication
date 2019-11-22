package com.xiaomawang.commonlib.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.base.XMActivity;
import com.xiaomawang.commonlib.utils.dev.app.BarUtils;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;


public class TitleManager implements View.OnClickListener{

    private static final String TAG = "TitleManager";

    private Context mContext;

    private ViewGroup rootLayout;

    private ImageView iv_back;

    private TextView tv_left;

    private TextView tv_title;

    private TextView tv_right;

    private TextView tv_right2;

    private View fakerBar;

    public TitleManager(Context context, View contentView){
        mContext = context;
        try {
            rootLayout = contentView.findViewById(R.id.title_layout);
            iv_back = contentView.findViewById(R.id.iv_back);
            tv_left = contentView.findViewById(R.id.tv_left);
            tv_title = contentView.findViewById(R.id.tv_title);
            tv_right = contentView.findViewById(R.id.tv_right);
            tv_right2 = contentView.findViewById(R.id.tv_right2);
            fakerBar = contentView.findViewById(R.id.v_fakerBar);

            setFakerBarColor(R.color.white);

            if (iv_back != null) {
                iv_back.setOnClickListener(this);
            }
            if (tv_left != null) {
                tv_left.setOnClickListener(this);
            }

        }catch (Exception e){
            Log.e(TAG,"--->"+e.getMessage());
        }

    }

    public ViewGroup getRootLayout() {
        return rootLayout;
    }

    public ImageView getIv_back() {
        return iv_back;
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public TextView getTv_right() {
        return tv_right;
    }

    public TextView getTv_right2() {
        return tv_right2;
    }

    public TextView getTv_left() {
        return tv_left;
    }

    public View getFakerBar() {
        return fakerBar;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_back||id == R.id.tv_left){
            XMActivity.getForegroundActivity().onBackPressed();
        }
    }

    public void setTitleBg(@ColorRes int colorId){
        if (rootLayout != null) {
            rootLayout.setBackgroundColor(ResourceUtils.getColor(colorId));
        }
    }

    public void setFakerBarColor(@ColorRes int colorId){
        if (fakerBar != null) {
            BarUtils.setStatusBarColor(fakerBar,ResourceUtils.getColor(colorId),0);
        }
    }

    public void setFakerBarColor(@ColorRes int colorId, @IntRange(from = 0,to = 255) int alpha){
        if (fakerBar != null) {
            BarUtils.setStatusBarColor(fakerBar,ResourceUtils.getColor(colorId),alpha);
        }
    }

    public void setStateBarColor(@ColorRes int colorId){
        if (fakerBar != null) {
            fakerBar.setVisibility(View.GONE);
        }
        Window window = ((Activity)mContext).getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(ResourceUtils.getColor(colorId));
        }
    }

    public void setBackgroudColor(@ColorRes int backgroudColor){
        if (rootLayout != null) {
            rootLayout.setBackgroundColor(ResourceUtils.getColor(backgroudColor));
        }
    }

    public void setBackgroudDrawable(@DrawableRes int backgroudDrawable){
        if (rootLayout != null) {
            rootLayout.setBackground(ResourceUtils.getDrawable(backgroudDrawable));
        }
    }

    public void setTitleSize(int textSize){
        if (tv_title != null) {
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    public void setTitle(CharSequence title){
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    public void setRight1(CharSequence right1){
        if (tv_right != null) {
            tv_right.setText(right1);
        }
    }

    public void setRight1Hint(CharSequence right1Hint){
        if (tv_right != null) {
            tv_right.setHint(right1Hint);
        }
    }

    public void setRight1Icon(@DrawableRes int drawable, int drawablePadding){
        if (tv_right != null) {
            tv_right.setCompoundDrawablesWithIntrinsicBounds(0,0,drawable,0);
            tv_right.setCompoundDrawablePadding(drawablePadding);
        }
    }

    public void recycle(){
        mContext = null;
        rootLayout = null;
        iv_back = null;
        tv_left = null;
        tv_title = null;
        tv_right = null;
        tv_right2 = null;
        fakerBar = null;
    }
}
