package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class PosterDetailAdapter extends SimpleCursorAdapter{

    public PosterDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

}
