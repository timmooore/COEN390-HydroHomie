package com.example.hydrohomie;

import android.util.Log;
import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SensorReaderData {

    public static void pushDummyDataToFirebase() {
        // Get reference to the Firebase database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("user_goals").child(user.getUid()).child("water_consumption_history");

        // Generate dummy consumption data for 3 days (from 26th March to 28th March)
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for (int i = 0; i < 3; i++) {
            // Set the date for the consumption data
            calendar.set(2024, Calendar.MARCH, 26 + i); // Adjust the year, month, and starting day as needed

            // Format the date as a string
            String dateString = dateFormat.format(calendar.getTime());

            // Generate dummy consumption value (random or predefined)
            int consumption = generateDummyConsumption();

            // Push the consumption data to Firebase under the date node
            historyRef.child(dateString).setValue(consumption);
        }
    }

    private static int generateDummyConsumption() {
        // Generate dummy consumption value (e.g., randomly or using predefined values)
        // For demonstration, returning a hardcoded value
        return 1; // Example: 2000 milliliters
    }

}
