package com.parser.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.R;
import com.parser.adapters.PosterDetailAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.PosterDetailDBHelper;
import com.parser.processors.PosterDetailProcessor;
import com.parser.processors.Processor;

public class PosterDetailFragment extends BaseDataFragment implements DetailFragment {
    public static final String POSTER_LINK_KEY = "link";

    //todo move to resources
    private static final String POSTER_TITLE = "Афиша";

    private String mLink;
    private PosterDetailProcessor mProcessor;
    private PosterDetailAdapter mAdapter;

    public static PosterDetailFragment getNewFragment(Bundle params) {
        PosterDetailFragment fragment = new PosterDetailFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        if (listView != null) {
            listView.setDividerHeight(0);
            listView.setDivider(null);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mLink = args.getString(POSTER_LINK_KEY);
        }
    }

    @Override
    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mAdapter = new PosterDetailAdapter(activity, R.layout.item_news_feed_image, null, getFields(), null, 0);
            }
        }
        return mAdapter;
    }

    @Override
    protected String getUrl(int offset) {
        return mLink;
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return 0;
    }

    @Override
    protected Processor getProcessor() {
        if (mProcessor == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mProcessor = new PosterDetailProcessor(activity);
            }
        }
        return mProcessor;
    }

    @Override
    protected Uri getUri() {
        return NewsContentProvider.POSTER_DETAIL_CONTENT_URI;
    }

    @Override
    protected String[] getFields() {
        return PosterDetailDBHelper.getDataFields();
    }

    @Override
    protected String getSelection() {
        return PosterDetailDBHelper.URL_COLUMN + " = ?";
    }

    @Override
    protected String[] getSelectionArgs() {
        String[] args = new String[1];
        args[0] = mLink;
        return args;
    }

    @Override
    public String getTitle() {
        return POSTER_TITLE;
    }

}
