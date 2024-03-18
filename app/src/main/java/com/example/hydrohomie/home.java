package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class home extends Fragment implements SensorReaderData.DataUpdateListener {

    private ProgressBar simpleProgressBar;
    private TextView titleNotif;
    private Button refreshButton;
    private int waterLevel = 50; // Initial water level in percentage

    public home() {
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        simpleProgressBar = view.findViewById(R.id.pb2);
        titleNotif = view.findViewById(R.id.titleMessage);
        refreshButton = view.findViewById(R.id.refresh2);

        // Enable options menu in the fragment
        setHasOptionsMenu(true);

        // Update UI with initial water level
        updateUI();

        // Set onClickListener for the refreshButton
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to read sensor data and send updates
                SensorReaderData.readSensorDataAndSendUpdates(home.this);
            }
        });

        return view;
    }

    // Method to update UI based on water level
    private void updateUI() {
        simpleProgressBar.setProgress(waterLevel);
        updateNotification();
    }

    // Method to update notification based on water level
    private void updateNotification() {
        // Update notification message based on water level
        String notificationMessage;
        if (waterLevel < 25) {
            notificationMessage = "LOW WATER LEVEL! REFILL THE BOTTLE!";
        } else if (waterLevel < 75) {
            notificationMessage = "MODERATE WATER LEVEL. KEEP HYDRATING!";
        } else {
            notificationMessage = "GOOD WATER LEVEL. STAY HYDRATED!";
        }
        titleNotif.setText(notificationMessage);
    }

    // Implementation of DataUpdateListener interface method
    @Override
    public void onDataUpdate(int waterLevel) {
        // Update water level and UI when new data is received
        this.waterLevel = waterLevel;
        updateUI();
    }
}
