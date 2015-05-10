package com.parser.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "s13news.db";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        NewsFeedDBHelper.onCreate(db);
        VKFeedDBHelper.onCreate(db);
        PosterFeedDBHelper.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        NewsFeedDBHelper.onUpdate(db);
        VKFeedDBHelper.onUpdate(db);
        PosterFeedDBHelper.onUpdate(db);
    }
}
