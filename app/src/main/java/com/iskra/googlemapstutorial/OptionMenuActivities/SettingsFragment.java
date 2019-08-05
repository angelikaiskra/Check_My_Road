package com.iskra.googlemapstutorial.OptionMenuActivities;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.iskra.googlemapstutorial.R;


public class SettingsFragment extends PreferenceFragmentCompat {


//    public SettingsFragment() {
//        // empty public constructor
//    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.preferences, rootKey);
    }


}
