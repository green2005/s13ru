package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.db.CursorHelper;
import com.parser.db.NewsFeedDBHelper;
import com.parser.fragments.PaginationSource;

public class VKNewsFeedAdapter extends SimpleCursorAdapter{
    private LayoutInflater mInflater;
    private PaginationSource mSource;

    public VKNewsFeedAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }


    public void setPaginationSource(PaginationSource source) {
        mSource = source;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return null;
        }
        cursor.moveToPosition(position);
        if (position == cursor.getCount() - 1) {
            if (mSource != null) {
                mSource.loadMore();
            }
        }
        View cnView = convertView;
        ViewHolder viewHolder;
        if (cnView == null) {
            cnView = mInflater.inflate(R.layout.item_news_feed, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            viewHolder.tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            viewHolder.imageView = (ImageView) cnView.findViewById(R.id.image);
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        viewHolder.tvText.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.TEXT_COLUMN));
        viewHolder.tvTitle.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.DATE_COLUMN));


        return cnView;
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvText;
        TextView tvDate;
        ImageView imageView;
        //TextView tvAuthor;
    }

}
