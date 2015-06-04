package com.parser.processors;

import android.content.Context;

import com.parser.bo.VKDetailItem;
import com.parser.db.VKDetailDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VKDetailsProcessor extends VKProcessor {
    private Context mContext;
    private VKDetailDBHelper mDbHelper;
    private SimpleDateFormat mDf;

    public VKDetailsProcessor(Context context) {
        super(context);
        mContext = context;
        mDf = new SimpleDateFormat("dd.MM.yyyy");
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        if (mDbHelper == null) {
            mDbHelper = new VKDetailDBHelper();
        }
        String postId = getPostId(url);
        List<VKDetailItem> items = getItems(stream, postId, url);
        if (isTopRequest) {
            mDbHelper.clearOldEntries(mContext);
        }
        mDbHelper.bulkInsert(items, mContext);
        return items.size();
    }

    private String getPostId(String url) {
        url = url.replace("?", "&");
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

    private List<VKDetailItem> getItems(InputStream stream, String postId, String url) throws Exception {
        List<VKDetailItem> vkDetailItems = new ArrayList<>();
        JSONObject response = getVKResponseObject(stream);

        //TODO move constants to constants
        JSONArray jProfiles = response.optJSONArray("profiles");
        JSONArray jComments = response.optJSONArray("items");
        JSONArray jWall = response.optJSONArray("items");

        Map<String, Profile> profilesMap = new HashMap<>();
        processProfiles(jProfiles, profilesMap);
        JSONArray jGroups = response.optJSONArray("groups");
        processGroups(jGroups, profilesMap);
        if (isWallPost(url)) {
            processWallPost(jWall, profilesMap, vkDetailItems, postId);
        } else {
            processComments(jComments, profilesMap, vkDetailItems, postId);
        }
        return vkDetailItems;
    }

    private boolean isWallPost(String url){
        //todo refactor it
        return url.contains("wall.getById");
    }

    private void processGroups(JSONArray jGroups, Map<String, Profile> profilesMap) {
        if (jGroups == null) {
            return;
        }
        for (int i = 0; i < jGroups.length(); i++) {
            JSONObject jGroup = jGroups.optJSONObject(i);
            Profile group = new Profile();
            group.setId("-"+jGroup.optString("id"));
            group.setName(jGroup.optString("name"));
            group.setUserPick(jGroup.optString("photo_50"));
            profilesMap.put(group.getId(), group);
        }
    }

    private void processProfiles(JSONArray jProfiles, Map<String, Profile> profilesMap) {
        if (jProfiles != null) {
            for (int i = 0; i < jProfiles.length(); i++) {
                JSONObject jProfile = jProfiles.optJSONObject(i);
                Profile profile = new Profile(jProfile);
                profilesMap.put(profile.getId(), profile);
            }
        }
    }

    private void processComments(JSONArray jComments, Map<String, Profile> profilesMap, List<VKDetailItem> postItems, String postId) {
        for (int i = 0; i < jComments.length(); i++) {
            JSONObject item = jComments.optJSONObject(i);
            VKDetailItem detailItem = new VKDetailItem();
            detailItem.setPostId(postId);
            detailItem.setAuthorId(item.optString("from_id"));
            Profile profile = profilesMap.get(detailItem.getAuthorId());
            if (profile != null) {
                detailItem.setAuthorImage(profile.getUserPick());
                detailItem.setAuthorName(profile.getName());
            }
            detailItem.setText(item.optString("text"));
            detailItem.setCommentId(item.optString("id"));
            String date = item.optString("date");
            detailItem.setDate(mDf.format(new java.util.Date(Long.parseLong(date) * 1000)));
            JSONArray jAttachments = item.optJSONArray("attachments");
            detailItem.setItemType(VKDetailItem.ItemType.COMMENT.ordinal());
            postItems.add(detailItem);
            processAttachments(jAttachments, postItems, postId, detailItem.getCommentId());
            VKDetailItem delimiter = new VKDetailItem();
            delimiter.setItemType(VKDetailItem.ItemType.DELIMITER.ordinal());
            postItems.add(delimiter);
        }
    }

    private void processAttachments(JSONArray jAttaches, List<VKDetailItem> postItems, String postId, String commentId) {
        if (jAttaches != null) {
            for (int j = 0; j < jAttaches.length(); j++) {
                JSONObject attachment = jAttaches.optJSONObject(j);
                JSONObject photo = attachment.optJSONObject("photo");
                if (photo != null) {
                    VKDetailItem photoItem = new VKDetailItem();
                    photoItem.setItemType(VKDetailItem.ItemType.ATTACHMENT_PHOTO.ordinal());
                    photoItem.setPostId(postId);
                    photoItem.setText(photo.optString("photo_604"));
                    photoItem.setWidth(photo.optInt("width"));
                    photoItem.setHeight(photo.optInt("height"));
                    photoItem.setCommentId(commentId);
                    postItems.add(photoItem);
                }
                JSONObject video = attachment.optJSONObject("video");
                if (video != null){
                    VKDetailItem videoItem = new VKDetailItem();
                    videoItem.setItemType(VKDetailItem.ItemType.ATTACHMENT_VIDEO.ordinal());
                    videoItem.setPostId(postId);
                    videoItem.setText(video.optString("photo_640"));
                    videoItem.setCommentId(video.optString("id"));
                    postItems.add(videoItem);
                }
            }
        }
    }

    private void processWallPost(JSONArray jWallArray, Map<String, Profile> profilesMap, List<VKDetailItem> postItems, String postId) {
        if (jWallArray == null || jWallArray.length() == 0) {
            return;
        }
        JSONObject jWall = jWallArray.optJSONObject(0);
        VKDetailItem item = new VKDetailItem();
        item.setItemType(VKDetailItem.ItemType.CONTENT.ordinal());
        item.setPostId(postId);
        item.setText(jWall.optString("text"));
        String date = jWall.optString("date");
        item.setDate(mDf.format(new java.util.Date(Long.parseLong(date) * 1000)));
        item.setAuthorId(jWall.optString("from_id"));
        item.setAuthorImage(profilesMap.get(item.getAuthorId()).getUserPick());
        item.setAuthorName(profilesMap.get(item.getAuthorId()).getName());
        postItems.add(item);

        JSONArray jAttachments = jWall.optJSONArray("attachments");
      //  postItems.add(item);
        processAttachments(jAttachments, postItems, postId, postId);
    }


    private class Profile {
        String name;
        String id;
        String userPick;

        Profile(JSONObject jProfile) {
            id = jProfile.optString("id");
            name = new String(jProfile.optString("first_name") + " " + jProfile.optString("last_name")).trim();
            userPick = jProfile.optString("photo_50");
        }

        Profile() {

        }

        String getName() {
            return name;
        }

        String getId() {
            return id;
        }

        String getUserPick() {
            return userPick;
        }

        void setName(String name) {
            this.name = name;
        }

        void setId(String id) {
            this.id = id;
        }

        void setUserPick(String userPick) {
            this.userPick = userPick;
        }
    }
}
