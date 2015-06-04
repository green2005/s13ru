package com.parser.db;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.VKDetailItem;

import java.util.List;

public class VKDetailDBHelper {
    public static final String TABLE_NAME = "vk_details";
    public static final String ID = "_id";
    public static final String POST_ID = "post_id";
    public static final String COMMENT_ID = "comment_id";
    public static final String TEXT = "item_text";
    public static final String ITEM_TYPE = "item_type";
    public static final String AUTHOR_NAME = "author_name";
    public static final String AUTHOR_ID = "author_id";
    public static final String AUTHOR_IMAGE = "author_image";
    public static final String DATE = "date";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";


    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            ID + " integer primary key AUTOINCREMENT, " +
            POST_ID + " text, " +
            COMMENT_ID + " text, " +
            TEXT + " text, " +
            ITEM_TYPE + " integer, " +
            AUTHOR_NAME + " text, " +
            AUTHOR_ID + " text, " +
            AUTHOR_IMAGE + " text, " +
            DATE + " text, " +
            WIDTH+" integer, "+
            HEIGHT+" integer "+
            ")";

    private static final String DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.VK_DETAIL_CONTENT_URI, null, null);
    }

    public void bulkInsert(List<VKDetailItem> items, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] values = getContentValues(items);
        resolver.bulkInsert(NewsContentProvider.VK_DETAIL_CONTENT_URI, values);
        resolver.notifyChange(NewsContentProvider.VK_DETAIL_CONTENT_URI, null);
    }

    private ContentValues[] getContentValues(List<VKDetailItem> items) {
        ContentValues[] values = new ContentValues[items.size()];
        int i = 0;
        for (VKDetailItem item : items) {
            values[i] = new ContentValues();
            values[i].put(VKDetailDBHelper.POST_ID, item.getPostId());
            values[i].put(VKDetailDBHelper.COMMENT_ID, item.getCommentId());
            values[i].put(VKDetailDBHelper.TEXT, item.getText());
            values[i].put(VKDetailDBHelper.ITEM_TYPE, item.getItemType());
            values[i].put(VKDetailDBHelper.AUTHOR_NAME, item.getAuthorName());
            values[i].put(VKDetailDBHelper.AUTHOR_ID, item.getAuthorId());
            values[i].put(VKDetailDBHelper.AUTHOR_IMAGE, item.getAuthorImage());
            values[i].put(VKDetailDBHelper.DATE, item.getDate());
            values[i].put(VKDetailDBHelper.WIDTH, item.getWidth());
            values[i].put(VKDetailDBHelper.HEIGHT, item.getHeight());
            i++;
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

    public static String[] getFields() {
        String fields[] = new String[11];
        fields[0] = ID;
        fields[1] = POST_ID;
        fields[2] = COMMENT_ID;
        fields[3] = TEXT;
        fields[4] = ITEM_TYPE;
        fields[5] = AUTHOR_NAME;
        fields[6] = AUTHOR_ID;
        fields[7] = AUTHOR_IMAGE;
        fields[8] = DATE;
        fields[9] = WIDTH;
        fields[10] = HEIGHT;
        return fields;
    }
}
