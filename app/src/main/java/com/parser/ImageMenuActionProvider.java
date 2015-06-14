package com.parser;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageMenuActionProvider extends android.support.v4.view.ActionProvider implements
        OnMenuItemClickListener {

    private Context mContext;
    private Map<String, ResolveInfo> mMenuItems;
    private GetImageUriListener mImageUrlListener;
    private Uri mImageUri;

    public ImageMenuActionProvider(Context context) {
        super(context);
        mContext = context;
        mMenuItems = new HashMap<>();
    }

    public void setImageUriListener(GetImageUriListener listener) {
        mImageUrlListener = listener;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean onPerformDefaultAction() {

        return super.onPerformDefaultAction();
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        mMenuItems.clear();
        subMenu.clear();
        PackageManager manager = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        List<ResolveInfo> infos;
        infos = manager.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        int j = 0;
        for (ResolveInfo info : infos) {
            ApplicationInfo appInfo = info.activityInfo.applicationInfo;
            MenuItem item = subMenu.add(0, j, j, manager.getApplicationLabel(appInfo))
                    .setIcon(appInfo.loadIcon(manager))
                    .setOnMenuItemClickListener(this);
            mMenuItems.put(item.getTitle().toString(), info);
            j++;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final ResolveInfo info = mMenuItems.get(item.getTitle().toString());
        if (info != null) {
            try {
                Runnable onAfterSave = new Runnable() {
                    @Override
                    public void run() {
                        doShareImage(info);
                    }
                };
                saveImage(onAfterSave);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void doShareImage(ResolveInfo info) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(android.content.Intent.EXTRA_STREAM, mImageUri);
        intent.setData(mImageUri);
        String activityName = info.activityInfo.name;
        intent.setClassName(info.activityInfo.packageName, activityName);
        intent.setType("image/*");
        mContext.startActivity(intent);
    }

    private void saveImage(final Runnable onAfterSave) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mImageUri = mImageUrlListener.onGetImageUri();
                handler.post(onAfterSave);
            }
        }).start();
    }
}
