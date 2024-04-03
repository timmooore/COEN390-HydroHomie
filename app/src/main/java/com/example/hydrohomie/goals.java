package com.example.hydrohomie;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class goals extends Fragment {

    protected EditText info1, info3, birthday;
    protected TextView infO1;
    protected Button save, edit;
    protected Spinner genderSpinner, daySpinner, monthSpinner, yearSpinner, activityLevelSpinner; // Added activityLevelSpinner
    private FirebaseAuth mAuth;

    public goals() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        mAuth = FirebaseAuth.getInstance();
        info1 = view.findViewById(R.id.info1);
        info3 = view.findViewById(R.id.info3);
        save = view.findViewById(R.id.Save);
        edit = view.findViewById(R.id.Edit);
        birthday = view.findViewById(R.id.birthday);
        activityLevelSpinner = view.findViewById(R.id.activityLevelSpinner); // Initialize activityLevelSpinner
        genderSpinner = view.findViewById(R.id.genderSpinner);


        // Populate gender spinner
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext

                        (),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        // Set the first item as "Gender"
        genderSpinner.setSelection(0);

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

                //Get selected values from spinners
                String selectedActivityLevel = activityLevelSpinner.getSelectedItem().toString();
                String selectedGender = genderSpinner.getSelectedItem().toString();
                String selectedWeight = info1.getText().toString();
                String selectedBirthday = birthday.getText().toString();

                // Calculate recommendedWaterIntake
                double recommendedWaterIntake = calculatedRecommendedWaterIntake(selectedActivityLevel, selectedGender, selectedWeight, selectedBirthday);

                Log.d("Recommended Water Intake", String.valueOf(recommendedWaterIntake));

                // Set recommendedWaterIntake to info3 EditText field
                info3.setText(String.valueOf(recommendedWaterIntake));


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
                selectedWeight = info3.getText().toString();
            }

            // Check if a birthday is selected
            String selectedDay = "";
            if (birthday.getText() != null) {
                selectedDay = birthday.getText().toString();
            }

            // Combine selected day, month, and year into a single string for birthday
            String selectedBirthday = selectedDay;

            // Get selected activity level
            String selectedActivityLevel = activityLevelSpinner.getSelectedItem().toString();

            // Calculate recommended water intake based on all different factors
            double recommendedWaterIntakeActivityLevel;
            double recommendedWaterIntakeWeight = 0;
            double recommendedWaterIntakeAge = 0;
            double recommendedWaterIntakeGender = 0;
            double calculatedRecommendedWaterIntake;
            String gender = "male";
            switch (selectedActivityLevel) {
                case "Moderate (15 to 45 min per day)":
                    recommendedWaterIntakeActivityLevel = calculateWaterIntakeForModerate(gender);
                    break;
                case "Active (46 min to 3 hours per day)":
                    recommendedWaterIntakeActivityLevel = calculateWaterIntakeForActive(gender);
                    break;
                default:
                    recommendedWaterIntakeActivityLevel = 0; // Default consumption
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
                    recommendedWaterIntakeAge = calculateBaseWaterIntakeForAge(Integer.parseInt(selectedBirthday), selectedGender);
                    break;
            }

            calculatedRecommendedWaterIntake = recommendedWaterIntakeActivityLevel + recommendedWaterIntakeGender + recommendedWaterIntakeWeight + recommendedWaterIntakeAge;

            //Set the recommended water intake value in the UI
            info3.setText(String.valueOf(calculatedRecommendedWaterIntake));

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
        activityLevelSpinner.setEnabled(false);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
    }

    private void enableText() {
        info1.setEnabled(true);
        info3.setEnabled(true);
        genderSpinner.setEnabled(true);
        activityLevelSpinner.setEnabled(true);
        save.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
    }

    private double calculateWaterIntakeForModerate(String gender) {
        // Baseline water intake in liters
        double baselineIntake = gender.equalsIgnoreCase("male") ? 3.7 : 2.7;

        //Additional water intake per 30 minutes of moderate activity in liters
        double additionalIntake = 0.35;

        //Return total water intake
        return baselineIntake + additionalIntake;
    }

    private double calculateWaterIntakeForActive(String gender) {
        //Taking the average between 45 mins and 4 hours of activity
        //Total water intake plus the additional intake for active
        return (0.35 / 30.0) * 142.5;
    }

    private double calculateWaterIntakeForWeight() {
        //calculate different values of water intake based on weight
        double weight = info1.getText().toString().isEmpty() ? 0 : Double.parseDouble(info1.getText().toString());
        Log.d("WEIGHT_DEBUG", String.valueOf(weight));
        return weight * 0.035;
    }


    private double calculatedRecommendedWaterIntake(String selectedActivityLevel, String selectedGender, String selectedWeight, String selectedBirthday) {
        // Initialize variables
        double recommendedWaterIntakeActivityLevel = 0;
        double baseRecommendedWaterIntake = 0;
        double weightAdjustedWaterIntake = 0;
        double calculatedRecommendedWaterIntake;

        // Validate and parse selectedBirthday to age
        try {
            if (!selectedBirthday.isEmpty() && selectedBirthday.matches("\\d+")) {
                int age = Integer.parseInt(selectedBirthday); // Assuming selectedBirthday is an age
                baseRecommendedWaterIntake = calculateBaseWaterIntakeForAge(age, selectedGender);
            } else {
                Log.d("Error", "Invalid birthday input: " + selectedBirthday);
                baseRecommendedWaterIntake = calculateBaseWaterIntakeForAge(25, selectedGender); // Default age
            }
        } catch (NumberFormatException e) {
            Log.e("NumberFormatException", "Could not parse selectedBirthday: " + selectedBirthday, e);
            baseRecommendedWaterIntake = calculateBaseWaterIntakeForAge(25, selectedGender); // Default age for exception
        }

        // Validate and parse selectedWeight
        try {
            if (!selectedWeight.isEmpty() && selectedWeight.matches("\\d+(\\.\\d+)?")) {
                double weight = Double.parseDouble(selectedWeight);
                weightAdjustedWaterIntake = calculateWaterIntakeForWeight(weight);
            } else {
                Log.d("Error", "Invalid weight input: " + selectedWeight);
                weightAdjustedWaterIntake = 0; // Assuming no weight input leads to no adjustment
            }
        } catch (NumberFormatException e) {
            Log.e("NumberFormatException", "Could not parse selectedWeight: " + selectedWeight, e);
            weightAdjustedWaterIntake = 0; // Default behavior for exception
        }

        // Adjust based on activity level
        switch (selectedActivityLevel) {
            case "Moderate (15 to 45 min per day)":
                recommendedWaterIntakeActivityLevel = 0.35;
                break;
            case "Active (46 min to 3 hours per day)":
                recommendedWaterIntakeActivityLevel = (0.35 / 30.0) * 142.5; // Example calculation
                break;
            default:
                recommendedWaterIntakeActivityLevel = 0; // No extra water for low activity levels
                break;
        }

        // Combine all factors to calculate the final recommended water intake
        calculatedRecommendedWaterIntake = baseRecommendedWaterIntake + weightAdjustedWaterIntake + recommendedWaterIntakeActivityLevel;

        return calculatedRecommendedWaterIntake;
    }

    private double calculateWaterIntakeForWeight(double weight) {
        // Assuming an increase of 0.03 liters per kilogram as a simplistic model
        return weight * 0.03;
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


}