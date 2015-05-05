package com.parser.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.DataSource;
import com.parser.R;
import com.parser.loader.ImageLoader;
import com.parser.processors.Processor;

public abstract class BaseDataFragment extends Fragment implements PaginationSource, LoaderManager.LoaderCallbacks<Cursor> {

    private LoadState mState = LoadState.BROWSING;
    private SwipeRefreshLayout mSwipe;
    private View mFooterView;
    private boolean mIsTopRequest = true;
    private int mOffset = 0;
    private DataSource mDataSource;
    private ImageLoader mImageLoader;

    private enum LoadState {
        LOADING,
        BROWSING,
        ERROR
    }


    protected abstract CursorAdapter getAdapter();

    protected abstract String getUrl(int offset);

    protected abstract int getNextDataOffset(int offset);

    protected abstract Processor getProcessor();

    protected abstract Uri getUri();

    protected abstract String[] getFields();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        mImageLoader = ImageLoader.get(getActivity());
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mImageLoader.resumeLoadingImages();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    mImageLoader.pauseLoadingImages(); //stopLoadingImages();
                }
            }
        });
    }

    protected void loadData(int offset) {
        //    private static final String FEED_URL = "http://s13.ru/archives/date/%s/feed";
        if (mState != LoadState.BROWSING) {
            return;
        }
        if (offset == 0) {
            mIsTopRequest = true;
        }
        mOffset = offset;
        String url = getUrl(offset);//String.format(FEED_URL, CalendarUtils.getCurrentDate(-offset));
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        DataSource dataSource = getDataSource();
        if (dataSource != null) {
            mState = LoadState.LOADING;
            dataSource.fillData(url, mIsTopRequest);
        }
    }

    private DataSource getDataSource() {
        Processor processor = getProcessor();
        if (processor == null) {
            return null;
        }
        if (mDataSource == null) {
            mDataSource = new DataSource(processor, new DataSource.Callbacks() {
                @Override
                public void onError(String errorMessage) {
                    mState = LoadState.ERROR;
                    mFooterView.setVisibility(View.GONE);
                    if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                        mSwipe.setRefreshing(false);
                    }
                }

                @Override
                public void onLoadDone(int recordsFetched) {
                    mState = LoadState.BROWSING;
                    mFooterView.setVisibility(View.GONE);
                    if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                        mSwipe.setRefreshing(false);
                    }
                    if (recordsFetched == 0) {
                        onEmptyDataFetched();
                    } else {
                        mIsTopRequest = false;
                    }
                }
            });
        }
        return mDataSource;
    }

    protected void onEmptyDataFetched() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getActivity(),
                getUri(), getFields(), null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getAdapter().swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    @Override
    public void loadMore() {
        int nextDataOffset = getNextDataOffset(mOffset);
        if (nextDataOffset > 0) {
            mFooterView.setVisibility(View.VISIBLE);
            loadData(nextDataOffset);
        }
    }

}
