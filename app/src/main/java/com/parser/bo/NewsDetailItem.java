package com.parser.bo;

public class NewsDetailItem {
    /*
     enum NewsItemType {
        TITLE,
        TEXT,
        IMAGE,
        REPLY_HEADER,
        REPLY
    }

    public static final String TABLE_NAME = "newsdetail";
    public static final String ID_COLUMN = "_id";
    public static final String RECORD_TYPE_COLUMN = "record_type";
    public static final String AUTHOR_COLUMN = "author";
    public static final String DATE_COLUMN = "date";
    public static final String TEXT_COLUMN = "text";
     */
    private int mRecordType;
    private String mText;
    private String mAuthor;
    private String mDate;
    private int mImageWidth;
    private int mImageHeight;

    private String mComment_id;
    private int mKarma_up;
    private int mKarma_down;
    private String mAuthor_image;


    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setContentType(int contentType) {
        mRecordType = contentType;
    }

    public int getContentType() {
        return mRecordType;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setHeight(int height){
        mImageHeight = height;
    }

    public int getHeight(){
        return mImageHeight;
    }

    public void setWidth(int width){
        mImageWidth = width;
    }

    public int getWidth(){
        return mImageWidth;
    }
    /*
      private String mComment_id;
    private int mKarma_up;
    private int mKarma_down;
    private String mAuthor_image;
     */

    public String getCommentId(){
        return mComment_id;
    }

    public void setCommentId(String id){
        mComment_id = id;
    }

    public int getmKarma_up(){
        return mKarma_up;
    }

    public void setKarma_up(int ups){
        mKarma_up = ups;
    }

    public int getKarmaDown(){
        return mKarma_down;
    }

    public void setkarmaDown(int downs){
        mKarma_down = downs;
    }

    public String getAuthorImage(){
        return mAuthor_image;
    }

    public void setAuthorImage(String imageUrl){
        mAuthor_image = imageUrl;
    }



}
