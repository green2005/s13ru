package com.parser.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class NewsContentProvider extends ContentProvider {

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int NEWS = 1;
    private static final int NEWS_ID = 2;
    private static final int VK_FEED = 3;
    private static final int VK_FEED_ID = 4;
    private static final int POSTER_FEED = 5;
    private static final int POSTER_FEED_ID = 6;
    private static final int NEWS_DETAIL = 7;
    private static final int NEWS_DETAIL_ID = 8;
    private static final int VK_DETAIL = 9;
    private static final int VK_DETAIL_ID = 10;
    private static final int POSTER_DETAIL = 11;
    private static final int POSTER_DETAIL_ID = 12;


    private static final String AUTHORITY = "com.parser";

    public static final String CONTENT_URI_PREFIX = "content://";

    public static final Uri NEWSFEED_CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + NewsFeedDBHelper.TABLE_NAME);

    public static Uri NEWSFEED_CONTENT_URI_ID = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + NewsFeedDBHelper.TABLE_NAME + "/#");


    public static final Uri VKFEED_CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + VKFeedDBHelper.TABLE_NAME);

    public static Uri VKFEED_CONTENT_URI_ID = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + VKFeedDBHelper.TABLE_NAME + "/#");

    public static final Uri POSTERFEED_CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + PosterFeedDBHelper.TABLE_NAME);

    public static Uri POSTERFEED_CONTENT_URI_ID = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + PosterFeedDBHelper.TABLE_NAME + "/#");

    public static final Uri NEWS_DETAIL_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + NewsDetailDBHelper.TABLE_NAME);

    public static Uri NEWS_DETAIL_CONTENT_URI_ID = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + NewsDetailDBHelper.TABLE_NAME + "/#");

    public static Uri VK_DETAIL_CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
                    + AUTHORITY + "/" + VKDetailDBHelper.TABLE_NAME
    );

    public static Uri VK_DETAIL_CONTENT_URI_ID = Uri.parse(CONTENT_URI_PREFIX
                    + AUTHORITY + "/" + VKDetailDBHelper.TABLE_NAME + "/#"
    );

    public static Uri POSTER_DETAIL_CONTENT_URI = Uri.parse( CONTENT_URI_PREFIX
                    +AUTHORITY + "/" + PosterDetailDBHelper.TABLE_NAME
    );

    public static Uri POSTER_DETAIL_CONTENT_URI_ID = Uri.parse( CONTENT_URI_PREFIX
                    +AUTHORITY + "/" + PosterDetailDBHelper.TABLE_NAME +"/#"
    );

