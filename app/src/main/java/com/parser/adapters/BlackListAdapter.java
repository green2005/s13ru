package com.parser.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.parser.R;
import com.parser.db.BlackListDBHelper;
import com.parser.db.CursorHelper;

import java.util.List;

public class BlackListAdapter extends CursorAdapter {
    public interface CheckedItemsChangedListener {
        void itemsChanged();
    }

    private LayoutInflater mInflater;
    private List<String> mPersons;
    private CheckedItemsChangedListener mItemsListChangedListener;

    public BlackListAdapter(Context context, Cursor c, int flags, List<String> persons, CheckedItemsChangedListener listener) {
        super(context, c, flags);
        mPersons = persons;
        mItemsListChangedListener = listener;
    }

    public void setPersonsList(List<String> persons) {
        mPersons = persons;
        if (mItemsListChangedListener != null) {
            mItemsListChangedListener.itemsChanged();
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = null;
        if (cursor.isClosed()) {
            return null;
        }
        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        v = mInflater.inflate(R.layout.item_black_list, null);
        final String userName = CursorHelper.getString(cursor, BlackListDBHelper.USER_COLUMN);
        TextView tvUser = (TextView) v.findViewById(R.id.tvPerson);
        CheckBox ch = (CheckBox) v.findViewById(R.id.chDel);
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPersons.add(userName);
                } else {
                    if (mPersons.contains(userName)) {
                        mPersons.remove(userName);
                    }
                }
                if (mItemsListChangedListener != null) {
                    mItemsListChangedListener.itemsChanged();
                }
            }
        });
        ch.setChecked(mPersons.contains(userName));
        tvUser.setText(userName);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String userName = CursorHelper.getString(cursor, BlackListDBHelper.USER_COLUMN);
        TextView tvUser = (TextView) view.findViewById(R.id.tvPerson);
        CheckBox ch = (CheckBox) view.findViewById(R.id.chDel);
        ch.setChecked(mPersons.contains(userName));
        tvUser.setText(userName);
    }
}
