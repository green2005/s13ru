package com.parser.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parser.bo.NewsDetailItem;

import java.util.List;

public class BlackListDBHelper {
    public static final String TABLE_NAME = "blackList";
    public static final String ID_COLUMN = "_id";
    public static final String USER_COLUMN = "author";

    private static final String CREATE_TABLE = " create table " + TABLE_NAME + " ( " +
            ID_COLUMN + " integer primary key AUTOINCREMENT , " +
            USER_COLUMN + " text )";

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

    public static String[] getFields() {
        String fields[] = new String[2];
        fields[0] = ID_COLUMN;
        fields[1] = USER_COLUMN;
        return fields;
    }


}
