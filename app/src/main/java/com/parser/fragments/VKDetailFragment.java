package com.parser.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;

import com.parser.processors.Processor;

public class VKDetailFragment extends BaseDataFragment implements DetailFragment {
    //todo move to resources
    private static final String VK_TITLE = "VK";

    public static VKDetailFragment getNewFragment(Bundle params){
        VKDetailFragment fragment = new VKDetailFragment();
        fragment.setArguments(params);
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
        return VK_TITLE;
    }


}
