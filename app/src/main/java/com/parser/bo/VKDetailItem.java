package com.parser.bo;

public class VKDetailItem {
    /*
     POST_ID + " text, " +
            COMMENT_ID + " text," +
            TEXT + " text, " +
            ITEM_TYPE + " integer," +
            AUTHOR_NAME + "text, " +
            AUTHOR_ID + "text, " +
            AUTHOR_IMAGE + " text, " +
            DATE + " text " +
     */
    public enum ItemType{
        CONTENT,
        COMMENT,
        ATTACHMENT,
        DELIMITER
    }


    private String mPostId;
    private String mText;
    private int mItemType;
    private String mAuthorName;
    private String mAuthorId;
    private String mAuthorImage;
    private String mDate;
    private String mCommentId;

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
}
