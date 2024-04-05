package com.example.hydrohomie;

import android.os.Bundle;
import android.util.Log;
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

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext

(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        // Set the first item as "Gender"
        genderSpinner.setSelection(0);

        // Populate day spinner
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(requireContext

(),
                R.array.day_array, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // Populate month spinner
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.month_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Populate year spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, generateYears());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Populate activity level spinner
        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(),
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
//                switch (selectedActivityLevel) {
//                    case "Not Active (0 to 14 min per day)":
//                        recommendedWaterIntake = 2.5; // Default consumption for not active
//                        break;
//                    case "Moderate (15 to 45 min per day)":
//                        recommendedWaterIntake = calculateWaterIntakeForModerate(gender);
//                        break;
//                    case "Active (46 min to 3 hours per day)":
//                        recommendedWaterIntake = calculateWaterIntakeForActive(gender);
//                        break;
//                    default:
//                        recommendedWaterIntake = 2.5; // Default consumption
//                        break;
//                }

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

                //Get selected values from spinners
                String selectedActivityLevel = activityLevelSpinner.getSelectedItem().toString();
                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedWeight = info1.getText().toString();
                String selectedBirthday = daySpinner.getSelectedItem().toString() + "/" + monthSpinner.getSelectedItem().toString() + "/" + yearSpinner.getSelectedItem().toString();
                String selectedYear = yearSpinner.getSelectedItem().toString();

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
            if (info1 != null) {
                selectedWeight = info1.getText().toString();
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

            // Parse the birthdate string into a Date object

            Date birthDate;
            try {
                birthDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(selectedBirthday);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            int age = calculateAge(birthDate);

            // Get selected activity level
            String selectedActivityLevel = activityLevelSpinner.getSelectedItem().toString();

            double calculatedRecommendedWaterIntake = calculateRecommendedWaterIntake(selectedActivityLevel, selectedGender, selectedWeight, age);
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.HALF_UP);
            Log.d("Recommended Water Intake", df.format(calculatedRecommendedWaterIntake));

            //Set the recommended water intake value in the UI
            info3.setText(df.format(calculatedRecommendedWaterIntake));

            //Save the value in the database
            DatabaseReference userGoalsRef;

            // Create a reference to the user's goals in the database
            userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            // Save the information to the user's goals
            userGoalsRef.child("info1").setValue(value1);
            userGoalsRef.child("info3").setValue(value3);
            userGoalsRef.child("water_recommendation").setValue(String.valueOf(calculatedRecommendedWaterIntake));
            userGoalsRef.child("gender").setValue(selectedGender);
            userGoalsRef.child("birthday").setValue(selectedBirthday);
            userGoalsRef.child("activity_level").setValue(selectedActivityLevel);
            userGoalsRef.child("recommendedWaterIntake").setValue(calculatedRecommendedWaterIntake);

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

    private int calculateAge(Date birthDate) {
        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();
        dob.setTime(birthDate);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        // Adjust age if the current date is before the birthday
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private double calculateWaterIntakeForActivity(String selectedActivityLevel) {
        double recommendedWaterIntakeActivityLevel;
        switch (selectedActivityLevel) {
            case "Moderate (15 to 45 min per day)":
                recommendedWaterIntakeActivityLevel = 0.35;
                break;
            case "Active (46 min to 3 hours per day)":
                //Taking the average between 45 mins and 4 hours of activity
                //Total water intake plus the additional intake for active
                recommendedWaterIntakeActivityLevel = (0.35/30.0) * 142.5;
                break;
            default:
                recommendedWaterIntakeActivityLevel = 0; // Default consumption
                break;
        }
        return recommendedWaterIntakeActivityLevel;
    }

    private double calculateWaterIntakeForWeight(double weight) {
        return weight * 0.015;
    }

    private double calculateBaseWaterIntakeForAge(int age, String gender) {
        // Baseline water intake in liters

        if (gender.equalsIgnoreCase("female")) {
            //calculate different values of water intake based on age in liters
            if (age >= 1 && age <= 3) {
                return 1.0;
            } else if (age >= 4 && age <= 8) {
                return 1.2;
            } else if (age >= 9 && age <= 13) {
                return 1.4;
            } else if (age >= 14 && age <= 18) {
                return 1.6;
            } else if (age >= 65) {
                return 1.8;
            } else {
                return 2.1;
            }
        } else {  // Men and others
            //calculate different values of water intake based on age in liters
            if (age >= 1 && age <= 3) {
                return 1.3;
            } else if (age >= 4 && age <= 8) {
                return 1.2;
            } else if (age >= 9 && age <= 13) {
                return 1.6;
            } else if (age >= 14 && age <= 18) {
                return 1.9;
            } else if (age >= 65) {
                return 2.3;
            } else {
                return 2.7;
            }
        }
    }

    // TODO: Update the selectedYear/selectedAge to be the user's age
    private double calculateRecommendedWaterIntake(String selectedActivityLevel, String selectedGender, String selectedWeight, int age) {
        Log.d("AGE_DEBUG", String.valueOf(age));
        // Get weight from String
        double weight = selectedWeight.isEmpty() ? 72 : Double.parseDouble(selectedWeight);
        Log.d("WEIGHT_DEBUG", String.valueOf(weight));

        // Calculate recommended water intake based on all different factors
        double baseRecommendedWaterIntake = calculateBaseWaterIntakeForAge(age, selectedGender);
        double recommendedWaterIntakeActivityLevel = calculateWaterIntakeForActivity(selectedActivityLevel);
        double recommendedWaterIntakeWeight = calculateWaterIntakeForWeight(weight);
        double calculatedRecommendedWaterIntake;

        calculatedRecommendedWaterIntake = baseRecommendedWaterIntake
                + recommendedWaterIntakeWeight
                + recommendedWaterIntakeActivityLevel;

        return calculatedRecommendedWaterIntake;
    }

    }