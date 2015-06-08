package com.parser.processors;

import android.content.Context;
import android.text.TextUtils;

import com.parser.bo.PosterDetailItem;
import com.parser.db.PosterDetailDBHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PosterDetailProcessor extends Processor {
    private PosterDetailDBHelper mDBHelper;
    private Context mContext;

    private static final String TITLE_PREFIX = "<div class=\"afisha-hline\">\n";
    private static final String TITLE_SUFFIX = "\n</div>";
    private static final String INFONAME_PREF = "<div class=\"view-event-field_name\">";
    private static final String INFONAME_POSTF = "</div>";
    private static final String INFOVALUE_PREF = "<div class=\"view-event-field_text\">";
    private static final String DESCRIPTION_PREF = "<div class=\"view-event-descr\">";
    private static final String DESCRIPTION_POSTF = "</div>";


    public PosterDetailProcessor(Context context) {
        this.mContext = context;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        if (mDBHelper == null) {
            mDBHelper = new PosterDetailDBHelper();
        }
        List<PosterDetailItem> posterItems = getPosterItems(stream);
        if (posterItems.size() > 0) {
            mDBHelper.clearOldEntries(mContext);
            mDBHelper.bulkInsert(posterItems, mContext, url);
        }
        return posterItems.size();
    }

    private List<PosterDetailItem> getPosterItems(InputStream stream) throws Exception {
        String response = getStringFromStream(stream, UTF8_CHARSET);
        List<PosterDetailItem> items = new ArrayList<>();

        Pattern pTitle = Pattern.compile(TITLE_PREFIX + ".*?" + TITLE_SUFFIX);

        Pattern pInfoName = Pattern.compile(INFONAME_PREF + ".*?" + INFONAME_POSTF);
        Pattern pInfoValue = Pattern.compile(INFOVALUE_PREF + ".*?" + INFONAME_POSTF);


        Pattern pDescription = Pattern.compile(DESCRIPTION_PREF +".*?"+DESCRIPTION_POSTF);

        Pattern pPoster = Pattern.compile("<div class=\"view-event-poster\">\n" +
                ".*?" + "</div>");
        Pattern pPosterImage = Pattern.compile("<img src=\"" + ".*?" + "\"");
        Pattern pPreview = Pattern.compile("<div class=\"view-event-trailer\">\n" +
                ".*?</div>");
        Pattern pPreviewSource = Pattern.compile("src=\".*?\"");

        Pattern pTimeAndPlace = Pattern.compile("<div class=\"view-event-showtime-item\">\n" +
                ".*?" + "<div class=\"view-event-showtime-item\">\n");

        Pattern pPlace = Pattern.compile("<div class=\"view-event-showtime-place\">.*?</div>");
        Pattern pDate = Pattern.compile("<div class=\"view-event-showtime-date\">.*?</div>");
        Pattern pTime = Pattern.compile("<div class=\"view-event-showtime-cif\">.*?</div>");


        Matcher mTitle = pTitle.matcher(response);
        if (mTitle.find()) {
            PosterDetailItem item = new PosterDetailItem();
            item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.TITLE.ordinal());
            String title = mTitle.group().substring(TITLE_PREFIX.length());
            title = title.substring(0, title.length() - TITLE_SUFFIX.length());
            item.setItemText(title);
            items.add(item);
        }

        Map<Integer, String> infos = new HashMap<>();
        int i = 0;
        Matcher mInfoName = pInfoName.matcher(response);
        while (mInfoName.find()) {
            String infoName = mInfoName.group().substring(INFONAME_PREF.length());
            infoName = infoName.substring(0, infoName.length() - INFONAME_POSTF.length());
            infos.put(i++, infoName);
        }

        i = 0;
        Matcher mInfoValue = pInfoValue.matcher(response);
        while (mInfoValue.find()) {
            String infoValue = mInfoValue.group().substring(INFOVALUE_PREF.length());
            infoValue = infoValue.substring(0, infoValue.length() - INFONAME_POSTF.length());
            String info = infos.get(i++);
            if (!TextUtils.isEmpty(info)){
                info = info + infoValue;
                PosterDetailItem item = new PosterDetailItem();
                item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal());
                item.setItemText(info);
            }
        }

        Matcher mDescription = pDescription.matcher(response);
        if (mDescription.find()){
            String description = mDescription.group().substring(DESCRIPTION_PREF.length());
            description = description.substring(0, description.length() - DESCRIPTION_POSTF.length());
            PosterDetailItem item = new PosterDetailItem();
            item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal());
            item.setItemText(description);
        }

        


        return items;
    }


}
