package com.mrs.mrs.swiprecyclerview.recyclerview;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ViewGroup;

import com.mrs.mrs.swiprecyclerview.recyclerview.multisupport.MultiTypeSupportAdapter;

/**
 * Created by mrs on 2017/5/4.
 */

public class SwipeItemTouchCallback extends ItemTouchHelper.Callback {

    private MultiTypeSupportAdapter mAdapter;

    public SwipeItemTouchCallback(MultiTypeSupportAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // mAdapter.(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int menuWidth = getMenuWidth(viewHolder);
        Log.e("onChildDraw: ", menuWidth + "--" + dX + "--" + (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) + "---" + (actionState == ItemTouchHelper.ACTION_STATE_DRAG));
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && menuWidth >= -dX) {
            viewHolder.itemView.scrollTo(-(int) dX, 0);
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 5000;
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return getMenuWidth(viewHolder);
    }

    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        return 3000L;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //  viewHolder.itemView.scrollTo(0, 0);
    }

    public int getMenuWidth(RecyclerView.ViewHolder viewHolder) {
        if (!(viewHolder.itemView instanceof ViewGroup))
            return 0;

        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getWidth();
    }
}