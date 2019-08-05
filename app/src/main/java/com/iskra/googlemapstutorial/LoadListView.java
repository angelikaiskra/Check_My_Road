package com.iskra.googlemapstutorial;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Angelika Iskra on 04.04.2018.
 */

public class LoadListView implements
        LoaderManager.LoaderCallbacks<List<Event>> {

    private static final String REQUEST_URL = "https://limitless-thicket-37613.herokuapp.com/api";
    private static final int EVENT_LOADER_ID = 1;

    public static final String TAG = LoadListView.class.getSimpleName();

    private Context mContext;
    private EventAdapter mAdapter;
    private LoaderManager mLoaderManager;
    private GoogleMap mGoogleMap;
    private MarkerOptions mMarkerOptions;
    private ListView eventListView;
    private TextView noDataText;

    private int mNumberEvents;

    private Notification mNotification;


    LoadListView(Context context, LoaderManager loaderManager, GoogleMap mGoogleMap, ListView eventListView, TextView noDataText) {
        this.mContext = context;
        this.mLoaderManager = loaderManager;
        this.mGoogleMap = mGoogleMap;
        this.eventListView = eventListView;
        this.noDataText = noDataText;

        mNotification = new Notification(context);
    }

    public void doWork() {

        mAdapter = new EventAdapter(mContext, new ArrayList<Event>());
        eventListView.setAdapter(mAdapter);

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        if (networkInfo != null && networkInfo.isConnected()) {
            mLoaderManager.initLoader(EVENT_LOADER_ID, null, this);
        } else {
            Log.d(TAG, "Unable to connect");
            Toast.makeText(mContext, "Błąd odświeżania", Toast.LENGTH_SHORT).show();
        }
    }


    public void resetLoader() {
        mLoaderManager.restartLoader(0, null, this);
    }

    private void setOnClickListener(final List<Event> events) {
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveCamera(events.get(position).getCoordinates());
            }
        });
    }

    private void moveCamera(LatLng location) {
        if (location != null) {
            LatLng latLng = new LatLng(location.latitude, location.longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mGoogleMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new EventLoader(mContext, REQUEST_URL);
    }


    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> events) {

        if (events != null && !events.isEmpty()) {
            mNumberEvents = events.size();
            mAdapter.clear();

            mAdapter.addAll(events);

            for (int i=0; i<events.size(); i++) {

                if (mGoogleMap != null) {
                    mMarkerOptions = createMarker(events.get(i).getCoordinates(), events.get(i).getType(), events.get(i).getDateEvent());
                    mGoogleMap.addMarker(mMarkerOptions);
                    setOnClickListener(events);
                }

                if (noDataText != null)
                    noDataText.setVisibility(View.INVISIBLE);
            }
        } else {
            Log.d(TAG, "No data");

            if (noDataText != null)
                noDataText.setVisibility(View.VISIBLE);
        }

            if (mNumberEvents != 0) {
                mNotification.createNotification(mNumberEvents);
                Log.d(TAG, "Notifications updated. Number events = " + mNumberEvents);
            } else {
                mNotification.destroyNotification();
            }

        mLoaderManager.destroyLoader(EVENT_LOADER_ID);
    }

    public void stopNotifications() {
        mNotification.destroyNotification();
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        // Loader reset, so I can clear out our existing data
        mAdapter.clear();
    }

    private MarkerOptions createMarker(LatLng coordinates, String type, Long dateEvent) {

        switch (type) {
            case "SpeedCamera": {
                BitmapDescriptor bitmapSpeedCamera = BitmapDescriptorFactory.fromResource(R.drawable.ic_speed_camera);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Fotoradar")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapSpeedCamera);
            }
            case "TrafficJam": {
                BitmapDescriptor bitmapTrafficJam = BitmapDescriptorFactory.fromResource(R.drawable.ic_traffic_jam);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Korek")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapTrafficJam);
            }
            case "PoliceControl": {
                BitmapDescriptor bitmapPoliceControl = BitmapDescriptorFactory.fromResource(R.drawable.ic_police_control);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Kontrola policyjna")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapPoliceControl);
            }
            case "CarAccident": {
                BitmapDescriptor bitmapCarAccident = BitmapDescriptorFactory.fromResource(R.drawable.ic_car_accident);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Wypadek samochodowy")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapCarAccident);
            }
            case "RoadBuilding": {
                BitmapDescriptor bitmapRoadBuilding = BitmapDescriptorFactory.fromResource(R.drawable.ic_road_building);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Budowa drogi")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapRoadBuilding);
            }
            case "Blockade": {
                BitmapDescriptor bitmapBlockade = BitmapDescriptorFactory.fromResource(R.drawable.ic_blockade);
                return new MarkerOptions()
                        .position(coordinates)
                        .title("Blokada drogi")
                        .snippet(formatDate(dateEvent))
                        .icon(bitmapBlockade);
            }
        }
        return null;
    }

    private String formatDate(Long dateEvent) {
        Date dateObject = new Date(dateEvent);

        return EventAdapter.formatDate(dateObject) + " " + EventAdapter.formatTime(dateObject);
    }

}
