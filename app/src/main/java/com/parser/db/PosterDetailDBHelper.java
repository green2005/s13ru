package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.NewsFeedItem;

import java.util.List;

public class PosterDetailDBHelper {

    public enum POSTER_RECORD_TYPE {
        DESCRIPTION,
        IMAGE_ATTACHMENT,
        VIDEO_ATTACHMENT,
        TIME_RECORD
    }

    public static final String TABLE_NAME = "poster_detail";
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "item_text";
    public static final String DATE_COLUMN = "date";
    public static final String PLACE_COLUMN = "place";
    public static final String URL_COLUMN = "url";
    public static final String CONTENT_TYPE_COLUMN = "content_type";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TEXT_COLUMN + " text, " +
                    DATE_COLUMN + " text, " +
                    PLACE_COLUMN + " text , " +
                    URL_COLUMN + " text, " +
                    CONTENT_TYPE_COLUMN + " Integer "+
                    ")";
    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public void bulkInsert(List<NewsFeedItem> feedItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems);
        resolver.bulkInsert(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, null);
    }

    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, null, null);
    }

    private ContentValues[] getContentValues(List<NewsFeedItem> feedItems) {
        ContentValues[] values = new ContentValues[feedItems.size()];
//        int i = 0;
//        for (NewsFeedItem item : feedItems) {
//            ContentValues value = new ContentValues();
//            value.put(NewsFeedDBHelper.AUTHOR_COLUMN, item.getAuthor());
//            value.put(NewsFeedDBHelper.TEXT_COLUMN, item.getText());
//            value.put(NewsFeedDBHelper.DATE_COLUMN, item.getDate());
//            value.put(NewsFeedDBHelper.LINK_COLUMN, item.getUrl());
//            value.put(NewsFeedDBHelper.TITLE_COLUMN, item.getTitle());
//            values[i++] = value;
//        }
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
        String[] dataFields = new String[6];
        dataFields[0] = ID_COLUMN;
        dataFields[1] = DATE_COLUMN;
        dataFields[2] = PLACE_COLUMN;
        dataFields[3] = URL_COLUMN;
        dataFields[4] = CONTENT_TYPE_COLUMN;
        return dataFields;
    }
}
