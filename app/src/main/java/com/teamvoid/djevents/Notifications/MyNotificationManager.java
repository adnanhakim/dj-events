package com.teamvoid.djevents.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.teamvoid.djevents.Activities.MainActivity;
import com.teamvoid.djevents.R;
import com.teamvoid.djevents.Utils.Constants;

public class MyNotificationManager {
    private Context context;
    private static MyNotificationManager instance;
    private final int REQUEST_CODE = 123;

    private MyNotificationManager(Context context) {
        this.context = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (instance == null)
            instance = new MyNotificationManager(context);
        return instance;
    }

    public void displayNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.dj_logo)
                .setContentTitle(title)
                .setContentText(body);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
