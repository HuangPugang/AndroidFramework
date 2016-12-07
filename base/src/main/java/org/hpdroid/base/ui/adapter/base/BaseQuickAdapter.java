package org.hpdroid.base.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaka on 16/1/29.
 */
public abstract class BaseQuickAdapter<T, H extends BaseAdapterHelper> extends BaseAdapter {

    protected final Context mContext;
    protected final LayoutInflater mInflater;
    protected final int mLayoutResId;
    protected final List<T> mData;

    public BaseQuickAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    public BaseQuickAdapter(Context context, int layoutResId, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mLayoutResId = layoutResId;
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<>(data);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final H h = getAdapterHelper(position, convertView, parent);
        convert(h, getItem(position));
        return h.getView();
    }

    @Override
    public boolean isEnabled(int position) {
        return position < getCount();
    }

    public void add(T item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void add(int position, T item) {
        mData.add(position, item);
        notifyDataSetChanged();
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
        if (position < getCount()) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    public void remove(T item) {
        mData.remove(item);
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

    protected abstract void convert(H helper, T item);

    protected abstract H getAdapterHelper(int position, View convertView, ViewGroup parent);
}
