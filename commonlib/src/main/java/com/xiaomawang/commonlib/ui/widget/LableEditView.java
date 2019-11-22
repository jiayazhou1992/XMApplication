package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputLayout;
import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;
import com.xiaomawang.commonlib.utils.dev.common.StringUtils;

public class LableEditView extends LinearLayout {

    private View contentView;

    private TextView tv_lable;

    private TextInputLayout textInputLayout;

    private EditText et_content;

    private TextView tv_tip;

    private boolean designHintEnabled;


    public LableEditView(Context context) {
        super(context);
        init(null);
    }

    public LableEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LableEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LableEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        setOrientation(HORIZONTAL);
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_lable_edit_view,this);
        tv_lable = contentView.findViewById(R.id.tv_lable);
        textInputLayout = contentView.findViewById(R.id.textInputLayout);
        et_content = contentView.findViewById(R.id.et_content);
        tv_tip = contentView.findViewById(R.id.tv_tip);

        if (attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.LableEditView);

            String lableStr = typedArray.getString(R.styleable.LableEditView_le_lable_Str);
            int labelSize = typedArray.getDimensionPixelSize(R.styleable.LableEditView_le_labelSize, ResourceUtils.getDimensionPX(R.dimen.textSize14));
            int labelColor = typedArray.getColor(R.styleable.LableEditView_le_labelColor, ResourceUtils.getColor(R.color.black_tran87));
            int labelGravity = typedArray.getInt(R.styleable.LableEditView_le_labelGravity, 2);
            String contentHintStr = typedArray.getString(R.styleable.LableEditView_le_content_hint);
            int contentSize = typedArray.getDimensionPixelSize(R.styleable.LableEditView_le_contentSize, ResourceUtils.getDimensionPX(R.dimen.textsize1));
            int contentColor = typedArray.getColor(R.styleable.LableEditView_le_contentColor, ResourceUtils.getColor(R.color.black_tran87));
            int contentHintColor = typedArray.getColor(R.styleable.LableEditView_le_contentHintColor, ResourceUtils.getColor(R.color.black_tran87));
            String tipStr = typedArray.getString(R.styleable.LableEditView_le_tip);
            boolean isSpanner = typedArray.getBoolean(R.styleable.LableEditView_le_spanner,false);
            designHintEnabled = typedArray.getBoolean(R.styleable.LableEditView_le_designHintEnabled , false);
            int inputType = typedArray.getInt(R.styleable.LableEditView_le_inputType, 0);
            int length = typedArray.getInt(R.styleable.LableEditView_le_length, 0);
            int label_width = typedArray.getDimensionPixelSize(R.styleable.LableEditView_le_label_width, 0);
            int label_contentGravity = typedArray.getInt(R.styleable.LableEditView_le_label_content_gravity, 0);

            typedArray.recycle();

            if (length > 0){
                et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            }

            if (inputType == 0){
                et_content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else if(inputType == 2){
                et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
            }else if (inputType == 3){
                et_content.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }else {
                et_content.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            LayoutParams params1 = (LayoutParams) tv_lable.getLayoutParams();
            if (labelGravity == 0) {
                params1.gravity = Gravity.TOP;
            }else if (labelGravity == 1){
                params1.gravity = Gravity.CENTER_VERTICAL;
            }else {
                params1.gravity = Gravity.BOTTOM;
            }
            if (label_width != 0){
                params1.width = label_width;
            }
            tv_lable.setLayoutParams(params1);

            LayoutParams params2 = (LayoutParams) tv_tip.getLayoutParams();
            if (labelGravity == 0) {
                params2.gravity = Gravity.TOP;
            }else if (labelGravity == 1){
                params2.gravity = Gravity.CENTER_VERTICAL;
            }else {
                params2.gravity = Gravity.BOTTOM;
            }
            tv_tip.setLayoutParams(params2);

            tv_lable.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelSize);
            tv_lable.setTextColor(labelColor);
            tv_lable.setText(lableStr);
            et_content.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);
            et_content.setTextColor(contentColor);
            et_content.setHintTextColor(contentHintColor);

            textInputLayout.setHintEnabled(designHintEnabled);
            if (designHintEnabled) {
                textInputLayout.setHint(contentHintStr);
                et_content.setPadding(0,SizeUtils.dipConvertPx(8),0,0);
            }else {
                et_content.setHint(contentHintStr);
                et_content.setPadding(0,0,0,0);
            }
            tv_tip.setText(tipStr);

            if (isSpanner){
                tv_tip.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_more1,0,0,0);
            }

            et_content.setGravity(label_contentGravity == 0 ? (Gravity.CENTER_VERTICAL | Gravity.RIGHT) : (Gravity.CENTER_VERTICAL | Gravity.LEFT));
        }
    }

    public void requestEtFocus(){
        et_content.requestFocus();
    }

    public String getText(){
        return et_content.getText().toString();
    }

    public void setHint(CharSequence hint){
        if (designHintEnabled) {
            textInputLayout.setHint(hint);
            //et_content.setPadding(0,SizeUtils.dipConvertPx(8),0,0);
        }else {
            et_content.setHint(hint);
            //et_content.setPadding(0,0,0,0);
        }
    }

    public String getHint(){
        if (designHintEnabled){
            return textInputLayout.getHint().toString();
        }else {
            return et_content.getHint().toString();
        }
    }

    public void setText(CharSequence text){
        et_content.setText(text);
    }

    public boolean contentIsEmpty(){
        return StringUtils.isEmpty(et_content.getText().toString());
    }

    public void setLable(CharSequence lable){
        tv_lable.setText(lable);
    }

    public String getLable(){
        return tv_lable.getText().toString();
    }

    public void setEditEnable(boolean editEnable){
        et_content.setEnabled(editEnable);

        /*(不可编辑但可点击,隐藏光标)
        editText.setCursorVisible(false);
        */
        et_content.setFocusable(editEnable);
        et_content.setFocusableInTouchMode(editEnable);
    }

    public void setLength(int max){
        et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
    }

    public void setInputType(int inputType){
        et_content.setInputType(inputType);
    }

    public void addTextChangedListener(TextWatcher watcher){
        et_content.addTextChangedListener(watcher);
    }
}
