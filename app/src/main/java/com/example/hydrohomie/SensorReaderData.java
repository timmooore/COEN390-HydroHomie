package com.example.hydrohomie;

import android.util.Log;
import java.util.ArrayList;

public class SensorReaderData {

    // Define an interface to handle data updates
    public interface DataUpdateListener {
        void onDataUpdate(float waterConsumption, float first, float last);
    }

    public static void readSensorDataAndSendUpdates(DataUpdateListener listener) {
        // Insert the provided values into the array
        float[] readings = {8, 8, 8, 5, 8, 30};

        // Assuming water level height is 30 cm
        float waterLevelHeight = 30;

        // Maximum water consumption (arbitrary value)
        float maxWaterConsumption = 100; // Assuming maximum water consumption is 100 cm

        if (readings.length > 0) {
            float firstReading = readings[0];
            float lastReading = readings[readings.length - 1];

            // Convert water levels to centimeters
            float initialWaterLevel = firstReading;
            float finalWaterLevel = lastReading;

            // Calculate water consumption (assuming positive values only)
            float waterConsumption = ((finalWaterLevel - initialWaterLevel) / waterLevelHeight) * 100;

            // Calculate percentage of water consumption relative to maximum
            float waterConsumptionPercentage = (waterConsumption / maxWaterConsumption) * 100;

            if (listener != null) {
                listener.onDataUpdate(waterConsumptionPercentage, initialWaterLevel, finalWaterLevel);
            }
        } else {
            Log.e("SensorReaderData", "No readings provided.");
        }
    }
}
