package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.db.NewsDetailDBHelper;
import com.parser.loader.ImageLoader;
import com.parser.processors.NewsDetailProcessor;

public class NewsDetailAdapter extends SimpleCursorAdapter {
    private static final int VIEW_TYPE_COUNT = 5;
    private static final int VIEW_TYPE = 1;

    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;


    public NewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
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
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int convertedViewType = (Integer)cnView.getTag(VIEW_TYPE);
        int viewType = getItemViewType(position);
        if (viewType == NewsDetailDBHelper.NewsItemType.TITLE.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_title, null);
            TextView tvTitle = (TextView)cnView.findViewById(R.id.tvTitle);
            tvTitle.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN)));
        } else if (viewType == NewsDetailDBHelper.NewsItemType.TEXT.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_text, null);
            TextView tvText = (TextView)cnView.findViewById(R.id.tvText);
            tvText.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN)));
        } else if (viewType == NewsDetailDBHelper.NewsItemType.IMAGE.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_image, null);
            ResizableImageView imageView = (ResizableImageView)cnView.findViewById(R.id.image);
            String url = cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN));
            mImageLoader.loadImage(imageView, url);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal()) {
            cnView = mInflater.inflate(R.layout.item_comment_header, null);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_comment, null);
            TextView tvUser = (TextView) cnView.findViewById(R.id.tvUserName);
            TextView tvComment = (TextView) cnView.findViewById(R.id.tvComment);
            tvUser.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.AUTHOR_COLUMN)));
            tvComment.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN)));
        }
        cnView.setTag(viewType);
        return cnView;
    }
}
