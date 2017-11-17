package com.qiaomu.libmultirecyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.qiaomu.libmultirecyclerview.ilayoutmanager.ILayoutManager;
import com.qiaomu.libmultirecyclerview.multisupport.LookSpanSize;
import com.qiaomu.libmultirecyclerview.widget.DragLinearLayout;


/**
 * Created by mrs on 2017/4/7.
 */

public class WrapRecyclerView extends RecyclerView {
    // 包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private Adapter mAdapter;
    private boolean loadMoreEnable = true;//默认可以加载更多
    private boolean isLoadingMore = false;//是否正在加载更多中，避免多次出发上拉加载更多
    private onRefreshListener listener;
    private View loadOverView;
    private View loadMoreView;
    private boolean enableSwipDimiss;//是否支持配合DragLinearLayout实现侧滑删除
    private LookSpanSize lookSpan;
    private ILayoutManager iLayoutmanager;
    private float MILLISECONDS_PER_INCH = getResources().getDisplayMetrics().density * 0.3f;
    private LinearSmoothScroller linearSmoothScroller;
    private float touchX, touchY;
    private boolean mIsRefreshing;
    private int mNewState;

    public WrapRecyclerView(Context context) {
        this(context, null);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //禁止多点触控
        setMotionEventSplittingEnabled(false);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mNewState = newState;
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    //正在刷新或者 正在加载 或者不支持上拉加载更多  或者没设置回调 直接return
                    if (mIsRefreshing || isLoadingMore || !loadMoreEnable || listener == null)
                        return;
                    int lastPos = getLastVisibleItemPosition();
                    loadMoreIfNeeded(lastPos);
                }
            }
        });

    }

    /*设置支持 侧滑删除 否则不会自动关闭打开的条目*/
    public void setSupportSwipDiMiss(boolean enableSwipDimiss) {
        this.enableSwipDimiss = enableSwipDimiss;
    }

    float x, y;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = ev.getX();
                y = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("dispatchTouchEvent: ", Math.abs(ev.getX() - x) + "--" + Math.abs(ev.getY() - y));
                if (Math.abs(ev.getX() - x) > Math.abs(ev.getY() - y)) {
                    stopScroll();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }


        return  super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!enableSwipDimiss)
            return super.onInterceptTouchEvent(ev);

        boolean intercept;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                closeMenuIfNeeded();
                touchX = ev.getX();
                touchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                intercept = Math.abs(ev.getY() - touchY) > Math.abs(ev.getX() - touchX);
                return intercept;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private void closeMenuIfNeeded() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof DragLinearLayout) {
                DragLinearLayout dragMenu = (DragLinearLayout) childAt;
                if (dragMenu.isOpen()) {
                    dragMenu.close();
                    break;
                }
            }
        }
    }


    public void setMyLayoutManager(ILayoutManager manager) {
        this.iLayoutmanager = manager;
        setLayoutManager(manager.getLayoutManager());
    }


    public int getLastVisibleItemPosition() {
        return iLayoutmanager.findLastVisiblePosition();
    }

    private void loadMoreIfNeeded(int lastPos) {
        int itemCount = mWrapRecyclerAdapter.getItemCount();
        //如果不是滑动到列表最底部
        if (lastPos != itemCount - 1) {
            return;
        }
        isLoadingMore = true;
        listener.onRefresh(false);
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
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(adapter, getLoadMoreView(), lookSpan);
        }

        super.setAdapter(mWrapRecyclerAdapter);

        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    public void addHeaderView(View view) {
        // 先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.addHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.addFooterView(view);
        }
    }

    public void removeHeaderView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.removeHeaderView(view);
        }
    }

    public void removeFooterView(View view) {
        if (mWrapRecyclerAdapter != null && view != null) {
            mWrapRecyclerAdapter.removeFooterView(view);
        }
    }

    public SparseArray<View> getHeaders() {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.getHeaderViews();
        }
        return null;
    }

    public SparseArray<View> getFooters() {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.getFooterViews();
        }
        return null;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        mWrapRecyclerAdapter.setLoadMoreEnable(loadMoreEnable);
    }

    public boolean isLoadMoreEnable() {
        return this.loadMoreEnable;
    }

    public boolean isLoadingMore() {
        return this.isLoadingMore;
    }

    public void setLoadMoreView(View loadMoreView) {
        this.loadMoreView = loadMoreView;
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.setLoadMoreView(loadMoreView);
            mWrapRecyclerAdapter.notifyDataSetChanged();
        }
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
        if (isLoadingMore) {
            isLoadingMore = false;
            if (loadMoreEnable) {
                mWrapRecyclerAdapter.notifyItemRemoved(mWrapRecyclerAdapter.getItemCount() - 1);
            } else {
                // mWrapRecyclerAdapter.notifyItemRemoved(mWrapRecyclerAdapter.getItemCount() - 1);
                mWrapRecyclerAdapter.notifyDataSetChanged();
            }
        } else
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

    public void setOnLoadMoreListener(onRefreshListener listener) {
        this.listener = listener;
    }

    public void setLookSpan(LookSpanSize lookSpan) {
        this.lookSpan = lookSpan;
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.setLookSpan(lookSpan);
        }
    }

    public void showLoadOverView() {
        showLoadOverView("我是有底线的");
    }

    public void showLoadOverView(String tip) {
        if (mWrapRecyclerAdapter == null)
            return;
        if (loadOverView == null) {
            loadOverView = new TextView(getContext());
            loadOverView.setLayoutParams(new LayoutParams(-1, dip2px(getContext(), 40)));
            ((TextView) loadOverView).setTextColor(ContextCompat.getColor(getContext(), R.color.system_gray));
            ((TextView) loadOverView).setTextSize(10);
            ((TextView) loadOverView).setText(tip);
            ((TextView) loadOverView).setGravity(Gravity.CENTER);
        }
        mWrapRecyclerAdapter.addFooterView(loadOverView);
    }

    private int dip2px(Context context, int i) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * i + 0.5f);
    }

    public void removeLoadOverView() {
        if (loadOverView == null || mWrapRecyclerAdapter == null)
            return;
        mWrapRecyclerAdapter.removeFooterView(loadOverView);
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        iLayoutmanager.scrollPositionWithOffset(position, offset);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
        if (linearSmoothScroller == null)
            linearSmoothScroller = new LinearSmoothScroller(getContext()) {
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return iLayoutmanager.computeScrollVector4Position(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.density;
                    //返回滑动一个pixel需要多少毫秒
                }
            };

        linearSmoothScroller.setTargetPosition(position);
        iLayoutmanager.getLayoutManager().startSmoothScroll(linearSmoothScroller);
    }

    public void setRefreshing(boolean isRefreshing) {
        mIsRefreshing = isRefreshing;
    }
}