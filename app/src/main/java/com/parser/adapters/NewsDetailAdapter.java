package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.parser.R;
import com.parser.db.NewsDetailDBHelper;
import com.parser.processors.NewsDetailProcessor;

public class NewsDetailAdapter extends SimpleCursorAdapter {
    private static final int VIEW_TYPE_COUNT = 5;
    private LayoutInflater mInflater;
    private Context mContext;


    public NewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getInt(cursor.getColumnIndex(NewsDetailDBHelper.RECORD_TYPE_COLUMN));
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        int viewType = getItemViewType(position);
        if (viewType == NewsDetailDBHelper.NewsItemType.TITLE.ordinal()) {
            if (cnView == null){
                cnView = mInflater.inflate(R.layout.item_post_title, null);
            }

        } else if (viewType == NewsDetailDBHelper.NewsItemType.TEXT.ordinal()) {

        } else if (viewType == NewsDetailDBHelper.NewsItemType.IMAGE.ordinal()) {

        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal()) {

        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal()) {

        }
        return super.getView(position, convertView, parent);
    }
}
