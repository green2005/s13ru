package com.parser.bo;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class VKFeedItem {
    private DateFormat mDf;
    private JSONObject mJo;
    private String mImageUrl;
    private int mImageWidth;
    private int mImageHeight;

    public VKFeedItem(JSONObject jo) {
        mDf = new SimpleDateFormat("dd.MM.yyyy");
        mJo = jo;
        processImageAttachment();
    }

    //-------------------------------------------
    public String getPostId() {
        return mJo.optString("id");
    }

    public String getDate() {
        String date = mJo.optString("date");
        return mDf.format(new java.util.Date(Long.parseLong(date) * 1000));
    }

    public String getTitle() {
        return mJo.optString("title");
    }

    public void processImageAttachment() {
        JSONObject attachmentJson = mJo.optJSONObject("attachment");
        if (attachmentJson == null){
            return;
        }
        JSONObject photoJson = attachmentJson.optJSONObject("photo");
        if (photoJson != null) {
            mImageUrl = photoJson.optString("src_big");
            mImageWidth = photoJson.optInt("width");
            mImageHeight = photoJson.optInt("height");
        }
        JSONObject albumJson = attachmentJson.optJSONObject("album");
        if (albumJson != null) {
            JSONObject thumbJson = albumJson.optJSONObject("thumb");
            if (thumbJson != null) {
                mImageUrl = thumbJson.optString("src_big");
                mImageWidth = thumbJson.optInt("width");
                mImageHeight = thumbJson.optInt("height");
            }
        }
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getImageWidth(){
        return mImageWidth;
    }

    public int getImageHeight(){
        return mImageHeight;
    }


    public String getDescription() {
        return mJo.optString("description");
    }

    public String getText() {
        return mJo.optString("text");
    }
}
