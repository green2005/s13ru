package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.db.CursorHelper;
import com.parser.db.VKFeedDBHelper;

public class PosterAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;


    public PosterAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return null;
        }
        cursor.moveToPosition(position);
        View cnView = convertView;
        ViewHolder viewHolder;
        if (cnView == null) {
            cnView = mInflater.inflate(R.layout.item_news_feed_image, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            viewHolder.tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            viewHolder.imageView = (ImageView) cnView.findViewById(R.id.image);
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        viewHolder.tvText.setText(CursorHelper.getString(cursor, VKFeedDBHelper.TEXT_COLUMN));
        viewHolder.tvTitle.setText(CursorHelper.getString(cursor, VKFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, VKFeedDBHelper.DATE_COLUMN));
        setImage(viewHolder.imageView, CursorHelper.getString(cursor, VKFeedDBHelper.IMAGE_URL_COLUMN));
        return cnView;
    }

    private void setImage(ImageView imageView, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, imageUrl);
            }
        }
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvText;
        TextView tvDate;
        ImageView imageView;
        //TextView tvAuthor;
    }
}
