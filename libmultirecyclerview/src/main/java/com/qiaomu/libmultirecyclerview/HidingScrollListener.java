package com.qiaomu.libmultirecyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by mrs on 17/4/17.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 30;
    private int scrolledDistance = 0;
    private boolean controlsVisible = false;


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (!controlsVisible && scrolledDistance > HIDE_THRESHOLD) {
            controlsVisible = true;
            onHide();
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int count = recyclerView.getAdapter().getItemCount();
            int position = manager.findLastVisibleItemPosition();
            if (controlsVisible && position >0) {
                onShow(position == count - 1);
                controlsVisible = false;
            }
            scrolledDistance = 0;
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        scrolledDistance += Math.abs(dy);
    }

    public abstract void onHide();

    public abstract void onShow(boolean showLast);
}
