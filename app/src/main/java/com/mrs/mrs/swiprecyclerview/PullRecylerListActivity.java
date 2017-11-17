package com.mrs.mrs.swiprecyclerview;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrs.mrs.multisupportrecyclerview.R;
import com.mrs.mrs.swiprecyclerview.widget.ProcessImageView;
import com.qiaomu.libmultirecyclerview.multisupport.MultiTypeSupport;
import com.qiaomu.libmultirecyclerview.multisupport.ViewHolder;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 */

public class PullRecylerListActivity extends BaseListActivity<PullRecylerListActivity.JavaBean> {
    @Override
    protected void onSetUpView() {

        addHeaderView(LayoutInflater.from(this).inflate(R.layout.header, mPullRecycler, false));
        View header = LayoutInflater.from(this).inflate(R.layout.header, mPullRecycler, false);
        header.setBackgroundColor(Color.BLUE);
        ((TextView) header).setText("这里是第二个Header....");
        addHeaderView(header);
        //支持侧滑自动关闭已打开的
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
        if (item.type == 3 || item.type == 2 || item.type == 1) {
            holder.setText(R.id.text1, item.txt);
            holder.setBackGroundColor(R.id.text1, (item.type == 3 ? Color.GRAY : (item.type == 2 ? Color.LTGRAY : Color.GREEN)));
        } else if (item.type == 4) {
            holder.setText(R.id.tv, item.txt);
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


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://refresh
                    loadCompleted((ArrayList<JavaBean>) msg.obj);
                    break;
                case 1:
                    loadCompleted((ArrayList<JavaBean>) msg.obj);
                    break;
                case 2:
                    loadFailed();
                    break;
            }

        }
    };

    protected void getDatas(final boolean refresh) {
        final int size = refresh ? 0 : mDatas.size();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<JavaBean> newlist = new ArrayList();
                for (int i = 0; i < 20; i++) {
                    if (i == 0) {
                        newlist.add(new JavaBean(1, "多条目类型1——" + (size + i)));
                    } else if (i == 1)
                        newlist.add(new JavaBean(2, "多条目类型2——" + (size + i)));
                    else if (i == 2)
                        newlist.add(new JavaBean(5, "多条目类型3——" + (size + i)));
                    else newlist.add(new JavaBean(4, "多条目类型4侧滑删除菜单——" + (size + i)));

                }
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (refresh) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = newlist;
                    mHandler.sendMessage(msg);
                } else if (mDatas.size() < 50) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = newlist;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    msg.obj = newlist;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void onRefresh(boolean isRefresh) {
        if (isRefresh) {
            mCurPage = 1;
            getDatas(true);
            //loadCompleted(list);
        } else {
            mPullRecycler.removeLoadOverView();
            getDatas(false);
        }

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
