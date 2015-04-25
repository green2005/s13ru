package com.parser.db;

import android.database.Cursor;

public class CursorHelper {
    public static String getString(Cursor cursor, String columnName) {
        if (cursor == null) {
            return "";
        }
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex < 0) {
            return "";
        }
        return cursor.getString(columnIndex);
    }
}
