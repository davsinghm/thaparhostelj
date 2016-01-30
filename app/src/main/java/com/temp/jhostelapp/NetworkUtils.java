package com.temp.jhostelapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.temp.jhostelapp.Constants.CHARSET_UTF8;

/**
 * Created by DSM_ on 1/29/16.
 */
public class NetworkUtils {



    public static String makeHttpRequest(String urlStr, String method, Params params) throws IOException {



        HttpURLConnection httpURLConnection;

        switch (method) {
            case "POST": {

                URL url = new URL(urlStr);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod(method);
                httpURLConnection.setRequestProperty("Accept-Charset", CHARSET_UTF8);
                httpURLConnection.setReadTimeout(Constants.OK_READ_TIMEOUT * 1000);
                httpURLConnection.setConnectTimeout(Constants.OK_CONNECT_TIMEOUT * 1000);
                httpURLConnection.connect();

                if (params.toString() != null) {
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(params.toString());
                    wr.flush();
                    wr.close();
                }

                break;
            }
            case "GET": {

                if (params.toString() != null) {
                    urlStr += "?" + params.toString();
                }

                URL url = new URL(urlStr);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(false);
                httpURLConnection.setRequestMethod(method);
                httpURLConnection.setRequestProperty("Accept-Charset", CHARSET_UTF8);
                httpURLConnection.setReadTimeout(Constants.OK_READ_TIMEOUT * 1000);
                httpURLConnection.setConnectTimeout(Constants.OK_CONNECT_TIMEOUT * 1000);
                httpURLConnection.connect();
                break;
            }

            default:

                throw new IOException();

        }

        InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();

    }

}
