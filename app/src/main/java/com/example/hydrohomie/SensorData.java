package com.example.hydrohomie;

import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SensorData {
    private double value; // Increasing integer value
    private Date timestamp; // Timestamp associated with the data point

    public SensorData(double value, Date timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getter methods
    public double getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // Generate data points with timestamps at 30-minute intervals
    public static List<SensorData> generateDataPoints(int numPoints) {
        List<SensorData> dataPoints = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Start at midnight
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        for (int i = 0; i < numPoints; i++) {
            dataPoints.add(new SensorData(i, calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30); // Increment by 30 minutes
        }

        return dataPoints;
    }

}
