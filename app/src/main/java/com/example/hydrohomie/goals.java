package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    protected EditText info1, info3;
    protected TextView infO1, infO3;
    protected Button save, edit;
    protected Spinner genderSpinner, daySpinner, monthSpinner, yearSpinner, activityLevelSpinner; // Added activityLevelSpinner
    private FirebaseAuth mAuth;

    public goals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        mAuth = FirebaseAuth.getInstance();
        info1 = view.findViewById(R.id.info1);
        info3 = view.findViewById(R.id.info3);
        infO3 = view.findViewById(R.id.infO3);
        save = view.findViewById(R.id.Save);
        edit = view.findViewById(R.id.edit);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        daySpinner = view.findViewById(R.id.daySpinner);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);
        activityLevelSpinner = view.findViewById(R.id.activityLevelSpinner); // Initialize activityLevelSpinner

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

        // Populate activity level spinner
        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.activity_level_array_prompt, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityLevelAdapter);

        // Set a listener for activity level spinner
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedActivityLevel = parent.getItemAtPosition(position).toString();
                double recommendedWaterIntake;

                // Calculate recommended water intake based on selected activity level
                String gender = "male";
                switch (selectedActivityLevel) {
                    case "Not Active (0 to 14 min per day)":
                        recommendedWaterIntake = 2.5; // Default consumption for not active
                        break;
                    case "Moderate (15 to 45 min per day)":
                        recommendedWaterIntake = calculateWaterIntakeForModerate(gender);
                        break;
                    case "Active (46 min to 3 hours per day)":
                        recommendedWaterIntake = calculateWaterIntakeForActive(gender);
                        break;
                    default:
                        recommendedWaterIntake = 2.5; // Default consumption
                        break;
                }

                // Set the recommended water intake value in the UI (You may remove this if not needed)
                // waterRecommendation.setText(String.valueOf(recommendedWaterIntake));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle situation when nothing is selected
            }
        });

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
            String value3 = info3.getText().toString();

            // Check if a gender is selected
            String selectedGender = "";
            if (genderSpinner.getSelectedItem() != null) {
                selectedGender = genderSpinner.getSelectedItem().toString();
            }

            //Check if a weight is selected
            String selectedWeight = "";
            if (info3 != null) {
                selectedWeight = info3.toString();
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

            // Get selected activity level
            String selectedActivityLevel = activityLevelSpinner.getSelectedItem().toString();

            // Calculate recommended water intake based on all different factors
            double recommendedWaterIntakeActivityLevel;
            double recommendedWaterIntakeWeight = 0;
            double recommendedWaterIntakeAge = 0;
            double recommendedWaterIntakeGender = 0;
            double recommendedWaterIntake;
            String gender = "male";
            switch (selectedActivityLevel) {
                case "Not Active (0 to 14 min per day)":
                    recommendedWaterIntakeActivityLevel = 2.5; // Default consumption for not active
                    break;
                case "Moderate (15 to 45 min per day)":
                    recommendedWaterIntakeActivityLevel = calculateWaterIntakeForModerate(gender);
                    break;
                case "Active (46 min to 3 hours per day)":
                    recommendedWaterIntakeActivityLevel = calculateWaterIntakeForActive(gender);
                    break;
                default:
                    recommendedWaterIntakeActivityLevel = 2.5; // Default consumption
                    break;
            }

            switch (selectedGender) {
                case "female":
                    recommendedWaterIntakeGender = 2;
                    break;
                case "male":
                    recommendedWaterIntakeGender = 2.6;
                    break;
            }

            if (selectedWeight.equals("weight")) {
                recommendedWaterIntakeWeight = calculateWaterIntakeForWeight();
            }

            switch (selectedBirthday) {
                case "birthday":
                    recommendedWaterIntakeAge = calculateWaterIntakeForAge(Integer.parseInt(selectedYear), selectedGender);
                    break;
            }

            recommendedWaterIntake = recommendedWaterIntakeActivityLevel + recommendedWaterIntakeGender + recommendedWaterIntakeWeight + recommendedWaterIntakeAge;

            //Set the recommended water intake value in the UI
            info3.setText(String.valueOf(recommendedWaterIntake));

            //Save the value in the database
            DatabaseReference userGoalsRef = null;
            userGoalsRef.child("recommendedWaterIntake").setValue(recommendedWaterIntake);

            // Create a reference to the user's goals in the database
            userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            // Save the information to the user's goals
            userGoalsRef.child("info1").setValue(value1);
            userGoalsRef.child("info3").setValue(value3);
            userGoalsRef.child("water_recommendation").setValue(String.valueOf(recommendedWaterIntake));
            userGoalsRef.child("gender").setValue(selectedGender);
            userGoalsRef.child("birthday").setValue(selectedBirthday);
            userGoalsRef.child("activity_level").setValue(selectedActivityLevel);

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
        info3.setEnabled(false);
        genderSpinner.setEnabled(false);
        daySpinner.setEnabled(false);
        monthSpinner.setEnabled(false);
        yearSpinner.setEnabled(false);
        activityLevelSpinner.setEnabled(false);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
    }

    private void enableText() {
        info1.setEnabled(true);
        info3.setEnabled(true);
        genderSpinner.setEnabled(true);
        daySpinner.setEnabled(true);
        monthSpinner.setEnabled(true);
        yearSpinner.setEnabled(true);
        activityLevelSpinner.setEnabled(true);
        save.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
    }

    private double calculateWaterIntakeForModerate(String gender) {
        // Baseline water intake in liters
        double baselineIntake = gender.equalsIgnoreCase("male") ? 3.7 : 2.7;

        //Additional water intake per 30 minutes of moderate activity in liters
        double additionalWaterIntakePer30Min = 0.35;

        //Return total water intake
        return additionalWaterIntakePer30Min;
    }

    private double calculateWaterIntakeForActive(String gender) {

        //Baseline water intake in liters
        double baselineIntake = gender.equalsIgnoreCase("male") ? 3.7 : 2.7;

        //Taking the average between 45 mins and 4 hours of activity
        double additionalIntakeForActive = (0.35/30.0) * 142.5;

        //Total water intake plus the additional intake for active
        return additionalIntakeForActive;
    }

    private double calculateWaterIntakeForWeight() {
        // Baseline water intake in liters
        String gender;
        gender = null;
        double baselineIntake = gender.equalsIgnoreCase("male") ? 3.7 : 2.7;

        //calculate different values of water intake based on weight
        double weight = info1.getText().toString().isEmpty() ? 0 : Double.parseDouble(info1.getText().toString());
        double waterIntake = weight * 0.035;

        return waterIntake;
    }

    private double calculateWaterIntakeForAge(int age, String gender) {
        // Baseline water intake in liters
        double baselineIntake = gender.equalsIgnoreCase("male") ? 3.7 : 2.7;

        //calculate different values of water intake based on age in liters
        if (age >= 1 && age <= 3) {
             return 1.3;
        } else if (age >= 4 && age <= 8) {
            return 1.7;
        } else if (age >= 9 && age <= 13) {
            return 2.4;
        } else if (age >= 14 && age <= 18) {
            return 3.3;
        } else if (age >= 19 && age <= 30) {
            return 3.7;
        } else if (age >= 31 && age <= 50) {
            return 3.7;
        } else if (age >= 51 && age <= 70) {
            return 3.7;
        } else {
            return 3.7;
        }

    }
}