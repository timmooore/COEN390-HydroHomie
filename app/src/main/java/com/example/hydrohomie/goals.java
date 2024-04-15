package com.example.hydrohomie;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private boolean isDataFetched;
    private boolean isFirstSelectionWeight = true;
    private boolean isFirstSelectionActivity = true;
    private boolean isFirstSelectionGender = true;

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

        isDataFetched = false;
        isFirstSelectionActivity = true;
        isFirstSelectionGender = true;

        birthday.setOnClickListener(v -> showBirthdayDialog());
        setupWeightEditText(view);
        setupGenderSpinner();
        setupActivityLevelSpinner();
        setupWeightEditTextListener();

        retrieveAndDisplayData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Remove the OnItemSelectedListener from genderSpinner
        if (genderSpinner != null) {
            genderSpinner.setOnItemSelectedListener(null);
        }

        // Remove the OnItemSelectedListener from activityLevelSpinner
        if (activityLevelSpinner != null) {
            activityLevelSpinner.setOnItemSelectedListener(null);
        }

        // Set all UI elements to null to release references
        weightEditText = null;
        genderSpinner = null;
        activityLevelSpinner = null;
    }

    private void setupGenderSpinner() {
        // Create an ArrayAdapter without using resource
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gender_array)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate a custom layout for the spinner item
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }

                // Get the TextView from the layout
                TextView textView = convertView.findViewById(android.R.id.text1);

                // Set text color and background tint
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(24, 12, 24, 12);// Change to your desired text color
                textView.setBackgroundColor(getResources().getColor(R.color.myBackgroundColor2)); // Change to your desired background color

                // Set the text of the spinner item
                textView.setText(getItem(position));


                return convertView;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
           // Adjust the values as needed
                return getView(position, convertView, parent);
            }
        };

        // Set the dropdown view resource
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        genderSpinner.setAdapter(genderAdapter);

        // Set the item selection listener
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDataFetched) {
                    if (isFirstSelectionGender) {
                        isFirstSelectionGender = false;
                    } else {
                        Log.d(TAG, "setupGenderSpinner: onItemSelected: called");
                        saveData("gender", parent.getItemAtPosition(position).toString());
                        updateRecommendedWaterIntake();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
    }


    private void setupActivityLevelSpinner() {
        // Create an ArrayAdapter without using resource
        ArrayAdapter<String> activityLevelAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.activity_level_array_prompt)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Inflate a custom layout for the spinner item
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
                }

                // Get the TextView from the layout
                TextView textView = convertView.findViewById(android.R.id.text1);

                // Set text color and background tint
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(24, 12, 24, 12);// Change to your desired text color
                textView.setBackgroundColor(getResources().getColor(R.color.myBackgroundColor2)); // Change to your desired background color

                // Set the text of the spinner item
                textView.setText(getItem(position));

                return convertView;
            }

            @Override

            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // Call getView() to reuse the custom layout for dropdown items
                return getView(position, convertView, parent);
            }
        };

        // Set the dropdown view resource
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        activityLevelSpinner.setAdapter(activityLevelAdapter);

        // Set the item selection listener
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "setupActivityLevelSpinner: onItemSelected: called");
                if (isDataFetched) {
                    if (isFirstSelectionActivity) {
                        isFirstSelectionActivity = false;
                    } else {
                        saveData("activity_level", parent.getItemAtPosition(position).toString());
                        updateRecommendedWaterIntake();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
    }


    private void showBirthdayDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, monthOfYear, dayOfMonth) -> {
            // Format the date
            String formattedDate = formatDate(year, monthOfYear, dayOfMonth);

            // Save the formatted date as birthday

            int age = calculateAgeBasedOnBirthday(formattedDate);
            if (age < 8) {
                // Display toast message indicating the user is too young
                Toast.makeText(requireContext(), "Sorry, you must be at least 8 years old to use this service.", Toast.LENGTH_LONG).show();
                return;
            } else {
                if (!isDataFetched) {
                    Log.d(TAG, "showBirthdayDialog: saveData: called");

                    // Set the formatted date text to birthday EditText
                    birthday.setText(formattedDate);

                    saveData("birthday", formattedDate);
                    updateRecommendedWaterIntake();
                }
            }
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
                if (!isDataFetched) {
                    isFirstSelectionWeight = false;
                } else {
                    Log.d(TAG, "setupWeightEditTextListener: afterTextChanged: called");
                    // Only attempt to save and update if there's actual numeric content
                    if (!s.toString().isEmpty() && s.toString().matches("\\d+")) {
                        saveData("weight", s.toString());
                        updateRecommendedWaterIntake();
                    }
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

                    if (Double.parseDouble(weight) < 10 || Double.parseDouble(weight) > 250) {
                        Toast.makeText(getContext(), "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    weightEditText.setText(weight);

                    // Save the weight immediately after input
                    //saveWeight(weight);

                    //updateRecommendedWaterIntake();
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

    private void retrieveAndDisplayData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "retrieveAndDisplayData: onDataChange: called");
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

                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setRoundingMode(RoundingMode.HALF_UP);

                        String recommendedIntakeString = df.format(recommendedWaterIntake);

                        // Update the UI
                       waterGoalEditText.setText(recommendedIntakeString);

                       isDataFetched = true;
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

        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);

        String recommendedIntakeString = df.format(recommendedWaterIntake);
        // Update the UI
        waterGoalEditText.setText(recommendedIntakeString);

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