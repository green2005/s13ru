package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.db.NewsDetailDBHelper;
import com.parser.loader.ImageLoader;

public class NewsDetailAdapter extends SimpleCursorAdapter {
    private static final int VIEW_TYPE_COUNT = 5;

    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;


    public NewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
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
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int convertedViewType = -1;
        if (cnView != null) {
            convertedViewType = (Integer) cnView.getTag(R.string.DETAIL_VIEW_TYPE);
        }
        int viewType = getItemViewType(position);
        if (viewType == NewsDetailDBHelper.NewsItemType.TITLE.ordinal()) {
            if (convertedViewType != viewType || cnView == null){
                cnView = mInflater.inflate(R.layout.item_post_title, null);
            }
            TextView tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
            TextView tvDate = (TextView)cnView.findViewById(R.id.tvDate);
            tvDate.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.DATE_COLUMN)));
            tvTitle.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
            Linkify.addLinks(tvTitle, Linkify.ALL);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.TEXT.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_text, null);
            TextView tvText = (TextView) cnView.findViewById(R.id.tvText);
            tvText.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
            Linkify.addLinks(tvText, Linkify.ALL);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.IMAGE.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_image, null);
            ResizableImageView imageView = (ResizableImageView) cnView.findViewById(R.id.image);
            String url = cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN));
            int imageWidth = cursor.getInt(cursor.getColumnIndex(NewsDetailDBHelper.IMAGE_WIDTH_COLUMN));
            int imageHeight = cursor.getInt(cursor.getColumnIndex(NewsDetailDBHelper.IMAGE_HEIGTH_COLUMN));
            if (imageWidth > 0){
                imageView.setOriginalImageSize(imageWidth, imageHeight);
            }
            mImageLoader.loadImage(imageView, url);

        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal()) {
            cnView = mInflater.inflate(R.layout.item_comment_header, null);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal()) {
            if (convertedViewType != viewType || cnView == null)
                cnView = mInflater.inflate(R.layout.item_post_comment, null);
            TextView tvUser = (TextView) cnView.findViewById(R.id.tvUserName);
            TextView tvComment = (TextView) cnView.findViewById(R.id.tvComment);
            TextView tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            TextView tvKarmaUp = (TextView) cnView.findViewById(R.id.tvUps);
            TextView tvKarmaDown = (TextView) cnView.findViewById(R.id.tvDowns);
            tvKarmaUp.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.KARMA_UP_COLUMN)));
            tvKarmaDown.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.KARMA_DOWN_COLUMN)));

            tvDate.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.DATE_COLUMN)));
            ImageView imvUserImage = (ImageView)cnView.findViewById(R.id.userPick);
            String userImageUrl = cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.AUTHOR_IMAGE_COLUMN));
            if (!TextUtils.isEmpty(userImageUrl)){
                mImageLoader.loadImage(imvUserImage, userImageUrl);
                imvUserImage.setVisibility(View.VISIBLE);
            } else
            {
                imvUserImage.setVisibility(View.GONE);
            }
            tvUser.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.AUTHOR_COLUMN)));
            tvComment.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
            Linkify.addLinks(tvComment, Linkify.ALL);
        }
        cnView.setTag(R.string.DETAIL_VIEW_TYPE, viewType);
        return cnView;
    }
}
