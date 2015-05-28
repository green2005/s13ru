package com.parser.processors;

import android.content.Context;

import java.io.InputStream;

public class VKDetailsProcessor extends VKProcessor {
    private Context mContext;


    public VKDetailsProcessor(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
        return 0;
    }
}
