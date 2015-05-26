package com.parser.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.processors.Processor;

public class VKFeedDetailFragment extends BaseDataFragment implements DetailFragment {
    


    public static VKFeedDetailFragment getNewFragment(Bundle args){
        VKFeedDetailFragment fragment = new VKFeedDetailFragment();
        fragment.setArguments(args);
        return fragment;
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
        return null;
    }
}
