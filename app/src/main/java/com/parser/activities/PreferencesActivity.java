package com.parser.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.parser.R;
import com.parser.fragments.PrefFragment;

public class PreferencesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prefFragment = PrefFragment.getNewFragment(new Bundle());
        ft.replace(android.R.id.content , prefFragment);
        ft.commit();
        setTitle(R.string.prefTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  android.R.id.home){
            finish();
            return true;
        } else
        return super.onOptionsItemSelected(item);
    }
}
