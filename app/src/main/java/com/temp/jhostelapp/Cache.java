package com.temp.jhostelapp;

import android.content.Context;

import com.temp.jhostelapp.ui.Complaint;
import com.temp.jhostelapp.ui.Noti;
import com.temp.jhostelapp.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DSM_ on 1/31/16.
 */
public class Cache {

    public static <T> void load(Context context, T t, ArrayList<T> arrayList, String fileName) {
        String cacheStr = FileUtils.readCache(context, fileName);
        if (cacheStr != null) {
            try {

                JSONArray jsonArray = new JSONArray(cacheStr);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    if (t instanceof Noti)
                        t = (T) new Noti().fromJSON(jso);
                     else if (t instanceof Complaint)
                        t = (T) new Complaint().fromJSON(jso);
                    else t = null;

                    if (t != null)
                        arrayList.add(t);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public static <T> String arrayToJSONArray(ArrayList<T> arrayList) {
        JSONArray jsonArray = new JSONArray();

        try {
            for (T t : arrayList) {
                String str = t.toString();
                if (str == null)
                    continue;
                JSONObject jsonObject = new JSONObject(str);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            return null;
        }
        return jsonArray.toString();
    }

}
