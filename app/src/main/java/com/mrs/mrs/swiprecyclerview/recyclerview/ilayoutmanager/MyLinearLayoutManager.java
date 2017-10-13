package com.mrs.mrs.swiprecyclerview.recyclerview.ilayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by qiaomnu on 17/6/9.
 */
public class MyLinearLayoutManager extends LinearLayoutManager implements ILayoutManager {
    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public int findLastVisiblePosition() {
        return findLastVisibleItemPosition();
    }

    @Override
    public int findFirstVisiblePosition() {
        return findFirstVisibleItemPosition();
    }

    @Override
    public void scrollPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public PointF computeScrollVector4Position(int targetPosition) {
        return computeScrollVectorForPosition(targetPosition);
    }

    @Override
    public boolean isStackFromEnd() {
        return getStackFromEnd();
    }

    @Override
    public void setStackFromEndIfPossible(boolean stackFromEnd) {
        setStackFromEnd(stackFromEnd);
    }

}
