package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.activities.DetailActivity;
import com.parser.db.CursorHelper;
import com.parser.db.NewsFeedDBHelper;
import com.parser.fragments.NewsDetailFragment;
import com.parser.fragments.PaginationSource;

public class NewsAdapter extends SimpleCursorAdapter implements AdapterView.OnItemClickListener {
    private LayoutInflater mInflater;
    private PaginationSource mSource;
    Context mContext;


    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        viewHolder.tvText.setText(Html.fromHtml(CursorHelper.getString(cursor, NewsFeedDBHelper.TEXT_COLUMN)));
       // Linkify.addLinks(viewHolder.tvText, Linkify.ALL);
        viewHolder.tvTitle.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.DATE_COLUMN));
        return cnView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = getCursor();
        if (cursor != null  && position < cursor.getCount()) {
            cursor.moveToPosition(position);
            String url = cursor.getString(cursor.getColumnIndex(NewsFeedDBHelper.LINK_COLUMN));
            Intent intent = new Intent(mContext, DetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(DetailActivity.DETAIL_TYPE, FragmentMenuItem.NEWS_ITEM.ordinal());
            bundle.putString(NewsDetailFragment.URL_PARAM, url);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvText;
        TextView tvDate;
        //TextView tvAuthor;
    }
}
