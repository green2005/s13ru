package com.parser.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NewsContentProvider extends ContentProvider {

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int NEWS = 10;
    private static final int NEWS_ID = 20;
    private static final int VK_FEED = 30;
    private static final int VK_FEED_ID = 40;

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


    public static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.";
    public static final String CONTENT_ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.";

    private DBHelper mDbHelper;

    static {
        sURIMatcher.addURI(AUTHORITY, NewsFeedDBHelper.TABLE_NAME, NEWS);
        sURIMatcher.addURI(AUTHORITY, NewsFeedDBHelper.TABLE_NAME + "/#", NEWS_ID);
        sURIMatcher.addURI(AUTHORITY, VKFeedDBHelper.TABLE_NAME, VK_FEED);
        sURIMatcher.addURI(AUTHORITY, VKFeedDBHelper.TABLE_NAME + "/#", VK_FEED_ID);
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
    public int bulkInsert(Uri uri, ContentValues[] values) {
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
        }
        return tableName;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
