package com.mrs.mrs.swiprecyclerview.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.mrs.mrs.multisupportrecyclerview.R;


/**
 * Created by mrs on 2017/5/12.
 */

public class ProcessImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint mPaint;// 画笔
    private float progress = 0;
    private int halfColor;
    private int fullColor;
    private int processColor;

    public ProcessImageView(Context context) {
        super(context);

    }

    public ProcessImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessImageView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(30);

        halfColor = ContextCompat.getColor(context, R.color.half_transparent);
        fullColor = ContextCompat.getColor(context, R.color.transparent);
        processColor = ContextCompat.getColor(context, R.color.white);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(halfColor);// 半透明
        canvas.drawRect(0,
                0,
                getWidth(),
                getHeight() - getHeight() * progress / 100,
                mPaint);

//        mPaint.setColor(fullColor);// 全透明
//        canvas.drawRect(0,
//                getHeight() - getHeight() * progress / 100,
//                getWidth(),
//                getHeight(),
//                mPaint);


        mPaint.setColor(progress >= 100 ? Color.TRANSPARENT : processColor);
        Rect rect = new Rect();
        String txt = progress + "%";
        mPaint.getTextBounds(txt, 0, txt.length(), rect);// 确定文字的宽度
        canvas.drawText(txt,
                getWidth() / 2 - rect.width() / 2,
                getHeight() / 2,
                mPaint);

    }

    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    public void startAnimation() {
        Log.e("startAnimation: ", progress + "");
        ValueAnimator animator = ValueAnimator.ofFloat(progress, 100);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = (float) animation.getAnimatedValue();
                setProgress(fraction);
            }
        });
        animator.start();
    }
}


