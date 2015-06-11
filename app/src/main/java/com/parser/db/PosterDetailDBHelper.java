package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.PosterDetailItem;

import java.util.List;

public class PosterDetailDBHelper {

    public enum POSTER_RECORD_TYPE {
        TITLE,
        DESCRIPTION,
        IMAGE_ATTACHMENT,
        VIDEO_ATTACHMENT,
        TIMEPLACE_RECORD
    }

    public static final String TABLE_NAME = "poster_detail";
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "item_text";
    public static final String DATE_COLUMN = "date_text";
    public static final String TIME_COLUMN = "time_text";
    public static final String PLACE_COLUMN = "place";
    public static final String URL_COLUMN = "url";
    public static final String CONTENT_TYPE_COLUMN = "content_type";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TEXT_COLUMN + " text, " +
                    DATE_COLUMN + " text, " +
                    TIME_COLUMN + " text, " +
                    PLACE_COLUMN + " text , " +
                    URL_COLUMN + " text, " +
                    CONTENT_TYPE_COLUMN + " Integer " +
                    ")";
    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public void bulkInsert(List<PosterDetailItem> feedItems, Context context, String url) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems, url);
        resolver.bulkInsert(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, null);
    }

    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.POSTER_DETAIL_CONTENT_URI, null, null);
    }

    private ContentValues[] getContentValues(List<PosterDetailItem> feedItems, String url) {
        ContentValues[] values = new ContentValues[feedItems.size()];
        int i = 0;
        for (PosterDetailItem item : feedItems) {
            ContentValues value = new ContentValues();
            value.put(TEXT_COLUMN, item.getItemText());
            value.put(DATE_COLUMN, item.getItemDate());
            value.put(TIME_COLUMN, item.getItemTime());
            value.put(PLACE_COLUMN, item.getPlace());
            value.put(URL_COLUMN, url);
            value.put(CONTENT_TYPE_COLUMN, item.getContentType());
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
    /*
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "item_text";
    public static final String DATE_COLUMN = "date_text";
    public static final String TIME_COLUMN = "time_text";
    public static final String PLACE_COLUMN = "place";
    public static final String URL_COLUMN = "url";
    public static final String CONTENT_TYPE_COLUMN = "content_type";
     */

    public static String[] getDataFields() {
        String[] dataFields = new String[7];
        dataFields[0] = ID_COLUMN;
        dataFields[1] = DATE_COLUMN;
        dataFields[2] = TIME_COLUMN;
        dataFields[3] = PLACE_COLUMN;
        dataFields[4] = URL_COLUMN;
        dataFields[5] = CONTENT_TYPE_COLUMN;
        dataFields[6] = TEXT_COLUMN;
        return dataFields;
    }
}
