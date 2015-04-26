package com.parser.processors;

import android.content.Context;
import android.util.Xml;

import com.parser.bo.NewsFeedItem;
import com.parser.db.NewsFeedDBHelper;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedProcessor extends Processor {
    private XmlPullParser mParser;
    private NewsFeedDBHelper mDBHelper;

    private static final String NS = null;
    private static final String RSS_HEADER = "rss";
    private static final String ITEM_HEADER = "item";
    private static final String ITEM_TITLE = "title";
    private static final String ITEM_DESCRIPTION = "description";
    private static final String ITEM_LINK = "link";
    private static final String ITEM_DATE = "pubdate";
    private static final String ITEM_AUTHOR = "dc:creator";

    private Context mContext;

    public NewsFeedProcessor(Context context) {
        mParser = Xml.newPullParser();
        mDBHelper = new NewsFeedDBHelper();
        mContext = context;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
        mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        mParser.setInput(stream, null);
        mParser.nextTag();
        List<NewsFeedItem> feedItems = parseResponse();
        if (isTopRequest) {
            mDBHelper.clearOldEntries(mContext);
        }
        mDBHelper.bulkInsert(feedItems, mContext);
        return feedItems.size();
    }

    private List<NewsFeedItem> parseResponse() throws Exception {
        List<NewsFeedItem> entries = new ArrayList<>();
        mParser.require(XmlPullParser.START_TAG, NS, RSS_HEADER);

        while (mParser.next() != XmlPullParser.END_DOCUMENT) {
            if (mParser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = mParser.getName();
            if (name.equals(ITEM_HEADER)) {
                entries.add(readEntry());
            }
        }
        return entries;
    }

    private NewsFeedItem readEntry() throws Exception {
        NewsFeedItem feedItem = new NewsFeedItem();
        int xmlTag = mParser.next();
        String name = "";
        String endName = "";
        while (!(ITEM_HEADER.equalsIgnoreCase(endName))) {
            switch (xmlTag) {
                case (XmlPullParser.START_TAG): {
                    name = mParser.getName();
                    break;
                }
                case (XmlPullParser.END_TAG): {
                    endName = mParser.getName();
                    name = "";
                    break;
                }
            }

            if (xmlTag == XmlPullParser.TEXT) {
                switch (name) {
                    case ITEM_TITLE: {
                        feedItem.setTitle(mParser.getText());
                        break;
                    }
                    case ITEM_DESCRIPTION: {
                        feedItem.setText(mParser.getText());
                        break;
                    }
                    case ITEM_LINK: {
                        feedItem.setUrl(mParser.getText());
                        break;
                    }
                    case ITEM_DATE: {
                        feedItem.setDate(mParser.getText());
                        break;
                    }
                    case ITEM_AUTHOR: {
                        feedItem.setAuthor(mParser.getText());
                        break;
                    }
                }
            }
            xmlTag = mParser.next();
        }
        return feedItem;
    }
}
