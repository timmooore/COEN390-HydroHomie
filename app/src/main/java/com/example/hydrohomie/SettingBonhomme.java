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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.*;
import androidx.fragment.app.Fragment;

import com.example.hydrohomie.R;
/*   this is

need to be deleted with it xml file





*/



public class SettingBonhomme extends Fragment {

    private SharedPreferences sharedPreferences;

    public SettingBonhomme() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_bonhomme, container, false);

        // Retrieve SharedPreferences instance
        sharedPreferences = requireActivity().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);

        // Retrieve UI elements
        Spinner notificationIntervalSpinner = view.findViewById(R.id.notificationIntervalSpinner);

        // Define options for the notification interval
        String[] intervals = {"Once a day", "Once every 4 hours"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, intervals);
        notificationIntervalSpinner.setAdapter(adapter);

        // Set listener for the notification interval selection
        notificationIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // Save the selected interval to SharedPreferences
                saveNotificationInterval(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private void saveNotificationInterval(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notification_interval", position);
        editor.apply();

        // Schedule or reschedule notifications based on the selected interval
        scheduleNotifications(position);
    }

    private void scheduleNotifications(int intervalPosition) {
        // Get the selected interval and calculate the delay in milliseconds
        long intervalMillis;
        if (intervalPosition == 0) { // Once a day
            intervalMillis = AlarmManager.INTERVAL_DAY;
        } else { // Once every 4 hours
            intervalMillis = AlarmManager.INTERVAL_HOUR * 4;
        }

        // Get a reference to AlarmManager
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        // Intent for the BroadcastReceiver that will handle the notification
        Intent notificationIntent = new Intent(requireContext(), Notification.class); //maybe NotificationReceiver.class

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel any previously set alarms
        alarmManager.cancel(pendingIntent);

        // Set the alarm to trigger at specified intervals
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intervalMillis, intervalMillis, pendingIntent);
    }

}
