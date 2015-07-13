package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.parser.API;
import com.parser.LinkifiedTextView;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.db.CursorHelper;
import com.parser.db.NewsDetailDBHelper;
import com.parser.loader.ImageLoader;

import java.util.HashMap;
import java.util.Map;

public class NewsDetailAdapter extends SimpleCursorAdapter {
    private static final int VIEW_TYPE_COUNT = 6;

    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private AdapterPositionListener mListener;
    private ThumbnailListener thumbnailListener;
    private Context mContext;


    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap = new HashMap<>();


    public NewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
        mContext = context;
        thumbnailListener = new ThumbnailListener(context);

    }


    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }

    public void setPositionChangeListener(AdapterPositionListener listener) {
        mListener = listener;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            return CursorHelper.getInt(cursor, NewsDetailDBHelper.RECORD_TYPE_COLUMN);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return super.isEnabled(position);
        }
        cursor.moveToPosition(position);
        int viewType = getItemViewType(position);
        return viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int convertedViewType = -1;

        if (cnView != null) {
            convertedViewType = (Integer) cnView.getTag(R.string.DETAIL_VIEW_TYPE);
            Integer bottomLeft = (Integer) cnView.getTag(R.string.BOTTOM_POSITION);
            if (bottomLeft != null && bottomLeft == 1 && mListener != null) {
                mListener.onBottomEscaped();
            }        }

        int viewType = getItemViewType(position);
        if (viewType == NewsDetailDBHelper.NewsItemType.TITLE.ordinal()) {
            cnView = getTitleView(cursor, cnView, convertedViewType, viewType);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.TEXT.ordinal()) {
            cnView = getTextView(cursor, cnView, convertedViewType, viewType);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.IMAGE.ordinal()) {
            cnView = getImageView(cursor, cnView, convertedViewType, viewType);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.VIDEO.ordinal()) {
            cnView = getVideoView(cursor, cnView, convertedViewType, viewType);
        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal()) {
            if (convertedViewType != viewType || cnView == null) {
                cnView = mInflater.inflate(R.layout.item_comment_header, null);
            }
        } else if (viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal()) {
            cnView = getCommentView(cursor, cnView, convertedViewType, viewType);
        }
        cnView.setTag(R.string.DETAIL_VIEW_TYPE, viewType);
        if (position == cursor.getCount() - 1) {
            if (mListener != null) {
                mListener.onBottomReached();
                cnView.setTag(R.string.BOTTOM_POSITION, 1);
            }
        }
        return cnView;
    }

    private View getTextView(Cursor cursor, View cnView, int convertedViewType, int viewType){
        if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_text, null);
        LinkifiedTextView tvText = (LinkifiedTextView) cnView.findViewById(R.id.tvText);
        tvText.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
        return cnView;
    }

    private View getTitleView(Cursor cursor, View cnView, int convertedViewType, int viewType)
    {
        if (convertedViewType != viewType || cnView == null) {
            cnView = mInflater.inflate(R.layout.item_post_title, null);
        }
        TextView tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
        TextView tvDate = (TextView) cnView.findViewById(R.id.tvDate);
        tvDate.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.DATE_COLUMN)));
        tvTitle.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
        return cnView;
    }

    private View getImageView(Cursor cursor, View cnView, int convertedViewType, int viewType){
        if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_image, null);
        ResizableImageView imageView = (ResizableImageView) cnView.findViewById(R.id.image);
        String url = cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN));
        int imageWidth = cursor.getInt(cursor.getColumnIndex(NewsDetailDBHelper.IMAGE_WIDTH_COLUMN));
        int imageHeight = cursor.getInt(cursor.getColumnIndex(NewsDetailDBHelper.IMAGE_HEIGTH_COLUMN));
        if (imageWidth > 0) {
            imageView.setOriginalImageSize(imageWidth, imageHeight);
        }
        mImageLoader.loadImage(imageView, url);
        return cnView;
    }



    private View getCommentView(Cursor cursor, View cnView, int convertedViewType, int viewType){
        if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_comment, null);
        TextView tvUser = (TextView) cnView.findViewById(R.id.tvUserName);
        LinkifiedTextView tvComment = (LinkifiedTextView) cnView.findViewById(R.id.tvComment);
        TextView tvDate = (TextView) cnView.findViewById(R.id.tvDate);
        TextView tvKarmaUp = (TextView) cnView.findViewById(R.id.tvUps);
        TextView tvKarmaDown = (TextView) cnView.findViewById(R.id.tvDowns);

        tvKarmaUp.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.KARMA_UP_COLUMN)));
        tvKarmaDown.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.KARMA_DOWN_COLUMN)));

        tvDate.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.DATE_COLUMN)));
        ImageView imvUserImage = (ImageView) cnView.findViewById(R.id.userPick);
        String userImageUrl = cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.AUTHOR_IMAGE_COLUMN));
        if (!TextUtils.isEmpty(userImageUrl)) {
            mImageLoader.loadImage(imvUserImage, userImageUrl);
            imvUserImage.setVisibility(View.VISIBLE);
        } else {
            imvUserImage.setVisibility(View.GONE);
        }
        tvUser.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.AUTHOR_COLUMN)));
        tvComment.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
        return cnView;
    }

    private View getVideoView(Cursor cursor, View cnView, int convertedViewType, int viewType){
        String youtubeId = CursorHelper.getString(cursor, NewsDetailDBHelper.TEXT_COLUMN);
        if (convertedViewType != viewType || cnView == null) {
            cnView = mInflater.inflate(R.layout.item_post_video, null);
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) cnView.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String youTubeId =(String) v.getTag();
                    Intent intent = YouTubeIntents.createPlayVideoIntentWithOptions(mContext, youTubeId, true, false);
                    mContext.startActivity(intent);
                }
            });

            thumbnail.setTag(youtubeId);
            thumbnail.initialize(API.YOUTUBE_KEY, thumbnailListener);
        } else {
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) cnView.findViewById(R.id.thumbnail);
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                thumbnail.setTag(youtubeId); //"UiLSiqyDf4Y"
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                //thumbnail.setImageResource(R.drawable.loading_thumbnail);
                loader.setVideo(youtubeId);
            }
        }
        return cnView;
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {
        private Context mContext;

        ThumbnailListener(Context context) {
            mContext = context;
        }

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            // view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {


            Toast.makeText(mContext, "initFail", Toast.LENGTH_SHORT).show();
            //   view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
            //Toast.makeText(mContext, "loaded",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            //   view.setImageResource(R.drawable.no_thumbnail);
            //Toast.makeText(mContext, errorReason.toString(),Toast.LENGTH_SHORT).show();

        }
    }

}

