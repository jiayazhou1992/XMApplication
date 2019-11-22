package com.xiaomawang.commonlib.widget.picselect;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ScreenUtils;

public class TypeDialog extends Dialog implements View.OnClickListener{

    private TextView tv_camera, tv_album, tv_cancel;

    private OnClickListener onClickListener;


    public TypeDialog(@NonNull Context context) {
        super(context);
    }

    public TypeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TypeDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AndroidRuntimeException: requestFeature() must be called before adding content
        requestWindowFeature(Window.FEATURE_NO_TITLE);//这个Widow是没有Title的

        setCanceledOnTouchOutside(true);//触摸屏幕取消窗体
        setCancelable(true);//按返回键取消窗体

        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        WindowManager.LayoutParams wl = window.getAttributes();

        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        onWindowAttributesChanged(wl);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_selectpic_type, null);
        setContentView(contentView);

        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth();
        contentView.setLayoutParams(layoutParams);


        tv_camera = findViewById(R.id.tv_camera);
        tv_album = findViewById(R.id.tv_album);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(this);
        tv_album.setOnClickListener(this);
        tv_camera.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.tv_camera){
            if (onClickListener != null){
                onClickListener.onClick(0);
                setOnDismissListener(null);
            }
        }else if (v.getId() == R.id.tv_album){
            if (onClickListener != null){
                onClickListener.onClick(1);
                setOnDismissListener(null);
            }
        }else {

        }

        dismiss();
    }

    public interface OnClickListener{
        void onClick(int type);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
