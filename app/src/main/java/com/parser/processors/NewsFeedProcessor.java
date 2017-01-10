package com.parser.processors;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Xml;

import com.parser.R;
import com.parser.bo.NewsFeedItem;
import com.parser.bo.VKImageItem;
import com.parser.db.NewsFeedDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String ITEM_DATE = "pubDate"; //"pubdate";
    private static final String ITEM_AUTHOR = "dc:creator";

    private Context mContext;
    private int mEntriesCount = 0;
    private static final int VK_ITEMS_COUNT = 5;
    private boolean mLoadImages = true;
    private String mNextUrl;
    private OnProcessorDoneListener mDoneListener;

    VKImageProcessor mImageProcessor;

    public NewsFeedProcessor(Context context) {
        mParser = Xml.newPullParser();
        mDBHelper = new NewsFeedDBHelper();
        mContext = context;
        mImageProcessor = new VKImageProcessor();

        SharedPreferences preferences;
        // preferences = mContext.getSharedPreferences(mContext.getResources().getString(R.string.load_images_key),Context.MODE_PRIVATE);
        // mLoadImages = preferences.getBoolean(mContext.getResources().getString(R.string.load_images_key), true);
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mLoadImages = preferences.getBoolean(mContext.getResources().getString(R.string.load_images_key), true);
    }

    public void setDoneListener(OnProcessorDoneListener listener) {
        mDoneListener = listener;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        List<NewsFeedItem> feedItems = parseResponse(stream);
        if (isTopRequest) {
            mDBHelper.clearOldEntries(mContext);
            mEntriesCount = 0;
        }

        if (mLoadImages && feedItems.size() > 0) {
          //  List<VKImageItem> images = mImageProcessor.processImages(mEntriesCount, feedItems.size() + VK_ITEMS_COUNT); //magic number
         //   mEntriesCount += feedItems.size() + VK_ITEMS_COUNT;
         //   mDBHelper.bulkInsertImages(images, mContext);
        }

        mDBHelper.bulkInsert(feedItems, mContext);
        if (mDoneListener != null) {
            mDoneListener.onDone(mNextUrl);
        }
        return feedItems.size();
    }

    private List<NewsFeedItem> parseResponse(InputStream stream) throws Exception {
        List<NewsFeedItem> entries = new ArrayList<>();
        String response = getStringFromStream(stream);
        JSONObject jdata = new JSONObject(response);
        JSONArray ja = new JSONArray(jdata.optString("data"));
        for (int i = 0; i < ja.length(); i++) {
            JSONObject post = ja.optJSONObject(i);
            String s13Name = mContext.getResources().getString(R.string.s13_name);
            NewsFeedItem item = new NewsFeedItem(post, s13Name);
            entries.add(item);
        }

        JSONObject paging = jdata.optJSONObject("paging");
        mNextUrl = paging.optString("next");
        return entries;
    }

    private String getStringFromStream(InputStream stream) throws Exception {
        String res = "";
        char buffer[] = new char[1024];
        StringBuilder sb = new StringBuilder();
        InputStreamReader is = new InputStreamReader(stream, "UTF-8");
        for (; ; ) {
            int rcount = is.read(buffer, 0, buffer.length);
            if (rcount < 0) {
                break;
            }
            sb.append(buffer, 0, rcount);
        }
        res = sb.toString();
        is.close();
        return res;
    }
}
