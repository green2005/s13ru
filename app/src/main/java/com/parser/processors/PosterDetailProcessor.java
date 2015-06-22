package com.parser.processors;

import android.content.Context;
import android.text.TextUtils;

import com.parser.DataSource;
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

    private static final String TITLE_PREFIX = "<title>";
    private static final String TITLE_SUFFIX = "</title>";
    private static final String INFONAME_PREF = "<div class=\"view-event-field_name\">";
    private static final String END_DIV = "</div>";
    private static final String INFOVALUE_PREF = "<div class=\"view-event-field_text\">";
    private static final String DESCRIPTION_PREF = "<div class=\"view-event-descr\">";
    private static final String IMG_PREFIX = "<img src=\"";
    private static final String IMG_POSTF = "\"";

    private static final String PLACE_PREF = "<div class=\"view-event-showtime-place\">";
    private static final String DATE_PREF = "<div class=\"view-event-showtime-date\">";
    private static final String TIME_PREF = "<div class=\"view-event-showtime-cif\">";

    private static final String PREVIEW_PREF = "src=\"";
    private static final String PREVIEW_POSTF = "\"";


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
        String response = getStringFromStream(stream, DataSource.WIN_CHARSET);
        List<PosterDetailItem> items = new ArrayList<>();
        Pattern pTitle = Pattern.compile(TITLE_PREFIX + ".*?" + TITLE_SUFFIX);
        Pattern pInfoName = Pattern.compile(INFONAME_PREF + ".*?" + END_DIV);
        Pattern pInfoValue = Pattern.compile(INFOVALUE_PREF + ".*?" + END_DIV);

        Pattern pDescription = Pattern.compile(DESCRIPTION_PREF + ".*?" + END_DIV);

        Pattern pPoster = Pattern.compile("<div class=\"view-event-poster\">" +
                ".*?" + "</div>");
        Pattern pPosterImage = Pattern.compile(IMG_PREFIX + ".*?" + IMG_POSTF);
        Pattern pPreview = Pattern.compile("<div class=\"view-event-trailer\">" +
                ".*?</div>");
        Pattern pPreviewSource = Pattern.compile(PREVIEW_PREF + ".*?" + PREVIEW_POSTF);
        Pattern pTimeAndPlace = Pattern.compile("<div class=\"view-event-showtime-item\">\n" +
                ".*?" + "<div class=\"view-event-showtime-item\">\n");

        Pattern pPlace = Pattern.compile(PLACE_PREF + ".*?" + END_DIV);
        Pattern pDate = Pattern.compile(DATE_PREF + ".*?" + END_DIV);
        Pattern pTime = Pattern.compile(TIME_PREF + ".*?" + END_DIV);


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
            infoName = infoName.substring(0, infoName.length() - END_DIV.length());
            infos.put(i++, infoName);
        }

        i = 0;
        Matcher mInfoValue = pInfoValue.matcher(response);
        while (mInfoValue.find()) {
            String infoValue = mInfoValue.group().substring(INFOVALUE_PREF.length());
            infoValue = infoValue.substring(0, infoValue.length() - END_DIV.length());
            String info = infos.get(i++);
            if (!TextUtils.isEmpty(info)) {
                PosterDetailItem item = new PosterDetailItem();
                item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal());
                item.setItemText(info + " " + infoValue);
                items.add(item);
            }
        }

        Matcher mPoster = pPoster.matcher(response);
        if (mPoster.find()) {
            mPoster = pPosterImage.matcher(mPoster.group());
            if (mPoster.find()) {
                PosterDetailItem item = new PosterDetailItem();
                item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.IMAGE_ATTACHMENT.ordinal());
                String img = mPoster.group().substring(IMG_PREFIX.length());
                img = img.substring(0, img.length() - IMG_POSTF.length());
                item.setItemText(img);
                items.add(item);
            }
        }

        Matcher mDescription = pDescription.matcher(response);
        if (mDescription.find()) {
            String description = mDescription.group().substring(DESCRIPTION_PREF.length());
            description = description.substring(0, description.length() - END_DIV.length());
            PosterDetailItem item = new PosterDetailItem();
            item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.DESCRIPTION.ordinal());
            item.setItemText(description);
            items.add(item);
        }

        Matcher mTimeAndPlace = pTimeAndPlace.matcher(response);
        int startPos = 0;
        while (mTimeAndPlace.find()) {
            PosterDetailItem item = new PosterDetailItem();
            item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.TIMEPLACE_RECORD.ordinal());
            String time;
            String date;
            String place;
            startPos = mTimeAndPlace.end();
            Matcher mTime = pTime.matcher(mTimeAndPlace.group());
            if (mTime.find()) {
                time = mTime.group().substring(TIME_PREF.length());
                time = time.substring(0, time.length() - END_DIV.length());
                item.setItemTime(time);
            }

            Matcher mDate = pDate.matcher(mTimeAndPlace.group());
            if (mDate.find()) {
                date = mDate.group().substring(DATE_PREF.length());
                date = date.substring(0, date.length() - END_DIV.length());
                item.setItemDate(date);
            }

            Matcher mPlace = pPlace.matcher(mTimeAndPlace.group());
            if (mPlace.find()) {
                place = mPlace.group().substring(PLACE_PREF.length());
                place = place.substring(0, place.length() - END_DIV.length());
                item.setPlace(place);
            }
            items.add(item);
        }

        PosterDetailItem item = null;

        Matcher mPlace = pPlace.matcher(response);
        if (mPlace.find()) {
            item = new PosterDetailItem();
            item.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.TIMEPLACE_RECORD.ordinal());
            String place = mPlace.group().substring(PLACE_PREF.length());
            place = place.substring(0, place.length() - END_DIV.length());
            item.setPlace(place);
        }

        Matcher mTime = pTime.matcher(response);
        if (mTime.find(startPos) && item != null) {
            String time = mTime.group().substring(TIME_PREF.length());
            time = time.substring(0, time.length() - END_DIV.length());
            item.setItemTime(time);
        }

        Matcher mDate = pDate.matcher(response);
        if (mDate.find(startPos) && item != null) {
            String date = mDate.group().substring(DATE_PREF.length());
            date = date.substring(0, date.length() - END_DIV.length());
            item.setItemDate(date);
        }

        if (item != null) {
            items.add(item);
        }

        Matcher mPreview = pPreview.matcher(response);
        if (mPreview.find()) {
            mPreview = pPreviewSource.matcher(mPreview.group());
            if (mPreview.find()) {
                PosterDetailItem posterItem = new PosterDetailItem();
                String previewUrl = mPreview.group().substring(PREVIEW_PREF.length());
                previewUrl = previewUrl.substring(0, previewUrl.length() - PREVIEW_POSTF.length());
                posterItem.setContentType(PosterDetailDBHelper.POSTER_RECORD_TYPE.VIDEO_ATTACHMENT.ordinal());
                posterItem.setItemText(previewUrl);
                items.add(posterItem);
            }
        }
        return items;
    }


}
