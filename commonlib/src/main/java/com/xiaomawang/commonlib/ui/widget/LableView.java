package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

public class LableView extends LinearLayout {

    private View contentView;

    private TextView tv_lable;

    private TextView tv_content;

    public LableView(Context context) {
        super(context);
        init(null);
    }

    public LableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setOrientation(HORIZONTAL);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_lable_view,this);
        tv_lable = contentView.findViewById(R.id.tv_lable);
        tv_content = contentView.findViewById(R.id.tv_content);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.LableView);

            String lableStr = typedArray.getString(R.styleable.LableView_lable_Str);
            String contentStr = typedArray.getString(R.styleable.LableView_contentStr);
            String contentHint = typedArray.getString(R.styleable.LableView_contentHint);
            int contentHintColor = typedArray.getColor(R.styleable.LableView_label_contentHintColor, ResourceUtils.getColor(R.color.black_tran38));
            int content_type = typedArray.getInt(R.styleable.LableView_lable_content_type,-1);
            int label_textsize = typedArray.getDimensionPixelSize(R.styleable.LableView_lable_textsize,-1);
            int label_contentSize = typedArray.getDimensionPixelSize(R.styleable.LableView_lable_contentSize,-1);
            int label_contentColor = typedArray.getColor(R.styleable.LableView_lable_contentColor,-1);
            int label_textcolor = typedArray.getColor(R.styleable.LableView_lable_textcolor,-1);
            int label_iconId = typedArray.getResourceId(R.styleable.LableView_lable_icon,-1);
            int label_right_icon = typedArray.getResourceId(R.styleable.LableView_label_right_icon, R.drawable.ic_go);
            boolean lable_contentBold = typedArray.getBoolean(R.styleable.LableView_lable_contentBold,false);
            boolean label_bold = typedArray.getBoolean(R.styleable.LableView_label_bold, false);
            boolean label_contentSingle = typedArray.getBoolean(R.styleable.LableView_label_contentSingle, false);
            int lable_gravity = typedArray.getInt(R.styleable.LableView_lable_gravity,1);
            int label_width = typedArray.getDimensionPixelSize(R.styleable.LableView_label_width, 0);
            int label_contentGravity = typedArray.getInt(R.styleable.LableView_label_content_gravity, 0);

            typedArray.recycle();

            tv_lable.setText(lableStr);

            tv_content.setHintTextColor(contentHintColor);
            tv_content.setText(contentStr);

            switch (content_type){
                case 0://spanner
                    if (StringUtils.isEmpty(contentHint)) {
                        tv_content.setHint(R.string.select);
                    }else {
                        tv_content.setHint(contentHint);
                    }
                    tv_content.setCompoundDrawablesWithIntrinsicBounds(0, 0, label_right_icon == 0 ? R.drawable.ic_more1 : label_right_icon, 0);
                    break;
                case 1://jump
                    if (!StringUtils.isEmpty(contentHint)){
                        tv_content.setHint(contentHint);
                    }
                    tv_content.setCompoundDrawablesWithIntrinsicBounds(0, 0, label_right_icon, 0);

                    break;
                default:
                    if (!StringUtils.isEmpty(contentHint)){
                        tv_content.setHint(contentHint);
                    }
                    break;
            }

            if (label_textsize!=-1){
                tv_lable.setTextSize(TypedValue.COMPLEX_UNIT_PX,label_textsize);
            }
            if (label_textcolor!=-1){
                tv_lable.setTextColor(label_textcolor);
            }
            if (label_contentSize!=-1){
                tv_content.setTextSize(TypedValue.COMPLEX_UNIT_PX,label_contentSize);
            }
            if (label_contentColor!=-1){
                tv_content.setTextColor(label_contentColor);
            }
            if (label_iconId!=-1){
                tv_lable.setCompoundDrawablesWithIntrinsicBounds(label_iconId,0,0,0);
            }
            if (lable_contentBold) {
                tv_content.setTypeface(Typeface.DEFAULT_BOLD);
            }else {
                tv_content.setTypeface(Typeface.DEFAULT);
            }
            if (label_bold){
                tv_lable.setTypeface(Typeface.DEFAULT_BOLD);
            }else {
                tv_lable.setTypeface(Typeface.DEFAULT);
            }

            tv_content.setSingleLine(label_contentSingle);
            tv_content.setEllipsize(TextUtils.TruncateAt.END);

            LayoutParams params = new LayoutParams(label_width == 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : label_width, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (lable_gravity == 1){
                params.gravity = Gravity.CENTER_VERTICAL;
            }else if (lable_gravity == 0){
                params.gravity = Gravity.TOP;
            }else if (lable_gravity == 2){
                params.gravity = Gravity.BOTTOM;
            }
            tv_lable.setLayoutParams(params);

            tv_content.setGravity(label_contentGravity == 0 ? (Gravity.CENTER_VERTICAL | Gravity.RIGHT) : (Gravity.CENTER_VERTICAL | Gravity.LEFT));
        }
    }

    public void setLableStr(CharSequence lableStr){
        tv_lable.setText(lableStr);
    }

    public void setContentStr(CharSequence contentStr){
        tv_content.setText(contentStr);
    }

    public void setContentHintStr(CharSequence contentHintStr){
        tv_content.setHint(contentHintStr);
    }

    public String getContentStr(){
        return tv_content==null?"":tv_content.getText().toString();
    }

    public String getContentHintStr(){
        return tv_content==null?"":tv_content.getHint().toString();
    }

    public boolean contentIsEmpty(){
        return StringUtils.isEmpty(tv_content.getText().toString());
    }
}
