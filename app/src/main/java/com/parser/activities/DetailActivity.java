package com.parser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.ShareLinkMenuActionProvider;
import com.parser.fragments.DetailFragment;
import com.parser.fragments.NewsDetailFragment;
import com.parser.fragments.PosterDetailFragment;
import com.parser.fragments.VKFeedDetailFragment;

public class DetailActivity extends ActionBarActivity {
    public static final String DETAIL_TYPE = "detailType";
    private int mDetailType;
    private String mUrl;
    private String mTitle;
    Fragment mFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Bundle params = getIntent().getExtras();
        if (params != null) {
            mDetailType = params.getInt(DETAIL_TYPE);
            if (mDetailType == FragmentMenuItem.NEWS_ITEM.ordinal()) {
                mFragment = NewsDetailFragment.getNewFragment(params);
            } else if (mDetailType == FragmentMenuItem.VK_ITEM.ordinal()) {
                mFragment = VKFeedDetailFragment.getNewFragment(params);
            } else if (mDetailType == FragmentMenuItem.POSTER_ITEM.ordinal()) {
                mFragment = PosterDetailFragment.getNewFragment(params);
            }
            if (mFragment != null) {
                String title = ((DetailFragment) mFragment).getToolBarTitle(this);
                if (!TextUtils.isEmpty(title)) {
                    getSupportActionBar().setTitle(title);
                }
            }
            if (mFragment != null) {

                if (mFragment instanceof DetailFragment) {
                    mUrl = ((DetailFragment) mFragment).getUrl();
                    mTitle = ((DetailFragment) mFragment).getItemTitle();
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, mFragment);
                ft.commit();
            }
        }
    }

    private void doShareContent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mUrl);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mTitle);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDetailType != FragmentMenuItem.NEWS_ITEM.ordinal()) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.menu_share_postlink, menu);
        ShareLinkMenuActionProvider provider = (ShareLinkMenuActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.share));
        provider.setTitle(mTitle);
        provider.setUrl(mUrl);

        MenuItem replyItem = menu.findItem(R.id.reply);

        if (mFragment instanceof MenuItem.OnMenuItemClickListener) {
            replyItem.setOnMenuItemClickListener((MenuItem.OnMenuItemClickListener) mFragment);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v != null) {
            MenuItem item =  menu.add("+");
            item.setIcon(R.drawable.commentup);
            item = menu.add("-");
            item.setIcon(R.drawable.commentdown);
            item = menu.add("ignore");
            item.setIcon(R.drawable.add_to_blacklist);
        }
//        super.onCreateContextMenu(menu, v, menuInfo);
    }
}
