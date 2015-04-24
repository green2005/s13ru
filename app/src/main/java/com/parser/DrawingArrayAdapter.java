package com.parser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DrawingArrayAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    FragmentMenuItem[] mItems;

    public DrawingArrayAdapter(Context context, FragmentMenuItem[] objects) {
        super();
        if (context == null) {
            throw new IllegalArgumentException("Context parameter is null");
        }
        if (objects == null) {
            throw new IllegalArgumentException("FragmentTypes array is null");
        }
        mInflater = LayoutInflater.from(context);
        mItems = objects;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        ViewHolder holder;
        if (cnView == null) {
            cnView = mInflater.inflate(R.layout.item_drawer_list, null);
            holder = new ViewHolder();
            cnView.setTag(holder);
            holder.textView = (TextView) cnView.findViewById(R.id.itemtext);
            holder.imageView = (ImageView) cnView.findViewById(R.id.img);
        } else {
            holder = (ViewHolder) cnView.getTag();
        }
        holder.textView.setText(mItems[position].getNameResourceId());
        holder.imageView.setImageResource(mItems[position].getImageResourceId());
        return cnView;
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
