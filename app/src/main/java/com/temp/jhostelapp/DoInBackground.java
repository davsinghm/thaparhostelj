package com.temp.jhostelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by DSM_ on 1/30/16.
 */
public class DoInBackground extends AsyncTask<String, Void, String> {

    public interface Callback {
        void onPostExecute(String result);

        void onCancelled();

        void onErrorOccurred(String error);
    }

    private Context context;
    private Callback callback;
    private ProgressDialog progressDialog;
    private Params params;

    public DoInBackground(Context context, Callback callback, Params params) {
        this.context = context;
        this.callback = callback;
        this.params = params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.progress_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        progressDialog.dismiss();
        callback.onPostExecute(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        progressDialog.dismiss();
        callback.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];

        try {
            return NetworkUtils.makeHttpRequest(url, "POST", this.params);

        } catch (IOException e) {
            e.printStackTrace();
            callback.onErrorOccurred(e.toString());
            return null;
        }

    }
}
