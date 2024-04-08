package com.example.hydrohomie;

import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.hydrohomie.SensorReaderData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import java.time.LocalTime;
public class home extends Fragment  {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference databaseReference,databaseReference1,databaseReference2;
private double recommandwater, percentage;

    private static final String CHANNEL_ID = "my_channel_id";
    private Toolbar toolbar;
    private CircularProgressIndicator circularProgress1;
    private TextView titleNotif;


    private Button notiTestButton, dummyButton;
    private TextView accumulateReading;


    private float waterLevel = 0; // Initial water level in percentage

    private final Handler handler = new Handler();
    public home() {
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        accumulateReading= view.findViewById(R.id.textView4);
        circularProgress1 = view.findViewById(R.id.circular_progress1);
        titleNotif = view.findViewById(R.id.titleMessage);
        LocalDate today = LocalDate.now();



        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString()).child("latest_time_slot");
            databaseReference2 = FirebaseDatabase.getInstance().getReference("user_goals").child(user.getUid()).child("recommendedWaterIntake");
            databaseReference1 = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString()).child("values");
        }

        // TODO: Remove
        notiTest(view);
   // Initialize last reading TextView


        // Start the timer to periodically update sensor data
        startTimer();

        getdata();
        return view;
    }

    private void notiTest(View view) {
        notiTestButton = view.findViewById(R.id.notiTestButton);
        dummyButton = view.findViewById(R.id.genDummyButton);

        notiTestButton.setOnClickListener(v -> {
            SensorData sd = new SensorData(2.1, LocalTime.now());
            boolean result = SensorData.isHydrated(SensorData.dataPoints, sd);
            Log.d("NOTI_TEST", "Result is: " + result);
        });

        dummyButton.setOnClickListener(v -> {
            dummyData();
        });
    }

    private void dummyData() {
        // Generate data from 8 AM to this time
        LocalTime latestTimeSlot = LocalTime.of(17, 42, 55);

        FirebaseAuth lAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = lAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the user's goals in the database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_data").child(userId);

            FirebaseUtils.generateDummyData(userRef, latestTimeSlot);
        }
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


    // Method to update notification based on water level
    private void updateNotification() {
        // Update notification message based on water level
        String notificationMessage;
        if (percentage < 25) {
            notificationMessage = "LOW WATER LEVEL! REFILL THE BOTTLE!";
            notification();
        } else if (percentage < 75) {
            notificationMessage = "MODERATE WATER LEVEL. KEEP HYDRATING!";
            notification();
        } else if (percentage <= 100) {
            notificationMessage = "GOOD WATER LEVEL. STAY HYDRATED!";
            notification();
        } else {
            // Handle case where percentage exceeds 100
            notificationMessage = "WATER LEVEL EXCEEDS MAXIMUM CAPACITY!";
            notification();
        }
        titleNotif.setText(notificationMessage);
    }



    private void getdata() {
        getRecommendedWaterIntake();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);

                if (value != null) {
                    DatabaseReference dataRef = databaseReference1.child(value);
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           double  value1 = snapshot.getValue(Double.class);
                            // Set the value to accumulateReading
                            if (value1 != 0.0) {




                                double recommendedWaterIntake = recommandwater;
                                double currentValue = value1/1000;

                                 percentage = (currentValue / recommendedWaterIntake) * 100;
                                circularProgress1.setProgress(percentage, 100);
                                accumulateReading.setText("Level Water Consummed "+value1+" mL");
                                updateNotification();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled for dataRef
                            Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled for databaseReference
                Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRecommendedWaterIntake() {
        // Fetch the recommended water intake value from Firebase
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if (snapshot1.exists()) {
                    // Get the recommended water intake value
                  String  recommendedWaterIntakeString1 = snapshot1.getValue(String.class);
                    if (recommendedWaterIntakeString1 != null) {
                        // Convert the String value to long
                        double recommendedWaterIntake = Double.parseDouble(recommendedWaterIntakeString1);
                        recommandwater=recommendedWaterIntake;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch recommended water intake: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void notification() {
//        // Define notification sound and vibration
//        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        long[] vibrationPattern = {0, 100, 200, 300}; // Vibrate for 100ms, then pause for 200ms, then vibrate for 300ms
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
//                .setSmallIcon(R.drawable.notification) // Set small icon
//                .setSound(notificationSound) // Set notification sound
//                .setVibrate(vibrationPattern) // Set vibration pattern
//                .setContentTitle("Notification") // Set notification title
//                .setContentText("This is a notification for you"); // Set notification content
//
//        // Define notification channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Display notification
//        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        if (notificationManager != null) {
//            notificationManager.notify(0, builder.build());
//        }
    }



}