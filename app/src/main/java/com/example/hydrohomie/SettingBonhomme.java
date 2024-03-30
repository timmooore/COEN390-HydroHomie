package com.example.hydrohomie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.TimeUnit;

public class SettingBonhomme extends Fragment {

    private SharedPreferences sharedPreferences;
    private Spinner notificationIntervalSpinner;
    private Button editButton;
    private Button saveButton;
    private boolean isEditModeEnabled = false;

    public SettingBonhomme() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_bonhomme, container, false);

        // Retrieve SharedPreferences instance
        sharedPreferences = requireActivity().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        // Retrieve UI elements
        notificationIntervalSpinner = view.findViewById(R.id.notificationIntervalSpinner);
        editButton = view.findViewById(R.id.editnotification);
        saveButton = view.findViewById(R.id.savenotification);

        // Define options for the notification interval from string array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.notification_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationIntervalSpinner.setAdapter(adapter);

        // Initially disable the spinner and hide the Save button
        notificationIntervalSpinner.setEnabled(false);
        saveButton.setVisibility(View.GONE);

        // Set listener for the Edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable edit mode
                isEditModeEnabled = true;
                // Enable the spinner and show the Save button
                notificationIntervalSpinner.setEnabled(true);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        // Set listener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the selected interval
                int selectedPosition = notificationIntervalSpinner.getSelectedItemPosition();
                saveNotificationInterval(selectedPosition);
                // Disable edit mode
                isEditModeEnabled = false;
                // Disable the spinner and hide the Save button
                notificationIntervalSpinner.setEnabled(false);
                saveButton.setVisibility(View.GONE);
            }
        });

        // Retrieve the saved interval position and set it as the selected option
        int savedIntervalPosition = sharedPreferences.getInt("notification_interval", 0);
        notificationIntervalSpinner.setSelection(savedIntervalPosition);

        return view;
    }

    private void saveNotificationInterval(int position) {
        // Save the selected interval position to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notification_interval", position);
        editor.apply();

        // Schedule or reschedule notifications based on the selected interval
        scheduleNotifications(position);
    }

    private void scheduleNotifications(int intervalPosition) {
        // Get the selected interval and calculate the delay in milliseconds
        long intervalMillis;
        switch (intervalPosition) {
            case 0: // Once a day
                intervalMillis = TimeUnit.DAYS.toMillis(1);
                break;
            case 1: // Once every 4 hours
                intervalMillis = TimeUnit.HOURS.toMillis(4);
                break;
            case 2: // Once every hour
                intervalMillis = TimeUnit.HOURS.toMillis(1);
                break;
            case 3: // Once every minute to test the notification.
                intervalMillis = TimeUnit.MINUTES.toMillis(1);
                break;
            default:
                // Default to once a day if interval position is invalid
                intervalMillis = TimeUnit.DAYS.toMillis(1);
        }

        // Schedule or reschedule notifications using AlarmManager
        Intent notificationIntent = new Intent(requireContext(), NotificationHelper.class);
        notificationIntent.putExtra("title", "Your Notification Title");
        notificationIntent.putExtra("message", "Your Notification Message");
        PendingIntent pendingIntent = PendingIntent.getActivity(requireContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE); // Specify FLAG_IMMUTABLE here

        // Set the notification to trigger after the specified interval
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis, intervalMillis, pendingIntent);
    }


}
