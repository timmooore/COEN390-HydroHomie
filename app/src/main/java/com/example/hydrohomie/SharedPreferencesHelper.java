package com.example.hydrohomie;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    public SharedPreferencesHelper(Context context)
    {
        sharedPreferences = context.getSharedPreferences("BluetoothAddressPreference",
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
}
