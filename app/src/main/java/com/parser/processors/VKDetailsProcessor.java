package com.parser.processors;

import android.content.Context;

import com.parser.bo.VKDetailItem;
import com.parser.db.VKDetailDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
        List<VKDetailItem> vkDetailItems = new ArrayList<>();
        JSONObject response = getVKResponseObject(stream);

        //TODO move constants to constants
        JSONArray profiles = response.optJSONArray("profiles");
        JSONArray replyItems = response.optJSONArray("items");
        HashMap<String, JSONObject> profilesMap = new HashMap<>();
        for (int i = 0; i < profiles.length(); i++) {
            JSONObject profile = profiles.optJSONObject(i);
            String userId = profile.optString("id");
            profilesMap.put(userId, profile);
        }
        for (int i = 0; i < replyItems.length(); i++) {
            JSONObject item = replyItems.optJSONObject(i);
            VKDetailItem detailItem = new VKDetailItem();
            detailItem.setPostId(postId);
            detailItem.setAuthorId(item.optString("from_id"));
            JSONObject profile = profilesMap.get(detailItem.getAuthorId());
            if (profile != null) {
                detailItem.setAuthorImage(profile.optString("photo_50"));
                detailItem.setAuthorName(new String(profile.optString("first_name") + " " + profile.optString("last_name")).trim());
            }
            detailItem.setText(item.optString("text"));
            detailItem.setCommentId(item.optString("id"));
            if (postId.equals(detailItem.getCommentId())) {
                detailItem.setItemType(VKDetailItem.ItemType.CONTENT.ordinal());
            } else {
                detailItem.setItemType(VKDetailItem.ItemType.COMMENT.ordinal());
            }
            vkDetailItems.add(detailItem);
            JSONArray attaches = item.optJSONArray("attachments");
            if (attaches != null) {
                for (int j = 0; j < attaches.length(); j++) {
                    JSONObject attachment = attaches.optJSONObject(j);
                    JSONObject photo = attachment.optJSONObject("photo");
                    if (photo != null) {
                        VKDetailItem photoItem = new VKDetailItem();
                        photoItem.setItemType(VKDetailItem.ItemType.ATTACHMENT.ordinal());
                        photoItem.setPostId(postId);
                        photoItem.setCommentId(detailItem.getCommentId());
                        vkDetailItems.add(photoItem);
                    }
                }
            }
            VKDetailItem delimiter = new VKDetailItem();
            delimiter.setItemType(VKDetailItem.ItemType.DELIMITER.ordinal());
            vkDetailItems.add(delimiter);
        }
        return vkDetailItems;
    }
}
