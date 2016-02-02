package com.temp.jhostelapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by DSM_ on 1/31/16.
 */
public class ComplaintActivity extends AppCompatActivity implements DoInBackground.Callback {

    private DoInBackground doInBackground;
    private String complaint;
    private String category;
    private Context context;

    @Override
    public void onPreExecute() {

        if (!NetworkUtils.isNetworkAvailable(this)) {
            doInBackground.cancel(true);
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
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
            params.add("rollNo", PreferenceHelper.getRollNo(this));
            params.add("token", PreferenceHelper.getToken(this));
            params.add("category", category);
            params.add("complaint", complaint);

            String response = NetworkUtils.makeHttpRequest(Constants.URL_SERVER_NEW_COMPLAINT, "POST", params);
            JSONObject jsonObject = new JSONObject(response);
            int returnCode = jsonObject.getInt("returnCode");

            if (returnCode == 1) {
                return "1";
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(String result) {
        doInBackground = null;

        if (result == null) {
            Toast.makeText(context, "Unable to send complaint", Toast.LENGTH_LONG).show();
        } else if ("1".equals(result)) {
            Toast.makeText(context, "Complaint sent successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_complain);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, new String[]{
                "Category", "WiFi", "Plumbing", "Hygiene", "Electrical", "Furniture", "Other"
        }));

        final EditText editText = (EditText) findViewById(R.id.editText);
        final DoInBackground.Callback callback = this;

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaint = editText.getText().toString();
                category = (String) spinner.getSelectedItem();
                if (complaint.isEmpty()) {
                    Toast.makeText(context, "Empty message", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (category.equals("Category")) {
                    Toast.makeText(context, "Select category", Toast.LENGTH_SHORT).show();
                    return;
                }
                doInBackground = new DoInBackground(context, callback, getString(R.string.progress_sending));
                doInBackground.execute();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (doInBackground != null)
            doInBackground.cancel(true);
    }
}
