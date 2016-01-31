package com.temp.jhostelapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.temp.jhostelapp.Cache;
import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.utils.FileUtils;
import com.temp.jhostelapp.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DSM_ on 1/31/16.
 */
public class ComplaintsFragment extends Fragment implements DoInBackground.Callback {

    private DoInBackground doInBackground = null;
    private NotiAdapter adapter;
    private long lastTimestamp;
    private long currentTimestamp;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private ArrayList<Noti> complaintList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complaints, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        complaintList = new ArrayList<>();
        lastTimestamp = PreferenceHelper.getLong(getContext(), PreferenceHelper.TIME_LASTEST_COMPLAINTS, 0);
        currentTimestamp = System.currentTimeMillis() / 1000;

        if (lastTimestamp != 0 && FileUtils.readCache(getContext(), Constants.FILE_COMPLAINTS) == null) {
            lastTimestamp = 0;
        }

        doInBackground = getDoInBackground();
        doInBackground.execute();

        Cache.load(getContext(), new Noti(), complaintList, Constants.FILE_COMPLAINTS);

        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ComplaintActivity.class);
                startActivity(intent);
            }
        });

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        adapter = new NotiAdapter(complaintList);

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();

        if (doInBackground != null)
            doInBackground.cancel(true);

    }

    @Override
    public void onPreExecute() {

        if (snackbar != null)
            snackbar.dismiss();

    }

    @Override
    public void onCancelled() {
        doInBackground = null;
    }

    @Override
    public String doInBackground(String... strings) {

        try {
            Params params = new Params();
            /*params.add("rollNo", PreferenceHelper.getRollNo(getContext()));
            params.add("token", PreferenceHelper.getToken(getContext()));
            params.add("timestamp", String.valueOf(lastTimestamp));*/

            return NetworkUtils.makeHttpRequest(Constants.URL_SERVER_COMPLAINTS, "POST", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(String result) {

    }

    private void showError(String error) {
        if (error != null) {
            switch (error) {
                case "OFFLINE":
                    showOfflineSnackbar(false);
                    break;
                case "NETWORK_ERROR":
                    showOfflineSnackbar(true);
                    break;
                case "INVALID_UN_PW":
                case "INVALID_TOKEN":
                case "FIELDS_MISSING":
                case "QUERY_FAILED":
                case "CONNECTION_FAILED":

                default:
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showOfflineSnackbar(boolean error) {
        snackbar = Snackbar.make(coordinatorLayout, error ? "You're offline" : "Couldn't connect to internet", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInBackground = getDoInBackground();
                doInBackground.execute();
            }
        });
        snackbar.show();
    }

    private DoInBackground getDoInBackground() {
        return new DoInBackground(getContext(), this, getString(R.string.progress_loading));
    }

}
