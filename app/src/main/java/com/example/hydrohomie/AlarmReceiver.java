package com.example.hydrohomie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private final String TAG = "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.hydrohomie.ACTION_TRIGGER_ALARM".equals(intent.getAction())) {
            Log.d(TAG, "ACTION_TRIGGER_ALARM action received");
            // Start the BluetoothService
            Intent serviceIntent = new Intent(context, BluetoothService.class);
            context.startService(serviceIntent);
        }
    }
}
