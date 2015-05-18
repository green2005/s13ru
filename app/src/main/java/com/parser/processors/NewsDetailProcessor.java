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

    public NewsDetailProcessor(Context context) {
        mContext = context;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
        String response = getStringFromStream(stream, UTF8_CHARSET);
        if (mDBHelper == null) {
            mDBHelper = new NewsDetailDBHelper();
        }
        List<NewsDetailItem> items = new ArrayList<>();
        parseResponse(response, items);
        mDBHelper.clearOldEntries(mContext);
        return mDBHelper.bulkInsert(items, mContext);
        //return 0;
    }

    private void parseResponse(String response, List<NewsDetailItem> items) {

        //its awful without API

        Pattern pText = Pattern.compile("class=\"itemtext\".*?<script");
        Pattern pTitle = Pattern.compile(" dc:title=\".*?\"");
        Pattern pDate = Pattern.compile("class=\"metadata\">.*?</a>");
        Pattern pDate2 = Pattern.compile("</strong>" + ". * ?" + "</a>");
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

        Matcher itemMatcher;

        itemMatcher = pDate.matcher(response);
        String date = "";
        if (itemMatcher.find()) {
            itemMatcher = pDate2.matcher(itemMatcher.group());
            if (itemMatcher.find()) {
                date = itemMatcher.group().substring("</strong>".length()).replace("</a>", "");
            }
        }

        Matcher mTitle = pTitle.matcher(response);
        if (mTitle.find()) {
            String itemText = mTitle.group().substring(" dc:title=\"".length()).replace("\"", "");
            NewsDetailItem item = new NewsDetailItem();
            item.setText(itemText);
            item.setDate(date);
            item.setContentType(NewsDetailDBHelper.NewsItemType.TITLE.ordinal());
            items.add(item);
        }

        Matcher mText = pText.matcher(response);
        int imageEnd = 0;
        int textStart;
        if (mText.find()) {
            String text = mText.group().substring("class=\"itemtext\"".length()).replace("<script", "");
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
                    items.add(item);
                    //  text = text.substring(imageEnd);
                    Matcher mImageUrl = pImageUrl.matcher(mImage2.group());
                    if (mImageUrl.find()) {
                        String imageUrl = mImageUrl.group().substring("src=\"".length()).replace("\"", "");
                        NewsDetailItem imageItem = new NewsDetailItem();
                        imageItem.setText(imageUrl);
                        imageItem.setContentType(NewsDetailDBHelper.NewsItemType.IMAGE.ordinal());
                        items.add(imageItem);
                    }
                }
            }
            NewsDetailItem item = new NewsDetailItem();
            text = text.substring(imageEnd);
            item.setText(text);
            item.setContentType(NewsDetailDBHelper.NewsItemType.TEXT.ordinal());
            items.add(item);
        }

        NewsDetailItem item = new NewsDetailItem();
        item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal());
        items.add(item);

        Matcher mComment = pComment.matcher(response);
        while (mComment.find()) {
            item = new NewsDetailItem();
            item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY.ordinal());
            items.add(item);

            String comment = mComment.group();

            Matcher mAuthor = pAuthor.matcher(comment);
            if (mAuthor.find()) {
                String author = mAuthor.group();
                Matcher mImage = pAuthorImage.matcher(author);
                if (mImage.find()) {
                    item.setAuthorImage(mImage.group().substring(("src='").length()).replace("'", ""));
                }
                mAuthor = pAuthorName1.matcher(author);
                if (mAuthor.find()) {
                    item.setAuthor(mAuthor.group());
                } else {
                    mAuthor = pAuthorName2.matcher(author);
                    if (mAuthor.find()) {
                        item.setAuthor(mAuthor.group());
                    }
                }
            }
            Matcher mCommentText = pCommentText.matcher(comment);
            if (mCommentText.find()) {
                mCommentText = pCommentTex2t.matcher(mCommentText.group());
                if (mCommentText.find()) {
                    item.setText(mCommentText.group());
                }
            }
            Matcher mCommentId = pCommentId.matcher(comment);
            if (mCommentId.find()) {
                item.setCommentId(mCommentId.group());
            }
            Matcher mCommentDate = pCommentDate.matcher(comment);
            if (mCommentDate.find()) {
                item.setDate(mCommentDate.group());
            }
            Matcher mThumb = pThumbsUp.matcher(comment);
            if (mThumb.find()) {
                mThumb = pThumb2.matcher(mThumb.group());
                if (mThumb.find()) {
                    item.setKarma_up(mThumb.group().length());
                }
            }
            mThumb = pThumbsDown.matcher(comment);
            if (mThumb.find()) {
                mThumb = pThumb2.matcher(mThumb.group());
                if (mThumb.find()) {
                    item.setkarmaDown(mThumb.group().length());
                }
            }


        }


    }

}
