package com.parser.processors;

import java.io.InputStream;

public abstract class Processor {
    public abstract int process(InputStream stream, boolean isTopRequest) throws Exception;
}