//    public static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.";
  //  public static final String CONTENT_ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.";

    private DBHelper mDbHelper;

    static {
        sURIMatcher.addURI(AUTHORITY, NewsFeedDBHelper.TABLE_NAME, NEWS);
        sURIMatcher.addURI(AUTHORITY, NewsFeedDBHelper.TABLE_NAME + "/#", NEWS_ID);
        sURIMatcher.addURI(AUTHORITY, VKFeedDBHelper.TABLE_NAME, VK_FEED);
        sURIMatcher.addURI(AUTHORITY, VKFeedDBHelper.TABLE_NAME + "/#", VK_FEED_ID);
        sURIMatcher.addURI(AUTHORITY, PosterFeedDBHelper.TABLE_NAME, POSTER_FEED);
        sURIMatcher.addURI(AUTHORITY, PosterFeedDBHelper.TABLE_NAME + "/#", POSTER_FEED_ID);
        sURIMatcher.addURI(AUTHORITY, NewsDetailDBHelper.TABLE_NAME, NEWS_DETAIL);
        sURIMatcher.addURI(AUTHORITY, NewsDetailDBHelper.TABLE_NAME + "/#", NEWS_DETAIL_ID);
        sURIMatcher.addURI(AUTHORITY, VKDetailDBHelper.TABLE_NAME, VK_DETAIL);
        sURIMatcher.addURI(AUTHORITY, VKDetailDBHelper.TABLE_NAME + "/#", VK_DETAIL_ID);
        sURIMatcher.addURI(AUTHORITY, PosterDetailDBHelper.TABLE_NAME, POSTER_DETAIL);
        sURIMatcher.addURI(AUTHORITY, PosterDetailDBHelper.TABLE_NAME + "/#", POSTER_DETAIL_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        String tableName = getTableNameByUriType(uriType);
        Uri contentUri = getContectUribyUriType(uriType);
        Cursor cr = mDbHelper.getReadableDatabase().query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cr.setNotificationUri(getContext().getContentResolver(), contentUri);
        return cr;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        String tableName = getTableNameByUriType(uriType);
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                db.insertWithOnConflict(tableName, null, value, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        String tableName = getTableNameByUriType(uriType);
        return mDbHelper.getWritableDatabase().delete(tableName, selection, selectionArgs);
    }

    private Uri getContectUribyUriType(int uriType) {
        Uri contentUri = null;
        switch (uriType) {
            case (NEWS): {
                contentUri = NEWSFEED_CONTENT_URI;
                break;
            }
            case (NEWS_ID): {
                contentUri = NEWSFEED_CONTENT_URI_ID;
                break;
            }
            case (VK_FEED): {
                contentUri = VKFEED_CONTENT_URI;
                break;
            }
            case (VK_FEED_ID): {
                contentUri = VKFEED_CONTENT_URI_ID;
                break;
            }
            case (POSTER_FEED): {
                contentUri = POSTERFEED_CONTENT_URI;
                break;
            }
            case (POSTER_FEED_ID): {
                contentUri = POSTERFEED_CONTENT_URI_ID;
                break;
            }
            case (NEWS_DETAIL): {
                contentUri = NEWS_DETAIL_URI;
                break;
            }
            case (NEWS_DETAIL_ID): {
                contentUri = NEWS_DETAIL_CONTENT_URI_ID;
                break;
            }
            case (VK_DETAIL): {
                contentUri = VK_DETAIL_CONTENT_URI;
                break;
            }
            case (VK_DETAIL_ID): {
                contentUri = VK_DETAIL_CONTENT_URI_ID;
                break;
            }
            case (POSTER_DETAIL):{
                contentUri = POSTER_DETAIL_CONTENT_URI;
                break;
            }

            case (POSTER_DETAIL_ID):{
                contentUri = POSTER_DETAIL_CONTENT_URI_ID;
                break;
            }
        }
        return contentUri;
    }

    private String getTableNameByUriType(int uriType) {
        String tableName = null;
        switch (uriType) {
            case (NEWS): {
                tableName = NewsFeedDBHelper.TABLE_NAME;
                break;
            }
            case (NEWS_ID): {
                tableName = NewsFeedDBHelper.TABLE_NAME;
                break;
            }
            case (VK_FEED): {
                tableName = VKFeedDBHelper.TABLE_NAME;
                break;
            }
            case (VK_FEED_ID): {
                tableName = VKFeedDBHelper.TABLE_NAME;
                break;
            }
            case (POSTER_FEED): {
                tableName = PosterFeedDBHelper.TABLE_NAME;
                break;
            }
            case (POSTER_FEED_ID): {
                tableName = PosterFeedDBHelper.TABLE_NAME;
                break;
            }
            case (NEWS_DETAIL): {
                tableName = NewsDetailDBHelper.TABLE_NAME;
                break;
            }
            case (NEWS_DETAIL_ID): {
                tableName = NewsDetailDBHelper.TABLE_NAME;
                break;
            }
            case (VK_DETAIL): {
                tableName = VKDetailDBHelper.TABLE_NAME;
                break;
            }
            case (VK_DETAIL_ID): {
                tableName = VKDetailDBHelper.TABLE_NAME;
                break;
            }
            case (POSTER_DETAIL):{
                tableName = PosterDetailDBHelper.TABLE_NAME;
                break;
            }
            case (POSTER_DETAIL_ID):{
                tableName = PosterDetailDBHelper.TABLE_NAME;
                break;
            }
        }
        return tableName;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int updated = 0;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        String tableName = getTableNameByUriType(uriType);
        db.beginTransaction();
        try {
            updated = db.update(tableName, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return updated;
    }
}
