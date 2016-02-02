package com.temp.jhostelapp.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.temp.jhostelapp.Cache;
import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.utils.FileUtils;
import com.temp.jhostelapp.utils.NetworkUtils;
import com.temp.jhostelapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DSM_ on 1/30/16.
 */
public class MessMenuFragment extends Fragment implements DoInBackground.Callback {

    private DoInBackground doInBackground = null;
    private MessMenuAdapter adapter;
    private long lastTimestamp;
    private long currentTimestamp;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private ArrayList<MessMeal> menuList;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messmenu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        menuList = new ArrayList<>();
        lastTimestamp = PreferenceHelper.getLong(getContext(), PreferenceHelper.TIME_LATEST_MESS_MENU, 0);
        currentTimestamp = System.currentTimeMillis() / 1000;

        if (lastTimestamp != 0 && FileUtils.readCache(getContext(), Constants.FILE_MESS_MENU) == null) {
            //checking if file doesn't exists (when read/write fails) and lastTimestamp should be made 0
            lastTimestamp = 0;
        }

        Cache.load(getContext(), new MessMeal(), menuList, Constants.FILE_MESS_MENU);

        textView = (TextView) getActivity().findViewById(R.id.textView);
        long lastUpdatedOn = PreferenceHelper.getLong(getContext(), PreferenceHelper.TIME_MESS_MENU_UPDATED_ON, 0);
        if (lastUpdatedOn != 0) {
            textView.setText("Last updated on: " + lastUpdatedOn);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Utils.getAccentColor(getContext()));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doInBackground = getDoInBackground();
                doInBackground.execute();
            }
        });
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        adapter = new MessMenuAdapter(menuList);

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        doInBackground = getDoInBackground();
        doInBackground.execute();
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

        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            doInBackground.cancel(true);
            showError("OFFLINE");
        }

    }

    @Override
    public void onCancelled() {
        doInBackground = null;
    }

    @Override
    public String doInBackground(String... strings) {

        try {
            Params params = new Params();
            params.add("rollNo", PreferenceHelper.getRollNo(getContext()));
            params.add("token", PreferenceHelper.getToken(getContext()));
            params.add("timestamp", String.valueOf(lastTimestamp));

            return NetworkUtils.makeHttpRequest(Constants.URL_SERVER_MESS_MENU, "POST", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(String result) {

        doInBackground = null;

        int newAdded = 0, modified = 0; //TODO remove debug

        try {

            if (result != null) {

                JSONObject jsonObject = new JSONObject(result);
                Log.e("TEST", jsonObject.toString());
                long lastUpdatedOn = 0;

                int returnCode = jsonObject.getInt("returnCode");
                if (returnCode == 1) {

                    JSONArray jsonArray = jsonObject.getJSONArray("menu");

                    if (jsonArray.length() > 0) {
                        lastUpdatedOn = Utils.parseLong(jsonObject.getString("timestamp"), 0);
                        if (lastUpdatedOn != 0)
                            textView.setText("Last updated on: " + lastUpdatedOn);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        MessMeal messMeal = new MessMeal().fromJSON(jso);
                        if (messMeal != null) {
                            if (menuList.contains(messMeal)) {
                                int index = menuList.indexOf(messMeal);
                                menuList.remove(index);
                                menuList.add(index, messMeal);
                                modified++;
                            } else {
                                menuList.add(i, messMeal);
                                newAdded++;
                            }
                        }
                    }

                    PreferenceHelper.putLong(getContext(), PreferenceHelper.TIME_MESS_MENU_UPDATED_ON, lastUpdatedOn);

                    //Save current timestamp
                    PreferenceHelper.putLong(getContext(), PreferenceHelper.TIME_LATEST_MESS_MENU, currentTimestamp);

                } else
                    showError(jsonObject.getString(jsonObject.getString("extraInfo")));
            } else
                showError("NETWORK_ERROR");

        } catch (JSONException e) {
            e.printStackTrace();
            showError("NETWORK_ERROR");
        }

        if (newAdded > 0) {
            String compStr = Cache.arrayToJSONArray(menuList);
            if (compStr != null)
                FileUtils.writeStringCache(getContext(), Constants.FILE_MESS_MENU, compStr);
        }

        adapter.setArrayList(menuList);
        adapter.notifyDataSetChanged();
        //TODO remove error
        Toast.makeText(getContext(), "New: " + newAdded + ", Modified: " + modified + ", Total: " + menuList.size(), Toast.LENGTH_SHORT).show();
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
        snackbar = Snackbar.make(coordinatorLayout, error ? "Couldn't connect to internet" : "You're offline", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doInBackground = getDoInBackground();
                doInBackground.execute();
            }
        });
        snackbar.show();
    }

    private DoInBackground getDoInBackground() {
        return new DoInBackground(this, swipeRefreshLayout);
    }

}
