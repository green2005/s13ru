package com.parser.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.CursorAdapter;

import com.parser.API;
import com.parser.processors.Processor;

public class VKFeedFragment extends BaseDataFragment {
    private static final int RECORD_PORTION = 10;
    private static final String VK_WALL_URL = API.VK_BASE_URL + "/method/wall.get?owner_id=" + API.VK_S13_OWNER_ID + "&count=" + RECORD_PORTION + "&offset=%d";

    public static VKFeedFragment getNewFragment(Bundle args) {
        VKFeedFragment fragment = new VKFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected CursorAdapter getAdapter() {
        return null;
    }

    @Override
    protected String getUrl(int offset) {
        //https://api.vk.com/method/wall.get?owner_id=-51743326&count=1&offset=0
        return String.format(VK_WALL_URL, offset);
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return offset + RECORD_PORTION;
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
}
