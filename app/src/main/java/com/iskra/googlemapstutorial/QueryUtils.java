package com.iskra.googlemapstutorial;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angelika Iskra on 03.04.2018.
 *
 * Taken from other project
 */
public abstract class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    public static List<Event> fetchEventData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list
        List<Event> events = extractFeatureFromJson(jsonResponse);

        // Return the list of events
        return events;
    }

    public static List<Event> extractFeatureFromJson(String eventJSON) {

        if (TextUtils.isEmpty(eventJSON)) {
            return null;
        }

        List<Event> events = new ArrayList<>();

        try {
            // Parse the response
            JSONObject baseJsonResponse = new JSONObject(eventJSON);
            JSONArray eventArray = baseJsonResponse.getJSONArray("EventObj");

            for (int i = 0; i < eventArray.length(); i++) {
                JSONObject currentEventArray = eventArray.getJSONObject(i);

                Long dateEvent = currentEventArray.getLong("dateEvent");
                String _id = currentEventArray.getString("_id");

                String type = currentEventArray.getString("type");

                JSONObject geometry = currentEventArray.getJSONObject("geometry");

                JSONArray coordinatesArray = geometry.getJSONArray("coordinates");
                double[] coordinates = new double[2];

                for (int j = 0; j < coordinatesArray.length(); j++) {
                    coordinates[j] = coordinatesArray.getDouble(j);
                }

                LatLng latLng = new LatLng(coordinates[0], coordinates[1]);

                Event event = new Event(dateEvent, _id, type, latLng);
                events.add(event);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the event JSON results", e);
        }

        return events;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful,
            // read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the event JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
