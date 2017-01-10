package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.activities.DetailActivity;
import com.parser.db.CursorHelper;
import com.parser.db.NewsFeedDBHelper;
import com.parser.fragments.NewsDetailFragment;
import com.parser.fragments.PaginationSource;
import com.parser.loader.ImageLoader;

public class NewsAdapter extends SimpleCursorAdapter implements AdapterView.OnItemClickListener {
    private LayoutInflater mInflater;
    private PaginationSource mSource;
    private Context mContext;
    private ImageLoader mImageLoader;
    private boolean mLoadImages = true;


    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = ImageLoader.get(mContext);

        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mLoadImages = preferences.getBoolean(mContext.getResources().getString(R.string.load_images_key), true);
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
        final ViewHolder viewHolder;
        if (cnView == null) {
            cnView = mInflater.inflate(R.layout.item_news_feed_image, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            viewHolder.tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            viewHolder.imageView = (ResizableImageView) cnView.findViewById(R.id.image);
            viewHolder.imageView.setClickable(false);
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        viewHolder.tvText.setText(Html.fromHtml(CursorHelper.getString(cursor, NewsFeedDBHelper.TEXT_COLUMN)));
        viewHolder.tvTitle.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, NewsFeedDBHelper.DATE_COLUMN));

        String imageUrl = CursorHelper.getString(cursor, NewsFeedDBHelper.IMAGE_URL);
        int imageHeight = CursorHelper.getInt(cursor, NewsFeedDBHelper.IMAGE_HEIGHT);
        int imageWidth = CursorHelper.getInt(cursor, NewsFeedDBHelper.IMAGE_WIDTH);
        setImage(viewHolder.imageView, imageUrl, imageWidth, imageHeight);
        return cnView;
    }

    private void setImage(ResizableImageView imageView, String imageUrl, int width, int height) {
        if (!mLoadImages || TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOriginalImageSize(width, height);
            if (mImageLoader != null) {
                mImageLoader.loadImage(imageView, imageUrl);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = getCursor();
        if (cursor != null && position < cursor.getCount()) {
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
        ResizableImageView imageView;

        //TextView tvAuthor;
    }
}
