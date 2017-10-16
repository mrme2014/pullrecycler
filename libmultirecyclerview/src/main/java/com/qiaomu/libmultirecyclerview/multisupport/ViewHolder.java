package com.qiaomu.libmultirecyclerview.multisupport;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mrs on 2017/4/5.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    // 用来存放子View减少findViewById的次数
    private SparseArray<View> mViews;

    public ViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }
    public <T extends View> T getView(int viewId) {
        // 先从缓存中找
        View view = mViews.get(viewId);
        if (view == null) {
            // 直接从ItemView中找
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public void onItemClick(View view) {

    }

    public void onItemLongClick(View view) {

    }

    public void setOnItemClickListener(int id, View.OnClickListener listener) {
        View view = getView(id);
        if (view==null)
            return;
        view.setOnClickListener(listener);
    }

    public void setText(int id, String txt) {
        TextView view = getView(id);
        if (view==null)
            return;
        view.setText(txt);
    }

    public void setBackGroundColor(int id, int bgColor) {
        View view = getView(id);
        if (view==null)
            return;
        view.setBackgroundColor(bgColor);
    }
}
