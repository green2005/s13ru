package com.parser.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.parser.R;
import com.parser.adapters.NewsDetailAdapter;
import com.parser.db.CursorHelper;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsDetailDBHelper;
import com.parser.processors.NewsDetailProcessor;
import com.parser.processors.Processor;
import com.parser.quickaction.ActionItem;
import com.parser.quickaction.QuickAction;

public class NewsDetailFragment extends BaseDataFragment implements DetailFragment {
    //todo move to resources
    public static final String URL_PARAM = "url_param";
    private String mUrl;
    private NewsDetailAdapter mAdapter;
    private NewsDetailProcessor mProcessor;

    private QuickAction mQuickAction;
    private ActionItem mKarmaUpAction;
    private ActionItem mKarmaDownAction;
    private int mSelectedRecord;


    public static NewsDetailFragment getNewFragment(Bundle params) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(params);
        return fragment;
    }

    public void setArguments(Bundle arguments) {
        super.setArguments(arguments);
        if (arguments != null) {
            mUrl = arguments.getString(URL_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initListView();
        return view;
    }

    private void initListView() {
        ListView listView = getListView();
        if (listView != null) {
            listView.setDivider(null);
            listView.setDividerHeight(0);
            //  listView.setEnabled(false);
        }
        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // mQuickAction.refreshActionItem(mKarmaDownAction);
                // mQuickAction.refreshActionItem(mKarmaUpAction);
                mSelectedRecord = position;
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                mQuickAction.show(view);
            }
        });
        prepareQuickAction();
    }

    private void prepareQuickAction() {
        Activity activity = getActivity();
        mKarmaDownAction = new ActionItem();
        mKarmaDownAction.setTitle(activity.getString(R.string.dislike));
        mKarmaDownAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.commentdownbig));

        mKarmaUpAction = new ActionItem();
        mKarmaUpAction.setTitle(activity.getString(R.string.like));
        mKarmaUpAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.commentupbig));

        ActionItem mReplyAction = new ActionItem();
        mReplyAction.setTitle(activity.getString(R.string.reply));
        mReplyAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.reply));

        mQuickAction = new QuickAction(activity);
        mQuickAction.addActionItem(mKarmaDownAction);
        mQuickAction.addActionItem(mKarmaUpAction);
        mQuickAction.addActionItem(mReplyAction);
        //mQuickAction.addActionItem(mReplyAction);


        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                switch (pos) {
                    case (0): {
                        addKarma(false);
                        break;
                    }
                    case (1): {
                        addKarma(true);
                        break;
                    }
                    case (2): {

                        break;
                    }
                }
            }
        });
    }

    private void addKarma(boolean karmaUp){
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null){return;}
        cursor.moveToPosition(mSelectedRecord);
        String commentId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);

        //mQuickAction.
    }

    @Override
    protected CursorAdapter getAdapter() {
        Activity activity = getActivity();
        if (activity != null && mAdapter == null) {
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
        if (mProcessor == null) {
            Activity activity = getActivity();
            if (activity != null) {
                mProcessor = new NewsDetailProcessor(mUrl, activity);
            }
        }
        return mProcessor;
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
    protected String getSelection() {
        return NewsDetailDBHelper.POST_ID + "=?";//NewsDetailDBHelper."";
    }

    @Override
    protected String[] getSelectionArgs() {
        String args[] = new String[1];
        args[0] = mUrl;
        return args;
    }

    @Override
    public String getTitle(Context context) {
        if (context != null) {
            return context.getResources().getString(R.string.news_fragment_title);
        } else
            return null;
    }
}
