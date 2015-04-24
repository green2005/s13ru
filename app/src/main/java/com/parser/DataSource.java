package com.parser;

import android.os.Handler;

import com.parser.processors.Processor;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class DataSource {

    private Processor mProcessor;
    private Callbacks mCallbacks;
    private Handler mHandler;

    public interface Callbacks {
        public void onError(String errorMessage);
        public void onLoadDone();
    }

    public DataSource(Processor processor, Callbacks callbacks) {
        mProcessor = processor;
        mCallbacks = callbacks;
        mHandler = new Handler();
    }

    public void fillData(final String url) {
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = getInputStream(url);
                    try {
                        mProcessor.process(getInputStream(url));
                    } finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }
                catch (final Exception e){
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallbacks != null){
                                mCallbacks.onError(e.getMessage());
                            }
                        }
                    });
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallbacks != null){
                            mCallbacks.onLoadDone();
                        }
                    }
                });
            }
        });
        loadThread.start();
        //todo add Executor
    }

    private InputStream getInputStream(String href) {
        try {
            URL url = new URL(href);
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        } catch (final Exception e) {
            if (mCallbacks != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallbacks != null) {
                            mCallbacks.onError(e.getMessage()); //handle exception in UI thread
                        }
                    }
                });
            }
        }
        return null;
    }

}
