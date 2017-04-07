package com.mrs.mrs.multisupportrecyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.mrs.mrs.multisupportrecyclerview.recyclerview.BaseListActivity;
import com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport.MultiTypeSupport;
import com.mrs.mrs.multisupportrecyclerview.recyclerview.multisupport.ViewHolder;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 */

public class MainActivity extends BaseListActivity<MainActivity.JavaBean> {
    private ArrayList<JavaBean> list = new ArrayList<>();

    @Override
    protected void onSetUpView() {
        addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, recycleList, false));
        addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, recycleList, false));
    }

//    @Override
//    public int getItemLayoutRes() {
//        return android.R.layout.simple_list_item_1;
//    }

    @Override
    public MultiTypeSupport getSupportMultiType() {
        MultiTypeSupport support = new MultiTypeSupport<JavaBean>() {
            @Override
            public int getTypeLayoutRes(JavaBean data, int pos) {
                if (data.type == 1)
                    return android.R.layout.simple_list_item_1;
                else if (data.type == 2)
                    return android.R.layout.simple_list_item_2;
                else if (data.type == 3)
                    return android.R.layout.simple_dropdown_item_1line;
                return android.R.layout.simple_list_item_1;
            }
        };
        return support;
    }

    @Override
    public void onBindHolder(ViewHolder holder, JavaBean item, int position) {
        if (item.type == 2)
            holder.setText(android.R.id.text2, item.txt);
        else if (item.type == 3)
            holder.setText(android.R.id.text1, item.txt);
        holder.setText(android.R.id.text1, item.txt);
    }

    @Override
    public void onRefresh() {
        mCurPage = 1;
        getDatas(true);
        loadCompleted(list);
    }

    @Override
    public void onLoadMore() {
        getDatas(false);
    }

    @Override
    public void onItemClick(View view,int pos) {
        Toast.makeText(this,"click_pos="+pos,Toast.LENGTH_SHORT).show();
        Log.e("onItemClick","pos="+pos);
    }

    protected ArrayList<JavaBean> getDatas(boolean refresh) {
        list.clear();
        if (refresh) {
            for (int i = 0; i < 20; i++) {
                if (i == 0) {
                    list.add(new JavaBean(1, "条目类型1---position" + i));
                } else if (i == 1)
                    list.add(new JavaBean(2, "条目类型2---position" + i));
                else if (i == 2)
                    list.add(new JavaBean(3, "条目类型3---position" + i));
                else list.add(new JavaBean(1, "条目类型1---position" + i));
            }
        } else {
            final int size = mDatas.size();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        if (i == 0) {
                            list.add(new JavaBean(1, "条目类型1---position" + (size + i)));
                        } else if (i == 1)
                            list.add(new JavaBean(2, "条目类型2---position" + (size + i)));
                        else if (i == 2)
                            list.add(new JavaBean(3, "条目类型3---position" + (size + i)));
                        else list.add(new JavaBean(1, "条目类型1---position" + (size + i)));
                    }
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadCompleted(list);
                            if (mDatas.size() > 60) {
                                disEnableLoadMore();
                            }
                        }
                    });
                }
            }).start();
        }
        return list;
    }

    class JavaBean {
        public int type;
        public String txt;

        public JavaBean(int type, String txt) {
            this.txt = txt;
            this.type = type;
        }
    }
}
