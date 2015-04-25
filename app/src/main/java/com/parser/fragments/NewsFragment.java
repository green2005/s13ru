package com.parser.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.DataSource;
import com.parser.R;
import com.parser.adapters.NewsAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsFeedDBHelper;
import com.parser.processors.NewsFeedProcessor;
import com.parser.processors.Processor;

public class NewsFragment extends BaseDataFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    NewsAdapter mAdapter;

    public static NewsFragment getNewFragment(Bundle params) {
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        loadData();
        initView(view);
        return view;
    }

    private void initView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.mainListView);
        LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = this;
        getLoaderManager().initLoader(0, null, loaderCallbacks);
        mAdapter = new NewsAdapter(getActivity(), R.layout.item_news_feed , null, getDataFields(), null, 0);
        listView.setAdapter(mAdapter);
    }


    private void loadData() {
        String url = "http://s13.ru/archives/date/2015/04/25/feed";//"http://s13.ru/archives/date/2015";
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Processor processor = new NewsFeedProcessor(activity.getApplicationContext());
        DataSource dataSource = new DataSource(processor, new DataSource.Callbacks() {
            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onLoadDone() {

            }
        });
        dataSource.fillData(url);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getActivity(),
                NewsContentProvider.NEWSFEED_CONTENT_URI, getDataFields(), null, null, null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getAdapter().swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
    }
    private CursorAdapter getAdapter(){
        if (mAdapter == null){
        }
        return mAdapter;
    }

    private String[] getDataFields(){
        String[] dataFields = new String[5];
        dataFields[0] = NewsFeedDBHelper.ID_COLUMN;
        dataFields[1] = NewsFeedDBHelper.TEXT_COLUMN;
        dataFields[2] = NewsFeedDBHelper.AUTHOR_COLUMN;
        dataFields[3] = NewsFeedDBHelper.LINK_COLUMN;
        dataFields[4] = NewsFeedDBHelper.TITLE_COLUMN;
        return dataFields;
    }
}
