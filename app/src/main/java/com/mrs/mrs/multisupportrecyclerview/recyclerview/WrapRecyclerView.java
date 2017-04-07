package com.mrs.mrs.multisupportrecyclerview.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.mrs.mrs.multisupportrecyclerview.R;


/**
 * Created by mrs on 2017/4/7.
 */

public class WrapRecyclerView extends RecyclerView {
    // 包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private RecyclerView.Adapter mAdapter;
    private boolean loadMoreEnable = true;//默认可以加载更多
    private boolean isLoadingMore = false;//是否正在加载更多中，避免多次出发上拉加载更多
    private View loadMoreView;
    private onLoadMoreListener listener;

    public WrapRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    if (isLoadingMore && !isLoadMoreEnable())
                        return;
                    LayoutManager manager = getLayoutManager();
                    if (manager instanceof LinearLayoutManager) {
                        int lastPos = ((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                        int itemCount = mWrapRecyclerAdapter.getItemCount();
                        //如果不是滑动到列表最底部
                        if (lastPos != itemCount - 1)
                            return;
                        if (lastPos == itemCount - 1 && isLoadMoreEnable()) {
                            isLoadingMore = true;
                            if (listener != null)
                                listener.onLoadMore();
                        } else if (!isLoadMoreEnable() || itemCount < Conf.DEFAULT_LIST_ITEM) {
                            isLoadingMore = false;
                        }
                    } else if (manager instanceof GridLayoutManager) {
                        // ((GridLayoutManager) manager).find
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public void setAdapter(Adapter adapter) {
        // 为了防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }

        this.mAdapter = adapter;

        if (adapter instanceof WrapRecyclerAdapter) {
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        } else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(getLoadMoreView(), adapter);
        }

        super.setAdapter(mWrapRecyclerAdapter);

        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);
        // 解决GridLayout添加头部和底部也要占据一行
        mWrapRecyclerAdapter.adjustSpanSize(this);
    }

    public void addHeaderView(View view) {
        // 先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addFooterView(view);
        }
    }

    public void removeHeaderView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeHeaderView(view);
        }
    }

    public void removeFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeFooterView(view);
        }
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        mWrapRecyclerAdapter.setShowLoadMore(loadMoreEnable);
    }

    public boolean isLoadMoreEnable() {
        return this.loadMoreEnable;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        mWrapRecyclerAdapter.setLoadMoreView(loadMoreView);
    }

    public View getLoadMoreView() {
        if (loadMoreView == null) {
            loadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.base_widget_load_more, this, false);
        }
        return loadMoreView;
    }

    public void setOnLoadCompleted() {
        if (mAdapter == null)
            return;
        isLoadingMore = false;
//        if (loadMoreEnable)
//            mWrapRecyclerAdapter.notifyItemRemoved(mWrapRecyclerAdapter.getItemCount() - 1);
        mWrapRecyclerAdapter.notifyDataSetChanged();
    }


    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemInserted(positionStart);
        }
    };

    public void setOnLoadMoreListener(onLoadMoreListener listener) {
        this.listener = listener;
    }
}