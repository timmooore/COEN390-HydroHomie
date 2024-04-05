package com.example.hydrohomie;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SensorData {
    private static final String TAG = "SensorData";
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
    public static List<SensorData> generateDataPoints(double recommendedIntake) {
        List<SensorData> dataPoints = new ArrayList<>();

        // Set the start time to 8 AM and end time to 12 AM
        LocalDateTime startTime = LocalDateTime.now().toLocalDate().atTime(8, 0);
        LocalDateTime endTime = LocalDateTime.now();
        endTime = endTime.toLocalDate().plusDays(1).atTime(0, 0);

        // Calculate duration between start and end times
        Duration duration;
        duration = Duration.between(startTime, endTime);

        // Get total minutes from duration
        long totalMinutes = duration.toMinutes();

        // Get the number of data points from the total minutes and granularity;
        int granularity = 15;  // Granularity in minutes
        int numPoints = (int) (totalMinutes / granularity);

        // Get the incremental intake
        double intakeIncrement = recommendedIntake / numPoints;
        double cumulatedIntake = 0D;

        // Format double value
        DecimalFormat df = new DecimalFormat("0.000");

        // TODO: Remove debug prints
        // Log.d(TAG, "startTime: " + startTime.toString() + ", endTime: " + endTime.toString() + ", totalMinutes: " + totalMinutes + ", numPoints: " + numPoints + ", inc_intake: " + intakeIncrement);

        // Loop from start time to end time, adding 5 minutes each iteration
        for (LocalDateTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(granularity)) {
            double roundedIntake = Double.parseDouble(df.format(cumulatedIntake));
            SensorData dataPoint = new SensorData(roundedIntake, time.toLocalTime());
            dataPoints.add(dataPoint);

            // Log.d(TAG, "Timestamp: " + time + ", value: " + cumulatedIntake);
            cumulatedIntake += intakeIncrement;
        }
        return dataPoints;
    }
}
