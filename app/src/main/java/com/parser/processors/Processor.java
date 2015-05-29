package com.parser.processors;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Processor {
    protected static final String WIN_CHARSET = "windows-1251";
    protected static final String UTF8_CHARSET = "UTF-8";

    public abstract int process(InputStream stream, boolean isTopRequest, String url) throws Exception;

    protected String getStringFromStream(InputStream stream, String decodingCharset) throws Exception {
        InputStreamReader is;
        if (!TextUtils.isEmpty(decodingCharset)) {
            is = new InputStreamReader(stream, decodingCharset);
        } else {
            is = new InputStreamReader(stream);
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();
        while (read != null) {
            //System.out.println(read);
            sb.append(read);
            read = br.readLine();
        }
        return sb.toString();
    }
}
