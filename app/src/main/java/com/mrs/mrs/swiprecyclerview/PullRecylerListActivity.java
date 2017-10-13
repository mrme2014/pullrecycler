package com.mrs.mrs.swiprecyclerview;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.mrs.mrs.multisupportrecyclerview.R;
import com.mrs.mrs.swiprecyclerview.recyclerview.ProcessImageView;
import com.mrs.mrs.swiprecyclerview.recyclerview.multisupport.MultiTypeSupport;
import com.mrs.mrs.swiprecyclerview.recyclerview.multisupport.TimeItemDecoration;
import com.mrs.mrs.swiprecyclerview.recyclerview.multisupport.ViewHolder;
import com.mrs.mrs.swiprecyclerview.widget.BadgeView;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 */

public class PullRecylerListActivity extends BaseListActivity<PullRecylerListActivity.JavaBean> {
    private ArrayList<JavaBean> list = new ArrayList<>();

    @Override
    protected void onSetUpView() {
        addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, mPullRecycler, false));
        addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, mPullRecycler, false));
        mPullRecycler.getRecyclerView().setSupportSwipDiMiss(true);
    }


    @Override
    public MultiTypeSupport getSupportMultiType() {
        MultiTypeSupport support = new MultiTypeSupport<JavaBean>() {
            @Override
            public int getTypeLayoutRes(JavaBean data, int pos) {
                if (data.type == 1)
                    return R.layout.simple_list_item_1;
                else if (data.type == 2)
                    return R.layout.simple_list_item_2;
                else if (data.type == 3)
                    return R.layout.simple_list_item_3;
                else if (data.type == 4)
                    return R.layout.activity_main_list_item;
                else return R.layout.list_item_img;
            }
        };
        return support;
    }

    @Override
    public void onBindHolder(ViewHolder holder, JavaBean item, final int position) {
        if (item.type == 3 || item.type == 2 || item.type == 1)
            holder.setText(R.id.text1, item.txt);
        else if (item.type == 4) {
            holder.setOnItemClickListener(R.id.iv_img, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, position);
                }
            });
            holder.setOnItemClickListener(R.id.tv_text, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount() - position);
                }
            });

        } else if (item.type == 5) {
            final ProcessImageView view = holder.getView(R.id.proimg);
            view.setImageResource(R.mipmap.ic_launcher_round);
            view.startAnimation();
        }
    }

    @Override
    public void onItemClick(View view, int pos) {
        Toast.makeText(this, mDatas.get(pos).type + "click_pos=" + pos, Toast.LENGTH_SHORT).show();
    }

    protected ArrayList<JavaBean> getDatas(boolean refresh) {
        list.clear();
        if (refresh) {
            for (int i = 0; i < 20; i++) {
                if (i == 0) {
                    list.add(new JavaBean(1, "多Item类型1---position" + i));
                } else if (i == 1)
                    list.add(new JavaBean(2, "多Item类型2---position" + i));
                else if (i == 2)
                    list.add(new JavaBean(3, "多Item类型3---position" + i));
                else if (i == 3)
                    list.add(new JavaBean(5, "多Item类型5---position" + i));
                else list.add(new JavaBean(4, "多Item类型4侧滑删除菜单---position" + i));

            }
        } else {
            final int size = mDatas.size();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        if (i == 0) {
                            list.add(new JavaBean(1, "多Item类型1---position" + (size + i)));
                        } else if (i == 1)
                            list.add(new JavaBean(2, "多Item类型2---position" + (size + i)));
                        else if (i == 2)
                            list.add(new JavaBean(5, "多Item类型3---position" + (size + i)));
                        else list.add(new JavaBean(4, "多Item类型4---position" + (size + i)));

                    }
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mDatas.size() > 50) {
                                loadFailed();
                            } else {
                                loadCompleted(list);
                            }
                        }
                    });
                }
            }).start();
        }
        return list;
    }

    @Override
    public void onRefresh(boolean isRefresh) {
        if (isRefresh) {
            mCurPage = 1;
            getDatas(true);
            loadCompleted(list);
        }
        if (!isRefresh)
            getDatas(false);
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
