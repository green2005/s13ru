package com.parser.processors;

import android.content.Context;

import com.parser.bo.VKDetailItem;
import com.parser.db.VKDetailDBHelper;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VKDetailsProcessor extends VKProcessor {
    private Context mContext;
    private VKDetailDBHelper mDbHelper;

    public VKDetailsProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        if (mDbHelper == null) {
            mDbHelper = new VKDetailDBHelper();
        }
        String postId = getPostId(url);
        List<VKDetailItem> items = getItems(stream, postId);
        if (isTopRequest) {
            mDbHelper.clearOldEntries(mContext);
        }
        mDbHelper.bulkInsert(items, mContext);
        return items.size();
    }

    private String getPostId(String url) {
        for (String urlPart : url.split("&")) {
            String[] pair = urlPart.split("=");
            if (pair.length == 2) {
                String key = pair[0];
                if (key.equalsIgnoreCase("post_id")) {
                    return pair[1];
                } else if (key.equalsIgnoreCase("posts")) {
                    String value = pair[1];
                    int i = value.indexOf("_");
                    if (i > 0) {
                        return value.substring(i + 1);
                    }
                }
            }
        }
        return null;
    }

    private List<VKDetailItem> getItems(InputStream stream, String postId) throws Exception {
        List<VKDetailItem> items = new ArrayList<>();
        JSONArray jsonArray = getVKResponseArray(stream);




        return items;
    }
}
