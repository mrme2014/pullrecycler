package com.mrs.mrs.multisupportrecyclerview.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mrs.mrs.multisupportrecyclerview.R;
import com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport.MultiTypeSupport;
import com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport.MultiTypeSupportAdapter;
import com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport.ViewHolder;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/6.
 */

public abstract class BaseListActivity<T> extends AppCompatActivity implements android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener,
        onLoadMoreListener, Toolbar.OnMenuItemClickListener {

    /**
     * 说明:
     * <p>
     * 这是一个通用的 可以快速搭建 列表界面的 BaseListActivity
     * 同时，支持刷新，加载更多，添加多headerview{@link BaseListActivity#addHeaderView}}，footerview{@link BaseListActivity#addFooterView(View)}}
     * <p>
     * <p>
     * * 使用实例
     * public class TestActivity extends BaseListActivity<JavaBean> {
     * private ArrayList<JavaBean> list = new ArrayList<>();
     *
     * @Override protected void onActivityInit() {
     * addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, recycleList, false));
     * addHeaderView(LayoutInflater.from(this).inflate(R.layout.dialog_more_select, recycleList, false));
     * }
     * @Override public int getItemLayoutRes() {
     * return android.R.layout.simple_list_item_1;
     * }
     * @Override public void onBindHolder(ViewHolder holder, JavaBean item, int position) {
     * //链式调用
     * holder.setText(android.R.id.text1, JavaBean.desc)
     * .setImageUrl(android.R.id.imageview, JavaBean.picUrl);
     * .setImageUrl(android.R.id.imageview1, JavaBean.picUrl1);
     * .setImageUrl(android.R.id.imageview2, JavaBean.picUrl2);
     * }
     * @Override public void onRefresh() {
     * mCurPage = 1;
     * loadCompleted(getDatas(true));
     * }
     * @Override public void onLoadMore() {
     * loadCompleted(getDatas(false));
     * }
     * @see WrapRecyclerView#setAdapter(RecyclerView.Adapter)
     * @see android.widget.ListView#addHeaderView(View, Object, boolean)
     * @see WrapRecyclerAdapter
     * <p>
     * <p>
     * 1.实现列表单布局  请复写
     * @see BaseListActivity#getItemLayoutRes()
     * <p>
     * 2.实现多条目布局，同样支持,请复写
     * @see BaseListActivity#getSupportMultiType()
     * <p>
     * 3.默认一页加载数量是20
     * @see Conf#DEFAULT_LIST_ITEM
     * <p>
     * 4.在刷新或者加载更多回调之后调用
     * @see BaseListActivity#loadFailed()
     * @see BaseListActivity#loadCompleted(ArrayList)
     * <p>
     */

    public WrapRecyclerView recycleList;
    public android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout;

    LinearLayout emptyView;
    public TextView mTitle;
    public Toolbar mToolbar;


    protected ArrayList<T> mDatas = new ArrayList<>();
    protected int mCurPage = 1;//默认初次加载页码为1
    protected ListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_widget_list);

        setUpRecyclerView();
        setUpSwipeRefreshLayout();
        onSetUpView();

    }

    private void setUpRecyclerView() {
        recycleList = (WrapRecyclerView) findViewById(R.id.recycleList);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mTitle = (TextView) findViewById(R.id.title);
        emptyView = (LinearLayout) findViewById(R.id.recordEmptyView);
        mAdapter = new ListAdapter(this, mDatas, getItemLayoutRes(), getSupportMultiType());
        recycleList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycleList.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        recycleList.setAdapter(mAdapter);
        recycleList.setOnLoadMoreListener(this);
    }

    private void setUpSwipeRefreshLayout() {
        SwipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout) findViewById(R.id.SwipeRefreshLayout);
        int schemeColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        SwipeRefreshLayout.setColorSchemeColors(schemeColor);
        SwipeRefreshLayout.setOnRefreshListener(this);
        SwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                SwipeRefreshLayout.setRefreshing(true);//刚进入界面就会自动刷新回调 onrefresh方法，开始加载数据
                onRefresh();
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationClick();
            }
        });
    }

    protected abstract void onSetUpView();

    //如果你想对导航栏 返回按钮点击时做特殊处理
    //可以复写该方法
    public void onNavigationClick() {
        this.finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        /**
         * swtich(item.getid()){
         *     case R.id.search:    do you  want
         *     break;
         *      case R.id.dialog:    show your dialog
         *     break;
         * }
         */
        //当点击某一个item的时候  同样可以使用dialog或者popwinow达到你想要的样子
        return false;
    }

    public void setUpMenu(@MenuRes int menuRes) {
        if (mToolbar == null || menuRes == -1)
            return;
        Menu menu = mToolbar.getMenu();
        if (menu != null)
            menu.clear();
        mToolbar.inflateMenu(menuRes);
        mToolbar.setOnMenuItemClickListener(this);
    }

    public void setUpTitle(String title) {
        if (mTitle == null)
            return;
        mTitle.setText(title);
    }

    public void setUpTitle(@StringRes int title) {
        if (mTitle == null)
            return;
        mTitle.setText(title);
    }

    public void disEnableLoadMore() {
        recycleList.setLoadMoreEnable(false);
    }

    public void enableLoadMore() {
        recycleList.setLoadMoreEnable(true);
    }

    public void loadFailed() {
        //刷新
        if (mCurPage == 1) {
            mDatas.clear();
            //空数据
            if (mDatas == null || mDatas.size() == 0) {
                recycleList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                //设置空白页面
            }
        } else {
            //加载更多
            recycleList.setLoadMoreEnable(false);
        }

        SwipeRefreshLayout.setRefreshing(false);
        recycleList.setOnLoadCompleted();
    }

    public void loadCompleted(ArrayList<T> list) {
        mCurPage++;
        recycleList.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        //刷新
        if (mCurPage == 2) {
            mDatas.clear();
            //空数据
            if (list == null || list.size() == 0) {
                //设置空白页面
                recycleList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

            } else if (list != null && list.size() > 0)
                mDatas.addAll(list);
            if (list != null && list.size() >= Conf.DEFAULT_LIST_ITEM)
                recycleList.setLoadMoreEnable(true);
        } else {
            //加载更多
            if (list != null && list.size() > 0)
                mDatas.addAll(list);
            //如果是第二页 而且返回的数据条目少于默认数量，则认为没有更多数据了，禁掉加载更多
            if (list == null || list.size() < Conf.DEFAULT_LIST_ITEM)
                recycleList.setLoadMoreEnable(false);
        }
        recycleList.setOnLoadCompleted();
        SwipeRefreshLayout.setRefreshing(false);

    }


    public class ListAdapter extends MultiTypeSupportAdapter<T> {
        public ListAdapter(Context context, ArrayList<T> list, int layoutRes, MultiTypeSupport typeSupport) {
            super(context, list, layoutRes, typeSupport);
        }

        @Override
        public void onItemClick(View view,int pos) {
            BaseListActivity.this.onItemClick(view,pos);
        }

        @Override
        public void onItemLongClick(View view,int pos) {
            BaseListActivity.this.onItemLongClick(view,pos);
        }

        @Override
        public void onBindNormalHolder(ViewHolder holder, T item, int position) {
            onBindHolder(holder, item, position);
        }
    }

    //如果 是单一条目 那么复写这个方法 设置条目  layout
    public int getItemLayoutRes() {
        return 0;
    }

    //如果是多条目，那么复写这个方法，根据条目类型，给出条目布局
    public MultiTypeSupport getSupportMultiType() {
        /**
         * @see MultiTypeSupport
         *
         */
        return null;
    }

    public void addHeaderView(View view) {
        // 先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (recycleList != null) {
            recycleList.addHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (recycleList != null) {
            recycleList.addFooterView(view);
        }
    }

    public void removeHeaderView(View view) {
        if (recycleList != null) {
            recycleList.removeHeaderView(view);
        }
    }

    public void removeFooterView(View view) {
        if (recycleList != null) {
            recycleList.removeFooterView(view);
        }
    }
    public void onItemClick(View view,int pos){

    }

    public void onItemLongClick(View view,int pos){

    }


    //实现类必须去实现
//    @Override
//    public void onRefresh() {
//
//    }
//
//    @Override
//    public void onLoadMore() {
//
//    }


    public abstract void onBindHolder(ViewHolder holder, T item, int position);

}
