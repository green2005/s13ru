package com.parser.processors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class Processor {
    public abstract int process(InputStream stream, boolean isTopRequest) throws Exception;

    protected String getStringFromStream(InputStream stream) throws Exception {
        InputStreamReader is = new InputStreamReader(stream);
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
