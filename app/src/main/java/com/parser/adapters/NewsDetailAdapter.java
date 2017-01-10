package com.parser.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.parser.API;
import com.parser.LinkifiedTextView;
import com.parser.R;
import com.parser.ResizableImageView;
import com.parser.db.CursorHelper;
import com.parser.db.DBHelper;
import com.parser.db.NewsDetailDBHelper;
import com.parser.fragments.OnCommentItemClickListener;
import com.parser.loader.ImageLoader;

import java.util.HashMap;
import java.util.Map;

public class NewsDetailAdapter extends SimpleCursorAdapter {
    private static final int VIEW_TYPE_COUNT = 6;


    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
     private ThumbnailListener thumbnailListener;
    private Context mContext;
    private boolean mLoadImages;
    private OnCommentItemClickListener mCommentClickListener;


    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap = new HashMap<>();


    public NewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.get(context);
        mContext = context;
        thumbnailListener = new ThumbnailListener(context);
        SharedPreferences preferences;
       // preferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.load_images_key),Context.MODE_PRIVATE);
       // mLoadImages = preferences.getBoolean(mContext.getResources().getString(R.string.load_images_key), true);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mLoadImages = preferences.getBoolean(mContext.getResources().getString(R.string.load_images_key), true);
    }

    public void setOnCommentClickListener(OnCommentItemClickListener listener){
        mCommentClickListener = listener;
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
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
        return false;
        /*Cursor cursor = getCursor();
        if (cursor == null) {
            return super.isEnabled(position);
        }
        cursor.moveToPosition(position);
        int viewType = getItemViewType(position);
        return viewType == NewsDetailDBHelper.NewsItemType.REPLY.ordinal();
   */
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

        return cnView;
    }

    private View getTextView(Cursor cursor, View cnView, int convertedViewType, int viewType) {
        if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_text, null);
        LinkifiedTextView tvText = (LinkifiedTextView) cnView.findViewById(R.id.tvText);
        tvText.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
        return cnView;
    }

    private View getTitleView(Cursor cursor, View cnView, int convertedViewType, int viewType) {
        if (convertedViewType != viewType || cnView == null) {
            cnView = mInflater.inflate(R.layout.item_post_title, null);
        }
        TextView tvTitle = (TextView) cnView.findViewById(R.id.tvTitle);
        TextView tvDate = (TextView) cnView.findViewById(R.id.tvDate);
        tvDate.setText(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.DATE_COLUMN)));
        tvTitle.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsDetailDBHelper.TEXT_COLUMN))));
        return cnView;
    }

    private View getImageView(Cursor cursor, View cnView, int convertedViewType, int viewType) {
        if (!mLoadImages){
            View cn = new View(mContext);
            cn.setVisibility(View.GONE);
            return cn;
        }
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


    private View getCommentView(Cursor cursor,View cnView, int convertedViewType, int viewType) {
        if (convertedViewType != viewType || cnView == null)
            cnView = mInflater.inflate(R.layout.item_post_comment, null);
        TextView tvUser = (TextView) cnView.findViewById(R.id.tvUserName);
        LinkifiedTextView tvComment = (LinkifiedTextView) cnView.findViewById(R.id.tvComment);
        TextView tvDate = (TextView) cnView.findViewById(R.id.tvDate);
        TextView tvKarmaUp = (TextView) cnView.findViewById(R.id.tvUps);
        TextView tvKarmaDown = (TextView) cnView.findViewById(R.id.tvDowns);
        final View cView = cnView;

        RelativeLayout lvMarks = (RelativeLayout) cnView.findViewById(R.id.lvMarks);
        final int itemPos = getCursor().getPosition();
        lvMarks.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommentClickListener!=null){
                    mCommentClickListener.onMoreBtnClick(itemPos, cView);
                }
            }
        }));
//        final ImageButton btn = (ImageButton) cnView.findViewById(R.id.morebtn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if (mCommentClickListener!=null){
//                mCommentClickListener.onMoreBtnClick(getCursor().getPosition(), cView);
//            }
//            }
//        });

        tvKarmaUp.setText(DBHelper.getStringFromCursor(NewsDetailDBHelper.KARMA_UP_COLUMN, cursor));
        tvKarmaDown.setText(DBHelper.getStringFromCursor(NewsDetailDBHelper.KARMA_DOWN_COLUMN, cursor));

        tvDate.setText(DBHelper.getStringFromCursor(NewsDetailDBHelper.DATE_COLUMN, cursor));
        ImageView imvUserImage = (ImageView) cnView.findViewById(R.id.userPick);
        String userImageUrl = DBHelper.getStringFromCursor(NewsDetailDBHelper.AUTHOR_IMAGE_COLUMN, cursor);
        if (!TextUtils.isEmpty(userImageUrl)) {
            mImageLoader.loadImage(imvUserImage, userImageUrl);
            imvUserImage.setVisibility(View.VISIBLE);
        } else {
            imvUserImage.setVisibility(View.GONE);
        }
        tvUser.setText(DBHelper.getStringFromCursor(NewsDetailDBHelper.AUTHOR_COLUMN, cursor));
        tvComment.setText(Html.fromHtml(DBHelper.getStringFromCursor(NewsDetailDBHelper.TEXT_COLUMN, cursor)));
        return cnView;
    }

    private View getVideoView(Cursor cursor, View cnView, int convertedViewType, int viewType) {
        if (!mLoadImages){
            View cn = new View(mContext);
            cn.setVisibility(View.GONE);
            return cn;
        }
        String youtubeId = CursorHelper.getString(cursor, NewsDetailDBHelper.TEXT_COLUMN);
        if (convertedViewType != viewType || cnView == null) {
            cnView = mInflater.inflate(R.layout.item_post_video, null);
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) cnView.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String youTubeId = (String) v.getTag();
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
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            //Toast.makeText(mContext, "initFail", Toast.LENGTH_SHORT).show();
           // loader.getErrorDialog(this.mContext, 0);
            view.setVisibility(View.GONE);
            //initialize_failed = true;
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

