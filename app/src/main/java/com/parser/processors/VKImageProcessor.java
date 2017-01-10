package com.parser.processors;

import android.app.ProgressDialog;

import com.parser.API;
import com.parser.R;
import com.parser.bo.VKImageItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class VKImageProcessor extends Processor {

    private static final String VK_WALL_URL = API.VK_BASE_URL + "/method/wall.get?owner_id=" + API.VK_S13_OWNER_ID +
            "&count=%d" + "&offset=%d";
    private static final String RESPONSE = "response";


    public List<VKImageItem> processImages(int offset, int count) throws Exception {
        String href = String.format(VK_WALL_URL, count, offset);
        InputStream stream = getInputStream(href);
        List<VKImageItem> items = new ArrayList<>();
        if (stream != null) {
            JSONArray ja = getVKResponseArray(stream);
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.optJSONObject(i);
                if (jo != null) {
                    JSONArray jAttachments = jo.optJSONArray("attachments");
                    if (jAttachments != null) {
                        VKImageItem item = new VKImageItem();
                        item.processAttachments(jAttachments);
                        if (item.getNoteLink()!=null && !item.getNoteLink().isEmpty()) {
                            if (item.getNoteLink().contains("s13.ru")) {
                                items.add(item);
                            }
                        }
                    }
                }
            }
            stream.close();
        }
        return items;
    }

    private InputStream getInputStream(String href) throws Exception {
        URL url = new URL(href);
        URLConnection urlConnection = url.openConnection();
        return urlConnection.getInputStream();
    }

    public JSONArray getVKResponseArray(InputStream stream) throws Exception {
        JSONObject serverResponse = getResponse(stream);
        return serverResponse.optJSONArray(RESPONSE);
    }

    private JSONObject getResponse(InputStream stream) throws Exception {
        if (stream == null) {
            return null;
        }
        String s = getStringFromStream(stream, null);
        JSONObject serverResponse = new JSONObject(s);
        return serverResponse;
    }

    @Override
    public int process(InputStream stream, boolean isTopRequest, String url) throws Exception {
        return 0;
    }
}
