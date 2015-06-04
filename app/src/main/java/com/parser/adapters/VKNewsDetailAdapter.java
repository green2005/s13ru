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
import android.widget.Toast;

import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.bo.VKDetailItem;
import com.parser.db.CursorHelper;
import com.parser.db.VKDetailDBHelper;
import com.parser.fragments.PaginationSource;
import com.parser.loader.ImageLoader;

public class VKNewsDetailAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private PaginationSource mSource;
    private Context mContext;

    public VKNewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
        mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return VKDetailItem.ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            return CursorHelper.getInt(cursor, VKDetailDBHelper.ITEM_TYPE);
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor != null) {

            if (position == cursor.getCount() - 1) {
                if (mSource != null) {
                    mSource.loadMore();
                }
            }

            cursor.moveToPosition(position);
            int itemType = getItemViewType(position);
            if (itemType == VKDetailItem.ItemType.CONTENT.ordinal()) {
                convertView = getContentView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.COMMENT.ordinal()) {
                convertView = getCommentView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.ATTACHMENT_PHOTO.ordinal()) {
                convertView = getAttachmentPhotoView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.ATTACHMENT_VIDEO.ordinal()) {
                convertView = getAttachmentVideoView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.DELIMITER.ordinal()) {
                convertView = getDelimiterView(convertView);
            }
        }
        return convertView;
    }

    private View getContentView(View view, Cursor cursor) {
        PostViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_vk_post, null);
            viewHolder = new PostViewHolder();
            viewHolder.tvAuthor = (TextView) view.findViewById(R.id.tvUserName);
            viewHolder.uPick = (ImageView) view.findViewById(R.id.userPick);
            viewHolder.tvDate = (TextView) view.findViewById(R.id.tvDate);
            viewHolder.tvText = (TextView) view.findViewById(R.id.tvComment);
            view.setTag(viewHolder);
        } else {
            viewHolder = (PostViewHolder) view.getTag();
        }
        setImage(viewHolder.uPick, CursorHelper.getString(cursor, VKDetailDBHelper.AUTHOR_IMAGE));
        viewHolder.tvText.setText(CursorHelper.getString(cursor, VKDetailDBHelper.TEXT));
        viewHolder.tvDate.setText(CursorHelper.getString(cursor, VKDetailDBHelper.DATE));
        viewHolder.tvAuthor.setText(CursorHelper.getString(cursor, VKDetailDBHelper.AUTHOR_NAME));
        return view;
    }

    private View getCommentView(View view, Cursor cursor) {
        return getContentView(view, cursor);
    }

    private View getAttachmentVideoView(View view, Cursor cursor) {
        if (view == null) {
            view = mInflater.inflate(R.layout.item_post_image, null);
        }
        ResizableImageView imageView = (ResizableImageView) view.findViewById(R.id.image);
        setImage(imageView, CursorHelper.getString(cursor, VKDetailDBHelper.TEXT));
        imageView.setTag(R.string.vk_video_id, CursorHelper.getString(cursor, VKDetailDBHelper.COMMENT_ID));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = (String) v.getTag(R.string.vk_video_id);
                //todo show video
                Toast.makeText(mContext, videoId, Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private View getAttachmentPhotoView(View view, Cursor cursor) {
        if (view == null) {
            view = mInflater.inflate(R.layout.item_post_image, null);
        }
        ResizableImageView imageView = (ResizableImageView) view.findViewById(R.id.image);
        setImage(imageView, CursorHelper.getString(cursor, VKDetailDBHelper.TEXT));
        return view;
    }

    public void setPaginationSource(PaginationSource source) {
        mSource = source;
    }

    private View getDelimiterView(View view) {
        return view;
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

    private class PostViewHolder {
        TextView tvAuthor;
        TextView tvDate;
        TextView tvText;
        ImageView uPick;
    }
}
