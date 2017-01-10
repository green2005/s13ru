package com.parser.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parser.ErrorHelper;
import com.parser.R;
import com.parser.activities.CommentEditDialog;
import com.parser.adapters.NewsDetailAdapter;
import com.parser.blogio.AuthDialog;
import com.parser.blogio.BlogConnector;
import com.parser.blogio.RequestListener;
import com.parser.db.BlackListDBHelper;
import com.parser.db.CursorHelper;
import com.parser.db.NewsContentProvider;
import com.parser.db.NewsDetailDBHelper;
import com.parser.processors.NewsDetailProcessor;
import com.parser.processors.Processor;
import com.parser.quickaction.ActionItem;
import com.parser.quickaction.QuickAction;

public class NewsDetailFragment extends BaseDataFragment implements DetailFragment, MenuItem.OnMenuItemClickListener,
        OnCommentItemClickListener {

    //todo move to resources
    public static final String URL_PARAM = "url_param";
    private String mUrl;
    private NewsDetailAdapter mAdapter;
    private NewsDetailProcessor mProcessor;

    private BlogConnector mConnector;
    private String mTitle;
    private PopupWindow mPopupWindow;


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
        //assert view != null;
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
                        ContentValues[] values = new ContentValues[1];
                        ContentValues cvals = new ContentValues();
                        cvals.put(NewsDetailDBHelper.AUTHOR_COLUMN, AuthDialog.getUserName(context));
                        cvals.put(NewsDetailDBHelper.RECORD_TYPE_COLUMN, NewsDetailDBHelper.NewsItemType.REPLY.ordinal());
                        cvals.put(NewsDetailDBHelper.TEXT_COLUMN, comment);
                        cvals.put(NewsDetailDBHelper.KARMA_DOWN_COLUMN, 0);
                        cvals.put(NewsDetailDBHelper.KARMA_UP_COLUMN, 0);
                        cvals.put(NewsDetailDBHelper.POST_ID, postUrl);
                        values[0] = cvals;

                        resolver.bulkInsert(NewsContentProvider.NEWS_DETAIL_URI, values);
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

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedRecord = position;
                updateQuickActions(position);
                mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
                mQuickAction.show(view);
            }
        });*/

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                }
            }
        });
        //      prepareQuickAction();
    }

    /*
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
            // mKarmaDownAction.setTitle(activity.getString(R.string.dislike));
            mKarmaDownAction.setIcon(activity.getResources().getDrawable(
                    com.parser.R.drawable.commentdownbig));

            mKarmaUpAction = new ActionItem();
            //  mKarmaUpAction.setTitle(activity.getString(R.string.like));
            mKarmaUpAction.setIcon(activity.getResources().getDrawable(
                    com.parser.R.drawable.commentupbig));

            ActionItem replyAction = new ActionItem();
            // replyAction.setTitle(activity.getString(R.string.reply));
            replyAction.setIcon(activity.getResources().getDrawable(
                    com.parser.R.drawable.reply));

            final ActionItem addToBlackList = new ActionItem();
            addToBlackList.setIcon(getResources().getDrawable(R.drawable.add_to_blacklist));

            mQuickAction = new QuickAction(activity);
            mQuickAction.addActionItem(mKarmaDownAction);
            mQuickAction.addActionItem(mKarmaUpAction);
            mQuickAction.addActionItem(replyAction);
            mQuickAction.addActionItem(addToBlackList);
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
                        case (3): {
                            addToBlackList();
                            break;
                        }
                    }
                }
            });
        }
    */
    private void addToBlackList(int recordIndex) {
        final Context context = getActivity();
        if (context == null) {
            return;
        }
        final android.support.v7.app.AlertDialog dialog;
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(recordIndex);
        final String authorName = CursorHelper.getString(cursor, NewsDetailDBHelper.AUTHOR_COLUMN);
        builder.setTitle(R.string.black_list);
        String s = context.getResources().getString(R.string.add_to_black_list);
        String msg = String.format(s, authorName);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ContentResolver resolver = context.getContentResolver();
                ContentValues[] values = new ContentValues[1];
                values[0] = new ContentValues();
                values[0].put(BlackListDBHelper.USER_COLUMN, authorName);
                resolver.bulkInsert(NewsContentProvider.BLACKLIST_URI, values);
                resolver.notifyChange(NewsContentProvider.NEWS_DETAIL_URI, null);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private boolean loggedIn() {
        if (!mConnector.loggedIn()) {
            Activity context = getActivity();
            if (context != null) {
                Toast.makeText(context, R.string.not_authenticated, Toast.LENGTH_SHORT).show();
            }
            return false;
        } else
            return true;
    }

    private void reply(int recordIndex) {
        if (!loggedIn()) {
            return;
        }
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(recordIndex);
        String authorName = CursorHelper.getString(cursor, NewsDetailDBHelper.AUTHOR_COLUMN);
        editComment(authorName + " : ");
//        mCommentEdit.setText(authorName + " : ");
//        mCommentEdit.setSelection(authorName.length()+3);
//        mCommentEdit.requestFocus();
    }

    private void addKarma(final boolean karmaUp, int recordIndex) {
        if (!loggedIn()) {
            return;
        }
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(recordIndex);
        final String commentId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);
        final String akismet = CursorHelper.getString(cursor, NewsDetailDBHelper.AKISMET);
        //final String postId = CursorHelper.getString(cursor, NewsDetailDBHelper.COMMENT_ID_COLUMN);
        final int ups = CursorHelper.getInt(cursor, NewsDetailDBHelper.KARMA_UP_COLUMN);
        final int downs = CursorHelper.getInt(cursor, NewsDetailDBHelper.KARMA_DOWN_COLUMN);
        final BlogConnector connector = BlogConnector.getBlogConnector();
        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (connector.loggedIn()) {
            connector.changeKarma(commentId, karmaUp, akismet ,new RequestListener() {
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
            mAdapter.setOnCommentClickListener(this);
            setTitle(mAdapter.getCursor());
        }
        return mAdapter;
    }

    @Override
    protected void loadDone(Cursor cursor) {
        setTitle(cursor);
    }

    private void setTitle(Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
            mTitle = CursorHelper.getString(cursor, NewsDetailDBHelper.TEXT_COLUMN);
        }
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
    public String getToolBarTitle(Context context) {
        if (context != null) {
            return context.getResources().getString(R.string.news_fragment_title);
        } else
            return null;
    }

    @Override
    public String getItemTitle() {
        return mTitle;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.reply) {

            if (mConnector.loggedIn()) {
                editComment(null);
            } else {
                Context context = getActivity();
                if (context != null) {
                    Toast.makeText(context, R.string.not_authenticated, Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;
    }

    @Override
    public void onMoreBtnClick(final int itemPos, View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        LayoutInflater inflater = getLayoutInflater(null);
        View popupView = inflater.inflate(R.layout.popup, null);
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }

        Cursor cr = getAdapter().getCursor();
        cr.moveToPosition(itemPos);
        final int canChangeKarma = CursorHelper.getInt(cr, NewsDetailDBHelper.CAN_CHANGE_KARMA);

        mPopupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (canChangeKarma != 1) {
            ImageView up = (ImageView) popupView.findViewById(R.id.up);
            up.setBackgroundResource(R.drawable.commentupbiggrey);

            ImageView down = (ImageView) popupView.findViewById(R.id.down);
            down.setBackgroundResource(R.drawable.commentdownbiggrey);
        }


        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int y = 8 - view.getHeight();
        int x = view.getWidth()/3;

        popupView.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        int height = 0;
        popupView.measure(height, height);
        height = popupView.getMeasuredHeight();
        y = 8 - height;
        /* int popupHeight = popupView.getMeasuredHeight();
        int topPos =  (popupHeight - height) / 2;*/
        mPopupWindow.showAsDropDown(view, x, y);
        if (canChangeKarma == 1) {
            RelativeLayout la = (RelativeLayout) popupView.findViewById(R.id.viewUp);
            la.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addKarma(true, itemPos);
                }
            });
            RelativeLayout la2 = (RelativeLayout) popupView.findViewById(R.id.viewDown);
            la2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addKarma(false, itemPos);
                }
            });
        }

        RelativeLayout replyLa = (RelativeLayout) popupView.findViewById(R.id.viewReply);
        replyLa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply(itemPos);
            }
        });

        RelativeLayout ignoreLa = (RelativeLayout) popupView.findViewById(R.id.viewBlock);
        ignoreLa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToBlackList(itemPos);
            }
        });
    }


    private int getHeight() {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.heightPixels;
    }


}
