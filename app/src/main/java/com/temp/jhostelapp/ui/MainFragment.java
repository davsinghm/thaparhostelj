package com.temp.jhostelapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.temp.jhostelapp.Cache;
import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.utils.FileUtils;
import com.temp.jhostelapp.NetworkUtils;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by DSM_ on 1/30/16.
 */
public class MainFragment extends Fragment implements DoInBackground.Callback {

    private DoInBackground doInBackground = null;
    private NotiAdapter adapter;
    private RecyclerView recyclerView;
    private long lastTimestamp;
    private long currentTimestamp;
    private String url;
    private Params params;
    private ArrayList<Noti> notiList;

    @Override
    public void onCancelled() {
        doInBackground =null;
    }

    @Override
    public String doInBackground(String... strings) {

        try {
            String response = NetworkUtils.makeHttpRequest(Constants.URL_SERVER_NOTI, "POST", params);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void onPostExecute(String result) {

        doInBackground = null;

        int newAdded = 0; //TODO remove debug

        try {

            if (result != null) {

                JSONObject jsonObject = new JSONObject(result);
                int returnCode = jsonObject.getInt("returnCode");
                if (returnCode == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("notifications");

                    for (int i = 0; i < (newAdded = jsonArray.length()); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        Noti noti = new Noti().fromJSON(jso);
                        if (noti != null)
                        notiList.add(i, noti);
                    }
                    //Save current timestamp
                    PreferenceHelper.putLong(getContext(), PreferenceHelper.TIME_LASTEST_NOTIFICATIONS, currentTimestamp);

                } //else
                    //TODO onErrorOccurred(jsonObject.getString(jsonObject.getString("extraInfo")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
           //TODO onErrorOccurred(e.toString());
        }


        if (newAdded > 0) {
            String notiStr = Cache.arrayToJSONArray(notiList);
            if (notiStr != null)
                FileUtils.writeStringCache(getContext(), Constants.FILE_NOTIFICATIONS, notiStr);
        }

        adapter.setArrayList(notiList);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "New: " + newAdded + ", Total: " + notiList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (doInBackground != null)
            doInBackground.cancel(true);

    }

    @Override
    public void onStart() {
        super.onStart();

        notiList = new ArrayList<>();
        lastTimestamp = PreferenceHelper.getLong(getContext(), PreferenceHelper.TIME_LASTEST_NOTIFICATIONS, 0);
        currentTimestamp = System.currentTimeMillis() / 1000;

        if (lastTimestamp != 0 && FileUtils.readCache(getContext(), Constants.FILE_NOTIFICATIONS) == null) {
            //checking if file doesnt exists (when read/write fails) and lastTimestamp should be made 0
            lastTimestamp = 0;
        }

        params = new Params();
        params.add("rollNo", PreferenceHelper.getRollNo(getContext()));
        params.add("token", PreferenceHelper.getToken(getContext()));
        params.add("timestamp", String.valueOf(lastTimestamp));

        doInBackground = new DoInBackground(getContext(), this, params);
        doInBackground.execute();

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Cache.load(getContext(), new Noti(), notiList, Constants.FILE_NOTIFICATIONS);
        adapter = new NotiAdapter(notiList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


}
