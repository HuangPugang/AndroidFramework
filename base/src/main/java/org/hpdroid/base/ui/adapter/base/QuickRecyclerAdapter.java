package org.hpdroid.base.ui.adapter.base;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaka on 16/5/24.
 */
public abstract class QuickRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected final Context mContext;
    protected final LayoutInflater mInflater;
    protected final Resources mResources;
    protected final int mLayoutResId;
    protected final List<T> mData;

    public QuickRecyclerAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    public QuickRecyclerAdapter(Context context, int layoutResId, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mResources = context.getResources();
        this.mLayoutResId = layoutResId;
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<>(data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder(mContext, mInflater.inflate(mLayoutResId, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        convert(holder, getItem(position),position);
    }

    public boolean isEmpty() {
        return mData == null || mData.isEmpty();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(T item) {
        int position = getItemCount();
        mData.add(item);
        notifyItemInserted(position);
    }

    public void add(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<T> items) {
        if (items != null && !items.isEmpty()) {
            mData.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addAll(int position, List<T> items) {
        if (items != null && !items.isEmpty()) {
            mData.addAll(position, items);
            notifyDataSetChanged();
        }
    }

    public void remove(int position) {
        if (position < getItemCount()) {
            mData.remove(position);
//            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

    public void remove(T item) {
//        int position = mData.indexOf(item);
        mData.remove(item);
//        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void changeData(List<T> items) {
        mData.clear();
        if (items != null)
            mData.addAll(items);
        notifyDataSetChanged();
    }

    protected abstract void convert(BaseViewHolder holder, T item,int position);
}
