package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.PosterFeedItem;

import java.util.List;

public class PosterFeedDBHelper {
    public static final String TABLE_NAME = "posters";
    public static final String ID_COLUMN = "_id";
    public static final String TITLE_COLUMN = "title";
    public static final String CAT_COLUMN = "cat";
    public static final String DESCRIPTION_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String LINK_COLUMN = "link";
    public static final String IMAGE_URL_COLUMN = "image_url";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TITLE_COLUMN +" text, "+
                    CAT_COLUMN+" text, "+
                    DESCRIPTION_COLUMN+" text, "+
                    DATE_COLUMN+" text, "+
                    LINK_COLUMN+" text, "+
                    IMAGE_URL_COLUMN +" text "+
                    ")";
    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpdate(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void bulkInsert(List<PosterFeedItem> feedItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems);
        resolver.bulkInsert(NewsContentProvider.POSTERFEED_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.POSTERFEED_CONTENT_URI, null);
    }

    public void clearOldEntries(Context context){
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.POSTERFEED_CONTENT_URI, null, null);
    }

    private ContentValues[] getContentValues(List<PosterFeedItem> feedItems) {
        ContentValues[] values = new ContentValues[feedItems.size()];
        int i = 0;
        for (PosterFeedItem item : feedItems) {
            ContentValues value = new ContentValues();
            value.put(TITLE_COLUMN, item.getTitle());
            value.put(CAT_COLUMN , item.getCat());
            value.put(DESCRIPTION_COLUMN, item.getDescription());
            value.put(DATE_COLUMN, item.getDate());
            value.put(LINK_COLUMN, item.getUrl());
            value.put(IMAGE_URL_COLUMN, item.getImageUrl());
            values[i++] = value;
        }
        return values;
    }

    public static String[] getDataFields(){
        String[] dataFields = new String[7];
        dataFields[0] = ID_COLUMN;
        dataFields[1] = TITLE_COLUMN;
        dataFields[2] = CAT_COLUMN;
        dataFields[3] = DESCRIPTION_COLUMN;
        dataFields[4] = DATE_COLUMN;
        dataFields[5] = LINK_COLUMN;
        dataFields[6] = IMAGE_URL_COLUMN;
        return dataFields;
    }
}
