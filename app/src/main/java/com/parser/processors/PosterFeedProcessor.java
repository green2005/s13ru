package com.parser.processors;

import android.content.Context;

import java.io.InputStream;

public class PosterFeedProcessor extends Processor {
    private Context mContext;

    public PosterFeedProcessor(Context context){
        mContext = context;

    }


    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
        return 0;
    }
}
