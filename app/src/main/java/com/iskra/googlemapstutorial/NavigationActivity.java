package com.iskra.googlemapstutorial;

import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Angelika Iskra on 27.02.2018.
 */

public class NavigationActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_nav);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStop() {
        loadListView.resetLoader();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
    }
}
