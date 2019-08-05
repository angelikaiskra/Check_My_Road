package com.iskra.googlemapstutorial;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Angelika Iskra on 04.04.2018.
 */

public class EventLoader extends AsyncTaskLoader<List<Event>> {

    private static final String TAG = EventLoader.class.getName();

    // Query URL
    private String mUrl;

    public EventLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    // This is on a background thread
    @Override
    public List<Event> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of events
        List<Event> events = QueryUtils.fetchEventData(mUrl);
        return events;
    }
}
