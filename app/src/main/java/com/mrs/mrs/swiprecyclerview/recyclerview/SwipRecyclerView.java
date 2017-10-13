package com.mrs.mrs.swiprecyclerview.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by mrs on 2017/5/3.
 */

public class SwipRecyclerView extends WrapRecyclerView {
    private boolean enableSwipDimiss=true;

    public SwipRecyclerView(Context context) {
        this(context, null);
    }

    public SwipRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && enableSwipDimiss)
            closeMenuIfNeeded();

        return super.onInterceptTouchEvent(e);
    }

    private void closeMenuIfNeeded() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof DragLinearLayout) {
                DragLinearLayout dragMenu = (DragLinearLayout) childAt;
                if (dragMenu.isOpen()) {
                    dragMenu.close();
                    break;
                }
            }
        }
    }

    public void setSupportSwipDiMiss(boolean enableSwipDimiss) {
        this.enableSwipDimiss = enableSwipDimiss;
    }
}
