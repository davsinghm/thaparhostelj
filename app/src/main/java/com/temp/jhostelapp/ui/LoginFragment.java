package com.temp.jhostelapp.ui;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.temp.jhostelapp.Constants;
import com.temp.jhostelapp.DoInBackground;
import com.temp.jhostelapp.MainActivityInterface;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;
import com.temp.jhostelapp.utils.NetworkUtils;
import com.temp.jhostelapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by DSM_ on 1/29/16.
 */
public class LoginFragment extends Fragment implements DoInBackground.Callback {

    private DoInBackground doInBackground = null;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;
    private String mPassword;

    private MainActivityInterface mMainActivityInterface;

    @Override
    public void onStart() {
        super.onStart();

        mMainActivityInterface = (MainActivityInterface) getActivity();
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);

        mUsernameView = (EditText) getActivity().findViewById(R.id.username);

        mPasswordView = (EditText) getActivity().findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) getActivity().findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();

        if (doInBackground != null)
            doInBackground.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    private void attemptLogin() {
        if (doInBackground != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            mUsername = email;
            mPassword = password;

            doInBackground = getDoInBackground();
            doInBackground.execute();
        }
    }

    private boolean isUsernameValid(String str) {
        return true;//str.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
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
        return new DoInBackground(getContext(), this, getString(R.string.progress_signing));
    }

    @Override
    public void onPreExecute() {
        Utils.hideKeyboard(getContext(), mPasswordView);

        if (snackbar != null)
            snackbar.dismiss();

        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            doInBackground.cancel(true);
            showError("OFFLINE");
        }

    }

    @Override
    public String doInBackground(String... strings) {

        try {

            Params params = new Params();
            params.add("rollNo", mUsername);
            params.add("password", mPassword);

            return NetworkUtils.makeHttpRequest(Constants.URL_SERVER_LOGIN, "POST", params);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onCancelled() {
        doInBackground = null;
    }

    @Override
    public void onPostExecute(String result) {
        doInBackground = null;

        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int returnCode = jsonObject.getInt("returnCode");

                if (returnCode == 1) {
                    String token = jsonObject.getString("token");

                    PreferenceHelper.putRollNo(getContext(), mUsername);
                    PreferenceHelper.putToken(getContext(), token);

                    Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                    mMainActivityInterface.loginSuccessful();


                } else {
                    showError(jsonObject.getString("extraInfo"));
                }


            } catch (JSONException e) {
                showError("NETWORK_ERROR");
            }
        }
        else
            showError("NETWORK_ERROR");
    }

}
