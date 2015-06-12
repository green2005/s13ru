package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.activities.DetailActivity;
import com.parser.db.CursorHelper;
import com.parser.db.PosterFeedDBHelper;
import com.parser.fragments.PosterDetailFragment;
import com.parser.loader.ImageLoader;

public class PosterFeedAdapter extends SimpleCursorAdapter implements ListView.OnItemClickListener {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private Context mContext;

    private static final int DEFAILT_IMAGE_WIDTH = 259;
    private static final int DEFAULT_IMAGE_HEIGHT = 371;

    public PosterFeedAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
        mContext = context;
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
            cnView = mInflater.inflate(R.layout.item_posters_feed, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            viewHolder.tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            viewHolder.imageView = (ResizableImageView) cnView.findViewById(R.id.image);
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        //viewHolder.tvText.setText(CursorHelper.getString(cursor, PosterFeedDBHelper.TEXT_COLUMN));
        viewHolder.tvTitle.setText(CursorHelper.getString(cursor, PosterFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, PosterFeedDBHelper.DATE_COLUMN));
        setImage(viewHolder.imageView, CursorHelper.getString(cursor, PosterFeedDBHelper.IMAGE_URL_COLUMN));
        return cnView;
    }

    private void setImage(ResizableImageView imageView, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setOriginalImageSize(DEFAILT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
            imageView.setVisibility(View.VISIBLE);
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, imageUrl);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            String url = CursorHelper.getString(cursor, PosterFeedDBHelper.LINK_COLUMN);
            Intent intent = new Intent(mContext, DetailActivity.class);
            Bundle args = new Bundle();
            args.putInt(DetailActivity.DETAIL_TYPE, FragmentMenuItem.POSTER_ITEM.ordinal());
            args.putString(PosterDetailFragment.POSTER_LINK_KEY, url);
            intent.putExtras(args);
            mContext.startActivity(intent);
        }
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvText;
        TextView tvDate;
        ResizableImageView imageView;
        //TextView tvAuthor;
    }
}
