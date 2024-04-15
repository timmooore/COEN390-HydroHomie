package com.example.hydrohomie;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class home extends Fragment  {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference databaseReference, databaseReference1, databaseReference2;

    private ValueEventListener valueEventListener, recommendedValueEventListener;
private double recommendedWater, percentage;
private static final String TAG = "home";
    private static final String CHANNEL_ID = "my_channel_id";
    private static boolean firstDataChange = true;
    private Toolbar toolbar;
    private CircularProgressIndicator circularProgress1;
    private TextView titleNotif;
    private Button notiTestButton, dummyButton;
    private TextView accumulateReading;

    public home() {
        // require an empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDate today = LocalDate.now();

        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString());
            databaseReference2 = FirebaseDatabase.getInstance().getReference("user_goals").child(user.getUid()).child("recommendedWaterIntake");
            databaseReference1 = FirebaseDatabase.getInstance().getReference("user_data").child(user.getUid()).child(today.toString()).child("latest_time_slot");
        }
        setupUserDataListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        accumulateReading = view.findViewById(R.id.textView4);
        circularProgress1 = view.findViewById(R.id.circular_progress1);
        titleNotif = view.findViewById(R.id.titleMessage);

        // TODO: Remove
        notiTest(view);

        // Fetch the recommended water intake value from Firebase
        // then fetch data and update UI in onDataChange
        getRecommendedWaterIntake();

        return view;
    }

    private void notiTest(View view) {
        notiTestButton = view.findViewById(R.id.notiTestButton);
        dummyButton = view.findViewById(R.id.genDummyButton);
        dummyButton.setBackgroundColor(Color.TRANSPARENT);
        dummyButton.setTextColor(Color.TRANSPARENT);

        notiTestButton.setBackgroundColor(Color.TRANSPARENT);
        notiTestButton.setTextColor(Color.TRANSPARENT);

        notiTestButton.setOnClickListener(v -> {
            FirebaseAuth lAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = lAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Generate data from 8 AM to this time
                LocalTime latestTimeSlot = LocalTime.of(20, 0, 0);

                // Create a reference to the user's goals in the database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_data").child(userId);
                FirebaseUtils.generateDummyDataHistory(userRef, latestTimeSlot);
            }
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

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: called");
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        if (recommendedValueEventListener != null) {
            databaseReference2.removeEventListener(recommendedValueEventListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }


    // Method to update UI based on water level


    // Method to update notification based on water level
    private void updateNotification() {
        // Update notification message based on water level
        String notificationMessage;
        if (percentage < 25) {
            notificationMessage = "LOW WATER LEVEL! REFILL THE BOTTLE!";

        } else if (percentage < 75) {
            notificationMessage = "MODERATE WATER LEVEL. KEEP HYDRATING!";

        } else if (percentage <= 100) {
            notificationMessage = "GOOD WATER LEVEL. STAY HYDRATED!";

        } else {
            // Handle case where percentage exceeds 100
            notificationMessage = "WATER LEVEL EXCEEDS MAXIMUM CAPACITY!";

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

                        DecimalFormat df = new DecimalFormat("0");

                        accumulateReading.setText("You have consumed " + df.format(value1) + " mL so far!");
                        updateNotification();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled for databaseReference
                if (getContext() != null) {
                    // Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Context is null");
                }
            }
        });
    }

    private void setupUserDataListener() {
        valueEventListener = new ValueEventListener() {
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

                            Context context = getContext();

                            if (context != null) {
                                SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "Intake");
                                sharedPreferencesHelper.saveCurrentIntake(value1);
                            }

                            percentage = (currentValue / recommendedWaterIntake) * 100;
                            Log.d(TAG, "setupDataListener: onDataChange: percentage: " + percentage + ", value1: " + value1 + ", recommendedWaterIntake: " + recommendedWaterIntake);
                            circularProgress1.setProgress(percentage, 100);

                            DecimalFormat df = new DecimalFormat("0");

                            accumulateReading.setText("You have consumed " + df.format(value1) + " mL so far!");
                            updateNotification();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled for databaseReference
                if (getContext() != null) {
                    // Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Context is null");
                }
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    private void getRecommendedWaterIntake() {
        // Fetch the recommended water intake value from Firebase

        Log.d(TAG, "getRecommendedWaterIntake: called");

        recommendedValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                Log.d(TAG, "getRecommendedWaterIntake: onDataChange: called");
                if (snapshot1.exists()) {
                    // Get the recommended water intake value
                    String  recommendedWaterIntakeString1 = snapshot1.getValue(String.class);
                    if (recommendedWaterIntakeString1 != null) {
                        Context context = getContext();

                        if (context != null) {
                            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context, "Intake");
                            sharedPreferencesHelper.saveRecommendedIntake(recommendedWaterIntakeString1);
                            Log.d(TAG, "getRecommendedWaterIntake: onDataChange: Intake: " + sharedPreferencesHelper.getRecommendedIntake());
                        }

                        // Convert the String value to long
                        recommendedWater = Double.parseDouble(recommendedWaterIntakeString1);
                    }
                    Log.d("home", "recommendedWater : " + recommendedWater);
                    getData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled for databaseReference
                if (getContext() != null) {
                    // Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Context is null");
                }
            }
        };
        databaseReference2.addValueEventListener(recommendedValueEventListener);
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