package com.parser.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.R;
import com.parser.adapters.NewsAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsFeedDBHelper;
import com.parser.processors.NewsFeedProcessor;
import com.parser.processors.OnProcessorDoneListener;
import com.parser.processors.Processor;

public class NewsFeedFragment extends BaseDataFragment implements OnProcessorDoneListener {
    private NewsAdapter mAdapter;
    private NewsFeedProcessor mNewsProcessor;

    private static final String FEED_URL = "http://s13.ru/archives/date/%s/feed";
    private static final String RESTORE_KEY = "restore_key";

    private static final String URL_KEY = "url_key";
    private static final String FB_VERSION = "v2.6";
    private static final String FB_S13_ID = "802566859849844";
    private static final String APPLICATION_ACCESS_TOKEN ="1628147844145629|kxHzO9o8_9hpKm-jORzNomCxqpo" ;// "855127034522464|KImJOzLhkmAd1ENZKTMu9cZqvIk";
    private static final String LIMIT = "10";
    private String mNextUrl;

    //fields=message,attachments
    private static final String FB_FEED_URL = String.format(
            "https://graph.facebook.com/%s/%s/feed?access_token=%s&fields=message,attachments,created_time,link" +
                    //"picture,full_picture,message" +
                    "&limit=%s",
            FB_VERSION, FB_S13_ID, APPLICATION_ACCESS_TOKEN, LIMIT
    );


    public static NewsFeedFragment getNewFragment(Bundle params) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        fragment.setArguments(params);
        return fragment;
    }

    protected String getUrl(int offset) {
        if (offset == 0) {
            return FB_FEED_URL;
        } else {
            return mNextUrl;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle b = new Bundle();
        b.putString(URL_KEY, mNextUrl);
        outState.putBundle(RESTORE_KEY, b);
//        outState.putSerializable(URL_KEY, mNextUrl);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Bundle b = savedInstanceState.getBundle(RESTORE_KEY);
            if (b != null) {
                mNextUrl = b.getString(URL_KEY);

            }
        }
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
        mNewsProcessor.setDoneListener(this);
        return mNewsProcessor;
    }

    @Override
    protected void onEmptyDataFetched() {
        setDataEof(false);
        loadMore();
    }

    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewsAdapter(getActivity(), R.layout.item_news_feed, null, NewsFeedDBHelper.getDataFields(), null, 0);
            mAdapter.setPaginationSource(this);
        }
        return mAdapter;
    }

    protected Uri getUri() {
        return NewsContentProvider.NEWSFEED_CONTENT_URI;
    }

    protected String[] getFields() {
        return NewsFeedDBHelper.getDataFields();
    }

    @Override
    protected String getSelection() {
        return null;
    }

    @Override
    protected String[] getSelectionArgs() {
        return new String[0];
    }

    @Override
    public void onDone(String nextUrl) {
        this.mNextUrl = nextUrl;
    }
}
