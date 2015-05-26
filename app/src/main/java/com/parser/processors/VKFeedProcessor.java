package com.parser.processors;

import android.content.Context;

import com.parser.bo.VKFeedItem;
import com.parser.db.VKFeedDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class VKFeedProcessor extends VKProcessor {
    private Context mContext;
    private VKFeedDBHelper mDBHelper;


    public VKFeedProcessor(Context context) {
        super(context);
        mContext = context;
        mDBHelper = new VKFeedDBHelper();
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest) throws Exception {
        JSONArray ja = getVKResponseArray(stream);
        ArrayList<VKFeedItem> values = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            JSONObject joItem = ja.optJSONObject(i);
            if (joItem != null) {
                VKFeedItem item = new VKFeedItem(joItem);
                values.add(item);
            }
        }
        if (isTopRequest) {
            mDBHelper.clearOldEntries(mContext);
        }
        mDBHelper.bulkInsert(values, mContext);
        return values.size();
    }


}
