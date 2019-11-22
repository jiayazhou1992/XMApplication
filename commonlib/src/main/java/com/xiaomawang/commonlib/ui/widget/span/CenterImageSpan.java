package com.xiaomawang.commonlib.ui.widget.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CenterImageSpan extends ImageSpan {
    public CenterImageSpan(@NonNull Bitmap b) {
        super(b);
    }

    public CenterImageSpan(@NonNull Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public CenterImageSpan(@NonNull Context context, @NonNull Bitmap bitmap) {
        super(context, bitmap);
    }

    public CenterImageSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
        super(context, bitmap, verticalAlignment);
    }

    public CenterImageSpan(@NonNull Drawable drawable) {
        super(drawable);
    }

    public CenterImageSpan(@NonNull Drawable drawable, int verticalAlignment) {
        super(drawable, verticalAlignment);
    }

    public CenterImageSpan(@NonNull Drawable drawable, @NonNull String source) {
        super(drawable, source);
    }

    public CenterImageSpan(@NonNull Drawable drawable, @NonNull String source, int verticalAlignment) {
        super(drawable, source, verticalAlignment);
    }

    public CenterImageSpan(@NonNull Context context, @NonNull Uri uri) {
        super(context, uri);
    }

    public CenterImageSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public CenterImageSpan(@NonNull Context context, int resourceId) {
        super(context, resourceId);
    }

    public CenterImageSpan(@NonNull Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        try {
            Drawable d = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom-top) - d.getBounds().bottom) / 2+top;
            canvas.translate(x, transY);
            d.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
        }
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        try {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        } catch (Exception e) {
            return 20;
        }
    }
}
