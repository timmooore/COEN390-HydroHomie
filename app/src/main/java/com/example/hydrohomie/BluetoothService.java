package com.example.hydrohomie;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";

    // Interval for Bluetooth communication (30 minutes)
    private static final long INTERVAL_MILLIS = 30 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BluetoothService onCreate()");
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BluetoothService onStartCommand()");

        // Start the Bluetooth communication task
        startBluetoothTask();

        // Return START_STICKY to restart the service if it's killed by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BluetoothService onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This service does not support binding, return null
        return null;
    }

    // Method to start the Bluetooth communication task
    private void startBluetoothTask() {
        // Implement Bluetooth communication logic here
        // This method will be called every 30 minutes
    }
}

