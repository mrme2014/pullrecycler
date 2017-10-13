package com.mrs.mrs.swiprecyclerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mrs on 2017/5/26.
 */

public class RoundImageView extends android.support.v7.widget.AppCompatImageView {

    float width, height;
    float conner = 12;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (width > conner && height > conner) {
            Path path = new Path();
            path.moveTo(conner, 0);
            path.lineTo(width - conner, 0);
            path.quadTo(width, 0, width, conner);
            path.lineTo(width, height - conner);
            path.quadTo(width, height, width - conner, height);
            path.lineTo(conner, height);
            path.quadTo(0, height, 0, height - conner);
            path.lineTo(0, conner);
            path.quadTo(0, 0, conner, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }
}
