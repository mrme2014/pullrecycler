package com.mrs.mrs.swiprecyclerview.recyclerview.multisupport;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrs.mrs.multisupportrecyclerview.R;

import java.util.ArrayList;

/**
 * Created by mrs on 2017/4/7.
 */

public abstract class MultiTypeSupportAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private ArrayList<T> mDatas;
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
        if (mLayoutRes == 0)
            throw new RuntimeException("filed mTypeSupport-&-mLayoutRes must be one is not null");
        View itemView = LayoutInflater.from(mContext).inflate(mLayoutRes, parent, false);
        ViewHolder holder = new ViewHolder(itemView);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
                return false;
            }
        });
        onBindNormalHolder(holder, mDatas.get(position), position);
    }

    public abstract void onItemClick(View view, int pos);

    public abstract void onItemLongClick(View view, int pos);


    public abstract void onBindNormalHolder(ViewHolder holder, T item, int position);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }
}
