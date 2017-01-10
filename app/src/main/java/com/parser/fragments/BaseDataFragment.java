package com.parser.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parser.DataSource;
import com.parser.ErrorHelper;
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
    private ListView mListView;
    private boolean mEofData = false;
    private ProgressBar mProgress;

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

    protected abstract String getSelection();

    protected abstract String[] getSelectionArgs();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentResourceId(), null);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);
        mImageLoader = ImageLoader.get(getActivity());
        loadData(0);
        initView(view);
        return view;
    }

    protected int getFragmentResourceId() {
        return R.layout.fragment_main;
    }

    public void setDataEof(boolean eof) {
        mEofData = eof;
    }

    protected ListView getListView() {
        return mListView;
    }

    private void initView(View view) {
        mListView = (ListView) view.findViewById(R.id.mainListView);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, null);
        mFooterView.setVisibility(View.GONE);
        mListView.addFooterView(mFooterView);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mState == LoadState.ERROR){
                    mState = LoadState.BROWSING;
                }
                setDataEof(false);
                loadData(0);
            }
        });
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = this;
        getLoaderManager().initLoader(0, null, loaderCallbacks);
        CursorAdapter adapter = getAdapter();
        if (adapter != null) {
            mListView.setAdapter(adapter);
            if (adapter instanceof AdapterView.OnItemClickListener) {
                mListView.setOnItemClickListener((AdapterView.OnItemClickListener) adapter);
            }
        }

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mImageLoader.resumeLoadingImages();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    mImageLoader.pauseLoadingImages();
                }
            }
        });
    }

    protected void loadData(int offset) {
        if (mState != LoadState.BROWSING) {
            return;
        }
        if (offset == 0) {
            mIsTopRequest = true;
        }
        mOffset = offset;
        String url = getUrl(offset);
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
                    mProgress.setVisibility(View.GONE);
                    mFooterView.setVisibility(View.GONE);
                    if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                        mSwipe.setRefreshing(false);
                    }
                    Context context = getActivity();
                    if (context != null) {
                        ErrorHelper.showError(context, errorMessage);
                    }
                    onLoadDone(0);
                }

                @Override
                public void onLoadDone(int recordsFetched) {
                    if (mState != LoadState.ERROR) {
                        mState = LoadState.BROWSING;
                    }
                    mFooterView.setVisibility(View.GONE);
                    if ((mSwipe != null) && (mSwipe.isRefreshing())) {
                        mSwipe.setRefreshing(false);
                    }
                    if (recordsFetched == 0) {
                        mEofData = true;
                        onEmptyDataFetched();
                    } else {
                        mEofData = false;
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
                getUri(), getFields(), getSelection(), getSelectionArgs(), null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            mProgress.setVisibility(View.GONE);
        }
        loadDone(cursor);
        getAdapter().swapCursor(cursor);
    }

    protected void loadDone(Cursor cursor){
        //
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }

    @Override
    public void loadMore() {
        if (!mEofData  &&  mState != LoadState.ERROR) {
            int nextDataOffset;
            nextDataOffset = getNextDataOffset(mOffset);
            if (nextDataOffset > 0) {
                mFooterView.setVisibility(View.VISIBLE);
                loadData(nextDataOffset);
            }
        }
    }
}
