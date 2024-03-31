package com.example.hydrohomie;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GoalsSignup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String[] gender = {"Select Gender", "Male", "Female", "Other"};
    private String[] activityLevel = {"Select Activity Level", "Not Active", "Moderately Active", "Very Active"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_signup); // Ensure this is your layout file


        setupBirthdayEditText();
        setupGenderSpinner();
        setupActivityLevelSpinner();
        setupWeightEditText();
        setupContinueButton(); // Ensure this call is included
    }

    private void setupContinueButton() {
        Button continueButton = findViewById(R.id.button2);
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(GoalsSignup.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupBirthdayEditText() {
        EditText birthdayEditText = findViewById(R.id.birthday);
        birthdayEditText.setOnClickListener(v -> showBirthdayDialog());
    }

    private void setupGenderSpinner() {
        Spinner genderSpinner = findViewById(R.id.genderSpinner2);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setOnItemSelectedListener(this);
    }

    private void setupActivityLevelSpinner() {
        Spinner activitySpinner = findViewById(R.id.activitySpinner);
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityLevel);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);
        activitySpinner.setOnItemSelectedListener(this);
    }

    private void setupWeightEditText() {
        EditText weightEditText = findViewById(R.id.weight);
        weightEditText.setOnClickListener(v -> showWeightInput());
    }

    private void showBirthdayDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            EditText editText = findViewById(R.id.birthday);
            editText.setText(formatDate(year, month, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    private void showWeightInput() {
        final EditText weightInput = new EditText(this);
        weightInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new AlertDialog.Builder(this)
                .setTitle("Enter Weight")
                .setView(weightInput)
                .setPositiveButton("OK", (dialog, which) -> {
                    EditText weightEditText = findViewById(R.id.weight);
                    weightEditText.setText(weightInput.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Implement your selection handling logic here
        // For demonstration:
        Toast.makeText(parent.getContext(), "Selected: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // This space intentionally left blank
    }
}
