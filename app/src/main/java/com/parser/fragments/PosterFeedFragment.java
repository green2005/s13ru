package com.parser.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.R;
import com.parser.adapters.PosterAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsFeedDBHelper;
import com.parser.db.PosterFeedDBHelper;
import com.parser.processors.PosterFeedProcessor;
import com.parser.processors.Processor;

public class PosterFeedFragment extends BaseDataFragment {
    private static final String AFISHA_URL = "http://afisha.s13.ru/";

    private PosterAdapter mAdapter;
    private PosterFeedProcessor mProcessor;

    public static PosterFeedFragment getNewFragment(Bundle args){
        PosterFeedFragment fragment = new PosterFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CursorAdapter getAdapter() {
        if (mAdapter == null){
            mAdapter = new PosterAdapter(getActivity(),  R.layout.item_news_feed, null, PosterFeedDBHelper.getDataFields(), null, 0);
        }
        return mAdapter;
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
        if (mProcessor == null){
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
}
