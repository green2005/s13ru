package com.parser.bo;

public class VKDetailItem {

    public enum ItemType{
        CONTENT,
        COMMENT,
        ATTACHMENT_PHOTO,
        ATTACHMENT_VIDEO,
        DELIMITER
    }


    private String mPostId = "";
    private String mText;
    private int mItemType;
    private String mAuthorName;
    private String mAuthorId;
    private String mAuthorImage;
    private String mDate;
    private String mCommentId = "";
    private int mWidth;
    private int mHeight;

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        mPostId = postId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int itemType) {
        mItemType = itemType;
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public void setAuthorName(String authorName) {
        mAuthorName = authorName;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(String authorId) {
        mAuthorId = authorId;
    }

    public String getAuthorImage() {
        return mAuthorImage;
    }

    public void setAuthorImage(String userPickUrl) {
        mAuthorImage = userPickUrl;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public void setCommentId(String commentId) {
        mCommentId = commentId;
    }

    public void setWidth(int width){
        mWidth = width;
    }

    public void setHeight(int height){
        mHeight = height;
    }

    public int getHeight(){
        return mHeight;
    }

    public int getWidth(){
        return mWidth;
    }
}
