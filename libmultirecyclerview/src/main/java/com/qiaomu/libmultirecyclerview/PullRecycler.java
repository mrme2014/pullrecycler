package com.qiaomu.libmultirecyclerview;

/**
 * Created by mrs on 2017/5/11.
 */


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiaomu.libmultirecyclerview.ilayoutmanager.ILayoutManager;
import com.qiaomu.libmultirecyclerview.multisupport.LookSpanSize;


/**
 * Created by mrs on 2017/5/11.
 */

public class PullRecycler extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private WrapRecyclerView recyclerView;
    private LoadFrameLayout loadFrameLayout;
    private ImageView emptyIv;
    private TextView emptyTipTv, emptyTipTitle;
    private onRefreshListener listener;
    public RecyclerView.Adapter adapter;
    private ILayoutManager iLayoutmanager;

    private boolean finalTomove, smoothMove;
    private int positionToTop;
    private boolean mEnableRefresh = true;

    public PullRecycler(@NonNull Context context) {
        super(context);
        setUpView(context);
    }

    public PullRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUpView(context);
    }

    public PullRecycler(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);
    }

    private void setUpView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.base_widget_pullrecycler, this, true);

        loadFrameLayout = (LoadFrameLayout) view.findViewById(R.id.loadFramLayout);
        emptyTipTitle = (TextView) loadFrameLayout.findViewById(R.id.emptyTipTitle);
        emptyIv = (ImageView) loadFrameLayout.findViewById(R.id.imageView);
        emptyTipTv = (TextView) loadFrameLayout.findViewById(R.id.emptyTipTv);

        recyclerView = (WrapRecyclerView) view.findViewById(R.id.recycleList);
        loadFrameLayout = (LoadFrameLayout) view.findViewById(R.id.loadFramLayout);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (finalTomove) {
                        finalTomove = false;
                        scrollPosition2TopIfPossible(positionToTop, smoothMove);
                    }
                }
            }

        });

    }


    /**
     * 空界面个性化
     * 可以单独设置提示内容，背景，是否显示图片
     *
     * @param showImage
     * @param tip
     * @param color
     */
    public void setEmptyView(boolean showImage, String tip, String titleTip, int color) {
        if (!showImage) {
            emptyIv.setVisibility(GONE);
            emptyTipTitle.setVisibility(VISIBLE);
            emptyTipTitle.setTextColor(color);
            emptyTipTitle.setText(titleTip);
            loadFrameLayout.setVisibility(VISIBLE);
            loadFrameLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        emptyTipTv.setText(tip);
        emptyTipTv.setTextColor(color);
        emptyTipTv.setTextSize(12);
        loadFrameLayout.showEmptyView();

    }

    public void showEmptyView(String emptyTip) {
        loadFrameLayout.setVisibility(VISIBLE);
        loadFrameLayout.showEmptyView();
        emptyTipTv.setText(emptyTip);
    }

    public void showEmptyView() {
        loadFrameLayout.setVisibility(VISIBLE);
        loadFrameLayout.showEmptyView();
    }

    public void showContentView() {
        loadFrameLayout.setVisibility(GONE);
        refreshLayout.setVisibility(VISIBLE);
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
    }

    public void setOnRefreshCompeleted() {
        if (isRefreshing()) {
            refreshLayout.setRefreshing(false);
            recyclerView.setRefreshing(false);
        }
        recyclerView.setOnLoadCompleted();
    }


    public void setLayoutManger(ILayoutManager manger) {
        this.iLayoutmanager = manger;
        recyclerView.setMyLayoutManager(manger);
    }

    public ILayoutManager getLayoutmanager() {
        return iLayoutmanager;
    }

    public boolean isStackFromEnd() {
        return iLayoutmanager == null ? false : iLayoutmanager.isStackFromEnd();
    }

    public void setHasFixedSize(boolean hasFix) {
        recyclerView.setHasFixedSize(hasFix);
    }

    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    public void addHeaderView(View view) {
        recyclerView.addHeaderView(view);
    }

    public void addFooterView(View view) {
        recyclerView.addFooterView(view);
    }

    public void removeHeaderView(View view) {
        recyclerView.removeHeaderView(view);
    }

    public void removeFooterView(View view) {
        recyclerView.removeFooterView(view);
    }

    @Override
    public void onRefresh() {
        if (listener != null && !recyclerView.isLoadingMore()) {
            listener.onRefresh(true);
            recyclerView.setRefreshing(true);
        }

    }

    public void setOnRefreshListener(onRefreshListener listener) {
        this.listener = listener;
        recyclerView.setOnLoadMoreListener(listener);
    }

    //不自动执行onrefresh的回调
    public void setRefreshing() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                recyclerView.setRefreshing(true);
            }
        });
    }

    //自动执行onrefresh的回调
    public void setAutoRefresh() {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                recyclerView.setRefreshing(true);
                onRefresh();
            }
        });
    }

    /*是否允许加载更多*/
    public boolean isEnableLoadMore() {
        return recyclerView.isLoadMoreEnable();
    }

    /*是否允许加载更多*/
    public void setEnableLoadMore(boolean enableLoadMore) {
        recyclerView.setLoadMoreEnable(enableLoadMore);
    }

    /*是否允许下拉刷新*/
    public void setEnableRefresh(boolean enableRefresh) {
        mEnableRefresh = enableRefresh;
        refreshLayout.setEnabled(enableRefresh);
    }

    /*设置是否正在成刷新*/
    public void setRefreshing(boolean refresh) {
        refreshLayout.setRefreshing(refresh);
    }

    /*添加装饰,可多次调用互不影响*/
    public void addItemDecoration(RecyclerView.ItemDecoration ItemDecoration) {
        recyclerView.addItemDecoration(ItemDecoration);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        recyclerView.setItemAnimator(animator);
    }

    public WrapRecyclerView getRecyclerView() {
        return recyclerView;
    }

    /*已经处理了  三种layoutManager的兼容 获取最后一条显示的postion*/
    public int getLastVisibleItemPosition() {
        return iLayoutmanager.findLastVisiblePosition();
    }

    public int getFirstVisibleItemPosition() {
        return iLayoutmanager.findFirstVisiblePosition();
    }


    /*设置 GridLayoutManager  或者 StaggeredGridLayoutManager模式下 不同条目所占的位置   要在设置适配器之前调用*/
    public void setLookSpan(LookSpanSize lookSpan) {
        if (lookSpan == null)
            return;
        recyclerView.setLookSpan(lookSpan);
    }

    /*显示没有更多加载内容的提示*/
    public void showLoadOverView() {
        recyclerView.showLoadOverView();
    }

    /*移除没有更多加载内容的提示*/
    public void removeLoadOverView() {
        recyclerView.removeLoadOverView();
    }

    /**
     * 检测垂直方向内容填充后是否可以滚动
     * <p>
     * 如果是在可获得View高度之前调用,那么得到的返回值将是-1
     *
     * @return true可是滚动 false当前内容填充不足以滚动
     * @doc return The adapter position of the last visible view or {@link RecyclerView#NO_POSITION} if
     * there aren't any visible items.
     * @see LinearLayoutManager#findLastVisibleItemPosition()
     */
    public boolean canScrollVertical() {
        if (adapter == null || adapter.getItemCount() == 0)
            return false;
        int lastVisibleItemPosition = getLastVisibleItemPosition();
        int firstVisibleItemPosition = getFirstVisibleItemPosition();
        if (lastVisibleItemPosition <= 0 || lastVisibleItemPosition == firstVisibleItemPosition)
            return false;
        return recyclerView.canScrollVertically(-1)
                || recyclerView.canScrollVertically(1);
    }


    /*键盘弹起时 ,根据IM消息规则 要不要滚动到最后一条*/
    public int scroll2LastPosition4IM(boolean smooth) {
        if (adapter == null)
            return 0;
        int lastItemPosition = adapter.getItemCount() - 1;
        if (lastItemPosition < 0) return -1;
        if (smooth) {
            recyclerView.smoothScrollToPosition(lastItemPosition);
        } else {
            recyclerView.scrollToPosition(lastItemPosition);
        }
        return lastItemPosition;
    }


    public void keepPositionAfterRefresh(final boolean refresh, final int countBeforeUpdate, boolean scrollDown, boolean active) {
        if (refresh) {
            int position = adapter.getItemCount() - countBeforeUpdate;
            if (position >= 0) recyclerView.scrollToPositionWithOffset(position, 0);
        }
        if (scrollDown)
            scroll2LastPosition4IM(true);
        if (!isStackFromEnd() && !active) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean canScrollVertically = canScrollVertical();
                    if (canScrollVertically)
                        iLayoutmanager.setStackFromEndIfPossible(true);
                }
            }, 300);
        }
    }

    public void scrollPosition2TopIfPossible(final int position, boolean smoothMove) {
        int firstItem = iLayoutmanager.findFirstVisiblePosition();
        int lastItem = iLayoutmanager.findLastVisiblePosition();
        if (position < firstItem) {
            // 如果要跳转的位置在第一个可见项之前，则smoothScrollToPosition可以直接跳转
            if (smoothMove) {
                recyclerView.smoothScrollToPosition(position);
            } else {
                recyclerView.scrollToPosition(position);
            }
        } else if (position <= lastItem) {
            // 如果要跳转的位置在第一个可见项之后，且在最后一个可见项之前
            // smoothScrollToPosition根本不会动，此时调用smoothScrollBy来滑动到指定位置
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < recyclerView.getChildCount()) {
                int top = recyclerView.getChildAt(movePosition).getTop();
                if (smoothMove) {
                    recyclerView.smoothScrollBy(0, top);
                } else {
                    recyclerView.scrollBy(0, top);
                }
            }
        } else {
            // 如果要跳转的位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，进入上一个控制语句
            if (smoothMove) {
                recyclerView.smoothScrollToPosition(position);
            } else {
                recyclerView.scrollToPosition(position);
            }
            this.finalTomove = true;
            this.smoothMove = smoothMove;
            this.positionToTop = position;
        }
    }

    public void setStackFromEnd(boolean stackFromEnd) {
        iLayoutmanager.setStackFromEndIfPossible(stackFromEnd);
    }
}
