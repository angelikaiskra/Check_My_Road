package com.iskra.googlemapstutorial;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

/**
 * Created by Angelika Iskra on 01.04.2018.
 */
public class LocationUtils extends FragmentActivity implements
        OnLocationUpdatedListener,
        OnActivityUpdatedListener,
        LocationSource.OnLocationChangedListener,
        GoogleMap.OnMyLocationClickListener {

    private final String TAG = LocationUtils.class.getSimpleName();
    private static final int LOCATION_PERMISSION_ID = 1001;

    private GoogleMap mMap;
    private Context mContext;
    private boolean mFollowUser = true;
    protected LocationGooglePlayServicesProvider provider;
    public Location mLocation;

    private SharedPreferences mSharedPreferences;

    LocationUtils(GoogleMap googleMap, Context context) {
        mMap = googleMap;
        mContext = context;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void startMap(Activity activity) {

        mMap.setOnMyLocationClickListener(this);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setTrafficEnabled(true);
            startLocation(mContext);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocation(mContext);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (provider != null) {
            provider.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startLocation(Context context) {
        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(context)
                .logging(true)
                .build();

        smartLocation.location(provider).start(this); //onLocationUpdatedListener
        smartLocation.activity().start(this); //onActivityUpdatedListener
    }

    private void updateCamera(Location location) {

        if (mFollowUser) {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.animateCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        updateCamera(location);
        mLocation = location;
        Log.i(TAG, "On Location Updated");
    }

    @Override
    public void onLocationChanged(Location location) {
        updateCamera(location);
        mLocation = location;
        Log.i(TAG, "On Location Changed");
    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {
        Log.i(TAG, "On Activity Updated");
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        mFollowUser = !mFollowUser;
        if (mFollowUser) {
            Toast.makeText(mContext, "Śledzenie kamery włączone", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(mContext, "Śledzenie kamery wyłączone", Toast.LENGTH_SHORT).show();
        }
    }

}
