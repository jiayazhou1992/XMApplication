package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;


public class VerticalLableView extends LinearLayout {

    private View contentView;

    private TextView tv_lable;

    private TextView tv_content;

    public VerticalLableView(Context context) {
        super(context);
        init(null);
    }

    public VerticalLableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public VerticalLableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VerticalLableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setOrientation(VERTICAL);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_verticallable_view,this);
        tv_lable = contentView.findViewById(R.id.tv_lable);
        tv_content = contentView.findViewById(R.id.tv_content);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.VerticalLableView);

            String lableStr = typedArray.getString(R.styleable.VerticalLableView_vl_lable_Str);
            String contentStr = typedArray.getString(R.styleable.VerticalLableView_vl_contentStr);
            String contentHint = typedArray.getString(R.styleable.VerticalLableView_vl_contentHint);
            int contentSize = typedArray.getDimensionPixelSize(R.styleable.VerticalLableView_vl_contentSize,getContext().getResources().getDimensionPixelSize(R.dimen.textsize1));
            int contentColor = typedArray.getColor(R.styleable.VerticalLableView_vl_contentColor, Color.BLACK);
            boolean contentBold = typedArray.getBoolean(R.styleable.VerticalLableView_vl_contentBold,true);

            typedArray.recycle();

            tv_lable.setText(lableStr);
            if (contentBold) {
                tv_content.setTypeface(Typeface.DEFAULT_BOLD);
            }else {
                tv_content.setTypeface(Typeface.DEFAULT);
            }
            tv_content.setText(contentStr);
            tv_content.setTextSize(TypedValue.COMPLEX_UNIT_PX,contentSize);
            tv_content.setTextColor(contentColor);

            if (!StringUtils.isEmpty(contentHint)){
                tv_content.setHint(contentHint);
            }
        }
    }

    public void setLableStr(CharSequence lableStr){
        tv_lable.setText(lableStr);
    }

    public void setContentStr(CharSequence contentStr){
        tv_content.setText(contentStr);
    }
}
