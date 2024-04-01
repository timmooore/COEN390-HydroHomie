package com.example.hydrohomie;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class WaterConsumptionHistory extends Fragment {
    private DatabaseReference databaseReference,databaseReference1;
    private TextView historyTextView;
    private Button selectDateButton;
    private CircularProgressIndicator circularProgress;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_water_consumption_history, container, false);

        historyTextView = view.findViewById(R.id.historyTextView);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        circularProgress = view.findViewById(R.id.circular_progress);
SensorReaderData.pushDummyDataToFirebase();
        // Set click listener for the select date button
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDatePickerDialog();
            }
        });

        // Initialize Firebase database reference

        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("user_goals").child(user.getUid()).child("water_consumption_history");

            databaseReference1 = FirebaseDatabase.getInstance().getReference("user_goals").child(user.getUid()).child("water_recommendation");
        }

        // Display water consumption history for the current date initially
        displayWaterConsumptionHistory();

        return view;
    }

    private void showCustomDatePickerDialog() {
        // Create and show the custom date picker dialog
        DatePickerDialogFragment1 datePickerDialogFragment = new DatePickerDialogFragment1();
        datePickerDialogFragment.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            // Fetch data from Firebase based on the selected date
            fetchDataFromFirebase(year, month, dayOfMonth);
        });
        datePickerDialogFragment.show(getFragmentManager(), "datePicker");
    }

    private void displayWaterConsumptionHistory() {
        // Display water consumption history for the current date initially
        if (databaseReference != null) {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Months start from 0, so add 1
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Fetch data from Firebase based on the current date
            fetchDataFromFirebase(year, month, dayOfMonth);
        }
    }




    private void fetchDataFromFirebase(int year, int month, int dayOfMonth) {
        // Construct the date string in the format "YYYY-MM-DD"
        String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth); // Note: Month starts from 0 in Java Calendar API

        // Get the reference to the Firebase database node corresponding to the selected date
        DatabaseReference dateRef = databaseReference.child(dateString);

        // Listen for a single data change event
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear previous data
                    historyTextView.setText("");
                    int totalWaterConsumption = 0;
                    // Iterate through child nodes (if any)
                    Object value = dataSnapshot.getValue();
                    if (value != null) {
                        // Append the data to the historyTextView
                        historyTextView.append(value.toString() + "\n");
                        Long waterConsumptionLong = (Long) value;
                        int waterConsumption = waterConsumptionLong != null ? waterConsumptionLong.intValue() : 0;
                        totalWaterConsumption += waterConsumption;
                        //  historyTextView.setText("1000");

                    } else {
                        Log.d("DataSnapshot", "Found null value");
                    }


                    getRecommendedWaterIntake(totalWaterConsumption);
                } else {
                    // If no data exists for the selected date
                    historyTextView.setText("No data found for the selected date");
                    double percentage = 0;
                    circularProgress.setProgress(percentage, 100);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getRecommendedWaterIntake(int totalWaterConsumption) {
        // Fetch the recommended water intake value from Firebase
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                if (snapshot1.exists()) {
                    // Get the recommended water intake value
                    String recommendedWaterIntakeString = snapshot1.getValue(String.class);
                    if (recommendedWaterIntakeString != null) {
                        // Convert the String value to long
                        double recommendedWaterIntake = Double.parseDouble(recommendedWaterIntakeString);
                        double currentValue = totalWaterConsumption;

                        double percentage = (currentValue / recommendedWaterIntake) * 100;
                        circularProgress.setProgress(percentage, 100);
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


}