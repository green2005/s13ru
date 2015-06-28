package com.parser.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parser.ErrorHelper;
import com.parser.R;
import com.parser.adapters.NewsDetailAdapter;
import com.parser.blogio.AuthDialog;
import com.parser.blogio.BlogConnector;
import com.parser.blogio.RequestListener;
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
    private int mSelectedRecord;
    private EditText mCommentEdit;


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
        initEditLayout(view);
        return view;
    }

    private void initEditLayout(View view) {
        RelativeLayout editLayout = (RelativeLayout) (view.findViewById(R.id.editing_layout));
        ImageButton sendBtn = (ImageButton) editLayout.findViewById(R.id.sendBtn);
        mCommentEdit = (EditText) editLayout.findViewById(R.id.commentEdit);
        Context context = getActivity();
        if (context != null) {
            String userName = AuthDialog.getUserName(context);
            String pwd = AuthDialog.getPwd(context);
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)) {
                editLayout.setVisibility(View.VISIBLE);
            } else {
                editLayout.setVisibility(View.GONE);
            }
        }
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(mCommentEdit.getText().toString());
            }
        });
    }

    private void sendComment(String comment) {
        Cursor cursor = getAdapter().getCursor();
        if ((cursor == null) || (cursor.isClosed())) {
            return;
        }
        Context context = getActivity();
        if (context == null) {
            return;
        }
        cursor.moveToFirst();
        String postId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);
        final BlogConnector connector = BlogConnector.getBlogConnector();
        if (connector.loggedIn()) {
            String akismet = CursorHelper.getString(cursor, NewsDetailDBHelper.AKISMET);
            final ProgressDialog pg = new ProgressDialog(context);
            pg.setTitle(context.getString(R.string.please_wait));
            pg.show();
            connector.addComment(comment, akismet, postId, new RequestListener() {
                @Override
                public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage) {
                    if (pg != null && pg.isShowing()) {
                        pg.dismiss();
                    }
                    if (result == BlogConnector.QUERY_RESULT.ERROR) {
                        Context context = getActivity();
                        if (context == null) {
                            return;
                        }
                        ErrorHelper.showError(context, errorMessage);
                    } else {
                        loadData(0);
                    }
                }
            });
        }
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_new_detail;
    }

    private void initListView() {
        ListView listView = getListView();
        if (listView != null) {
            listView.setDivider(null);
            listView.setDividerHeight(0);
        }
        assert listView != null;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedRecord = position;
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                mQuickAction.show(view);
            }
        });
        prepareQuickAction();
    }

    private void prepareQuickAction() {
        Activity activity = getActivity();
        ActionItem karmaDownAction = new ActionItem();
        karmaDownAction.setTitle(activity.getString(R.string.dislike));
        karmaDownAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.commentdownbig));

        ActionItem mKarmaUpAction = new ActionItem();
        mKarmaUpAction.setTitle(activity.getString(R.string.like));
        mKarmaUpAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.commentupbig));

        ActionItem replyAction = new ActionItem();
        replyAction.setTitle(activity.getString(R.string.reply));
        replyAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.reply));

        mQuickAction = new QuickAction(activity);
        mQuickAction.addActionItem(karmaDownAction);
        mQuickAction.addActionItem(mKarmaUpAction);
        mQuickAction.addActionItem(replyAction);
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

    private void addKarma(boolean karmaUp) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
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
    protected void loadData(final int offset) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        BlogConnector connector = BlogConnector.getBlogConnector();
        if (connector.loggedIn()) {
            super.loadData(offset);
            return;
        }
        String userName = AuthDialog.getUserName(context);
        String pwd = AuthDialog.getPwd(context);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(pwd)) {
            connector.login(userName, pwd, new RequestListener() {
                @Override
                public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage) {
                    doLoadData(offset);
                }
            });
        } else {
            super.loadData(offset);
        }
    }

    private void doLoadData(int offset) {
        super.loadData(offset);
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
