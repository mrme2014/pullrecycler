package com.mrs.mrs.swiprecyclerview.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.mrs.mrs.swiprecyclerview.recyclerview.multisupport.LookSpanSize;

/**
 * Created by mrs on 2017/4/7.
 * <p>
 * <p>
 * 添加多头部 尾部 加载更多适配器
 * <p>
 * <p>
 * 配合WrapRecyclerView 使用
 */

public class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = this.getClass().toString();
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

    // 列表的Adapter
    private RecyclerView.Adapter mAdapter;
    private LookSpanSize lookSpan;

    public WrapRecyclerAdapter(RecyclerView.Adapter adapter, View loadMoreView, LookSpanSize lookSpan) {
        this.loadMoreView = loadMoreView;
        this.mAdapter = adapter;
        this.lookSpan = lookSpan;
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
            return createHeaderFooterViewHolder(footerView);
        }
        if (loadMoreEnable && viewType == TYPE_LOAD_MORE) {
            return createHeaderFooterViewHolder(loadMoreView);
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position) || (loadMoreEnable && position == getItemCount() - 1)) {
            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
            return;
        }

        // 计算一下位置
        position = position - mHeaderViews.size();
        mAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMoreEnable && position == getItemCount() - 1) {
            return TYPE_LOAD_MORE;
        }

        if (isHeaderPosition(position)) {
            // 直接返回position位置的key
            return mHeaderViews.keyAt(position);
        }
        if (isFooterPosition(position)) {
            // 直接返回position位置的key
            position = position - mHeaderViews.size() - mAdapter.getItemCount();
            return mFooterViews.keyAt(position);
        }

        // 返回列表Adapter的getItemViewType
        position = position - mHeaderViews.size();

        return mAdapter.getItemViewType(position);
    }

    private boolean isFooterViewType(int viewType) {
        int position = mFooterViews.indexOfKey(viewType);
        return position >= 0;
    }


    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        return new RecyclerView.ViewHolder(view) {

        };
    }

    private boolean isHeaderViewType(int viewType) {
        int position = mHeaderViews.indexOfKey(viewType);
        return position >= 0;
    }


    private boolean isFooterPosition(int position) {
        return position >= (mHeaderViews.size() + mAdapter.getItemCount());
        // + (loadMoreEnable ? 1 : 0);
    }

    private boolean isHeaderPosition(int position) {
        return position < mHeaderViews.size();
    }

    @Override
    public int getItemCount() {
        // 条数三者相加 = 底部条数 + 头部条数 + Adapter的条数
        int count = mAdapter.getItemCount() + mHeaderViews.size() + mFooterViews.size() + (loadMoreEnable ? 1 : 0);
        return count;
    }

    // 添加头部
    public void addHeaderView(View view) {
        int position = mHeaderViews.indexOfValue(view);
        if (position < 0) {
            mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
        }
        notifyDataSetChanged();
    }

    public SparseArray<View> getHeaderViews() {
        return mHeaderViews;
    }

    public SparseArray<View> getFooterViews() {
        return mFooterViews;
    }

    // 添加底部
    public void addFooterView(View view) {
        int position = mFooterViews.indexOfValue(view);
        if (position < 0) {
            mFooterViews.put(BASE_ITEM_TYPE_FOOTER++, view);
        }
        notifyItemInserted(getItemCount());
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

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {   //  1   2
            final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter = isHeaderPosition(position)
                            || isFooterPosition(position)
                            || (loadMoreEnable && position == getItemCount() - 1);
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : (lookSpan == null ? 1 : lookSpan.getSpanSize(position));
                }
            });
        }

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == 0);
        }
    }

    public void setLookSpan(LookSpanSize lookSpan) {
        this.lookSpan = lookSpan;
        notifyDataSetChanged();
    }

    public void setLoadMoreView(View view) {
        this.loadMoreView = view;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        //notifyDataSetChanged();
        //外面手动调用notifyDataSetChanged
    }
}
