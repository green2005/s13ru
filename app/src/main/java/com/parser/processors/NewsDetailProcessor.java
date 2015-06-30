package com.parser.processors;

import android.content.Context;

import com.parser.bo.NewsDetailItem;
import com.parser.db.NewsDetailDBHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.parser.DataSource;

public class NewsDetailProcessor extends Processor {
    private static final String IMG_PREF = "<img "; //"<img class=";
    private static final String IMG_POSTF = "/>";
    private static final String IMG_URL_PREF = "src=\"";
    private static final String IMG_URL_POSTF = "\"";
    private static final String ITEM_TEXT_PREF = "class=\"itemtext\">";
    private static final String ITEM_TEXT_POSTF = "<script";
    private static final String TITLE_PREF = " dc:title=\"";
    private static final String TITLE_POSTF = "\"";
    private static final String WIDTH_PREF = "width=\"";
    private static final String WIDTH_POSTF = "\"";
    private static final String HEIGHT_PREF = "height=\"";
    private static final String HEIGHT_POSTF = "\"";
    private static final String THUMB_PREF = "\">";
    private static final String THUMB_POSTF = "</span>";
    private static final String COMMENTDATE_PREF = "<small>";
    private static final String COMMENTDATE_POSTF = "</small>";
    private static final String AUTHOR_IMAGE_PREF = "src='";
    private static final String AUTHOR_IMAGE_POSTF = "'";

    private static final String COMMENT_ID_PREFIX = "id=\"co_";
    private static final String COMMENT_ID_POSTFIX = "\"";

    private static final String CAN_CHANGE_KARMA_PREFIX = "ckratingKarma";

    private static final String DEFAULT_IMAGE_HEIGHT = "270";
    private static final String DEFAULT_IMAGE_WIDTH = "411";

    private NewsDetailDBHelper mDBHelper;
    private Context mContext;
    private String mUrl;

