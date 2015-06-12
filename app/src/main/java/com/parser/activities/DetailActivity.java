package com.parser.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.parser.FragmentMenuItem;
import com.parser.R;
import com.parser.fragments.DetailFragment;
import com.parser.fragments.NewsDetailFragment;
import com.parser.fragments.PosterDetailFragment;
import com.parser.fragments.VKFeedDetailFragment;

public class DetailActivity extends ActionBarActivity {
    public static final String DETAIL_TYPE = "detailType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Bundle params = getIntent().getExtras();
        if (params != null) {
            Fragment detailFragment = null;
            int detailType = params.getInt(DETAIL_TYPE);
            if (detailType == FragmentMenuItem.NEWS_ITEM.ordinal()) {
                detailFragment = NewsDetailFragment.getNewFragment(params);
            } else if (detailType == FragmentMenuItem.VK_ITEM.ordinal()) {
                detailFragment = VKFeedDetailFragment.getNewFragment(params);
            } else if (detailType == FragmentMenuItem.POSTER_ITEM.ordinal()) {
                detailFragment = PosterDetailFragment.getNewFragment(params);
            }
            if (detailFragment != null) {
                String title = ((DetailFragment) detailFragment).getTitle(this);
                if (!TextUtils.isEmpty(title)) {
                    getSupportActionBar().setTitle(title);
                }
            }
            if (detailFragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, detailFragment);
                ft.commit();
            }
        }
    }
}
