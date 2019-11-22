package com.xiaomawang.commonlib.ui.widget.recyclerview.dragsortrecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaomawang.commonlib.utils.dev.app.SizeUtils;

public class SwipeDelectImpl implements ItemTouchHelperImpl.OnChidDrawCallback {

    private ItemTouchHelperImpl itemTouchHelper;

    private Paint mPaint;

    private String text = "左滑删除";

    private int text_w , text_h;

    public SwipeDelectImpl() {
        if (mPaint == null){
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(SizeUtils.dipConvertPx(17));
            text_w = getTextWidth(text);
            text_h = getTextHeight(text);
        }
    }



    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        viewHolder.itemView.setTranslationX(dX);

        c.drawColor(Color.RED);
        int w = viewHolder.itemView.getMeasuredWidth();
        int h = viewHolder.itemView.getMeasuredHeight();
        int t = viewHolder.itemView.getTop();
        float x = w - text_w - SizeUtils.dipConvertPx(16);
        float y = h / 2f + text_h / 2f;
        c.drawText(text, x, t + y, mPaint);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

    }

    private int getTextHeight(String text){
        Rect rect = new Rect();
        mPaint.getTextBounds(text,0,text.length(), rect);
        return rect.height();
    }

    private int getTextWidth(String text){
        Rect rect = new Rect();
        mPaint.getTextBounds(text,0,text.length(), rect);
        return rect.width();
    }
}
