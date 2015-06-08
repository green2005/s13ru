package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.db.CursorHelper;
import com.parser.db.PosterDetailDBHelper;

public class PosterDetailAdapter extends SimpleCursorAdapter{
    private Context mContext;
    private LayoutInflater mInflater;


    public PosterDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return PosterDetailDBHelper.POSTER_RECORD_TYPE.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor!=null){
            cursor.moveToPosition(position);
            return CursorHelper.getInt(cursor, PosterDetailDBHelper.CONTENT_TYPE_COLUMN);

        } else
        {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor == null){
            return convertView;
        }
        cursor.moveToPosition(position);
        int viewType = getItemViewType(position);
        if (viewType == PosterDetailDBHelper.POSTER_RECORD_TYPE.TITLE.ordinal()){
            convertView = getTitleView(convertView, cursor);
        }
  


        return super.getView(position, convertView, parent);
    }

    private View getTitleView(View convertView, Cursor cursor){
        convertView = mInflater.inflate(R.layout.item_poster_title, null);
        TextView tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(CursorHelper.getString(cursor, PosterDetailDBHelper.TEXT_COLUMN));
        return convertView;
    }



}
