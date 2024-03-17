package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class goals extends Fragment {

    protected EditText info1, info2, info3, waterRecommendation;
    protected TextView infO1, infO2, infO3;
    protected Button save, edit;
    protected Spinner genderSpinner, daySpinner, monthSpinner, yearSpinner;
    private FirebaseAuth mAuth;

    public goals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        mAuth = FirebaseAuth.getInstance();

        info1 = view.findViewById(R.id.info1);
        info2 = view.findViewById(R.id.info2);
        info3 = view.findViewById(R.id.info3);
        infO1 = view.findViewById(R.id.infO1);
        infO2 = view.findViewById(R.id.infO2);
        infO3 = view.findViewById(R.id.infO3);
        save = view.findViewById(R.id.Save);
        edit = view.findViewById(R.id.edit);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        daySpinner = view.findViewById(R.id.daySpinner);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        waterRecommendation = view.findViewById(R.id.waterRecommendation);

        // Populate gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        // Set the first item as "Gender"
        genderSpinner.setSelection(0);

        // Populate day spinner
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.day_array, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // Populate month spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.month_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Populate year spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, generateYears());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Set default water recommendation
        waterRecommendation.setText("2.5");

        // Retrieve and display data when the fragment is created
        retrieveAndDisplayData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
                disableText();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableText();
            }
        });

        return view;
    }

    private List<String> generateYears() {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        return years;
    }

    private void retrieveAndDisplayData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the user's goals in the database
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve data from the database
                        String dbValue1 = dataSnapshot.child("info1").getValue(String.class);
                        String dbValue2 = dataSnapshot.child("info2").getValue(String.class);
                        String dbValue3 = dataSnapshot.child("info3").getValue(String.class);

                        info1.setText(dbValue1);
                        info2.setText(dbValue2);
                        info3.setText(dbValue3);

                        // Disable or enable text based on data presence
                        if (dbValue1 != null && dbValue2 != null && dbValue3 != null) {
                            disableText();
                        } else {
                            enableText();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }

    private void saveInformation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            String value1 = info1.getText().toString();
            String value2 = info2.getText().toString();
            String value3 = info3.getText().toString();

            // Check if a gender is selected
            String selectedGender = "";
            if (genderSpinner.getSelectedItem() != null) {
                selectedGender = genderSpinner.getSelectedItem().toString();
            }

            // Check if a birthday is selected
            String selectedDay = "";
            if (daySpinner.getSelectedItem() != null) {
                selectedDay = daySpinner.getSelectedItem().toString();
            }

            String selectedMonth = "";
            if (monthSpinner.getSelectedItem() != null) {
                selectedMonth = monthSpinner.getSelectedItem().toString();
            }

            String selectedYear = "";
            if (yearSpinner.getSelectedItem() != null) {
                selectedYear = yearSpinner.getSelectedItem().toString();
            }

            // Combine selected day, month, and year into a single string for birthday
            String selectedBirthday = selectedDay + "/" + selectedMonth + "/" + selectedYear;

            double userWeight = Double.parseDouble(value1); // Assuming value1 contains the user's weight in kilograms
            boolean isPhysicallyActive = false; // You need to determine the user's activity level

            // Calculate recommended water intake using the WaterIntakeCalculator
            double recommendedWaterIntake = WaterIntakeCalculator.calculateRecommendedWaterIntake(userWeight, selectedGender, isPhysicallyActive);
            String waterRec = String.valueOf(recommendedWaterIntake);

            // Create a reference to the user's goals in the database
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            // Save the information to the user's goals
            userGoalsRef.child("info1").setValue(value1);
            userGoalsRef.child("info2").setValue(value2);
            userGoalsRef.child("info3").setValue(value3);
            userGoalsRef.child("water_recommendation").setValue(waterRec);
            userGoalsRef.child("gender").setValue(selectedGender);
            userGoalsRef.child("birthday").setValue(selectedBirthday);

            // Retrieve data from userGoalsRef and update TextViews
            userGoalsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve data from the database
                        String dbValue1 = dataSnapshot.child("info1").getValue(String.class);
                        String dbValue2 = dataSnapshot.child("info2").getValue(String.class);
                        String dbValue3 = dataSnapshot.child("info3").getValue(String.class);
                        info1.setText(dbValue1);
                        info2.setText(dbValue2);
                        info3.setText(dbValue3);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }

    private void disableText() {
        info1.setEnabled(false);
        info2.setEnabled(false);
        info3.setEnabled(false);
        waterRecommendation.setEnabled(false);
        genderSpinner.setEnabled(false);
        daySpinner.setEnabled(false);
        monthSpinner.setEnabled(false);
        yearSpinner.setEnabled(false);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
    }

    private void enableText() {
        info1.setEnabled(true);
        info2.setEnabled(true);
        info3.setEnabled(true);
        waterRecommendation.setEnabled(true);
        genderSpinner.setEnabled(true);
        daySpinner.setEnabled(true);
        monthSpinner.setEnabled(true);
        yearSpinner.setEnabled(true);
        save.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
    }

    public static class WaterIntakeCalculator {

        // Calculate the recommended daily water intake in milliliters based on user's characteristics
        public static double calculateRecommendedWaterIntake(double weight, String gender, boolean isPhysicallyActive) {
            // Baseline water intake in milliliters
            double baselineIntake = (gender.equalsIgnoreCase("male")) ? 3700 : 2700;

            // Activity adjustment in milliliters
            double activityAdjustment = (isPhysicallyActive) ? 500 : 0; // Additional 500 mL if physically active

            // Weight adjustment in milliliters
            double weightAdjustment = weight * 32.5; // Average of 30-35 mL/kg

            // Calculate total recommended water intake
            return baselineIntake + activityAdjustment + weightAdjustment;
        }
    }
}
