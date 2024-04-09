package com.example.hydrohomie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class goals extends Fragment {
    private static final String TAG = "Goals";
    protected EditText weightEditText, waterGoalEditText, birthday;
    protected Spinner genderSpinner, activityLevelSpinner; // Added activityLevelSpinner
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public goals() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        weightEditText = view.findViewById(R.id.weightEditText);
        waterGoalEditText = view.findViewById(R.id.waterGoalEditText);
        birthday = view.findViewById(R.id.birthday);
        birthday.setText("01/01/2000");
        activityLevelSpinner = view.findViewById(R.id.activityLevelSpinner); // Initialize activityLevelSpinner
        genderSpinner = view.findViewById(R.id.genderSpinner);

        retrieveAndDisplayData();

        birthday.setOnClickListener(v -> showBirthdayDialog());
        setupWeightEditText(view);
        setupGenderSpinner();
        setupActivityLevelSpinner();
        setupWeightEditTextListener();

        return view;
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveData("gender", parent.getItemAtPosition(position).toString());
                updateRecommendedWaterIntake();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupActivityLevelSpinner() {
        ArrayAdapter<CharSequence> activityLevelAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.activity_level_array_prompt, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityLevelAdapter);
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveData("activity_level", parent.getItemAtPosition(position).toString());
                updateRecommendedWaterIntake();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showBirthdayDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, monthOfYear, dayOfMonth) -> {
            // Format the date
            String formattedDate = formatDate(year, monthOfYear, dayOfMonth);

            // Set the formatted date text to birthday EditText
            birthday.setText(formattedDate);

            // Save the formatted date as birthday
            saveData("birthday", formattedDate);

            updateRecommendedWaterIntake();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }




    private String formatDate(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    private void setupWeightEditText(View view) {
        EditText weightEditText = view.findViewById(R.id.weightEditText); // Make sure the ID matches your layout
        weightEditText.setOnClickListener(v -> showWeightInput());
    }

    private void setupWeightEditTextListener() {
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Implementation not needed for this example
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Implementation not needed for this example
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Only attempt to save and update if there's actual numeric content
                if (!s.toString().isEmpty() && s.toString().matches("\\d+")) {
                    saveData("weight", s.toString());
                    updateRecommendedWaterIntake();
                }
            }
        });
    }

    private void saveData(String field, String value) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("user_goals").child(userId).child(field).setValue(value);
        }
    }

    private void showWeightInput() {
        final EditText weightInput = new EditText(getContext());
        weightInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(getContext())
                .setTitle("Enter Weight")
                .setView(weightInput)
                .setPositiveButton("OK", (dialog, which) -> {
                    EditText weightEditText = requireView().findViewById(R.id.weightEditText);
                    String weight = weightInput.getText().toString();
                    weightEditText.setText(weight);
                    // Save the weight immediately after input
                    saveWeight(weight);

                    updateRecommendedWaterIntake();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveWeight(String weight) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("user_goals").child(userId).child("weight").setValue(weight);
        }
    }

    private void saveActivityLevel(String gender) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("user_goals").child(userId).child("activity_level").setValue(gender);
        }
    }



    private List<String> generateYears() {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        return years;
    }

    // TODO: Ensure generateDataPoints is onChange of recommended intake
    private void retrieveAndDisplayData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String weight = dataSnapshot.child("weight").getValue(String.class);
                        String gender = dataSnapshot.child("gender").getValue(String.class);
                        String birthdayStr = dataSnapshot.child("birthday").getValue(String.class);
                        String activityLevel = dataSnapshot.child("activity_level").getValue(String.class);

                        if (weight == null) weight = "72";
                        if (gender == null) weight = "Male";
                        if (birthdayStr == null) weight = "01/01/2000";
                        if (activityLevel == null) weight = "Not Active";

                        // Update UI with retrieved data
                        weightEditText.setText(weight);
                        birthday.setText(birthdayStr);
                        // For gender and activity level, you need to set the spinner selection accordingly

                        // Here you might need custom logic to select the right spinner item based on the value
                        // This is an example for gender, do similarly for activity level
                        ArrayAdapter<CharSequence> genderAdapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
                        int genderPosition = genderAdapter.getPosition(gender);
                        genderSpinner.setSelection(genderPosition, true);

                        ArrayAdapter<CharSequence> activityLevelAdapter = (ArrayAdapter<CharSequence>) activityLevelSpinner.getAdapter();
                        int activityLevelPosition = activityLevelAdapter.getPosition(activityLevel);
                        activityLevelSpinner.setSelection(activityLevelPosition, true);

                        // Calculate age
                        int age = calculateAgeBasedOnBirthday(birthdayStr);

                        // Now calculate the recommended water intake
                        double recommendedWaterIntake = calculateRecommendedWaterIntake(activityLevel,
                                gender, weight, age);

                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.HALF_UP);

                        String recommendedIntakeString = df.format(recommendedWaterIntake);
                        // Update the UI
                        waterGoalEditText.setText(recommendedIntakeString + " liters");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private static int calculateAge(Date birthDate) {
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

    private static double calculateWaterIntakeForActivity(String selectedActivityLevel) {
        double recommendedWaterIntakeActivityLevel;
        switch (selectedActivityLevel) {
            case "Moderate (15 to 45 min per day)":
                recommendedWaterIntakeActivityLevel = 0.35;
                break;
            case "Active (46 min to 3 hours per day)":
                recommendedWaterIntakeActivityLevel = (0.35/30.0) * 142.5;
                break;
            default:
                recommendedWaterIntakeActivityLevel = 0; // Default consumption
                break;
        }
        return recommendedWaterIntakeActivityLevel;
    }

    private static double calculateWaterIntakeForWeight(double weight) {
        return weight * 0.015;
    }


    private static double calculateBaseWaterIntakeForAge(int age, String gender) {
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

    public static double calculateRecommendedWaterIntake(String selectedActivityLevel,
                                                         String selectedGender,
                                                         String selectedWeight,
                                                         int age) {
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

    public static int calculateAgeBasedOnBirthday(String birthdayStr) {
        try {
            Date birthDate = new SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault()).parse(birthdayStr);
            return calculateAge(birthDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing birthday", e);
            return 0; // Default or error age
        }
    }

    private void updateRecommendedWaterIntake() {
        String weight = weightEditText.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String birthday = this.birthday.getText().toString();
        String activityLevel = activityLevelSpinner.getSelectedItem().toString();

        // Calculate age
        int age = calculateAgeBasedOnBirthday(birthday);

        // Now calculate the recommended water intake
        double recommendedWaterIntake = calculateRecommendedWaterIntake(activityLevel,
                gender, weight, age);

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String recommendedIntakeString = df.format(recommendedWaterIntake);
        // Update the UI
        waterGoalEditText.setText(recommendedIntakeString + " liters");

        // Save the updated recommended water intake to Firebase
        saveRecommendedWaterIntake(recommendedIntakeString, recommendedWaterIntake);
    }

    private void saveRecommendedWaterIntake(String recommendedIntakeString,
                                            double recommendedWaterIntake) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);
            userGoalsRef.child("recommendedWaterIntake").setValue(recommendedIntakeString);

            FirebaseUtils.generateRecommendedIntakeData(userGoalsRef, recommendedWaterIntake);
        }
    }
}