package com.qiaomu.libmultirecyclerview.multisupport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 * <p>
 * <p>
 * 多条目支持类型适配器
 */

public abstract class MultiTypeSupportAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    public Context mContext;
    public ArrayList<T> mDatas;
    //支持多布局的回调
    private MultiTypeSupport mTypeSupport;
    //如果不支持多布局,那么mLayoutRes就是 item布局
    private int mLayoutRes;

    public MultiTypeSupportAdapter(Context context, ArrayList<T> list, int layoutRes, MultiTypeSupport typeSupport) {
        this.mContext = context;
        this.mDatas = list;
        this.mTypeSupport = typeSupport;
        this.mLayoutRes = layoutRes;

    }

    @Override
    public int getItemViewType(int position) {
        //如果是应用了多布局，那么通过getItemViewType 把item的layout布局id传进来
        if (mTypeSupport != null)
            return mTypeSupport.getTypeLayoutRes(mDatas.get(position), position);
        return super.getItemViewType(position);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 多布局支持
        if (mTypeSupport != null) {
            mLayoutRes = viewType;
        }
        if (mLayoutRes == 0) {//容错处理,如果布局类型匹配不到，那么也不至于找不到资源崩溃
            TextView textView = new TextView(mContext);
            textView.setVisibility(View.GONE);
            return new ViewHolder(textView);
        }
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutRes, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        onBindNormalHolder(holder, mDatas.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick(v, position);
                return true;
            }
        });
    }

    public void onItemClick(View view, int position) {

    }

    public void onItemLongClick(View view, int position) {

    }

    public abstract void onBindNormalHolder(ViewHolder holder, T t, int position);

    @Override
    public int getItemCount() {

        return mDatas == null ? 0 : mDatas.size();
    }

}
