package com.parser.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.CalendarUtils;
import com.parser.R;
import com.parser.adapters.NewsAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsFeedDBHelper;
import com.parser.processors.NewsFeedProcessor;
import com.parser.processors.Processor;

public class NewsFeedFragment extends BaseDataFragment {
    private NewsAdapter mAdapter;
    private Processor mNewsProcessor;

    private static final String FEED_URL = "http://s13.ru/archives/date/%s/feed";


    public static NewsFeedFragment getNewFragment(Bundle params) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        fragment.setArguments(params);
        return fragment;
    }

    protected String getUrl(int offset) {
        return String.format(FEED_URL, CalendarUtils.getCurrentDate(-offset));
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return offset + 1;
    }

    @Override
    protected Processor getProcessor() {
        if (mNewsProcessor == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mNewsProcessor = new NewsFeedProcessor(activity.getApplicationContext());
            }
        }
        return mNewsProcessor;
    }

    @Override
    protected void onEmptyDataFetched() {
        loadMore();
    }

    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewsAdapter(getActivity(), R.layout.item_news_feed, null, NewsFeedDBHelper.getDataFields(), null, 0);
            mAdapter.setPaginationSource(this);
        }
        return mAdapter;
    }

    protected Uri getUri(){
        return NewsContentProvider.NEWSFEED_CONTENT_URI;
    }

    protected String[] getFields(){
        return NewsFeedDBHelper.getDataFields();
    }


}
