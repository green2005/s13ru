package com.parser.blogio;

public interface RequestListener {
        public void onRequestDone(BlogConnector.QUERY_RESULT result, String errorMessage);
}
