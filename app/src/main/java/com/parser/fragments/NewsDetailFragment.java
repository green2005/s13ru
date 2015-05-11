package com.parser.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.R;
import com.parser.adapters.NewsDetailAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsDetailDBHelper;
import com.parser.processors.Processor;

public class NewsDetailFragment extends BaseDataFragment implements DetailFragment{
    //todo move to resources
    private static final String NEWS_TITLE  = "Новости";
    public static final String URL_PARAM = "url_param";
    private String mUrl;
    private NewsDetailAdapter mAdapter;


    public static NewsDetailFragment getNewFragment(Bundle params){
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(params);
        return fragment;
    }

    public void setArguments(Bundle arguments){
        super.setArguments(arguments);
        if (arguments != null){
            mUrl = arguments.getString(URL_PARAM);
        }
    }

    @Override
    protected CursorAdapter getAdapter() {
        Activity activity = getActivity();
        if (activity != null) {
            mAdapter = new NewsDetailAdapter(activity, R.layout.item_news_feed_image, null, NewsDetailDBHelper.getFields(), null, 0);
        }
        return mAdapter;
    }

    @Override
    protected String getUrl(int offset) {
        return mUrl;
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return 0;
    }

    @Override
    protected Processor getProcessor() {
        return null;
    }

    @Override
    protected Uri getUri() {
        return NewsContentProvider.NEWS_DETAIL_URI;
    }

    @Override
    protected String[] getFields() {
        return NewsDetailDBHelper.getFields();
    }

    @Override
    public String getTitle() {
        return NEWS_TITLE;
    }
}
