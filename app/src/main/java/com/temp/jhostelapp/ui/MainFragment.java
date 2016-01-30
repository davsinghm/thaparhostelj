package com.temp.jhostelapp.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.FileUtils;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public void onStart() {
        super.onStart();

        lastTimestamp = PreferenceHelper.getLong(getContext(), "lastTimestamp", 0);
        currentTimestamp = System.currentTimeMillis() / 1000;

        if (lastTimestamp != 0 && FileUtils.readCache(getContext(), "notifications") == null) {
            //checking if file doesnt exists (when read/write fails) and lastTimestamp should be made 0
            lastTimestamp = 0;
        }

        Params params = new Params();
        params.add("rollNo", PreferenceHelper.getRollNo(getContext()));
        params.add("token", PreferenceHelper.getToken(getContext()));
        params.add("timestamp", String.valueOf(lastTimestamp));

        doInBackground = new DoInBackground(getContext(), this, params);
        doInBackground.execute(Constants.URL_SERVER_NOTI);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotiAdapter(new ArrayList<Noti>());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void loadSavedNoti(ArrayList<Noti> arrayList) {
        String cacheStr = FileUtils.readCache(getContext(), "notifications");
        if (cacheStr != null) {
            try {

                JSONArray jsonArray = new JSONArray(cacheStr);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    Noti noti = new Noti();
                    noti.setTitle(jso.getString("title"));
                    noti.setMessage(jso.getString("body"));
                    noti.setTimestamp(Utils.parseLong(jso.getString("timestamp"), 0));
                    arrayList.add(noti);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private String arrayToJSONArray(ArrayList<Noti> arrayList) {
        JSONArray jsonArray = new JSONArray();

        try {
            for (Noti noti : arrayList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", noti.getTitle());
                jsonObject.put("body", noti.getMessage());
                jsonObject.put("timestamp", noti.getTimestamp() + "");
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            return null;
        }
        return jsonArray.toString();
    }

    @Override
    public void onPostExecute(String result) {

        ArrayList<Noti> arrayList = new ArrayList<>();
        int newAdded = 0;

        try {

            if (result != null) {

                JSONObject jsonObject = new JSONObject(result);
                int returnCode = jsonObject.getInt("returnCode");
                if (returnCode == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("notifications");

                    for (int i = 0; i < (newAdded = jsonArray.length()); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        Noti noti = new Noti();
                        noti.setTitle(jso.getString("title"));
                        noti.setMessage(jso.getString("body"));
                        noti.setTimestamp(Utils.parseLong(jso.getString("timestamp"), 0));
                        arrayList.add(noti);
                    }
                    //Save current timestamp
                    PreferenceHelper.putLong(getContext(), "lastTimestamp", currentTimestamp);

                } else
                    onErrorOccurred(jsonObject.getString(jsonObject.getString("extraInfo")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            onErrorOccurred(e.toString());
        }

        loadSavedNoti(arrayList);

        if (newAdded > 0) {
            String notiStr = arrayToJSONArray(arrayList);
            if (notiStr != null)
                FileUtils.writeStringCache(getContext(), "notifications", notiStr);
        }

        adapter.setArrayList(arrayList);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "New: " + newAdded + ", Total: " + arrayList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public void onErrorOccurred(String error) {

    }
}
