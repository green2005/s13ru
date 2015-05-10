package com.parser.processors;

import android.content.Context;

import com.parser.R;
import com.parser.exceptions.VKException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public abstract class VKProcessor extends Processor {
    private static final String VK_ERROR_RESPONSE = "error";
    private static final String VK_ERROR_MSG = "error_msg";
    private static final String RESPONSE = "response";

    private Context mContext;

    public VKProcessor(Context context){
        mContext = context;
    }


    private JSONObject getResponse(InputStream stream) throws Exception {
        if (stream == null) {
            throw new IllegalArgumentException(mContext.getResources().getString(R.string.response_is_empty));
        }
        String s = getStringFromStream(stream);
        JSONObject serverResponse = new JSONObject(s);
        if (!serverResponse.has(RESPONSE)) {  //Process VK Errors
            generateVKServerError(serverResponse);
        }
        return serverResponse;
    }

    public JSONObject getVKResponseObject(InputStream stream) throws Exception {
        JSONObject serverResponse = getResponse(stream);
        return serverResponse.optJSONObject(RESPONSE);
    }

    public JSONArray getVKResponseArray(InputStream stream) throws Exception {
        JSONObject serverResponse = getResponse(stream);
        return serverResponse.optJSONArray(RESPONSE);
    }

    private void generateVKServerError(JSONObject serverResponse) throws VKException, JSONException {
        String errorMsg;
        if (serverResponse.has(VK_ERROR_RESPONSE)) {
            errorMsg = serverResponse.getJSONObject(VK_ERROR_RESPONSE).optString(VK_ERROR_MSG);
        } else {
            errorMsg = mContext.getResources().getString(R.string.unknown_server_error);
        }
        throw new VKException(errorMsg);
    }



}
