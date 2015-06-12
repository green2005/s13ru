package com.parser.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.parser.R;
import com.parser.ZoomableImageView;
import com.parser.loader.ImageLoader;

public class ImageZoomActivity extends ActionBarActivity{
    public static final String IMAGE_URL = "url";

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null){
            Bundle args = intent.getExtras();
            if (args!=null){
                mUrl = args.getString(IMAGE_URL);
            }
        }
        setContentView(R.layout.activity_image_zoom);
       // getActionBar().hide();
        ZoomableImageView imageView = (ZoomableImageView)findViewById(R.id.imageView);
        ImageLoader imageLoader = ImageLoader.get(this);
        imageLoader.loadImage(imageView, mUrl);
    }
}
