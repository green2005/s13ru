package com.parser.processors;

import java.io.InputStream;

public abstract class Processor {
    public abstract void process(InputStream stream) throws Exception;
}
