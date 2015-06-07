package com.parser.bo;

public class PosterDetailItem {
    /*
      public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String PLACE_COLUMN = "place";
    public static final String URL_COLUMN = "url";
    public static final String CONTENT_TYPE_COLUMN = "content_type";
     */
    private String mText;
    private String mDate;
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
