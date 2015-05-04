package com.parser.bo;


import android.content.Context;

import org.json.JSONObject;

import java.text.DateFormat;

public class VKFeedItem {
    private String mImageUrl;
    private String mText;
    private DateFormat mDf;
    private JSONObject mJo;

    public VKFeedItem(DateFormat df, JSONObject jo) {
        mDf = df;
        mJo = jo;
    }

    public VKFeedItem(Context context, JSONObject jo) {
        mDf = android.text.format.DateFormat.getDateFormat(context);
        mJo = jo;
    }


    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }


    //-------------------------------------------
    public String getPostId() {
        return mJo.optString("id");
    }

    public String getDate() {
        String date = mJo.optString("date");
        return new java.util.Date(Long.parseLong(date) * 1000).toString();
    }

    public String getTitle() {
        return mJo.optString("title");
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDescription() {
        return mJo.optString("description");
    }

    public String getText() {
        return mJo.optString("text");
    }
}