package com.parser.bo;

public class NewsDetailItem {

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
    private String mPost_url;
    private String mAkismet;
    private String mAk_js;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setAkismet(String akismet){
        mAkismet = akismet;
    }

    public String getAkismet(){
        return mAkismet;
    }

    public String getAk_js(){
        return mAk_js;
    }

    public void setAk_js(String ak_js){
        mAk_js = ak_js;
    }

    public String getPostUrl(){
        return mPost_url;
    }

    public void setPostUrl(String postUrl){
        mPost_url = postUrl;
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

    public void setHeight(String height){
        mImageHeight = Integer.parseInt(height);
    }

    public int getHeight(){
        return mImageHeight;
    }

    public void setWidth(String width){
        mImageWidth = Integer.parseInt(width);
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

    public void setKarmaUp(String ups){
        mKarma_up = Integer.parseInt(ups);
    }

    public int getKarmaDown(){
        return mKarma_down;
    }

    public void setkarmaDown(String downs){
        mKarma_down = Integer.parseInt(downs);
    }

    public String getAuthorImage(){
        return mAuthor_image;
    }

    public void setAuthorImage(String imageUrl){
        mAuthor_image = imageUrl;
    }



}
