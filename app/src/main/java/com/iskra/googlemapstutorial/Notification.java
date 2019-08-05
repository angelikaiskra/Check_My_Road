package com.iskra.googlemapstutorial;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Angelika Iskra on 18.04.2018.
 */
public class Notification {

    public static final String TAG = Notification.class.getSimpleName();

    private Context context;
    private Boolean notificationOn;
    private String stringSound;
    private Boolean ifVibrate;
    private Uri uriSound;
    private long[] vibrate;

    private static Boolean ifFirst = true;

    private NotificationManager mNotificationManager;
    private static SharedPreferences mPreferences;


    Notification(Context context) {
        this.context = context;
    }

    public void createNotification(int numberEvents) {
        refreshPreferences();

        if (ifFirst) {
            Log.d(TAG, "Create notification first time");

            if (ifVibrate)
                vibrate = new long[]{0, 1000, 1000, 1000};
            else
                vibrate = new long[] {0,0};

            if (notificationOn) {
                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("default",
                            "Check My Road Channel",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("NOTIFICATION_CHANNEL_DESCRIPTION");
                    mNotificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder mBuilder = buildBuilder(numberEvents, uriSound, vibrate);
                if (mNotificationManager != null)
                    mNotificationManager.notify(0, mBuilder.build());

                ifFirst = false;
            }
        } else {

            NotificationCompat.Builder mBuilder = buildBuilder(numberEvents, uriSound, vibrate);
            if (mNotificationManager != null)
                mNotificationManager.notify(0, mBuilder.build());
        }
    }

    private NotificationCompat.Builder buildBuilder(int numberEvents, Uri uriSound, long[] vibrate) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.ic_notifications_blue) // notification icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentTitle("Nowe zdarzenia w Twojej okolicy") // title for notification
                .setContentText(String.valueOf(numberEvents))// message for notification
                .setSound(uriSound) // set alarm sound for notification
                .setVibrate(vibrate)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(true); // clear notification after click

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);

        return mBuilder;
    }

    public void destroyNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(0);
            ifFirst = true;
        }
    }

    private void refreshPreferences() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        notificationOn = mPreferences.getBoolean(context.getString(R.string.check_box_notifications_on), true);
        stringSound = mPreferences.getString(context.getString(R.string.key_notifications_new_event_ringtone), null);
        ifVibrate = mPreferences.getBoolean(context.getString(R.string.key_vibrate), true);
        uriSound = Uri.parse(stringSound);
    }
}
