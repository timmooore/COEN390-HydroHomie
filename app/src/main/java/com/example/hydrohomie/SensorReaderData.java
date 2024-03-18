// SensorReaderData.java
package com.example.hydrohomie;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SensorReaderData {

    // Define an interface to handle data updates
    public interface DataUpdateListener {
        void onDataUpdate(int waterLevel);
    }

    // Method to read water level data from a file and send updates
    public static void readSensorDataAndSendUpdates(DataUpdateListener listener) {
        // Specify the path to your text file
        String filePath = "C:\\Users\\yasse\\OneDrive\\Desktop\\dummy file.txt";

        // Attempt to read the file
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            // Read each line from the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Process the line (e.g., parse timestamp and water level)
                int waterLevel = processLine(line);
                // Notify listener about data update
                if (listener != null) {
                    listener.onDataUpdate(waterLevel);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }

    // Method to process a line and extract water level
    private static int processLine(String line) {
        // Split the line based on the colon (:)
        String[] parts = line.split(":");
        if (parts.length == 2) {
            return Integer.parseInt(parts[1].trim());
        } else {
            System.out.println("Invalid line format: " + line);
            return -1; // Error case
        }
    }
}
