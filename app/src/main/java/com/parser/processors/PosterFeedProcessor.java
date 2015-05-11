package com.parser.processors;

import android.content.Context;

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


    public PosterFeedProcessor(Context context) {
        mContext = context;
        mDbHelper = new PosterFeedDBHelper();
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
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
            if (posterItem.getUrl().contains("kino")) {
                posterItem.setCat("Кино");
            } else if (posterItem.getUrl().contains("party")) {
                posterItem.setCat("Вечеринки");
            } else if (posterItem.getUrl().contains("concerts")) {
                posterItem.setCat("Концерты");
            } else if (posterItem.getUrl().contains("theatre")) {
                posterItem.setCat("Спектакли");
            } else if (posterItem.getUrl().contains("exhibition")) {
                posterItem.setCat("Выставки");
            } else if (posterItem.getUrl().contains("event")) {
                posterItem.setCat("События");
            }
            items.add(posterItem);
        }
        return items;
    }
}
