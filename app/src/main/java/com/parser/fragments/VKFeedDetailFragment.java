package com.parser.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.API;
import com.parser.R;
import com.parser.adapters.VKNewsDetailAdapter;
import com.parser.db.NewsContentProvider;
import com.parser.db.VKDetailDBHelper;
import com.parser.processors.Processor;
import com.parser.processors.VKDetailsProcessor;

public class VKFeedDetailFragment extends BaseDataFragment implements DetailFragment {

    public static final String POST_ID_KEY = "post_id";
    private static final int COMMENT_COUNT = 10;
    private static final String VK_WALL_URL = API.VK_BASE_URL + "/method/wall.getById?posts=" + API.VK_S13_OWNER_ID + "_%s" +
            "&extended=1&v=" + API.VK_API_VERSION;
    private static final String VK_COMMENT_URL = API.VK_BASE_URL + "/method/wall.getComments?owner_id=" + API.VK_S13_OWNER_ID +
            "&post_id=%s&need_likes=1&count=" + COMMENT_COUNT + "&offset=%d&extended=1&v=" + API.VK_API_VERSION;

    private VKDetailsProcessor mProcessor;
    private VKNewsDetailAdapter mAdapter;

    private String mPost_id;

    public static VKFeedDetailFragment getNewFragment(Bundle args) {
        VKFeedDetailFragment fragment = new VKFeedDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        if (listView != null) {
            listView.setDividerHeight(0);
            listView.setDivider(null);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mPost_id = args.getString(POST_ID_KEY);
    }

    @Override
    protected CursorAdapter getAdapter() {
        if (mAdapter == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mAdapter = new VKNewsDetailAdapter(activity, R.layout.item_news_feed_image, null, getFields(), null, 0);
                mAdapter.setPaginationSource(this);
            }
        }
        return mAdapter;
    }


    @Override
    protected String getUrl(int offset) {
        String url;
        if (offset == 0) { //first we get data, then - comments
            url = String.format(VK_WALL_URL, mPost_id);
        } else {
            url = String.format(VK_COMMENT_URL, mPost_id, offset - 1);
        }
        return url;
    }

    @Override
    protected int getNextDataOffset(int offset) {
        if (offset == 0) {
            return 1;
        } else {
            return offset + COMMENT_COUNT;
        }
    }

    @Override
    protected Processor getProcessor() {
        if (mProcessor == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mProcessor = new VKDetailsProcessor(activity);
            }
        }
        return mProcessor;
    }

    @Override
    protected Uri getUri() {
        return NewsContentProvider.VK_DETAIL_CONTENT_URI;
    }

    @Override
    protected String[] getFields() {
        return VKDetailDBHelper.getFields();
    }

    @Override
    protected String getSelection() {
        return VKDetailDBHelper.POST_ID + "=?";
    }

    @Override
    protected String[] getSelectionArgs() {
        String[] ids = new String[1];
        ids[0] = mPost_id;
        return ids;
    }

    @Override
    public String getToolBarTitle(Context context) {
        if (context != null) {
            return context.getResources().getString(R.string.vk_fragment_title);
        } else
            return null;
    }

    @Override
    public String getItemTitle() {
        return null;
    }

    @Override
    public String getUrl() {
        return mPost_id;
    }
}
