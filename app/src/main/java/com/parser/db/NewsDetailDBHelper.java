package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.NewsDetailItem;

import java.util.List;

public class NewsDetailDBHelper {

    public enum NewsItemType {
        TITLE,
        TEXT,
        IMAGE,
        REPLY_HEADER,
        REPLY,
        VIDEO
    }

    public static final String TABLE_NAME = "newsdetail";
    public static final String ID_COLUMN = "_id";
    public static final String RECORD_TYPE_COLUMN = "record_type";
    public static final String AUTHOR_COLUMN = "author";
    public static final String DATE_COLUMN = "date";
    public static final String TEXT_COLUMN = "text";
    public static final String IMAGE_WIDTH_COLUMN = "image_width";
    public static final String IMAGE_HEIGTH_COLUMN = "image_height";
    public static final String AUTHOR_IMAGE_COLUMN = "author_image";
    public static final String KARMA_UP_COLUMN = "karma_up";
    public static final String KARMA_DOWN_COLUMN = "karma_down";
    public static final String COMMENT_ID_COLUMN = "comment_id";
    public static final String POST_ID = "post_id";
    public static final String AKISMET = "akismet";
    public static final String AK_JS = "ak_js";
    public static final String CAN_CHANGE_KARMA = "can_change_karma";

    private static final String CREATE_TABLE = " create table " + TABLE_NAME + " ( " +
            ID_COLUMN + " integer primary key AUTOINCREMENT , " +
            RECORD_TYPE_COLUMN + " integer, " +
            AUTHOR_COLUMN + " text, " +
            DATE_COLUMN + " text, " +
            IMAGE_WIDTH_COLUMN + " integer , " +
            IMAGE_HEIGTH_COLUMN + " integer, " +
            AUTHOR_IMAGE_COLUMN + " text, " +
            KARMA_DOWN_COLUMN + " integer, " +
            KARMA_UP_COLUMN + " integer, " +
            COMMENT_ID_COLUMN + " text, " +
            TEXT_COLUMN + " text, " +
            POST_ID + " text, " +
            AKISMET + " text, " +
            AK_JS + " text ," +
            CAN_CHANGE_KARMA + " integer " +
            " ) ";

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

    public void bulkInsert(List<NewsDetailItem> detailItems, Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues[] contentValues = getContentValues(detailItems);
        resolver.bulkInsert(NewsContentProvider.NEWS_DETAIL_URI, contentValues);
        resolver.notifyChange(NewsContentProvider.NEWS_DETAIL_URI, null);
    }

    public void clearOldEntries(Context context) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(NewsContentProvider.NEWS_DETAIL_URI, null, null);
    }

    private ContentValues[] getContentValues(List<NewsDetailItem> items) {
        ContentValues[] values = new ContentValues[items.size()];
        int i = 0;
        for (NewsDetailItem item : items) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(AUTHOR_COLUMN, item.getAuthor());
            contentValues.put(DATE_COLUMN, item.getDate());
            contentValues.put(RECORD_TYPE_COLUMN, item.getContentType());
            contentValues.put(IMAGE_WIDTH_COLUMN, item.getWidth());
            contentValues.put(IMAGE_HEIGTH_COLUMN, item.getHeight());
            contentValues.put(AUTHOR_IMAGE_COLUMN, item.getAuthorImage());
            contentValues.put(COMMENT_ID_COLUMN, item.getCommentId());
            contentValues.put(KARMA_DOWN_COLUMN, item.getKarmaDown());
            contentValues.put(KARMA_UP_COLUMN, item.getmKarma_up());
            contentValues.put(TEXT_COLUMN, item.getText());
            contentValues.put(POST_ID, item.getPostUrl());
            contentValues.put(AKISMET, item.getAkismet());
            contentValues.put(AK_JS, item.getAk_js());
            contentValues.put(CAN_CHANGE_KARMA, item.getCanChangeKarma());
            values[i++] = contentValues;
        }
        return values;
    }

    public static String[] getFields() {
        String fields[] = new String[14];
        fields[0] = ID_COLUMN;
        fields[1] = RECORD_TYPE_COLUMN;
        fields[2] = AUTHOR_COLUMN;
        fields[3] = DATE_COLUMN;
        fields[4] = TEXT_COLUMN;
        fields[5] = IMAGE_WIDTH_COLUMN;
        fields[6] = IMAGE_HEIGTH_COLUMN;
        fields[7] = AUTHOR_IMAGE_COLUMN;
        fields[8] = KARMA_UP_COLUMN;
        fields[9] = KARMA_DOWN_COLUMN;
        fields[10] = COMMENT_ID_COLUMN;
        fields[11] = POST_ID;
        fields[12] = AKISMET;
        fields[13] = CAN_CHANGE_KARMA;
        return fields;
    }

}