    public NewsDetailProcessor(String url, Context context) {
        mContext = context;
        mUrl = url;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        String response = getStringFromStream(stream, DataSource.UTF8_CHARSET);
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

        Pattern pText = Pattern.compile(ITEM_TEXT_PREF + ".*?" + ITEM_TEXT_POSTF);
        Pattern pTitle = Pattern.compile(TITLE_PREF + ".*?" + TITLE_POSTF);
        Pattern pDate = Pattern.compile("class=\"metadata\">.*?</a>");
        Pattern pDate2 = Pattern.compile("</strong>.*?<");
        Pattern pImage2 = Pattern.compile(IMG_PREF + ".*?" + IMG_POSTF);
        Pattern pImageUrl = Pattern.compile(IMG_URL_PREF + ".*?" + IMG_URL_POSTF);

        Pattern pComment = Pattern.compile("li class=\" item\" id=\"comment.*?</li>");
        Pattern pAuthor = Pattern.compile("class=\"commentauthor\".*?</span>");

        Pattern pAuthorName1 = Pattern.compile("'>.*?</span>");
        Pattern pAuthorName2 = Pattern.compile("/>.*?</span>");
        Pattern pAuthorImage = Pattern.compile(AUTHOR_IMAGE_PREF+".*?"+AUTHOR_IMAGE_POSTF);

        Pattern pCommentText = Pattern.compile("<span id=\"co_.*?</span>");
        Pattern pCommentId = Pattern.compile(COMMENT_ID_PREFIX + ".*?"+COMMENT_ID_POSTFIX);

        Pattern pCommentTex2t = Pattern.compile(">.*?</span>");

        Pattern pThumbsDown = Pattern.compile("alt=\"Thumb down\".*?</span>");
        Pattern pThumbsUp = Pattern.compile("alt=\"Thumb up\".*?</span>");
        Pattern pThumb2 = Pattern.compile(THUMB_PREF + ".*?" + THUMB_POSTF);

        Pattern pCommentDate = Pattern.compile(COMMENTDATE_PREF + ".*?" + COMMENTDATE_POSTF);
        Pattern pWidth = Pattern.compile(WIDTH_PREF + ".*?" + WIDTH_POSTF);
        Pattern pHeight = Pattern.compile(HEIGHT_PREF + ".*?" + HEIGHT_POSTF);

        Pattern pAkismet = Pattern
                .compile("id=\"akismet_comment_nonce\".*?/>");
        Pattern pValue = Pattern.compile("value=\".*?\"");

        Pattern pAk_js = Pattern
                .compile("id=\"ak_js\".*?/>");


        String akismet = "";
        String ak_js = "";
        String postId = "";
        Matcher makismet_comment = pAkismet.matcher(response);
        if (makismet_comment.find()) {
            Matcher mvalue = pValue.matcher(makismet_comment.group());
            if (mvalue.find()) {
                akismet = mvalue.group();
                akismet = akismet.replace("\"", "");
                akismet = akismet.replace("value=", "");
            }
        }

        Matcher mak_js = pAk_js.matcher(response);
        if (mak_js.find()) {
            Matcher mvalue = pValue.matcher(mak_js.group());
            if (mvalue.find()) {
                ak_js = mvalue.group();
                ak_js = ak_js.replace("\"", "");
                ak_js = ak_js.replace("value=", "");
            }
        }


        Pattern ppost_id = Pattern.compile("name=\"comment_post_ID\".*?/>");
        Matcher mpost_id = ppost_id.matcher(response);
        if (mpost_id.find()) {
            Matcher mvalue = pValue.matcher(mpost_id.group());
            if (mvalue.find()) {
                postId = mvalue.group();
                postId = postId.replace("\"", "");
                postId = postId.replace("value=", "");
            }
        }

        Matcher itemMatcher;
        itemMatcher = pDate.matcher(response);
        String date = "";
        if (itemMatcher.find()) {
            itemMatcher = pDate2.matcher(itemMatcher.group());
            if (itemMatcher.find()) {
                date = itemMatcher.group().substring(10);
                date = date.substring(0, date.length() - 2);
            }
        }

        Matcher mTitle = pTitle.matcher(response);
        if (mTitle.find()) {
            String itemText = mTitle.group().substring(TITLE_PREF.length()); //(" dc:title=\"".length()).replace("\"", "");
            itemText = itemText.substring(0, itemText.length() - TITLE_POSTF.length());
            NewsDetailItem item = new NewsDetailItem();
            item.setText(itemText);
            item.setDate(date);
            item.setContentType(NewsDetailDBHelper.NewsItemType.TITLE.ordinal());
            item.setPostUrl(mUrl);
            item.setCommentId(postId);
            item.setAkismet(akismet);
            item.setAk_js(ak_js);
            items.add(item);
        }

        Matcher mText = pText.matcher(response);
        int imageEnd = 0;
        int textStart;

        if (mText.find()) {
            String text = mText.group().substring(ITEM_TEXT_PREF.length()); //.substring("class=\"itemtext\"".length()).replace("<script", "").substring(1);
            text = text.substring(0, text.length() - ITEM_TEXT_POSTF.length());
            Matcher mImage = pImage2.matcher(text);
            while (mImage.find()) {
                int textEnd = mImage.start();
                textStart = imageEnd;
                imageEnd = mImage.end();
                String itemText = text.substring(textStart, textEnd);
                addTextItem(items, itemText);
                processImageItem(items, mImage.group(), pImageUrl, pWidth, pHeight);
            }
            text = text.substring(imageEnd);
            addTextItem(items, text);
        }


        NewsDetailItem item = new NewsDetailItem();
        item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY_HEADER.ordinal());
        item.setPostUrl(mUrl);
        items.add(item);

