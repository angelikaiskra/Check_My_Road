package com.iskra.googlemapstutorial;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.iskra.googlemapstutorial.OptionMenuActivities.AboutUsActivity;
import com.iskra.googlemapstutorial.OptionMenuActivities.FeedbackActivity;
import com.iskra.googlemapstutorial.OptionMenuActivities.SettingsActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Angelika Iskra on 3.02.2018.
 */

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private android.support.v7.widget.Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    protected LoadListView loadListView;

    private SharedPreferences.Editor editor;
    private SharedPreferences mPreferences;

    NavigationView navigationView;
    View headerView;

    LocationUtils locationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        checkIfLogged();

        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupActionBar();
        setupNavigationView();

        // Keeps the screen always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        locationUtils = new LocationUtils(googleMap, getApplicationContext());
        locationUtils.startMap(this);

        final ListView eventListView = (ListView) findViewById(R.id.list_view_info);
        TextView noDataText = (TextView) findViewById(R.id.no_data);
        loadListView = new LoadListView(this, getLoaderManager(), googleMap, eventListView, noDataText);
        loadListView.doWork();

        setRepeatingAsyncTask();
    }

    @Override
    protected void onResume() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Preferences refreshed");

        TextView mTextMyNick = headerView.findViewById(R.id.my_nick);
        String myNick = mPreferences.getString(getString(R.string.key_your_nickname), "Mój nick");
        mTextMyNick.setText(myNick);

        super.onResume();
    }

    private void checkIfLogged() {
        boolean isLogged = mPreferences.getBoolean("logged", false);

        if (isLogged) {
            Toast.makeText(getApplicationContext(), "Jesteś zalogowany", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        loadListView.stopNotifications();
        super.onDestroy();
    }

    private void setRepeatingAsyncTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        loadListView.resetLoader();
                        Log.d(TAG, "Loader Reset");
                    }
                });
            }
        };

        String syncFrequencyString = mPreferences.getString(getString(R.string.list_sync_frequency), "30");
        int syncFrequency = Integer.parseInt(syncFrequencyString);
        timer.schedule(task, 0, syncFrequency * 1000);  // interval of x seconds
    }


    private void setupNavigationView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        headerView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);
    }


    private void setupActionBar() {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        TextView mTextMyNick = findViewById(R.id.my_nick);
        String myNick = mPreferences.getString(getString(R.string.key_your_nickname), "Mój nick");
        mTextMyNick.setText(myNick);

        int id = item.getItemId();
        if (id == R.id.my_account) {
            checkIfLogged();
        } else if (id == R.id.achievements) {
            Toast.makeText(this, "Dostępne w przyszłych wersjach aplikacji", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.my_notifications) {
            Toast.makeText(this, "Dostępne w przyszłych wersjach aplikacji", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.current_informations) {
            this.onPause();
            Intent intent = new Intent(MainActivity.this, CurrentInformationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings) {
            this.onPause();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.log_out) {
            editor = mPreferences.edit();
            editor.putBoolean("logged", false);
            editor.apply();
            Toast.makeText(getApplicationContext(), "Pomyślnie wylogowano", Toast.LENGTH_LONG).show();
            checkIfLogged();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_setting: {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.about_us: {
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.feedback: {
                Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            }
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_fotoradar: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "SpeedCamera");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_korek: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "TrafficJam");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_kontrola: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "PoliceControl");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_wypadek: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "CarAccident");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_remont: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "RoadBuilding");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_blokada: {
                Intent intent = new Intent(this, CustomDialog.class);
                intent.putExtra("EVENT_NAME", "Blockade");
                intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                startActivity(intent);
                break;
            }
            case R.id.button_fullscreen: {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent(this, NavigationActivity.class);
                    intent.putExtra("latitude", locationUtils.mLocation.getLatitude());
                    intent.putExtra("longitude", locationUtils.mLocation.getLongitude());
                    startActivity(intent);
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_cant_open, Toast.LENGTH_LONG).show();
                    break;
                }
            }
            default: {
                Toast.makeText(this, "Default", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

}
