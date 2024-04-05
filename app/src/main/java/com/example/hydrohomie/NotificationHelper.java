package com.example.hydrohomie;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final CharSequence CHANNEL_NAME = "My Channel";

    public static void showNotification(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Set the small icon for the notification
                .setContentTitle(title) // Set the title of the notification
                .setContentText(message) // Set the content text of the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Set the priority of the notification

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the Android version is Oreo or higher, then create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel
            channel.setDescription("My Notification Channel");
            // Register the notification channel with the system
            notificationManager.createNotificationChannel(channel);
        }

        // Display the notification
        notificationManager.notify(0, builder.build());
    }
}
