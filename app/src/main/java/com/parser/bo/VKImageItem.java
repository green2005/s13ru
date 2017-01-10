package com.parser.bo;


import org.json.JSONArray;
import org.json.JSONObject;

public class VKImageItem {
    private int width;
    private int height;
    private String imageLink;
    private String noteLink;

    public void processAttachments(JSONArray attachments) {
        for (int j = 0; j < attachments.length(); j++) {
            JSONObject attachment = attachments.optJSONObject(j);
            if (attachment != null) {
                String attachmentType = attachment.optString("type");
                if (attachmentType.equalsIgnoreCase("photo")) {
                    JSONObject photo = attachment.optJSONObject("photo");
                    if (photo != null) {
                        imageLink = photo.optString("src_big");
                        width = photo.optInt("width");
                        height = photo.optInt("height");
                    }
                } else if (attachmentType.equalsIgnoreCase("link")) {
                    JSONObject link = attachment.optJSONObject("link");
                    if (link != null) {
                        noteLink = link.optString("url");
                    }
                }
            }
        }


    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getNoteLink() {
        return noteLink;
    }

}
