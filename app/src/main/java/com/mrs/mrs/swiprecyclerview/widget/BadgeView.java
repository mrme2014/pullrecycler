package com.mrs.mrs.swiprecyclerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by qiaomu on 2017/7/5.
 */

public class BadgeView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private int mNum = 0;
    private int mRadiu = 8;
    private int mOverNum = 9;
    private int mOverNumPlus = 99;
    private String mOverNumStr = "99+";

    public BadgeView(Context context) {
        this(context, null);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(getTextSize());
        mRadiu = dip2px(context, mRadiu);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(2 * mRadiu, MeasureSpec.EXACTLY);
        if (mNum > 9 && mNum <= mOverNumPlus) {
            float width = mPaint.measureText(String.valueOf(mNum));
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(3 * mRadiu, MeasureSpec.EXACTLY);
        } else if (mNum > mOverNumPlus) {
            float width = mPaint.measureText(mOverNumStr);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (2 * mRadiu + width), MeasureSpec.EXACTLY);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(2 * mRadiu, MeasureSpec.EXACTLY);
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNum <= 0) return;
        if (mNum > mOverNum) {
            String drawStr = mNum > mOverNumPlus ? mOverNumStr : String.valueOf(mNum);
            float width = mPaint.measureText(drawStr);
            RectF rectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            mPaint.setColor(Color.RED);
            canvas.drawRoundRect(rectF, mRadiu, mRadiu, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(drawStr, (getMeasuredWidth() - width) / 2, getBaseline(), mPaint);
        } else {
            String valueOfNum = String.valueOf(mNum);
            float width = mPaint.measureText(valueOfNum);
            mPaint.setColor(Color.RED);
            canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mRadiu, mPaint);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(valueOfNum, (getMeasuredWidth() - width) / 2, getBaseline(), mPaint);
        }

    }

    public void setBadgeNum(int num) {
        if (num <= 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        this.mNum = num;
        measure(getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    private int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
