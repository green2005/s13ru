package com.parser.processors;

import android.content.Context;

import com.parser.bo.NewsDetailItem;
import com.parser.db.NewsDetailDBHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsDetailProcessor extends Processor {
    private NewsDetailDBHelper mDBHelper;
    private Context mContext;
    private String mUrl;

    public NewsDetailProcessor(String url, Context context) {
        mContext = context;
        mUrl = url;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        String response = getStringFromStream(stream, UTF8_CHARSET);
        if (mDBHelper == null) {
            mDBHelper = new NewsDetailDBHelper();
        }
        List<NewsDetailItem> items = new ArrayList<>();
        parseResponse(response, items);
        mDBHelper.clearOldEntries(mContext);
        mDBHelper.bulkInsert(items, mContext);
        return items.size();
    }

    private void parseResponse(String response, List<NewsDetailItem> items) {

        //its awful without API

        Pattern pText = Pattern.compile("class=\"itemtext\".*?<script");
        Pattern pTitle = Pattern.compile(" dc:title=\".*?\"");
        Pattern pDate = Pattern.compile("class=\"metadata\">.*?</a>");
        Pattern pDate2 = Pattern.compile("</strong>.*?<");
        Pattern pImage = Pattern.compile("<a href=\".*?" + "</a>");
        Pattern pImage2 = Pattern.compile("<img class=.*?/>");
        Pattern pImageUrl = Pattern.compile("src=\".*?\"");

        Pattern pComment = Pattern.compile("li class=\" item\" id=\"comment.*?</li>");
        Pattern pAuthor = Pattern.compile("class=\"commentauthor\".*?</span>");

        Pattern pAuthorName1 = Pattern.compile("'>.*?</span>");
        Pattern pAuthorName2 = Pattern.compile("/>.*?</span>");
        Pattern pAuthorImage = Pattern.compile("src='.*?'");

        Pattern pCommentText = Pattern.compile("<span id=\"co_.*?</span>");
        Pattern pCommentId = Pattern.compile("id=\"co_.*?\"");
        Pattern pCommentTex2t = Pattern.compile(">.*?</span>");

        Pattern pThumbsDown = Pattern.compile("alt=\"Thumb down\".*?</span>");
        Pattern pThumbsUp = Pattern.compile("alt=\"Thumb up\".*?</span>");
        Pattern pThumb2 = Pattern.compile("\">.*?</span>");

        Pattern pCommentDate = Pattern.compile("<small>.*?</small>");
        Pattern pWidth = Pattern.compile( "width=\".*?\"");
        Pattern pHeight = Pattern.compile("height=\".*?\"");

        Matcher itemMatcher;

        itemMatcher = pDate.matcher(response);
        String date = "";
        if (itemMatcher.find()) {
            itemMatcher = pDate2.matcher(itemMatcher.group());
            if (itemMatcher.find()) {
                date = itemMatcher.group().substring(10);
                date = date.substring(0,date.length() - 2);
            }
        }

        Matcher mTitle = pTitle.matcher(response);
        if (mTitle.find()) {
            String itemText = mTitle.group().substring(" dc:title=\"".length()).replace("\"", "");
            NewsDetailItem item = new NewsDetailItem();
            item.setText(itemText);
            item.setDate(date);
            item.setContentType(NewsDetailDBHelper.NewsItemType.TITLE.ordinal());
            item.setPostId(mUrl);
            items.add(item);
        }

        Matcher mText = pText.matcher(response);
        int imageEnd = 0;
        int textStart;
        if (mText.find()) {
            String text = mText.group().substring("class=\"itemtext\"".length()).replace("<script", "").substring(1);
            Matcher mImage = pImage.matcher(text);
            while (mImage.find()) {
                Matcher mImage2 = pImage2.matcher(mImage.group());
                if (mImage2.find()) {
                    int textEnd = mImage.start();
                    textStart = imageEnd;
                    imageEnd = mImage.end();
                    String itemText = text.substring(textStart, textEnd);
                    NewsDetailItem item = new NewsDetailItem();
                    item.setText(itemText);
                    item.setContentType(NewsDetailDBHelper.NewsItemType.TEXT.ordinal());
                    item.setPostId(mUrl);
                    items.add(item);
                    //  text = text.substring(imageEnd);
                    Matcher mImageUrl = pImageUrl.matcher(mImage2.group());
                    Matcher mWidth = pWidth.matcher(mImage2.group());
                    Matcher mHeight = pHeight.matcher(mImage2.group());

                    if (mImageUrl.find()) {
                        String imageUrl = mImageUrl.group().substring("src=\"".length()).replace("\"", "");
                        NewsDetailItem imageItem = new NewsDetailItem();
                        imageItem.setText(imageUrl);
                        imageItem.setContentType(NewsDetailDBHelper.NewsItemType.IMAGE.ordinal());
                        if (mWidth.find()){
                            String width = mWidth.group().substring(7);
                            width = width.substring(0, width.length() - 1);
                            imageItem.setWidth(width);
                        }
                        if (mHeight.find()){
                            String height = mHeight.group().substring(8);
                            height = height.substring(0, height.length() - 1);
                            imageItem.setHeight(height);
                        }
                        imageItem.setPostId(mUrl);
                        items.add(imageItem);
                    }
                }
            }
            NewsDetailItem item = new NewsDetailItem();
            text = text.substring(imageEnd);
            item.setText(text);
            item.setContentType(NewsDetailDBHelper.NewsItemType.TEXT.ordinal());
            item.setPostId(mUrl);
            items.add(item);
        }

        NewsDetailItem item = new NewsDetailItem();
        item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal());
        item.setPostId(mUrl);
        items.add(item);

        Matcher mComment = pComment.matcher(response);
        while (mComment.find()) {
            item = new NewsDetailItem();
            item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY.ordinal());
            item.setPostId(mUrl);
            items.add(item);

            String comment = mComment.group();

            Matcher mAuthor = pAuthor.matcher(comment);
            if (mAuthor.find()) {
                String author = mAuthor.group();
                Matcher mImage = pAuthorImage.matcher(author);
                if (mImage.find()) {
                    String authorImage = mImage.group().substring(("src='").length()).replace("'", "");
                    authorImage = authorImage.replace("#038;", "");
                    item.setAuthorImage(authorImage);
                }
                mAuthor = pAuthorName1.matcher(author);
                if (mAuthor.find()) {
                    author = mAuthor.group().substring(2);
                    author = author.substring(0, author.length() - 4);
                } else {
                    mAuthor = pAuthorName2.matcher(author);
                    while (mAuthor.find()) {
                        author = mAuthor.group().substring(8);
                        mAuthor = pAuthorName2.matcher(author);
                    }
                }
                int authorLen = author.length();
                if (authorLen > 7){
                 author = author.substring(0,author.length()-7);
                }
                item.setAuthor(author);
            }
            Matcher mCommentText = pCommentText.matcher(comment);
            if (mCommentText.find()) {
                mCommentText = pCommentTex2t.matcher(mCommentText.group());
                if (mCommentText.find()) {
                    item.setText(mCommentText.group().substring(1));
                }
            }
            Matcher mCommentId = pCommentId.matcher(comment);
            if (mCommentId.find()) {
                item.setCommentId(mCommentId.group());
            }
            Matcher mCommentDate = pCommentDate.matcher(comment);
            if (mCommentDate.find()) {
                String commentDate = mCommentDate.group().substring(7);
                commentDate = commentDate.substring(0, commentDate.length() - 8);
                item.setDate(commentDate);
            }
            Matcher mThumb = pThumbsUp.matcher(comment);
            String thumbs;
            if (mThumb.find()) {
                mThumb = pThumb2.matcher(mThumb.group());
                if (mThumb.find()) {
                    thumbs = mThumb.group().substring(2);
                    thumbs = thumbs.substring(0, thumbs.length() - 7);
                    item.setKarma_up(thumbs);
                }
            }
            mThumb = pThumbsDown.matcher(comment);
            if (mThumb.find()) {
                mThumb = pThumb2.matcher(mThumb.group());
                if (mThumb.find()) {
                    thumbs = mThumb.group().substring(2);
                    thumbs = thumbs.substring(0, thumbs.length() - 7);
                    item.setkarmaDown(thumbs);
                }
            }


        }


    }


}
