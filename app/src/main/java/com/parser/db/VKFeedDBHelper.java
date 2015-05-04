package com.parser.db;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

<<<<<<< HEAD
import com.parser.bo.VKFeedItem;

import java.util.List;

public class VKFeedDBHelper {
=======
import com.parser.bo.NewsFeedItem;

import java.util.List;

public class VKFeedDBHelper  {

>>>>>>> origin/master
    public static final String TABLE_NAME = "vkfeed";
    public static final String ID_COLUMN = "_id";
    public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String AUTHOR_COLUMN = "author";
<<<<<<< HEAD
    public static final String TITLE_COLUMN = "title";
    public static final String POST_ID_COLUMN = "post_id";
    public static final String IMAGE_URL_COLUMN = "image_url";
    public static final String DESCRIPTION_COLUMN = "description";
=======
    public static final String VK_ID_COLUMN = "vkid";
    public static final String LINK_COLUMN = "link";
    public static final String TITLE_COLUMN = "title";
    public static final String IMAGE_COLUMN = "image";
>>>>>>> origin/master

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID_COLUMN + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    TEXT_COLUMN + " text," +
                    DATE_COLUMN + " text," +
                    AUTHOR_COLUMN + " text, " +
<<<<<<< HEAD
                    TITLE_COLUMN + " text, " +
                    POST_ID_COLUMN + " text, " +
                    IMAGE_URL_COLUMN + " text, " +
                    DESCRIPTION_COLUMN +" text " +
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

    public void bulkInsert(List<VKFeedItem> items, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(items);
=======
                    TITLE_COLUMN +" text,"+
                    LINK_COLUMN + " text, " +
                    VK_ID_COLUMN + " text, "+
                    IMAGE_COLUMN +" text "+
                    ")";

    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public void bulkInsert(List<NewsFeedItem> feedItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(feedItems);
>>>>>>> origin/master
        resolver.bulkInsert(NewsContentProvider.VKFEED_CONTENT_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.VKFEED_CONTENT_URI, null);
    }

<<<<<<< HEAD
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
        String[] dataFields = new String[8];
        dataFields[0] =  ID_COLUMN;
        dataFields[1] =  TEXT_COLUMN;
        dataFields[2] =  AUTHOR_COLUMN;
        dataFields[3] =  DATE_COLUMN;
        dataFields[4] =  TITLE_COLUMN;
        dataFields[5] = IMAGE_URL_COLUMN;
        dataFields[6] = DESCRIPTION_COLUMN;
        dataFields[7] = POST_ID_COLUMN;
        return dataFields;
    }
=======
    public void clearOldEntries(Context context){
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.VKFEED_CONTENT_URI, null, null);
    }

    private ContentValues[] getContentValues(List<NewsFeedItem> feedItems) {
        ContentValues[] values = new ContentValues[feedItems.size()];
        int i = 0;
        for (NewsFeedItem item : feedItems) {
            ContentValues value = new ContentValues();
            /*
             public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String AUTHOR_COLUMN = "author";
    public static final String VK_ID_COLUMN = "vkid";
    public static final String LINK_COLUMN = "link";
    public static final String TITLE_COLUMN = "title";
    public static final String IMAGE_COLUMN = "image";
             */

            value.put(NewsFeedDBHelper.AUTHOR_COLUMN, item.getAuthor());
            value.put(NewsFeedDBHelper.TEXT_COLUMN , item.getText());
            value.put(NewsFeedDBHelper.DATE_COLUMN, item.getDate());
            value.put(NewsFeedDBHelper.LINK_COLUMN, item.getUrl());
            value.put(NewsFeedDBHelper.TITLE_COLUMN, item.getTitle());
            values[i++] = value;
        }
        return values;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public static String[] getDataFields(){
        String[] dataFields = new String[5];
        dataFields[0] = NewsFeedDBHelper.ID_COLUMN;
        dataFields[1] = NewsFeedDBHelper.TEXT_COLUMN;
        dataFields[2] = NewsFeedDBHelper.AUTHOR_COLUMN;
        dataFields[3] = NewsFeedDBHelper.LINK_COLUMN;
        dataFields[4] = NewsFeedDBHelper.TITLE_COLUMN;
        return dataFields;
    }

>>>>>>> origin/master
}
