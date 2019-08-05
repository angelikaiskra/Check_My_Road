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

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private SendRequestTask mAuthTask = null;

    // UI references
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordAgainView;
    private TextView mRegisterText;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    View focusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordAgainView = (EditText) findViewById(R.id.password_again);
        mRegisterText = (TextView) findViewById(R.id.register_text);
        mRegisterText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(RegistrationActivity.super.getApplication(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Pressed back button");
    }

    private void attemptRegister() {

        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordAgainView.setError(null);

        // Store values at the time of the login attempt
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordAgain = mPasswordAgainView.getText().toString();

        focusView = null;

        boolean boolPassword = checkPassword(mPasswordView, mPasswordAgainView, password, passwordAgain);
        boolean boolEmail = checkEmail(mEmailView, email);

        if (boolEmail && boolPassword) {

            JSONObject jsonRegister = createJson(email, password);

            mAuthTask = new SendRequestTask(getApplicationContext());
            mAuthTask.execute("https://limitless-thicket-37613.herokuapp.com/registration", jsonRegister.toString());
            try {

                if (mAuthTask.get().equals("OK")) {
                    Toast.makeText(this, "Zarejestrowano, zaloguj się", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(RegistrationActivity.super.getApplication(), LoginActivity.class);
                    startActivity(intent);
                } else if (mAuthTask.get().equals("Email already taken")) {
                    Toast.makeText(this, "Użytkownik z podanym emailem już istnieje", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Błąd rejestracji", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPassword(EditText mPasswordView, EditText mPasswordAgainView, String password, String mPasswordAgain) {
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            return false;
        }

        if (!password.matches(".*\\d+.*")) {
            mPasswordView.setError(getString(R.string.password_error_number));
            focusView = mPasswordView;
            return false;
        }

        if (!(password.length() > 4)) {
            mPasswordView.setError(getString(R.string.password_error_length));
            focusView = mPasswordView;
            return false;
        }

        if (!(password.matches(mPasswordAgain))) {
            mPasswordAgainView.setError(getString(R.string.password_error_different));
            focusView = mPasswordAgainView;
            return false;
        }

        return true;
    }

    private boolean checkEmail(EditText mEmailView, String email) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            return false;

        } else if (!email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            return false;
        }
        return true;
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

}


