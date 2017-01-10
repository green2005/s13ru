package com.parser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareLinkMenuActionProvider extends android.support.v4.view.ActionProvider implements MenuItem.OnMenuItemClickListener{
    private Map<MenuItem, ResolveInfo> mMenuItems;
    private String mUrl;
    private String mTitle;

     public ShareLinkMenuActionProvider(Context context ) {
        super(context);
        mMenuItems = new HashMap<>();
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setUrl(String url){
        mUrl = url;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        mMenuItems.clear();
        subMenu.clear();
        Context context = getContext();
        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> infos;
        infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        int j = 0;
        for (ResolveInfo info : infos) {
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            MenuItem item = subMenu.add(0, j, j, manager.getApplicationLabel(appInfo))
                    .setIcon(appInfo.loadIcon(manager))
                    .setOnMenuItemClickListener(this);
            mMenuItems.put(item, info);
            j++;
        }
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return doShareLink(item);
    }

    private boolean doShareLink(MenuItem item){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, mUrl);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mTitle);
        ResolveInfo info = mMenuItems.get(item);
        String activityName = info.activityInfo.name;
        intent.setClassName(info.activityInfo.packageName, activityName);
        intent.setType("text/plain");
        getContext().startActivity(intent);
        return true;
    }
}
