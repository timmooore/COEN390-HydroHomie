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
    public static void accumulateValue(DatabaseReference databaseRef, final String currentTime, final double valueToAdd) {
        // Get DatabaseReference for the current time node
        DatabaseReference timeRef = databaseRef.child(currentTime);

        // Perform a transaction to accumulate values
        timeRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Get the current value from the database
                Double currentValue = mutableData.getValue(Double.class);

                // If the current value is null, initialize it to 0
                if (currentValue == null) {
                    currentValue = 0.0;
                }

                // Add the new value to the current value
                currentValue += valueToAdd;

                // Update the value in the database
                mutableData.setValue(currentValue);

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
