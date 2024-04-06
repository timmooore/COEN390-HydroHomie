package com.example.hydrohomie;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SensorData {
    static final List<SensorData> dataPoints = new ArrayList<>();
    private static final String TAG = "SensorData";
    private static final double threshold = 0.5;
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

    /**
     * Generate data points with timestamps at minute intervals specified by granularity
     * @param recommendedIntake The total daily recommended water intake of the user
     * @return A List of SensorData entries at minute intervals with the recommended
     * accumulated intake for that time of day
     */
    //
    public static List<SensorData> generateDataPoints(double recommendedIntake) {
        Log.d(TAG, "generateDataPoints: method invoked");
        //List<SensorData> dataPoints = new ArrayList<>();

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

    /**
     * Compares the most recent sensor data reading with where the user should
     * be throughout the day and returns whether we should notify them to hydrate
     * @param dataPoints
     * @param input
     * @return
     */
    public static boolean isHydrated(List<SensorData> dataPoints, SensorData input) {
        double diff;
        for (SensorData dataPoint : dataPoints) {
            LocalTime dataPointTime = dataPoint.getTimestamp();
            // Check if the input timestamp is before the current data point's timestamp
            if (input.getTimestamp().isBefore(dataPointTime)) {
                // Found the interval just after the input timestamp
                // Compare the input value with the previous data point's value (floor value)
                if (dataPointTime.equals(LocalTime.MIDNIGHT)) {
                    // Handle the case where dataPointTime is midnight (start of next day)
                    return input.getValue() >= dataPoints.get(dataPoints.size() - 1).getValue();
                } else {
                    int currentIndex = dataPoints.indexOf(dataPoint);
                    SensorData floorDataPoint = dataPoints.get(currentIndex - 1);
                    diff = floorDataPoint.getValue() - input.getValue();
                    Log.d(TAG, "dataPoint: time: " + floorDataPoint.getTimestamp() + ", value: " + floorDataPoint.getValue() + ", input: time: " + input.getTimestamp() + ", value: " + input.getValue());
                    return diff >= threshold;
                }
            }
        }
        // If input timestamp is after all data points, compare with the last data point
        SensorData lastDataPoint = dataPoints.get(dataPoints.size() - 1);
        diff = lastDataPoint.getValue() - input.getValue();
        return diff >= threshold;
    }

    public static List<SensorData> generateDummyData(LocalTime timestamp) {
        List<SensorData> dummyDataList = new ArrayList<>();

        LocalTime startTime = LocalTime.of(8, 0, 0);

        double cumulatedIntake = 0D;
        Log.d(TAG, "timestamp: " + timestamp);

        int i = 0;
        for (LocalTime time = startTime; time.isBefore(timestamp); time = time.plusMinutes(5)) {
            // Format double value
            DecimalFormat df = new DecimalFormat("0.00");

            double roundedIntake = Double.parseDouble(df.format(cumulatedIntake));
            SensorData dataPoint = new SensorData(roundedIntake, time);
            dummyDataList.add(dataPoint);

            if (i == 0) {
                // Create an instance of Random class
                Random random = new Random();

                // Generate a random double between 15.0 (inclusive) and 80.0 (exclusive)
                double randomIntake = 15 + (120 - 15) * random.nextDouble();
                Log.d(TAG, "Timestamp: " + time + ", value: " + cumulatedIntake);
                cumulatedIntake += randomIntake;
                i = 3;
            }
            i--;
        }

        return dummyDataList;
    }
}
