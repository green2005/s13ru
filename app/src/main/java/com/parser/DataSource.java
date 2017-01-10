package com.parser;

import android.os.Handler;

import com.parser.blogio.BlogConnector;
import com.parser.processors.Processor;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class DataSource {

    private Processor mProcessor;
    private Callbacks mCallbacks;
    private Handler mHandler;
    private int mRecordsFetched = 0;

    public static final String WIN_CHARSET = "windows-1251";
    public static final String UTF8_CHARSET = "UTF-8";

    private static final String S13_URL = "s13.ru/archives";


    public interface Callbacks {
        public void onError(String errorMessage);

        public void onLoadDone(int recordsFetched);
    }

    public DataSource(Processor processor, Callbacks callbacks) {
        mProcessor = processor;
        mCallbacks = callbacks;
        mHandler = new Handler();
    }

    public void fillData(final String url, final boolean isTopRequest) {
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream stream = null;
                try {
                    if (url.contains(S13_URL)) {
                        BlogConnector blogConnector = BlogConnector.getBlogConnector();
                        stream = blogConnector.getInputStream(url, UTF8_CHARSET);
                    } else {
                        stream = getInputStream(url);
                    }
                    if (stream == null) {
                        mRecordsFetched = 0;
                    } else {
                        mRecordsFetched = mProcessor.process(stream, isTopRequest, url);
                    }
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCallbacks != null) {
                                mCallbacks.onError(e.getMessage());
                            }
                        }
                    });
                }
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (Exception e) {
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallbacks != null) {
                            mCallbacks.onLoadDone(mRecordsFetched);
                        }
                    }
                });
            }
        });
        loadThread.start();
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
