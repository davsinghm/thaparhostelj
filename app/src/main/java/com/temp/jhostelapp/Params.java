package com.temp.jhostelapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import static com.temp.jhostelapp.Constants.CHARSET_UTF8;

/**
 * Created by DSM_ on 1/30/16.
 */
public class Params {

    private HashMap<String ,String> hashMap;

    public Params() {
        hashMap = new HashMap<>();
    }

    public Params add(String key, String value) {
        hashMap.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        if (hashMap.isEmpty())
            return null;
        StringBuilder sbParams = new StringBuilder();
        int i = 0;
        for (String key : hashMap.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=").append(URLEncoder.encode(hashMap.get(key), CHARSET_UTF8));

            } catch (UnsupportedEncodingException e) {
                return null;
            }
            i++;
        }
        return sbParams.toString();
    }
}
