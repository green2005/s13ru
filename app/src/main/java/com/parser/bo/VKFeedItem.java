package com.parser.bo;
import android.content.Context;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class VKFeedItem {
    private String mImageUrl;
    private String mText;
    private DateFormat mDf;
    private JSONObject mJo;

    public VKFeedItem(DateFormat df, JSONObject jo) {
        //mDf = df;
        mDf = new SimpleDateFormat("dd.MM.yyyy");
        mJo = jo;
    }

    public VKFeedItem(Context context, JSONObject jo) {
        mDf = new SimpleDateFormat("dd.MM.yyyy");
        // mDf = android.text.format.DateFormat.getDateFormat(context);
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
        return mDf.format(new java.util.Date(Long.parseLong(date) * 1000));
    }

    public String getTitle() {
        return mJo.optString("title");
    }

    public String getImageUrl() {
        JSONObject attachmentJson = mJo.optJSONObject("attachment");
        if (attachmentJson == null){
            return "";
        }
        JSONObject photoJson = attachmentJson.optJSONObject("photo");
        if (photoJson != null){
            return photoJson.optString("src_big");
        }
        JSONObject albumJson = attachmentJson.optJSONObject("album");
        if (albumJson != null){
            JSONObject thumbJson = albumJson.optJSONObject("thumb");
            if (thumbJson != null){
                return thumbJson.optString("src_big");
            }
        }
        return "";
    }

    public String getDescription() {
        return mJo.optString("description");
    }

    public String getText() {
        return mJo.optString("text");
    }
}
