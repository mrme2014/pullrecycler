package com.qiaomu.libmultirecyclerview.ilayoutmanager;

import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;

/**
 * Created by qiaomu on 2017/6/9.
 */

public interface ILayoutManager {
    RecyclerView.LayoutManager getLayoutManager();

    int findLastVisiblePosition();

    int findFirstVisiblePosition();

    void scrollPositionWithOffset(int position, int offset);

    PointF computeScrollVector4Position(int targetPosition);

    boolean isStackFromEnd();

    void setStackFromEndIfPossible(boolean stackFromEnd);

}