        Matcher mComment = pComment.matcher(response);
        while (mComment.find()) {
            item = new NewsDetailItem();
            item.setContentType(NewsDetailDBHelper.NewsItemType.REPLY.ordinal());
            item.setPostUrl(mUrl);
            items.add(item);

            String comment = mComment.group();

            if (comment.contains(CAN_CHANGE_KARMA_PREFIX)) {
                item.setCanChangeKarma(1);
            } else
            {
                item.setCanChangeKarma(0);
            }

            Matcher mAuthor = pAuthor.matcher(comment);
            if (mAuthor.find()) {
                String authorText = mAuthor.group();
                item.setAuthorImage(getAuthorImgage(authorText, pAuthorImage));
                item.setAuthor(getAuthorName(authorText, pAuthorName1, pAuthorName2));
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
                String commentId = mCommentId.group().substring(COMMENTDATE_PREF.length());
                commentId = commentId.substring(0, commentId.length() - COMMENT_ID_POSTFIX.length());
                item.setCommentId(commentId);
            }
            Matcher mCommentDate = pCommentDate.matcher(comment);
            if (mCommentDate.find()) {
                String commentDate = mCommentDate.group().substring(COMMENTDATE_PREF.length());
                commentDate = commentDate.substring(0, commentDate.length() - COMMENTDATE_POSTF.length());
                item.setDate(commentDate);
            }
            item.setKarmaUp(getKarma(comment, pThumbsUp, pThumb2));
            item.setkarmaDown(getKarma(comment, pThumbsDown, pThumb2));
        }
    }

    private String getAuthorName(String authorText, Pattern pAuthorName1, Pattern pAuthorName2){
        Matcher mAuthor = pAuthorName1.matcher(authorText);
        if (mAuthor.find()) {
            authorText = mAuthor.group().substring(2);
            authorText = authorText.substring(0, authorText.length() - 4);
        } else {
            mAuthor = pAuthorName2.matcher(authorText);
            while (mAuthor.find()) {
                authorText = mAuthor.group().substring(8);
                mAuthor = pAuthorName2.matcher(authorText);
            }
        }
        int authorLen = authorText.length();
        if (authorLen > 7) {
            authorText = authorText.substring(0, authorText.length() - 7);
        }
        return  authorText;
    }

    private String getAuthorImgage(String authorText, Pattern pAuthorImage){
        Matcher mImage = pAuthorImage.matcher(authorText);
        if (mImage.find()) {
            String authorImage = mImage.group().substring(AUTHOR_IMAGE_PREF.length()); //substring(("src='").length()).replace("'", "");
            authorImage = authorImage.substring(0, authorImage.length() - AUTHOR_IMAGE_POSTF.length());
            authorImage = authorImage.replace("#038;", "");
            return authorImage;
        }
        return "";
    }

    private String getKarma(String comment, Pattern pThumb, Pattern pThumb2){
        Matcher mThumb = pThumb.matcher(comment);
        if (mThumb.find()) {
            mThumb = pThumb2.matcher(mThumb.group());
            if (mThumb.find()) {
                String thumbs = mThumb.group().substring(THUMB_PREF.length());
                thumbs = thumbs.substring(0, thumbs.length() - THUMB_POSTF.length());
                return thumbs;
            }
        }
        return "0";
    }

    private void processImageItem(List<NewsDetailItem> items, String textPart, Pattern pImageUrl, Pattern pWidth, Pattern pHeight) {
        Matcher mImageUrl = pImageUrl.matcher(textPart);
        Matcher mWidth = pWidth.matcher(textPart);
        Matcher mHeight = pHeight.matcher(textPart);

        if (mImageUrl.find()) {
            String imageUrl = mImageUrl.group().substring(IMG_URL_PREF.length());
            imageUrl = imageUrl.substring(0, imageUrl.length() - IMG_URL_POSTF.length());
            NewsDetailItem imageItem = new NewsDetailItem();
            imageItem.setText(imageUrl);
            imageItem.setContentType(NewsDetailDBHelper.NewsItemType.IMAGE.ordinal());
            if (mWidth.find()) {
                String width = mWidth.group().substring(WIDTH_PREF.length());
                width = width.substring(0, width.length() - WIDTH_POSTF.length());
                imageItem.setWidth(width);
            } else
            {
                imageItem.setWidth(DEFAULT_IMAGE_WIDTH);
            }

            if (mHeight.find()) {
                String height = mHeight.group().substring(HEIGHT_PREF.length());
                height = height.substring(0, height.length() - HEIGHT_POSTF.length());
                imageItem.setHeight(height);
            } else
            {
                imageItem.setHeight(DEFAULT_IMAGE_HEIGHT);
            }
            imageItem.setPostUrl(mUrl);
            items.add(imageItem);
        }
    }

    private void addTextItem(List<NewsDetailItem> items, String text) {
        NewsDetailItem item = new NewsDetailItem();
        item.setText(text);
        item.setContentType(NewsDetailDBHelper.NewsItemType.TEXT.ordinal());
        item.setPostUrl(mUrl);
        items.add(item);
    }
}
