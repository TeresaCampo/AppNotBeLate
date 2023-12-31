package com.teresa.appnotbelate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "MyDebug";

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context, intent.getStringExtra("title"), intent.getStringExtra("message"));
    }
    private void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarm_channel")
                .setSmallIcon(R.drawable.ic_stat_name) // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // For pre-Oreo devices
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // To show content in lock screen

        notificationManager.notify(1, builder.build());
        Log.d(TAG, "Alarm triggered");

    }
}
