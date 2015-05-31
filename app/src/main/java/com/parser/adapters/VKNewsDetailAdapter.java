package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.parser.bo.VKDetailItem;
import com.parser.db.CursorHelper;
import com.parser.db.VKDetailDBHelper;

public class VKNewsDetailAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;
    public VKNewsDetailAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return VKDetailItem.ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            return CursorHelper.getInt(cursor, VKDetailDBHelper.ITEM_TYPE);
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            int itemType = getItemViewType(position);
            if (itemType == VKDetailItem.ItemType.CONTENT.ordinal()) {
                convertView = getContentView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.COMMENT.ordinal()) {
                convertView = getCommentView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.ATTACHMENT.ordinal()) {
                convertView = getAttachmentView(convertView, cursor);
            } else if (itemType == VKDetailItem.ItemType.DELIMITER.ordinal()) {
                convertView = getDelimiterView(convertView);
            }
        }
        return super.getView(position, convertView, parent);
    }

    private View getContentView(View view, Cursor cursor) {
        if (view == null){
            view =

        }


    }

    private View getCommentView(View view, Cursor cursor) {

    }

    private View getAttachmentView(View view, Cursor cursor) {

    }

    private View getDelimiterView(View view) {

    }
}
