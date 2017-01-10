package com.parser.bo;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsFeedItem {
    private String mTitle;
    private String mImageUrl;
    private String mDate;
    private String mUrl;
    private String mId;
    private String mAuthor;
    private String mText;
    private String mImageWidth;
    private String mImageHeight;

    public NewsFeedItem(JSONObject post, String s13Name) {

        mUrl = post.optString("link");
        mText = post.optString("message");
        int j = mText.indexOf("\n");
        if (j > 1) {
            mText = mText.substring(0, j - 1);
        } else {
            mText = mText.replace(mUrl, "");
        }
        mId = post.optString("id");
        mDate = post.optString("created_time").replace("T", " ").replace("+0000", "");
        JSONObject jAttachment = post.optJSONObject("attachments");
        if (jAttachment != null) {
            JSONArray attachments = jAttachment.optJSONArray("data");
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.optJSONObject(i);
                mTitle = attachment.optString("title");
               if ((mTitle.length() > s13Name.length())&&(mTitle.contains("s13"))) { //2
                    mTitle = mTitle.substring(0, mTitle.length() - s13Name.length()); //1
                }
                JSONObject media = attachment.optJSONObject("media");
                if (media != null) {
                    JSONObject image = media.optJSONObject("image");
                    if (image != null) {
                        mImageWidth = image.optString("width");
                        mImageHeight = image.optString("height");
                        mImageUrl = image.optString("src");
                    }
                }
            }
        }
    }

    public String getTitle() {
        return mTitle;
    }


    public String getImageUrl() {
        return mImageUrl;
    }

    public String getImageWidth() {
        return mImageWidth;
    }

    public String getImageHeight() {
        return mImageHeight;
    }

    public String getDate() {
        return mDate;
    }


    public String getUrl() {
        return mUrl;
    }


    public String getId() {
        return mId;
    }


    public String getAuthor() {
        return mAuthor;
    }


    public String getText() {
        return mText;
    }


}
