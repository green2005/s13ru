package com.parser.bo;

public class PosterDetailItem {

    private String mText;
    private String mDate;
    private String mTime;
    private String mPlace;
    private String mUrl;
    private int mContentType;

    public int getContentType() {
        return mContentType;
    }

    public void setContentType(int contentType) {
        this.mContentType = contentType;
    }

    public String getItemText() {
        return mText;
    }

    public void setItemText(String text) {
        this.mText = text;
    }

    public String getItemDate() {
        return mDate;
    }

    public String getItemTime() {
        return mTime;
    }

    public void setItemTime(String time) {
        mTime = time;
    }

    public void setItemDate(String date) {
        this.mDate = date;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        this.mPlace = place;
    }

    public String getItemUrl() {
        return mUrl;
    }

    public void setItemUrl(String url) {
        this.mUrl = url;
    }


}
