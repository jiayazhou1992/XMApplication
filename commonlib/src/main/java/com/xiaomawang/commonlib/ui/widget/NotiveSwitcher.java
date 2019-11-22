package com.xiaomawang.commonlib.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.ResourceUtils;

public class NotiveSwitcher extends TextSwitcher implements ViewSwitcher.ViewFactory {

    public NotiveSwitcher(Context context) {
        super(context);
        init();
    }

    public NotiveSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setFactory(this);
    }


    @Override
    public View makeView() {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
        textView.setTextColor(ResourceUtils.getColor(R.color.black_tran40));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}
