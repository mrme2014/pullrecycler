package com.mrs.mrs.swiprecyclerview.recyclerview.multisupport;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mrs on 2017/5/9.
 */

public class TimeItemDecoration extends RecyclerView.ItemDecoration {
    Paint paint;
    private int position;
    private int height;

    public TimeItemDecoration() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        position = parent.getChildAdapterPosition(view);
        height = view.getHeight();
        outRect.top = 20;
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            c.drawText("onDraw"+position, child.getWidth() / 2-paint.measureText("onDraw"+position,0,("onDraw"+position).length())/2, child.getTop(), paint);
        }
    }
}
