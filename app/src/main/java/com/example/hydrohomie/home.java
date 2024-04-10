package com.example.hydrohomie;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class home extends Fragment  {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference databaseReference,databaseReference1,databaseReference2;
private double recommendedWater, percentage;
private static final String TAG = "home";
    private static final String CHANNEL_ID = "my_channel_id";
    private static boolean firstDataChange = true;
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


        // TODO: Yas fix the databaseRef for recommendation
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString());
            databaseReference2 = FirebaseDatabase.getInstance().getReference("user_goals").child(user.getUid()).child("recommendedWaterIntake");
            databaseReference1 = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString()).child("latest_time_slot");
        }

        // TODO: Remove
        notiTest(view);
   // Initialize last reading TextView


        // Start the timer to periodically update sensor data
        startTimer();

        // Fetch the recommended water intake value from Firebase
        // then fetch data and update UI in onDataChange
        getRecommendedWaterIntake();
        setupUserDataListener();

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

        dummyButton.setOnClickListener(v -> dummyData());
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

    private void getData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("home", "getData: onDataChange: called");
                String latestTimeSlot = snapshot.child("latest_time_slot").getValue(String.class);

                if (latestTimeSlot != null) {
                    // DatabaseReference dataRef = databaseReference1.child(value);
                    Double value1 = snapshot.child("values").child(latestTimeSlot).getValue(Double.class);
                    // Set the value to accumulateReading
                    if (value1 != null && value1 != 0.0) {
                        double recommendedWaterIntake = recommendedWater;
                        double currentValue = value1 / 1000;

                        percentage = (currentValue / recommendedWaterIntake) * 100;
                        Log.d(TAG, "getData: onDataChange: percentage: " + percentage + ", value1: " + value1 + ", recommendedWaterIntake: " + recommendedWaterIntake);
                        circularProgress1.setProgress(percentage, 100);
                        accumulateReading.setText("You have consumed " + value1 + " mL so far!");
                        updateNotification();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled for databaseReference
                Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupUserDataListener() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firstDataChange) {
                    firstDataChange = false;
                } else {
                    Log.d("home", "setupUserDataListener: onDataChange: called");
                    String latestTimeSlot = snapshot.child("latest_time_slot").getValue(String.class);

                    if (latestTimeSlot != null) {
                        // DatabaseReference dataRef = databaseReference1.child(value);
                        Double value1 = snapshot.child("values").child(latestTimeSlot).getValue(Double.class);
                        // Set the value to accumulateReading
                        if (value1 != null && value1 != 0.0) {
                            double recommendedWaterIntake = recommendedWater;
                            double currentValue = value1 / 1000;

                            percentage = (currentValue / recommendedWaterIntake) * 100;
                            Log.d(TAG, "getData: onDataChange: percentage: " + percentage + ", value1: " + value1 + ", recommendedWaterIntake: " + recommendedWaterIntake);
                            circularProgress1.setProgress(percentage, 100);
                            accumulateReading.setText("You have consumed " + value1 + " mL so far!");
                            updateNotification();
                        }
                    }
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

        Log.d(TAG, "getRecommendedWaterIntake: called");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                Log.d(TAG, "getRecommendedWaterIntake: onDataChange: called");
                if (snapshot1.exists()) {
                    // Get the recommended water intake value
                    String  recommendedWaterIntakeString1 = snapshot1.getValue(String.class);
                    if (recommendedWaterIntakeString1 != null) {
                        // Convert the String value to long
                        recommendedWater = Double.parseDouble(recommendedWaterIntakeString1);
                    }
                    Log.d("home", "recommendedWater : " + recommendedWater);
                    getData();
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