package com.iskra.googlemapstutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;


public class CustomDialog extends Activity {

    public static final String TAG = CustomDialog.class.getSimpleName();

    private Button mButtonSend, mButtonCancel;
    private TextView mEventText;
    private ImageView mIconView;
    private String mEventName;
    private Double mLatitude;
    private Double mLongitude;

    private SendRequestTask mRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.event_activity);

        mEventText = (TextView) findViewById(R.id.event_text);
        mIconView = (ImageView) findViewById(R.id.backgound_icon);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mLatitude = extras.getDouble("latitude");
            mLongitude = extras.getDouble("longitude");

            mEventName = extras.getString("EVENT_NAME");

            switch (mEventName) {
                case "SpeedCamera": {
                    mEventText.setText(getString(R.string.button_fotoradar));
                    mIconView.setImageResource(R.drawable.ic_speed_camera);
                    break;
                }
                case "TrafficJam": {
                    mEventText.setText(getString(R.string.button_korek));
                    mIconView.setImageResource(R.drawable.ic_traffic_jam);
                    break;
                }
                case "PoliceControl": {
                    mEventText.setText(getString(R.string.button_kontrola));
                    mIconView.setImageResource(R.drawable.ic_police_control);
                    break;
                }
                case "CarAccident": {
                    mEventText.setText(getString(R.string.button_wypadek));
                    mIconView.setImageResource(R.drawable.ic_car_accident);
                    break;
                }
                case "RoadBuilding": {
                    mEventText.setText(getString(R.string.button_remont));
                    mIconView.setImageResource(R.drawable.ic_road_building);
                    break;
                }
                case "Blockade": {
                    mEventText.setText(getString(R.string.button_blokada));
                    mIconView.setImageResource(R.drawable.ic_blockade);
                    break;
                }
            }
        }

        mButtonSend = findViewById(R.id.button_send);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();

                try {
                    json.put("type", mEventName);
                    JSONObject jsonGeometry = new JSONObject();
                    jsonGeometry.put("type", "point");
                    JSONArray coordinatesArray = new JSONArray();
                    coordinatesArray.put(mLatitude);
                    coordinatesArray.put(mLongitude);
                    jsonGeometry.put("coordinates", coordinatesArray);
                    json.put("geometry", jsonGeometry);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mRequestTask = new SendRequestTask(getApplicationContext());
                mRequestTask.execute("https://limitless-thicket-37613.herokuapp.com/api", json.toString());

                try {
                    if(mRequestTask.get().equals("Forbidden")) {
                        Toast.makeText(getApplicationContext(), "Zaloguj się!", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Pomyslnie wysłano", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonCancel = findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        super.onCreate(savedInstanceState);
    }


}
