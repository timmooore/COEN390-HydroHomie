package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.example.hydrohomie.R;

public class details extends Fragment {

    // Constants
    private static final int DAILY_GOAL = 2000; // Example: 2000ml (2 liters)

    // Member variable to track consumed water
    private int consumedWater; // Example: Total consumed water for the day

    public details() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        // Find the DatePickerTimeline and ProgressBar in the layout
        DatePickerTimeline datePickerTimeline = rootView.findViewById(R.id.datePickerTimeline);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar);

        // Calculate completion percentage
        int completionPercentage = calculateCompletionPercentage(consumedWater, DAILY_GOAL);

        // Update the ProgressBar with the calculated completion percentage
        progressBar.setProgress(completionPercentage);

        // Set listener for date selection events
        datePickerTimeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                // Handle date selection if needed
                // For example, update consumedWater based on user input
                // Then recalculate and update completion percentage
                consumedWater = getConsumedWaterForSelectedDate(year, month, day);
                int newCompletionPercentage = calculateCompletionPercentage(consumedWater, DAILY_GOAL);
                progressBar.setProgress(newCompletionPercentage);
            }
        });

        return rootView;
    }

    // Method to calculate completion percentage
    private int calculateCompletionPercentage(int consumedWater, int dailyGoal) {
        // Ensure consumed water does not exceed daily goal
        consumedWater = Math.min(consumedWater, dailyGoal);
        // Calculate completion percentage
        return (int) ((float) consumedWater / dailyGoal * 100);
    }

    // Method to get consumed water amount for a selected date (example method)
    private int getConsumedWaterForSelectedDate(int year, int month, int day) {
        // Example: Retrieve consumed water amount from a data source (database, SharedPreferences) for the selected date
        // For demonstration purposes, returning a hardcoded value
        return 1500; // Example: 1500ml consumed on the selected date
    }
}
