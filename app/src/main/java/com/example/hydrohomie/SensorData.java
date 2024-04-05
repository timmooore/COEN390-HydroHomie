package com.example.hydrohomie;

import android.util.Log;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SensorData {
    private final double value; // Increasing integer value
    private final LocalTime timestamp; // Timestamp associated with the data point

    public SensorData(double value, LocalTime timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getter methods
    public double getValue() { return value; }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    // Generate data points with timestamps at 30-minute intervals
    public static List<SensorData> generateDataPoints(int numPoints, double recommendedIntake) {
        List<SensorData> dataPoints = new ArrayList<>();
        double incrementalIntake = recommendedIntake / numPoints;
        // Set the start time to 8 AM and end time to 12 AM
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(0, 0);

        // Loop from start time to end time, adding 5 minutes each iteration
        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(5)) {
            SensorData dataPoint = new SensorData(incrementalIntake, time);
            dataPoints.add(dataPoint);
            Log.d("SensorData", "Timestamp: " + time + ", value: " + incrementalIntake);
            incrementalIntake += incrementalIntake;
        }
        return dataPoints;
    }

}
