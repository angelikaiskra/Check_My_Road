package com.iskra.googlemapstutorial;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private SendRequestTask mAuthTask = null;

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mRegisterText;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    boolean cancel;
    View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRegisterText = (TextView) findViewById(R.id.register_text);
        mRegisterText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(LoginActivity.super.getApplication(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Pressed back button");
    }

    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        cancel = false;
        focusView = null;

        checkPassword(mPasswordView, password);
        checkEmail(mEmailView, email);

        if (cancel) {
            focusView.requestFocus();
        } else {
            JSONObject jsonLogin = createJson(email, password);

            mAuthTask = new SendRequestTask(this);
            mAuthTask.execute("https://limitless-thicket-37613.herokuapp.com/login", jsonLogin.toString());
            try {

                if (mAuthTask.get().contains("OK")) {
                    Log.d(TAG, mAuthTask.get());
                    StringBuilder token = new StringBuilder(mAuthTask.get());
                    token.delete(0,4);
                    StringBuilder bearer = new StringBuilder("Bearer ");
                    token = bearer.append(token);
                    editor = settings.edit();
                    editor.putBoolean("logged", true);
                    editor.putString("email", email);
                    editor.putString("key_your_nickname", email);
                    editor.putString("token", token.toString());
//                    editor.putString("password", password);
                    editor.apply();
                    Toast.makeText(this, "Zalogowano", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if (mAuthTask.get().equals("Email not existing")) {
                    Toast.makeText(this, "Ten email nie istnieje", Toast.LENGTH_SHORT).show();
                }
                else if (mAuthTask.get().equals("Invalid password")) {
                    Toast.makeText(this, "Błędne hasło", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Błąd logowania", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void checkPassword(EditText mPasswordView, String password) {
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (!(password.length() > 4)) {
            mPasswordView.setError(getString(R.string.password_error_length));
            focusView = mPasswordView;
            cancel = true;
        }
    }

    private void checkEmail(EditText mEmailView, String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;

        } else if (!email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
    }


    private JSONObject createJson(String email, String passwd) {

        JSONObject json = new JSONObject();

        try {
            json.put("email", email);

            String password = passwd;
            password = EncryptionClass.SHA1(password);

            json.put("password", password);

        } catch (JSONException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return json;
    }

// Maybe I will use it in the future
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setScaleY(3f);
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        }


}


