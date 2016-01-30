package com.temp.jhostelapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by DSM_ on 1/29/16.
 */

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        mContext = this;

        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
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
            cancel  = true;
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

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private ProgressDialog mProgressDialog;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(getString(R.string.progress_signing));
            //mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                Thread.sleep(2000);

                return "";
            } catch (InterruptedException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;

            mProgressDialog.dismiss();

            if (success != null) {
                Intent intent = new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

