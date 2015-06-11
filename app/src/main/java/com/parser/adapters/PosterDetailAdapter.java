package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.LinkifiedTextView;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.db.CursorHelper;
import com.parser.db.PosterDetailDBHelper;
import com.parser.loader.ImageLoader;

public class PosterDetailAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;


    public PosterDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
    }

    @Override
    public int getViewTypeCount() {
        return PosterDetailDBHelper.POSTER_RECORD_TYPE.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            return CursorHelper.getInt(cursor, PosterDetailDBHelper.CONTENT_TYPE_COLUMN);

        } else {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return convertView;
        }
        cursor.moveToPosition(position);
        int viewType = getItemViewType(position);
        if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.TITLE.ordinal()) {
            convertView = getTitleView(convertView, cursor);
        } else if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal()) {
            convertView = getDescriptionView(convertView, cursor);
        } else if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal()) {
            convertView = getImageAttachmentView(convertView, cursor);
        } else if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.TIMEPLACE_RECORD.ordinal()) {
            convertView = getTimePlaceView(convertView, cursor);
        } else if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.VIDEO_ATTACHMENT.ordinal()) {
            convertView = getVideoAttachmentView(convertView, cursor);
        }
        return convertView;
    }

    private View getTitleView(View convertView, Cursor cursor) {
        if (convertView == null || convertView.getTag() != PosterDetailDBHelper.POSTER_RECORD_TYPE.TITLE.ordinal()) {
            convertView = mInflater.inflate(R.layout.item_poster_title, null);
            convertView.setTag(PosterDetailDBHelper.POSTER_RECORD_TYPE.TITLE.ordinal());
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(CursorHelper.getString(cursor, PosterDetailDBHelper.TEXT_COLUMN));
        return convertView;
    }

    private View getDescriptionView(View convertView, Cursor cursor) {
        if (convertView == null || convertView.getTag() != PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal()) {
            convertView = mInflater.inflate(R.layout.item_post_text, null);
            convertView.setTag(PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal());
        }
        LinkifiedTextView tvText = (LinkifiedTextView) convertView.findViewById(R.id.tvText);
        tvText.setText(Html.fromHtml(CursorHelper.getString(cursor, PosterDetailDBHelper.TEXT_COLUMN)));
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private View getImageAttachmentView(View convertView, Cursor cursor) {
        if (convertView == null || convertView.getTag() != PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal()) {
            convertView = mInflater.inflate(R.layout.item_post_image, null);
            convertView.setTag(PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal());
        }
        ResizableImageView imageView = (ResizableImageView) convertView.findViewById(R.id.image);
        if (mImageLoader != null) {
            mImageLoader.loadImage(imageView, CursorHelper.getString(cursor, PosterDetailDBHelper.TEXT_COLUMN));
        }
        return convertView;
    }

    private View getTimePlaceView(View convertView, Cursor cursor) {
        if (convertView == null || convertView.getTag() != PosterDetailDBHelper.POSTER_RECORD_TYPE.TIMEPLACE_RECORD.ordinal()) {
            convertView = mInflater.inflate(R.layout.item_place_date, null);
            convertView.setTag(PosterDetailDBHelper.POSTER_RECORD_TYPE.TIMEPLACE_RECORD.ordinal());
        }
        TextView tvPlace = (TextView) convertView.findViewById(R.id.tvPlace);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        tvPlace.setText(CursorHelper.getString(cursor, PosterDetailDBHelper.PLACE_COLUMN));
        tvDate.setText(CursorHelper.getString(cursor, PosterDetailDBHelper.DATE_COLUMN));
        tvTime.setText(CursorHelper.getString(cursor, PosterDetailDBHelper.TIME_COLUMN));
        return convertView;
    }

    private View getVideoAttachmentView(View convertView, Cursor cursor) {
        if (convertView == null || convertView.getTag() != PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal()) {
            convertView = mInflater.inflate(R.layout.item_post_image, null);
            convertView.setTag(PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal());
        }
        ResizableImageView imageView = (ResizableImageView) convertView.findViewById(R.id.image);
        if (mImageLoader != null) {
            mImageLoader.loadImage(imageView, CursorHelper.getString(cursor, PosterDetailDBHelper.TEXT_COLUMN));
        }
        return convertView;
    }
}
