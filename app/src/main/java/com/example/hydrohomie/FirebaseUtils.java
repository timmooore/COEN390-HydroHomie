package com.example.hydrohomie;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class FirebaseUtils {
    private static final String TAG = "FirebaseUtils";

    // Method to accumulate values in Firebase Realtime Database
    public static void accumulateValue(DatabaseReference databaseRef, final String currentTime, final double value) {
        // Perform a transaction to accumulate values
        databaseRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Get the latest time slot from the database
                Double cumulatedValue = mutableData.child("cumulated_value").getValue(Double.class);

                // TODO: Handle edge case where data is updated at midnight
                // If the latest time slot is null, set it to the first key in 'values'
                if (cumulatedValue == null) {
                    mutableData.child("cumulated_value").setValue(0);
                    mutableData.child("values").child(currentTime).setValue(value);
                } else {
                    // Add the new value to the cumulated value
                    cumulatedValue += value;

                    // Update the value in the database
                    mutableData.child("values").child(currentTime).setValue(cumulatedValue);
                }
                // Return the updated value
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    // Handle errors
                    Log.e(TAG, "Error writing data to Firebase", databaseError.toException());
                } else {
                    // Transaction completed successfully
                    Log.d(TAG, "Data written successfully to Firebase");
                }
            }
        });
    }

    // Method to accumulate values in Firebase Realtime Database
    public static void updateCumulatedValue(DatabaseReference databaseRef, final String currentTime, final double valueToAdd) {
        // Perform a transaction to accumulate values
        databaseRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Get the latest time slot from the database
                String latestTimeSlot = mutableData.child("latest_time_slot").getValue(String.class);

                // TODO: Handle edge case where data is updated at midnight
                // If the latest time slot is null, set it to the first key in 'values'
                if (latestTimeSlot == null) {
                    mutableData.child("latest_time_slot").setValue(currentTime);
                    mutableData.child("values").child(currentTime).setValue(valueToAdd);
                } else {
                    // Get the current value from the database
                    Double currentValue = mutableData.child("values").child(latestTimeSlot).getValue(Double.class);

                    // If the current value is null, initialize it to 0
                    if (currentValue == null) {
                        currentValue = 0D;
                    }

                    // Add the new value to the current value
                    currentValue += valueToAdd;

                    // Update the latest time slot
                    mutableData.child("latest_time_slot").setValue(currentTime);
                    // Update the value in the database
                    mutableData.child("values").child(currentTime).setValue(currentValue);
                }
                // Return the updated value
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    // Handle errors
                    Log.e(TAG, "Error writing data to Firebase", databaseError.toException());
                } else {
                    // Transaction completed successfully
                    Log.d(TAG, "Data written successfully to Firebase");
                }
            }
        });
    }
}
