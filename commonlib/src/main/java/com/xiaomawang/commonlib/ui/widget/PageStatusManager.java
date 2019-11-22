package com.xiaomawang.commonlib.ui.widget;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.xiaomawang.commonlib.R;

public class PageStatusManager {
    private static final String TAG = "PageStatusManager";

    private ViewGroup ll_status;

    private ImageView iv_status;

    private TextView tv_status_msg;

    private ProgressBar progressBar_status;

    public PageStatusManager(View rootView) {
        try {
            ll_status = rootView.findViewById(R.id.ll_status);
            iv_status = rootView.findViewById(R.id.iv_status);
            tv_status_msg = rootView.findViewById(R.id.tv_status_msg);
            progressBar_status = rootView.findViewById(R.id.progressBar_status);

        }catch (Exception e){
            Log.e(TAG,"--->"+e.getMessage());
        }
    }

    public void setStatusBackColor(@ColorRes int backColor){
        if (ll_status!=null){
            ll_status.setBackgroundResource(backColor);
        }
    }


    public void showStatus(boolean show){
        if (ll_status != null) {
            ll_status.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show){
                tv_status_msg.setText("暂无数据");
            }
        }
    }

    public void showStatus(boolean show, String msg){
        if (ll_status != null) {
            ll_status.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show){
                tv_status_msg.setText(msg);
            }
        }
    }

//    public void showStatus(int code, String msg){
//        boolean show = code != BaseConstans.SUCCESS;
//        int imgId = R.drawable.ic_pagestatus_empty;
//        if (code == BaseConstans.NO_NET || code == BaseConstans.NO_HOST){
//            imgId = R.drawable.ic_pagestatus_networkerror;
//        }else if (code == BaseConstans.TIME_OUT){
//            imgId = R.drawable.ic_pagestatus_loaderror;
//        }
//
//        showStatus(show, msg, imgId);
//    }

    public void showStatus(boolean show, String msg, @DrawableRes int img){
        if (ll_status != null) {
            iv_status.setImageResource(img);
            ll_status.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show){
                tv_status_msg.setText(msg);
            }
        }
    }

    public void showLoading(boolean loading){
        if (progressBar_status != null){
            progressBar_status.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    public void setReloadClickListener(View.OnClickListener reloadClickListener){
        iv_status.setOnClickListener(reloadClickListener);
        tv_status_msg.setOnClickListener(reloadClickListener);
    }

    public void recycle(){
        ll_status = null;
        iv_status = null;
        tv_status_msg = null;
        progressBar_status = null;
    }
}
