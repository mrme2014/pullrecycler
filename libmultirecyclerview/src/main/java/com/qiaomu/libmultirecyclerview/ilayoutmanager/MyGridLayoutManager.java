package com.qiaomu.libmultirecyclerview.ilayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by qiaomu on 2017/6/9.
 */
public class MyGridLayoutManager extends GridLayoutManager implements ILayoutManager {

    public MyGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
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
