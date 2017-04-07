package com.mrs.mrs.multisupportrecyclerview.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mrs on 2017/4/7.
 */

public class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "WrapRecyclerAdapter";
    private View loadMoreView;
    private boolean loadMoreEnable = false;
    // 用来存放底部和头部View的集合  比Map要高效一些
    // 可以点击进入看一下官方的解释
    private SparseArray<View> mHeaderViews = new SparseArray<>();
    private SparseArray<View> mFooterViews = new SparseArray<>();

    // 基本的头部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_HEADER = 10000000;
    // 基本的底部类型开始位置  用于viewType
    private static int BASE_ITEM_TYPE_FOOTER = 20000000;

    private static int TYPE_LOAD_MORE = 3000000;

    private Context context;
    // 列表的Adapter
    private RecyclerView.Adapter mAdapter;

    public WrapRecyclerAdapter(View loadMoreView, RecyclerView.Adapter adapter) {
        this.loadMoreView = loadMoreView;
        this.mAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // viewType 可能就是 SparseArray 的key
        if (isHeaderViewType(viewType)) {
            View headerView = mHeaderViews.get(viewType);
            return createHeaderFooterViewHolder(headerView);
        }

        if (isFooterViewType(viewType)) {
            View footerView = mFooterViews.get(viewType);
            Log.e("onCreateViewHolder", viewType + "");
            return createHeaderFooterViewHolder(footerView);
        }
        if (loadMoreEnable && viewType == TYPE_LOAD_MORE) {
            return createHeaderFooterViewHolder(loadMoreView);
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position) || (loadMoreEnable&&position == getItemCount() - 1)) {
            return;
        }
        // 计算一下位置
        position = position - mHeaderViews.size();
        mAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            // 直接返回position位置的key
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)) {
            // 直接返回position位置的key
            position = position - mHeaderViews.size() - mAdapter.getItemCount();
            return mFooterViews.keyAt(position);
        }
        if (loadMoreEnable && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }
        // 返回列表Adapter的getItemViewType
        position = position - mHeaderViews.size();

        return mAdapter.getItemViewType(position);
    }

    /**
     * 是不是底部类型
     */
    private boolean isFooterViewType(int viewType) {
        int position = mFooterViews.indexOfKey(viewType);
        return position >= 0;
    }

    /**
     * 创建头部或者底部的ViewHolder
     */
    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {

        };
    }

    /**
     * 是不是头部类型
     */
    private boolean isHeaderViewType(int viewType) {
        int position = mHeaderViews.indexOfKey(viewType);
        return position >= 0;
    }

    /**
     * 是不是底部位置
     */
    private boolean isFooterPosition(int position) {
        return position >= (mHeaderViews.size() + mAdapter.getItemCount())
                + (loadMoreEnable ? 1 : 0);
    }

    /**
     * 是不是头部位置
     */
    private boolean isHeaderPosition(int position) {
        return position < mHeaderViews.size();
    }

    @Override
    public int getItemCount() {
        // 条数三者相加 = 底部条数 + 头部条数 + Adapter的条数
        int count = mAdapter.getItemCount() + mHeaderViews.size() + mFooterViews.size();
        Log.e("getItemCount", count + "");
        return count;
    }

    /**
     * 获取列表的Adapter
     */
    private RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    // 添加头部
    public void addHeaderView(View view) {
        int position = mHeaderViews.indexOfValue(view);
        if (position < 0) {
            mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
        }
        notifyDataSetChanged();
    }

    // 添加底部
    public void addFooterView(View view) {
        int position = mFooterViews.indexOfValue(view);
        if (position < 0) {//BASE_ITEM_TYPE_FOOTER++
            mFooterViews.put(mFooterViews.size(), view);
        }
        notifyItemInserted(getItemCount() - 1);
        notifyDataSetChanged();
    }

    // 移除头部
    public void removeHeaderView(View view) {
        int index = mHeaderViews.indexOfValue(view);
        if (index < 0) return;
        mHeaderViews.removeAt(index);
        notifyDataSetChanged();
    }

    // 移除底部
    public void removeFooterView(View view) {
        int index = mFooterViews.indexOfValue(view);
        if (index < 0) return;
        mFooterViews.removeAt(index);
        notifyDataSetChanged();
    }

    public void setLoadMoreView(View view) {
        this.loadMoreView = view;
    }

    public void setShowLoadMore(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
       // notifyDataSetChanged();
    }

    /**
     * 解决GridLayoutManager添加头部和底部不占用一行的问题
     *
     * @param recycler
     */
    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter =
                            isHeaderPosition(position) || isFooterPosition(position);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                }
            });
        }
    }
}
