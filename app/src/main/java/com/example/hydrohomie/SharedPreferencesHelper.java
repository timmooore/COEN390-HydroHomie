package com.example.hydrohomie;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    public SharedPreferencesHelper(Context context, String name)
    {
        sharedPreferences = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
    }

    public void saveBluetoothAddress(String bluetoothAddress)
    {
        String key = "bluetoothAddress";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, bluetoothAddress);
        editor.apply();
    }

    public String getBluetoothAddress() {
        return sharedPreferences.getString("bluetoothAddress", null);
    }

    public void saveRecommendedIntake(String recommendedIntake)
    {
        String key = "recommendedIntake";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, recommendedIntake);
        editor.apply();
    }

    public String getRecommendedIntake() {
        return sharedPreferences.getString("recommendedIntake", "2.7");
    }

    public void saveCurrentIntake(double currentIntake)
    {
        String key = "currentIntake";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, (float) currentIntake);
        editor.apply();
    }

    public double getCurrentIntake() {
        return sharedPreferences.getFloat("currentIntake", 0);
    }
}
