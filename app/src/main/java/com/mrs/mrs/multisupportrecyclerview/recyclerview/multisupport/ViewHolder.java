package com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mrs on 2017/4/5.
 */

public  class ViewHolder extends RecyclerView.ViewHolder {
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

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    public ViewHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    /**
     * 设置ImageView的资源
     */
    public ViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resourceId);
        return this;
    }

    /**
     * 设置图片通过路径,这里稍微处理得复杂一些，因为考虑加载图片的第三方可能不太一样
     * 也可以直接写死
     */
    public ViewHolder setImageByUrl(Context context, int viewId, String path) {
        ImageView imageView = getView(viewId);
        //PicUtils.load(context,imageView,path);
        return this;
    }


    public void onItemClick(View view, int adapterPosition) {

    }

    public void onItemLongClick(View view, int adapterPosition) {

    }


}
