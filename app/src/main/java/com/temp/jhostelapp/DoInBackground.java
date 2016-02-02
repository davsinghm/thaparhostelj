package com.temp.jhostelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by DSM_ on 1/30/16.
 */
public class DoInBackground extends AsyncTask<String, Void, String> {

    public interface Callback {

        void onPreExecute();

        void onPostExecute(String result);

        void onCancelled();

        String doInBackground(String... strings);
    }

    private Context context;
    private Callback callback;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private String message;


    public DoInBackground(Context context, Callback callback, String string) {
        this.context = context;
        this.callback = callback;
        this.message = string;
    }

    public DoInBackground(Callback callback, SwipeRefreshLayout swipeRefreshLayout) {
        this.callback = callback;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (context != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        callback.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (progressDialog != null)
            progressDialog.dismiss();

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

        callback.onPostExecute(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if (progressDialog != null)
            progressDialog.dismiss();

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

        callback.onCancelled();
    }

    @Override
    protected String doInBackground(String... params) {

        //TODO remove wait simulation

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return callback.doInBackground(params);

    }
}
