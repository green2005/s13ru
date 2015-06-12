package com.parser.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;


import com.parser.ErrorHelper;
import com.parser.NewsApplication;
import com.parser.os.VKExecutor;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;


public class ImageLoader {
    private class LoadingImages {
        private Map<String, CopyOnWriteArraySet<ImageView>> mLoadingImagesList;
        private Map<String, Runnable> mRunningRunnables;

        LoadingImages() {
            mLoadingImagesList = new ConcurrentHashMap<>();
            mRunningRunnables = new ConcurrentHashMap<>();
        }

        void addImage(String url, ImageView imageView) {
            CopyOnWriteArraySet<ImageView> imageViews = mLoadingImagesList.get(url);
            if (imageViews == null) {
                imageViews = new CopyOnWriteArraySet<>();
                imageViews.add(imageView);
                mLoadingImagesList.put(url, imageViews);
            } else {
                imageViews.add(imageView);
            }
        }

        void addThread(String url, Runnable imageRunnable) {
            mRunningRunnables.put(url, imageRunnable);
        }

        Set<Runnable> getThreads() {
            return new HashSet<>(mRunningRunnables.values());
        }

        void loadingDone(String url) {
            mLoadingImagesList.remove(url);
            mRunningRunnables.remove(url);
        }

        //  boolean isImageLoading(String url) {
        //      return mLoadingImagesList.containsKey(url);
        //  }

        private void clear() {
            mRunningRunnables.clear();
            mLoadingImagesList.clear();
        }

        Set<ImageView> getImageViews(String url) {
            return mLoadingImagesList.get(url);
        }
    }
    //private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;
    private Handler mHandler;
    private LoadingImages mLoadingList;
    private VKExecutor mExecutor;
    public static final String KEY = "ImageLoader";
    private AtomicBoolean mIsResumed ;
    private LinkedList<Map<String,ImageView>> mPausedImages;

    public ImageLoader(Context context) {
        mHandler = new Handler();
        mCache = new ImageCache(context);
        mExecutor = new VKExecutor();
        mLoadingList = new LoadingImages();
        mIsResumed = new AtomicBoolean(true);
        mPausedImages = new LinkedList<>();
    }

    public static ImageLoader get(Context context) {
        try {
            ImageLoader imageLoader = (ImageLoader)NewsApplication.get(context, ImageLoader.KEY);
            if (imageLoader.getIsPaused()){
                imageLoader.resumeLoadingImages();
            }
            return imageLoader;
        } catch (Exception e) {
            ErrorHelper.showError(context, e);
        }
        return null;
    }

    public void loadImage(final ImageView imageView, final String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }
        imageView.setImageBitmap(null);
        imageView.setTag(url);
        if (!TextUtils.isEmpty(url)) {
            Bitmap bmp = mCache.getBitmapFromLRUCache(url);
            if (bmp != null) {
                imageView.setImageBitmap(bmp);
             } else {
                if (mIsResumed.get()) {
                    Thread imageLoadThread = new ImageLoadThread(mHandler, imageView, url);
                    mLoadingList.addThread(url, imageLoadThread);
                    mExecutor.start(imageLoadThread);
                } else
                {   Map<String, ImageView> map = new HashMap<>();
                    map.put(url, imageView);
                    mPausedImages.push(map);
                }
            }
        }
    }

    public void pauseLoadingImages(){
        mIsResumed.set(false);
    }

    public boolean getIsPaused(){
        return  !mIsResumed.get();
    }

    public void resumeLoadingImages(){
        mIsResumed.set(true);
        while (!mPausedImages.isEmpty() && mIsResumed.get()) {
            Map<String, ImageView> map = mPausedImages.pop();
            for (String url:map.keySet()){
              ImageView imageView = map.get(url);
              if (url.equals(imageView.getTag())){
                  loadImage(imageView, url);
              }
            }
        }
        //mPausedImages.clear();
    }

    public String getUrl(ImageView imageView) {
        return (String)imageView.getTag();
    }

    private class ImageLoadThread extends Thread {
        Handler mHandler;
        ImageView mImageView;
        String mUrl;

        ImageLoadThread(Handler handler, ImageView imageView, String url) {
            mHandler = handler;
            mImageView = imageView;
            mUrl = url;
        }

        public void run() {
            try {
                Set<ImageView> imageViews = mLoadingList.getImageViews(mUrl);
                if (imageViews != null) {
                    //image is already loading
                    imageViews.add(mImageView);
                } else {
                    //try to find it in cache
                    final Bitmap bmp = mCache.getImage(mUrl);
                    if (bmp != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mUrl.equals(mImageView.getTag()))
                                    mImageView.setImageBitmap(bmp);
                            }
                        });
                    } else {
                        //                    Thread.sleep(LOAD_IMAGE_DELAY);
                        mLoadingList.addImage(mUrl, mImageView);
                        doLoadImage(mUrl, mHandler);
                    }
                }
            } catch (Exception e) {
                mCache.removeImage(mUrl);
                mLoadingList.loadingDone(mUrl);
            }
        }
    }

    private void doLoadImage(final String imageUrl, final Handler mHandler) {
        InputStream is;
        try {
            URL url = new URL(imageUrl);
            is = url.openStream();
            mCache.putImage(imageUrl, is);
            final Bitmap bmp = mCache.getImage(imageUrl);
            mCache.addBitmapToLRUCache(imageUrl, bmp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Set<ImageView> imageViews = mLoadingList.getImageViews(imageUrl);
                    if (imageViews != null) {
                        for (ImageView image : imageViews) {
                            if (imageUrl.equals(image.getTag()))
                                image.setImageBitmap(bmp);
                        }
                        mLoadingList.loadingDone(imageUrl);
                    }
                }
            });
        } catch (Exception e) {
            mCache.removeImage(imageUrl);
            mLoadingList.loadingDone(imageUrl);
        }
    }

    public void stopLoadingImages() {
        for (Runnable r : mLoadingList.getThreads()) {
            mExecutor.remove(r);
        }
        mLoadingList.clear();
    }

    public void clear() {
        mCache.clearImages();
    }
}
