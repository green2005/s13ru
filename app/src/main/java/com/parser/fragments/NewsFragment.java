package com.parser.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.CalendarUtils;
import com.parser.DataSource;
import com.parser.R;
import com.parser.adapters.NewsAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsFeedDBHelper;
import com.parser.processors.NewsFeedProcessor;
import com.parser.processors.Processor;

public class NewsFragment extends BaseDataFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private enum LoadState {
        LOADING,
        BROWSING,
        ERROR
    }

    private NewsAdapter mAdapter;
    private SwipeRefreshLayout mSwipe;
    private int mOffSet = 0;
    private LoadState mState = LoadState.BROWSING;
    private View mFooterView;
    private boolean mIsTopRequest = true;

    private static final String FEED_URL = "http://s13.ru/archives/date/%s/feed";


    public static NewsFragment getNewFragment(Bundle params) {
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        loadData(0);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.mainListView);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, null);
        mFooterView.setVisibility(View.GONE);
        listView.addFooterView(mFooterView);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = this;
        getLoaderManager().initLoader(0, null, loaderCallbacks);
        listView.setAdapter(getAdapter());
    }


    private void loadData(int offset) {
        //    private static final String FEED_URL = "http://s13.ru/archives/date/%s/feed";
        if (mState != LoadState.BROWSING) {
            return;
        }
        if (offset == 0){
            mIsTopRequest = true;
        }

        mOffSet = offset;
        String url = String.format(FEED_URL, CalendarUtils.getCurrentDate(-offset));
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Processor processor = new NewsFeedProcessor(activity.getApplicationContext());
        DataSource dataSource = new DataSource(processor, new DataSource.Callbacks() {
            @Override
            public void onError(String errorMessage) {
                mState = LoadState.ERROR;
                mFooterView.setVisibility(View.GONE);
                if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                    mSwipe.setRefreshing(false);
                }
            }

            @Override
            public void onLoadDone(int recordsFecthed) {
                mState = LoadState.BROWSING;
                mFooterView.setVisibility(View.GONE);
                if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                    mSwipe.setRefreshing(false);
                }
                if (recordsFecthed == 0) {
                    loadMore();
                } else
                {
                    mIsTopRequest = false;
                }
            }
        });
        mState = LoadState.LOADING;
        dataSource.fillData(url, mIsTopRequest);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getActivity(),
                NewsContentProvider.NEWSFEED_CONTENT_URI, NewsFeedDBHelper.getDataFields(), null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    private CursorAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new NewsAdapter(getActivity(), R.layout.item_news_feed, null, NewsFeedDBHelper.getDataFields(), null, 0);
            mAdapter.setPaginationSource(this);
        }
        return mAdapter;
    }

    @Override
    public void loadMore() {
        mFooterView.setVisibility(View.VISIBLE);
        loadData(mOffSet + 1);
    }
}
