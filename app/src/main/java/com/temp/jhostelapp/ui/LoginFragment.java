package com.temp.jhostelapp.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.temp.jhostelapp.Interface;
import com.temp.jhostelapp.NetworkUtils;
import com.temp.jhostelapp.Params;
import com.temp.jhostelapp.PreferenceHelper;
import com.temp.jhostelapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by DSM_ on 1/29/16.
 */
public class LoginFragment extends Fragment {

    private UserLoginTask mAuthTask = null;

    private EditText mUsernameView;
    private EditText mPasswordView;

    private Interface mInterface;

    @Override
    public void onStart() {
        super.onStart();

        mInterface = (Interface) getActivity();

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
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthTask != null)
            mAuthTask.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
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

            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String str) {
        return true;//str.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public class UserLoginTask extends AsyncTask<Void, String, String> {

        private final String mUsername;
        private final String mPassword;
        private ProgressDialog mProgressDialog;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.progress_signing));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            Params params1 = new Params();
            params1.add("rollNo", mUsername);
            params1.add("password", mPassword);

            try {

                String string = NetworkUtils.makeHttpRequest(Constants.URL_SERVER_LOGIN, "POST", params1);
                JSONObject jsonObject = new JSONObject(string);
                int returnCode = jsonObject.getInt("returnCode");

                if (returnCode == 1) {
                    String token = jsonObject.getString("token");
                    publishProgress(token);
                    return null;

                } else {
                    return jsonObject.getString("extraInfo");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {

            PreferenceHelper.putRollNo(getContext(), mUsername);
            PreferenceHelper.putToken(getContext(), values[0]);
        }

        @Override
        protected void onPostExecute(String error) {
            mAuthTask = null;

            mProgressDialog.dismiss();

            if (error == null) {
                Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                mInterface.loginSuccessful();

            } else {

                switch (error) {
                    case "INVALID_UN_PW":
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        break;
                    //TODO add all
                    default:
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();

                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

            mProgressDialog.dismiss();
        }
    }
}
