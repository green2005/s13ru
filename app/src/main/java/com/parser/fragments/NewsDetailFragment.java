package com.parser.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parser.ErrorHelper;
import com.parser.R;
import com.parser.activities.CommentEditDialog;
import com.parser.adapters.AdapterPositionListener;
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
    private ActionItem mKarmaUpAction;
    private ActionItem mKarmaDownAction;
    private FloatingActionButton mActionButton;
    private BlogConnector mConnector;

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
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.releaseLoaders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mConnector = BlogConnector.getBlogConnector();
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        mActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mActionButton.setVisibility(View.GONE);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnector.loggedIn()) {
                    editComment(null);
                } else {
                    Context context = getActivity();
                    if (context != null) {
                        Toast.makeText(context, R.string.not_authenticated, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        initListView();
        return view;
    }

    private void editComment(String initialComment) {
        Context context = getActivity();
        if (context != null) {
            android.support.v4.app.FragmentManager fm = getFragmentManager();
            CommentEditDialog.getNewDialog(initialComment, new CommentEditDialog.OnDoneEditingListener() {
                @Override
                public void onDoneEditing(String comment) {
                    sendComment(comment);
                }
            }).show(fm, "enter your dialog");
        }
    }


    private void sendComment(final String comment) {
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
        final String postUrl = CursorHelper.getString(cursor, NewsDetailDBHelper.POST_ID);
        if (mConnector.loggedIn()) {
            String akismet = CursorHelper.getString(cursor, NewsDetailDBHelper.AKISMET);
            String ak_js = CursorHelper.getString(cursor, NewsDetailDBHelper.AK_JS);
            final ProgressDialog pg = new ProgressDialog(context);
            pg.setMessage(context.getString(R.string.please_wait));
            pg.setCancelable(false);
            pg.show();
            mConnector.addComment(comment, akismet, ak_js, postId, new RequestListener() {
                @Override
                public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage) {
                    if (pg.isShowing()) {
                        pg.dismiss();
                    }
                    if (result == BlogConnector.QUERY_RESULT.ERROR) {
                        Context context = getActivity();
                        if (context == null) {
                            return;
                        }
                        ErrorHelper.showError(context, errorMessage);
                    } else {
                        Context context = getActivity();
                        if (context == null) {
                            return;
                        }

                        ContentResolver resolver = context.getContentResolver();
                        ContentValues[] valueses = new ContentValues[1];
                        ContentValues values = new ContentValues();
                        values.put(NewsDetailDBHelper.AUTHOR_COLUMN, AuthDialog.getUserName(context));
                        values.put(NewsDetailDBHelper.RECORD_TYPE_COLUMN, NewsDetailDBHelper.NewsItemType.REPLY.ordinal());
                        values.put(NewsDetailDBHelper.TEXT_COLUMN, comment);
                        values.put(NewsDetailDBHelper.KARMA_DOWN_COLUMN, 0);
                        values.put(NewsDetailDBHelper.KARMA_UP_COLUMN, 0);
                        values.put(NewsDetailDBHelper.POST_ID, postUrl);
                        valueses[0] = values;

                        resolver.bulkInsert(NewsContentProvider.NEWS_DETAIL_URI, valueses);
                        resolver.notifyChange(NewsContentProvider.NEWS_DETAIL_URI, null);
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
                updateQuickActions(position);
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                mQuickAction.show(view);
            }
        });
        prepareQuickAction();
    }

    private void updateQuickActions(int position) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (mAdapter == null) {
            return;
        }
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        if (cursor.moveToPosition(position)) {
            int canChangeKarma = CursorHelper.getInt(cursor, NewsDetailDBHelper.CAN_CHANGE_KARMA);
            if (canChangeKarma == 1) {
                mKarmaUpAction.setEnabled(true);
                mKarmaDownAction.setEnabled(true);
                mKarmaDownAction.setIcon(context.getResources().getDrawable(
                        com.parser.R.drawable.commentdownbig));
                mKarmaUpAction.setIcon(context.getResources().getDrawable(
                        com.parser.R.drawable.commentupbig));
            } else {
                mKarmaUpAction.setEnabled(false);
                mKarmaDownAction.setEnabled(false);
                mKarmaDownAction.setIcon(context.getResources().getDrawable(
                        R.drawable.commentdownbiggrey));
                mKarmaUpAction.setIcon(context.getResources().getDrawable(
                        R.drawable.commentupbiggrey));
            }
        }
        mQuickAction.refreshActionItem(mKarmaDownAction);
        mQuickAction.refreshActionItem(mKarmaUpAction);
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

        ActionItem replyAction = new ActionItem();
        replyAction.setTitle(activity.getString(R.string.reply));
        replyAction.setIcon(activity.getResources().getDrawable(
                com.parser.R.drawable.reply));
        mQuickAction = new QuickAction(activity);
        mQuickAction.addActionItem(mKarmaDownAction);
        mQuickAction.addActionItem(mKarmaUpAction);
        mQuickAction.addActionItem(replyAction);
        mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                switch (pos) {
                    case (0): {
                        if (mKarmaDownAction.getEnabled()) {
                            addKarma(false);
                        }
                        break;
                    }
                    case (1): {
                        if (mKarmaUpAction.getEnabled()) {
                            addKarma(true);
                        }
                        break;
                    }
                    case (2): {
                        reply();
                        break;
                    }
                }
            }
        });
    }

    private void reply() {
        if (!mConnector.loggedIn()) {
            Activity context = getActivity();
            if (context != null) {
                Toast.makeText(context, R.string.not_authenticated, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(mSelectedRecord);
        String authorName = CursorHelper.getString(cursor, NewsDetailDBHelper.AUTHOR_COLUMN);
        editComment(authorName + " : ");
//        mCommentEdit.setText(authorName + " : ");
//        mCommentEdit.setSelection(authorName.length()+3);
//        mCommentEdit.requestFocus();
    }

    private void addKarma(final boolean karmaUp) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(mSelectedRecord);
        final String commentId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);
        //final String postId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);
        final int ups = CursorHelper.getInt(cursor, NewsDetailDBHelper.KARMA_UP_COLUMN);
        final int downs = CursorHelper.getInt(cursor, NewsDetailDBHelper.KARMA_DOWN_COLUMN);
        final BlogConnector connector = BlogConnector.getBlogConnector();
        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (connector.loggedIn()) {
            connector.changeKarma(commentId, karmaUp, new RequestListener() {
                @Override
                public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage) {
                    Context context = getActivity();
                    if (context == null) {
                        return;
                    }
                    if (result == BlogConnector.QUERY_RESULT.ERROR) {
                        ErrorHelper.showError(context, errorMessage);
                    } else {
                        ContentResolver resolver = context.getContentResolver();
                        ContentValues values = new ContentValues();
                        String[] args = new String[1];
                        args[0] = commentId;
                        if (karmaUp) {
                            values.put(NewsDetailDBHelper.KARMA_UP_COLUMN, ups + 1);
                        } else {
                            values.put(NewsDetailDBHelper.KARMA_DOWN_COLUMN, downs + 1);
                        }
                        values.put(NewsDetailDBHelper.CAN_CHANGE_KARMA, 0);
                        resolver.update(NewsContentProvider.NEWS_DETAIL_URI, values, NewsDetailDBHelper.COMMENT_ID_COLUMN + " = ?", args);
                        resolver.notifyChange(NewsContentProvider.NEWS_DETAIL_URI, null);
                    }
                }
            });
        } else {
            Toast.makeText(context, R.string.not_authenticated, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected CursorAdapter getAdapter() {
        Activity activity = getActivity();
        if (activity != null && mAdapter == null) {
            mAdapter = new NewsDetailAdapter(activity, R.layout.item_news_feed_image, null, NewsDetailDBHelper.getFields(), null, 0);
            AdapterPositionListener listener = new AdapterPositionListener() {
                @Override
                public void onBottomReached() {
                    mActionButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBottomEscaped() {
                    mActionButton.setVisibility(View.GONE);
                }
            };
            mAdapter.setPositionChangeListener(listener);
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
