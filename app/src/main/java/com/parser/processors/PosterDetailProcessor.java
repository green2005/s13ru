package com.parser.processors;

import com.parser.db.PosterDetailDBHelper;

import java.io.InputStream;

public class PosterDetailProcessor extends Processor {
    private PosterDetailDBHelper mDBHelper;

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        String response = getStringFromStream(stream, UTF8_CHARSET);
        if (mDBHelper == null){
            mDBHelper = new PosterDetailDBHelper();
        }
        

        return 0;
    }


}
