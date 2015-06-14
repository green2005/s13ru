package com.parser.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.parser.R;

public class PrefFragment extends PreferenceFragment{
    public static Fragment getNewFragment(Bundle args){
        Fragment prefFragment = new PrefFragment();
        prefFragment.setArguments(args);
        return prefFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getActivity().getApplication().getPackageName());
        addPreferencesFromResource(R.xml.preferences);
    }




}
