package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.activities.DetailActivity;
import com.parser.db.CursorHelper;
import com.parser.db.VKFeedDBHelper;
import com.parser.fragments.PaginationSource;
import com.parser.fragments.VKFeedDetailFragment;
import com.parser.loader.ImageLoader;

public class VKNewsFeedAdapter extends SimpleCursorAdapter implements ListView.OnItemClickListener {
    private LayoutInflater mInflater;
    private PaginationSource mSource;
    private ImageLoader mImageLoader;
    private Context mContext;

    public VKNewsFeedAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
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
            cnView = mInflater.inflate(R.layout.item_news_feed_image, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            viewHolder.tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            viewHolder.imageView = (ResizableImageView) cnView.findViewById(R.id.image);
            cnView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) cnView.getTag();
        }
        viewHolder.tvText.setText(Html.fromHtml(CursorHelper.getString(cursor, VKFeedDBHelper.TEXT_COLUMN)));
        // Linkify.addLinks(viewHolder.tvText, Linkify.ALL);
        viewHolder.tvTitle.setVisibility(View.GONE);

        //viewHolder.tvTitle.setText(CursorHelper.getString(cursor, VKFeedDBHelper.TITLE_COLUMN));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, VKFeedDBHelper.DATE_COLUMN));
        setImage(viewHolder.imageView, CursorHelper.getString(cursor, VKFeedDBHelper.IMAGE_URL_COLUMN),
                CursorHelper.getInt(cursor, VKFeedDBHelper.IMAGE_WIDTH),
                CursorHelper.getInt(cursor, VKFeedDBHelper.IMAGE_HEIGHT)
        );
        return cnView;
    }

    private void setImage(ResizableImageView imageView, String imageUrl, int width, int height) {
        if (TextUtils.isEmpty(imageUrl)) {
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
        if (cursor == null) {
            return;
        }
        String postId = CursorHelper.getString(cursor, VKFeedDBHelper.POST_ID_COLUMN);
        Bundle args = new Bundle();
        args.putInt(DetailActivity.DETAIL_TYPE, FragmentMenuItem.VK_ITEM.ordinal());
        args.putString(VKFeedDetailFragment.POST_ID_KEY, postId);
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtras(args);
        mContext.startActivity(intent);
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvText;
        TextView tvDate;
        ResizableImageView imageView;
        //TextView tvAuthor;
    }

}
