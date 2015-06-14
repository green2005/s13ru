package com.parser.activities;


import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.parser.GetImageUriListener;
import com.parser.ImageMenuActionProvider;
import com.parser.R;
import com.parser.loader.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageZoomActivity extends ActionBarActivity {
    public static final String IMAGE_URL = "url";
    private String mUrl;
    private ImageView mImageView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zoomimageview_activity, menu);
        ImageMenuActionProvider provider = (ImageMenuActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.share));
        if (provider != null) {
            provider.setImageUriListener(new GetImageUriListener() {
                @Override
                public Uri onGetImageUri() {
                    if (mImageView == null) {
                        return null;
                    }
                    Drawable d = mImageView.getDrawable();
                    if (d == null) {
                        return null;
                    }
                    Bitmap bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ File.separator
                            + getPackageName());
                    f.mkdirs();
                    File outFile = new File(f.getAbsolutePath()+File.separator+"out.png");
                    String fileName = outFile.getAbsolutePath(); //dir + File.separator + "out.png";
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        fileOutputStream.close();
                        return Uri.fromFile(outFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getExtras();
            if (args != null) {
                mUrl = args.getString(IMAGE_URL);
            }
        }
        setContentView(R.layout.activity_image_zoom);
        // getActionBar().hide();
        mImageView = (ImageView) findViewById(R.id.imageView);
        ImageLoader imageLoader = ImageLoader.get(this);
        imageLoader.loadImage(mImageView, mUrl);
        PhotoViewAttacher attacher = new PhotoViewAttacher(mImageView);
        attacher.setZoomable(true);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
