package com.parser.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.parser.R;
import com.parser.authentication.AUTH_RESULT;
import com.parser.authentication.AuthDialog;
import com.parser.authentication.AuthenticateListener;

public class PrefFragment extends PreferenceFragment {
    private static final String MARKET_URL = "market://details?id=";

    public static String getPreferencesName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    public static Fragment getNewFragment(Bundle args) {
        Fragment prefFragment = new PrefFragment();
        prefFragment.setArguments(args);
        return prefFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(getPreferencesName(getActivity()));
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        String key = preference.getKey();
        if (TextUtils.isEmpty(key)){
            return false;
        }
        Activity activity = getActivity();
        if (activity == null){
            return false;
        }
        if (key.equalsIgnoreCase(activity.getString(R.string.rate_key))){
            rateMe();
        } else
        if (key.equalsIgnoreCase(activity.getString(R.string.authenticate_key))){
            authenticate();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void authenticate(){
        Activity activity = getActivity();
        if (activity == null){
            return;
        }
        AuthDialog.editAuth(activity, new AuthenticateListener() {
            @Override
            public void onAuthResult(AUTH_RESULT result) {

            }
        });
    }

    private void rateMe() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Uri uri = Uri.parse(MARKET_URL + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            //ErrorHelper.showErrorDialog(R.string.errormarket, activity, null);
        }
    }
}
