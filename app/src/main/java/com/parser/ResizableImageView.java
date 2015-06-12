package com.parser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.parser.adapters.ImageClickListener;

public class ResizableImageView extends ImageView {
    /*
    Copyright:
    http://stackoverflow.com/questions/5554682/android-imageview-adjusting-parents-height-and-fitting-width
     */
    private int mOriginalWidth;
    private int mOriginalHeight;

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(new ImageClickListener(this, context));
    }

    public void setOriginalImageSize(int width, int height) {
        mOriginalHeight = height;
        mOriginalWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOriginalWidth != 0) {
            setImageSize(widthMeasureSpec, mOriginalWidth, mOriginalHeight);
        } else {
            Drawable d = getDrawable();
            if (d != null) {
                setImageSize(widthMeasureSpec, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private void setImageSize(int widthMeasureSpec, int imageWidth, int imageHeight) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) Math.ceil((float) width * (float) imageHeight / (float) imageWidth);
        setMeasuredDimension(width, height);
    }

}
