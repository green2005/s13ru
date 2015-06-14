package com.parser.processors;

import android.content.Context;

import com.parser.R;
import com.parser.bo.PosterFeedItem;
import com.parser.db.PosterFeedDBHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PosterFeedProcessor extends Processor {
    private Context mContext;
    private PosterFeedDBHelper mDbHelper;
    private static final String KINO_URL_PART = "kino";
    private static final String PARTY_URL_PART = "party";
    private static final String CONCERTS_URL_PART = "concerts";
    private static final String THEATRE_URL_PART = "theatre";
    private static final String EXHIBITION_URL_PART = "exhibition";
    private static final String EVENT_URL_PART = "event";
    private static final String SPORT_URL_PART = "sport";


    public PosterFeedProcessor(Context context) {
        mContext = context;
        mDbHelper = new PosterFeedDBHelper();
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        List<PosterFeedItem> items = getPosters(stream);
        if (items != null && items.size() > 0) {
            mDbHelper.clearOldEntries(mContext);
            mDbHelper.bulkInsert(items, mContext);
            return items.size();
        } else
            return 0;
    }

    private List<PosterFeedItem> getPosters(InputStream stream) throws Exception {
        List<PosterFeedItem> items = new ArrayList<>();
        String response = getStringFromStream(stream, WIN_CHARSET);
        Pattern pItem = Pattern.compile("class=\"home-events-item-poster\">.*?</a></div>");
        Pattern pLink = Pattern.compile("<a href=\".*?\"");
        Pattern pDate = Pattern.compile("\"home-events-item-date\">.*?</div>");
        Pattern pTitle = Pattern.compile("title=\".*?\"");
        Pattern pImage = Pattern.compile("<img src=\".*?\"");
        Matcher mItem = pItem.matcher(response);
        while (mItem.find()) {
            PosterFeedItem posterItem = new PosterFeedItem();
            String item = mItem.group().substring("class=\"home-events-item-poster\">".length());
            Matcher mLink = pLink.matcher(item);
            if (mLink.find()) {
                posterItem.setUrl(mLink.group().substring("<a href=\"".length()).replace("\"", ""));
            }
            Matcher mDate = pDate.matcher(item);
            if (mDate.find()) {
                posterItem.setDate(mDate.group().substring("\"home-events-item-date\">".length()).replace("</div>", ""));
            }
            Matcher mTitle = pTitle.matcher(item);
            if (mTitle.find()) {
                String title = mTitle.group().substring("title=".length()).replace("\"", "");
                posterItem.setTitle(title);
            }

            Matcher mImage = pImage.matcher(item);
            if (mImage.find()) {
                posterItem.setImageUrl(mImage.group().substring("<img src=\"".length()).replace("\"", ""));
            }
            String cat = mContext.getString(getPosterCatByUrlPart(posterItem.getUrl()));
            posterItem.setCat(cat);
            items.add(posterItem);
        }
        return items;
    }

    private int getPosterCatByUrlPart(String url) {
        if (url.contains(KINO_URL_PART)) {
            return R.string.poster_cinema;
        } else if (url.contains(PARTY_URL_PART)) {
            return R.string.poster_party;
        } else if (url.contains(CONCERTS_URL_PART)) {
            return R.string.poster_concerts;
        } else if (url.contains(THEATRE_URL_PART)) {
            return R.string.poster_theatre;
        } else if (url.contains(EXHIBITION_URL_PART)) {
            return R.string.poster_exhibition;
        } else if (url.contains(EVENT_URL_PART)) {
            return R.string.poster_event;
        } else if (url.contains(SPORT_URL_PART)){
            return R.string.poster_sport;
        } else
        {
            return R.string.poster_event;
        }
    }

}
