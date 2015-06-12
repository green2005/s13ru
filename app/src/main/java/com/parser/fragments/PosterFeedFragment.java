package com.parser.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CursorAdapter;

import com.parser.R;
import com.parser.adapters.PosterFeedAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.PosterFeedDBHelper;
import com.parser.processors.PosterFeedProcessor;
import com.parser.processors.Processor;

public class PosterFeedFragment extends BaseDataFragment {
    private static final String AFISHA_URL = "http://afisha.s13.ru/";

    private PosterFeedAdapter mAdapter;
    private PosterFeedProcessor mProcessor;
    private String mSelection = null;
    private String mSelectionArgs[];

    public static PosterFeedFragment getNewFragment(Bundle args) {
        PosterFeedFragment fragment = new PosterFeedFragment();
        fragment.setHasOptionsMenu(true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new PosterFeedAdapter(getActivity(), R.layout.item_news_feed, null, PosterFeedDBHelper.getDataFields(), null, 0);
        }
        return mAdapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int resId = -1;

        switch (item.getItemId()) {
            case (R.id.item_cinema): {
                resId = R.string.poster_cinema;
                break;
            }
            case (R.id.item_concerts): {
                resId = R.string.poster_concerts;
                break;
            }
            case (R.id.item_events): {
                resId = R.string.poster_event;
                break;
            }
            case (R.id.item_party): {
                resId = R.string.poster_party;
                break;
            }
            case (R.id.item_theatre): {
                resId = R.string.poster_theatre;
                break;
            }
            case (R.id.item_exhibition): {
                resId = R.string.poster_exhibition;
                break;
            }
        }
        Activity activity = getActivity();
        if (resId != -1 && activity != null) {
            mSelection = PosterFeedDBHelper.CAT_COLUMN + " = ?";
            mSelectionArgs = new String[1];
            mSelectionArgs[0] = activity.getString(resId);
            getLoaderManager().restartLoader(0, null, this);
//            CursorAdapter adapter = getAdapter();
//            adapter.re
//            if (adapter  != null){
//                Cursor cursor = adapter.getCursor();
//                if (cursor!=null){
//                    cursor.r
//                }
//
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getUrl(int offset) {
        return AFISHA_URL;
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return 0;
    }

    @Override
    protected Processor getProcessor() {
        if (mProcessor == null) {
            mProcessor = new PosterFeedProcessor(getActivity());
        }
        return mProcessor;
    }

    @Override
    protected Uri getUri() {
        return NewsContentProvider.POSTERFEED_CONTENT_URI;
    }

    @Override
    protected String[] getFields() {
        return PosterFeedDBHelper.getDataFields();
    }

    @Override
    protected String getSelection() {
        return mSelection;
    }

    @Override
    protected String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posterfeed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
