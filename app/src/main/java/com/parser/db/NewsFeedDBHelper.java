package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.NewsFeedItem;

import java.util.List;

public class NewsFeedDBHelper {
    public static final String TABLE_NAME = "newsfeed";
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String AUTHOR_COLUMN = "author";
    public static final String LINK_COLUMN = "link";
    public static final String TITLE_COLUMN = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String IMAGE_WIDTH = "image_width";
    public static final String IMAGE_HEIGHT = "image_height";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TEXT_COLUMN + " text," +
                    DATE_COLUMN + " text," +
                    AUTHOR_COLUMN + " text, " +
                    TITLE_COLUMN + " text," +
                    LINK_COLUMN + " text, " +
                    IMAGE_URL + " text, " +
                    IMAGE_WIDTH + " text, " +
                    IMAGE_HEIGHT + " text " +
                    ")";


    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public void bulkInsert(List<NewsFeedItem> feedItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems);
        resolver.bulkInsert(NewsContentProvider.NEWSFEED_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.NEWSFEED_CONTENT_URI, null);
    }


    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.NEWSFEED_CONTENT_URI, null, null);
    }

    private ContentValues[] getContentValues(List<NewsFeedItem> feedItems) {
        ContentValues[] values = new ContentValues[feedItems.size()];
        int i = 0;
        for (NewsFeedItem item : feedItems) {
            ContentValues value = new ContentValues();
            value.put(NewsFeedDBHelper.AUTHOR_COLUMN, item.getAuthor());
            value.put(NewsFeedDBHelper.TEXT_COLUMN, item.getText());
            value.put(NewsFeedDBHelper.DATE_COLUMN, item.getDate());
            value.put(NewsFeedDBHelper.LINK_COLUMN, item.getUrl());
            value.put(NewsFeedDBHelper.TITLE_COLUMN, item.getTitle());
            value.put(NewsFeedDBHelper.IMAGE_URL, item.getImageUrl());
            value.put(NewsFeedDBHelper.IMAGE_WIDTH, item.getImageWidth());
            value.put(NewsFeedDBHelper.IMAGE_HEIGHT, item.getImageHeight());
            values[i++] = value;
        }
        return values;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpdate(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public static String[] getDataFields() {
        String[] dataFields = new String[9];
        dataFields[0] = NewsFeedDBHelper.ID_COLUMN;
        dataFields[1] = NewsFeedDBHelper.TEXT_COLUMN;
        dataFields[2] = NewsFeedDBHelper.AUTHOR_COLUMN;
        dataFields[3] = NewsFeedDBHelper.LINK_COLUMN;
        dataFields[4] = NewsFeedDBHelper.TITLE_COLUMN;
        dataFields[5] = NewsFeedDBHelper.DATE_COLUMN;
        dataFields[6] = NewsFeedDBHelper.IMAGE_URL;
        dataFields[7] = NewsFeedDBHelper.IMAGE_WIDTH;
        dataFields[8] = NewsFeedDBHelper.IMAGE_HEIGHT;
        return dataFields;
    }
}
