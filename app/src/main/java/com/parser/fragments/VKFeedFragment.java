package com.parser.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CursorAdapter;

import com.parser.API;
import com.parser.R;
import com.parser.adapters.VKNewsFeedAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.VKFeedDBHelper;
import com.parser.processors.Processor;
import com.parser.processors.VKFeedProcessor;

public class VKFeedFragment extends BaseDataFragment {

    private VKFeedProcessor mProcessor;
    private VKNewsFeedAdapter mAdapter;

    private static final int RECORD_PORTION_SIZE = 10;
    private static final String VK_WALL_URL = API.VK_BASE_URL + "/method/wall.get?owner_id=" + API.VK_S13_OWNER_ID +
            "&count=" + RECORD_PORTION_SIZE + "&offset=%d";

    public static VKFeedFragment getNewFragment(Bundle args) {
        VKFeedFragment fragment = new VKFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new VKNewsFeedAdapter(getActivity(), R.layout.item_news_feed_image, null, VKFeedDBHelper.getDataFields(), null, 0);
            mAdapter.setPaginationSource(this);
        }
        return mAdapter;
    }

    @Override
    protected String getUrl(int offset) {
        //https://api.vk.com/method/wall.get?owner_id=-51743326&count=1&offset=0
        return String.format(VK_WALL_URL, offset);
    }

    @Override
    protected int getNextDataOffset(int offset) {
        return offset + RECORD_PORTION_SIZE;
    }

    @Override
    protected Processor getProcessor() {
        if (mProcessor == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mProcessor = new VKFeedProcessor(activity);
            }
        }
        return mProcessor;
    }

    @Override
    protected Uri getUri() {
        return NewsContentProvider.VKFEED_CONTENT_URI;
    }

    @Override
    protected String[] getFields() {
        return VKFeedDBHelper.getDataFields();
    }
}
