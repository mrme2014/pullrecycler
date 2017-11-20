package com.qiaomu.libmultirecyclerview.ilayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by qiaomnu on 17/6/9.
 */
public class MyStaggeredGridLayoutManager extends StaggeredGridLayoutManager implements ILayoutManager {
    boolean mCanScrollVertically = true;
    public MyStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public int findLastVisiblePosition() {
        int[] positions = null;
        positions = findLastVisibleItemPositions(positions);
        return positions[0];
    }

    @Override
    public int findFirstVisiblePosition() {
        int[] positions = null;
        positions = findFirstVisibleItemPositions(positions);
        return positions[0];
    }

    @Override
    public void scrollPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public PointF computeScrollVector4Position(int targetPosition) {
        return null;
    }

    @Override
    public boolean isStackFromEnd() {
        return false;
    }

    @Override
    public void setStackFromEndIfPossible(boolean stackFromEnd) {
    }

    @Override
    public void setCanScrollVertically(boolean canScrollVertically) {

        mCanScrollVertically = canScrollVertically;
    }
    @Override
    public boolean canScrollVertically() {
        return mCanScrollVertically ? super.canScrollVertically() : false;
    }
}
