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

        String doInBackground(String... strings);
    }

    private Context context;
    private Callback callback;
    private ProgressDialog progressDialog;

    public DoInBackground(Context context, Callback callback, Params params) {
        this.context = context;
        this.callback = callback;
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
    protected String doInBackground(String... params) {

        //TODO remove dialog wait simulation

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return callback.doInBackground(params);

    }
}
