package com.parser.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import com.parser.processors.Processor;

public class PosterDetailFragment extends BaseDataFragment implements DetailFragment{
    public static final String POSTER_LINK_KEY = "link";

    //todo move to resources
    private static final String POSTER_TITLE = "Афиша";

    private String mLink;

    public static PosterDetailFragment getNewFragment(Bundle params){
        PosterDetailFragment fragment = new PosterDetailFragment();
        fragment.setArguments(params);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null){
            mLink = args.getString(POSTER_LINK_KEY);
        }
    }

    @Override
    protected CursorAdapter getAdapter() {
        return null;
    }

    @Override
    protected String getUrl(int offset) {
        return null;
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
        return null;
    }

    @Override
    protected String[] getFields() {
        return new String[0];
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
    public String getTitle() {
        return POSTER_TITLE;
    }

}
