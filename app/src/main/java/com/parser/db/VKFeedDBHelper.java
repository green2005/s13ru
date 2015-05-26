package com.parser.db;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.VKFeedItem;

import java.util.List;

public class VKFeedDBHelper {


    public static final String TABLE_NAME = "vkfeed";
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String AUTHOR_COLUMN = "author";

    public static final String POST_ID_COLUMN = "post_id";
    public static final String IMAGE_URL_COLUMN = "image_url";
    public static final String IMAGE_WIDTH = "image_width";
    public static final String IMAGE_HEIGHT = "image_height";

    public static final String DESCRIPTION_COLUMN = "description";


    public static final String TITLE_COLUMN = "title";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TEXT_COLUMN + " text," +
                    DATE_COLUMN + " text," +
                    AUTHOR_COLUMN + " text, " +

                    TITLE_COLUMN + " text, " +
                    POST_ID_COLUMN + " text, " +
                    IMAGE_URL_COLUMN + " text, " +
                    DESCRIPTION_COLUMN +" text, " +
                    IMAGE_WIDTH + " Integer, " +
                    IMAGE_HEIGHT +" Integer "+
                    ")";
    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase db) {
        if (db != null) {
            db.execSQL(CREATE_TABLE);
        }
    }

    public static void onUpdate(SQLiteDatabase db) {
        if (db != null) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }


    public void bulkInsert(List<VKFeedItem> feedItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems);
        resolver.bulkInsert(NewsContentProvider.VKFEED_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.VKFEED_CONTENT_URI, null);
    }

    private ContentValues[] getContentValues(List<VKFeedItem> items) {
        if (items == null) {
            return null;
        }
        ContentValues[] contentValues = new ContentValues[items.size()];
        int i = 0;
        for (VKFeedItem item : items) {
            ContentValues value = new ContentValues();
            value.put(TEXT_COLUMN, item.getText());
            value.put(TITLE_COLUMN, item.getTitle());
            value.put(IMAGE_URL_COLUMN, item.getImageUrl());
            value.put(IMAGE_WIDTH, item.getImageWidth());
            value.put(IMAGE_HEIGHT, item.getImageHeight());

            value.put(POST_ID_COLUMN, item.getPostId());
            value.put(DATE_COLUMN, item.getDate());
            value.put(DESCRIPTION_COLUMN, item.getDescription());
            contentValues[i++] = value;
        }
        return contentValues;
    }


    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.VKFEED_CONTENT_URI, null, null);
    }

    public static String[] getDataFields(){
        String[] dataFields = new String[10];
        dataFields[0] =  ID_COLUMN;
        dataFields[1] =  TEXT_COLUMN;
        dataFields[2] =  AUTHOR_COLUMN;
        dataFields[3] =  DATE_COLUMN;
        dataFields[4] =  TITLE_COLUMN;
        dataFields[5] = IMAGE_URL_COLUMN;
        dataFields[6] = DESCRIPTION_COLUMN;
        dataFields[7] = POST_ID_COLUMN;
        dataFields[8] = IMAGE_WIDTH;
        dataFields[9] = IMAGE_HEIGHT;
        return dataFields;
    }

}
