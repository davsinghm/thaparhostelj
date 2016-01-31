package com.temp.jhostelapp.ui;

import com.temp.jhostelapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DSM_ on 1/30/16.
 */
public class Noti {

    private String title;
    private String message;
    private long timestamp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Noti fromJSON(JSONObject jsonObject) {
        try {
            setTitle(jsonObject.getString("title"));
            setMessage(jsonObject.getString("body"));
            setTimestamp(Utils.parseLong(jsonObject.getString("timestamp"), 0));
            return this;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", getTitle());
            jsonObject.put("body", getMessage());
            jsonObject.put("timestamp", String.valueOf(getTimestamp()));
            return jsonObject.toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
