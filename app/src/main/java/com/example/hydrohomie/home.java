package com.example.hydrohomie;

import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.hydrohomie.SensorReaderData;

public class home extends Fragment  {
    private static final String CHANNEL_ID = "my_channel_id";

    private ProgressBar simpleProgressBar;
    private TextView titleNotif;
    private TextView firstReadingTextView; // Added TextView for displaying the first reading
    private TextView lastReadingTextView; // Added TextView for displaying the last reading
    private Button refreshButton;
    private float waterLevel = 0; // Initial water level in percentage
    private float firstReading = -1; // Variable to hold the first reading
    private float lastReading = -1; // Variable to hold the last reading
    private final Handler handler = new Handler();
    public home() {
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        simpleProgressBar = view.findViewById(R.id.pb2);
        titleNotif = view.findViewById(R.id.titleMessage);
        firstReadingTextView = view.findViewById(R.id.firstReadingTextView); // Initialize first reading TextView
        lastReadingTextView = view.findViewById(R.id.lastReadingTextView); // Initialize last reading TextView

        // Enable options menu in the fragment
        setHasOptionsMenu(true);

        // Initialize UI with initial water level and readings
        updateUI();

        // Start the timer to periodically update sensor data
        startTimer();

        return view;
    }
    // Method to start the timer for periodic updates
    private void startTimer() {

        final int delay = 30000; // 30 seconds in milliseconds


        handler.postDelayed(new Runnable() {
            public void run() {
                // Call method to read sensor data and send updates
                //SensorReaderData.readSensorDataAndSendUpdates(home.this);

                // Repeat this runnable task after the specified delay
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    // Method to stop the timer for periodic updates
    private void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }


    // Method to update UI based on water level
    private void updateUI() {
        simpleProgressBar.setProgress((int) waterLevel);
        updateNotification();
    }

    // Method to update notification based on water level
    private void updateNotification() {
        // Update notification message based on water level
        String notificationMessage;
        if (waterLevel < 25) {
            notificationMessage = "LOW WATER LEVEL! REFILL THE BOTTLE!";
            notification();
        } else if (waterLevel < 75) {
            notificationMessage = "MODERATE WATER LEVEL. KEEP HYDRATING!";
            notification();
        } else {
            notificationMessage = "GOOD WATER LEVEL. STAY HYDRATED!";
            notification();
        }
        titleNotif.setText(notificationMessage);

        // Update first and last reading TextViews
        firstReadingTextView.setText("First Reading: " + firstReading);
        lastReadingTextView.setText("Last Reading: " + lastReading);
    }

//    // Implementation of DataUpdateListener interface method
//    @Override
//    public void onDataUpdate(float waterLevel, float first, float last) {
//        // Update water level and UI when new data is received
//        this.waterLevel = waterLevel;
//        updateUI();
//
//        // Check if it's the first reading
//        if (firstReading == -1) {
//            setFirstReading(first);
//        }
//
//        // Always update the last reading
//        setLastReading(last);
//    }

    public void notification() {
        // Define notification sound and vibration
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrationPattern = {0, 100, 200, 300}; // Vibrate for 100ms, then pause for 200ms, then vibrate for 300ms

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Set small icon
                .setSound(notificationSound) // Set notification sound
                .setVibrate(vibrationPattern) // Set vibration pattern
                .setContentTitle("Notification") // Set notification title
                .setContentText("This is a notification for you"); // Set notification content

        // Define notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Display notification
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }



}