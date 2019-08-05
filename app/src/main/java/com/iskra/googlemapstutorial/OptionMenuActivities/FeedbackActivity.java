package com.iskra.googlemapstutorial.OptionMenuActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.iskra.googlemapstutorial.R;
import com.iskra.googlemapstutorial.SendRequestTask;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedbackActivity extends AppCompatActivity
{
    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private Button mButtonSend;
    private EditText mEditText;
    private RatingBar mRatingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mEditText = findViewById(R.id.comment_view);
        mRatingBar = findViewById(R.id.rating_bar);

        mButtonSend = (Button) findViewById(R.id.email_send_button);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mText = mEditText.getText().toString().trim();
                float mStars = mRatingBar.getRating();

                if (mText.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Wypełnij pole!", Toast.LENGTH_SHORT).show();
                } else {
                    new SendRequestTask(getApplicationContext()).execute("https://limitless-thicket-37613.herokuapp.com/feedback", createJson(mText, mStars).toString());
                    Toast.makeText(getBaseContext(), "Dziękujemy za opinię!", Toast.LENGTH_SHORT).show();
                    mEditText.setText("");
                }
            }
        });
    }

    private JSONObject createJson(String text, float stars) {

        JSONObject json = new JSONObject();

        try {
            json.put("opinion", text);
            json.put("rating", stars);

            Log.d(TAG, "Json feedback = " + json);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}

