package com.xiaomawang.commonlib.ui.widget.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaomawang.commonlib.R;
import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

public class SlideViewHolder extends RecyclerView.ViewHolder {

    protected View contentView;

    private TextView tv_dele;

    public SlideViewHolder(Context context, ViewGroup parent, @LayoutRes int layoutId){
        super(LayoutInflater.from(context).inflate(R.layout.item_slide_layout,parent,false));

        tv_dele = itemView.findViewById(R.id.tv_dele);
        contentView = LayoutInflater.from(context).inflate(layoutId,(ViewGroup) itemView,false);
        ((ViewGroup) itemView).addView(contentView);

    }

    public void translationX(float value){
        if (ViewCompat.getTranslationX(contentView) > SizeUtils.dipConvertPx(-80)) {
            ViewCompat.setTranslationX(contentView, value);
        }
    }

    public void setOnDeleCiliclistener(View.OnClickListener onDeleCiliclistener){
        tv_dele.setOnClickListener(onDeleCiliclistener);
    }
}
