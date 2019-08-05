package com.iskra.googlemapstutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;


/**
 * Created by Angelika Iskra on 10.04.2018.
 */
public class SendRequestTask extends AsyncTask<String, Void, String> {

    //    private String universalToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7Il9pZCI6IjVhZGU0MmRmYjkwY2VjMDAwNDFmYmFlZiIsImVtYWlsIjoidG9qYUBlbWFpbC5jb20iLCJwYXNzd29yZCI6IiQyYiQxMCRpejJpWnJtSTNFb2hJSmpUeTROeVd1RmZHeERaSFlWb3dDQWFDU2thV3hsWWd5bmEzZE8wbSIsIl9fdiI6MH0sImlhdCI6MTUyNDU1MjI2MH0.6OAR2HWIydE5W0aOsNVy5vtPsIFxCBVSRscER04_a-Q";
    private static String TAG = SendRequestTask.class.getSimpleName();

    private Context mContext;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    public SendRequestTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            settings = PreferenceManager.getDefaultSharedPreferences(mContext);
            String token = settings.getString("token", "");

            String address = params[0];
            Log.d(TAG, params[1]);

            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestProperty("Authorization", token);

            Log.d(TAG, token);

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

            writer.write(params[1]);
            writer.flush();
            writer.close();


            InputStream inputStream;

            // get stream
            if (urlConnection.getResponseCode() <= 400) {
                inputStream = urlConnection.getInputStream();
            } else {
                inputStream = urlConnection.getErrorStream();
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String temp;
            StringBuilder response = new StringBuilder();
            while ((temp = bufferedReader.readLine()) != null) {
                response.append(temp);
            }

            return response.toString();

        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(TAG, "POST RESPONSE: " + result);
    }

}
