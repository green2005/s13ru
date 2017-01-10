package com.parser.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parser.R;
import com.parser.adapters.BlackListAdapter;
import com.parser.db.BlackListDBHelper;
import com.parser.db.NewsContentProvider;

import java.util.ArrayList;

public class BlackListEditActivity extends ActionBarActivity {
    private ArrayList<String> mPersons;
    private BlackListAdapter mAdapter;
    private Cursor mCursor;
    private static final String PERSONS_KEY = "persons_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersons = new ArrayList<>();
        setContentView(R.layout.activity_black_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ContentResolver resolver = getContentResolver();
        mCursor = resolver.query(NewsContentProvider.BLACKLIST_URI, null, "", null, "");
        final TextView listEmpty = (TextView) findViewById(R.id.labelListEmpty);
        if (mCursor.getCount() > 0 ){
            listEmpty.setVisibility(View.GONE);
        } else
        {
            listEmpty.setVisibility(View.VISIBLE);
        }
        final Button delBtn = (Button) findViewById(R.id.delBtn);
        final BlackListAdapter.CheckedItemsChangedListener personListChangeListener = new BlackListAdapter.CheckedItemsChangedListener() {
            @Override
            public void itemsChanged() {
                if (mPersons.size() > 0) {
                    delBtn.setVisibility(View.VISIBLE);
                } else {
                    delBtn.setVisibility(View.GONE);
                }
            }
        };

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = getContentResolver();
                String where = BlackListDBHelper.USER_COLUMN + "=?";
                String names[] = new String[1];
                for (String name : mPersons) {
                    names[0] = name;
                    resolver.delete(NewsContentProvider.BLACKLIST_URI, where, names);
                }
                mCursor = resolver.query(NewsContentProvider.BLACKLIST_URI, null, "", null, "");

                if (mCursor.getCount() > 0 ){
                    listEmpty.setVisibility(View.GONE);
                } else
                {
                    listEmpty.setVisibility(View.VISIBLE);
                }
                mAdapter.changeCursor(mCursor);
                resolver.notifyChange(NewsContentProvider.BLACKLIST_URI, null);
                personListChangeListener.itemsChanged();
                delBtn.setVisibility(View.GONE);
            }
        });

        mAdapter = new BlackListAdapter(this, mCursor, 0, mPersons, personListChangeListener);
        ListView lvBlackList = (ListView) findViewById(R.id.lvBlackList);
        lvBlackList.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(PERSONS_KEY, mPersons);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(PERSONS_KEY)) {
            mPersons = savedInstanceState.getStringArrayList(PERSONS_KEY);
            mAdapter.setPersonsList(mPersons);
            mAdapter.notifyDataSetChanged();
        }
    }
}
