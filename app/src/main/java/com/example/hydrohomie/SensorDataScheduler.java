package com.example.hydrohomie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SensorDataScheduler {
    private static final boolean useWorker = false;
    private static final String TAG = "SensorDataScheduler";

    // Schedule alarms to trigger the SensorDataService every hour at 00 and 30 minutes
    public static void scheduleSensorDataReading(Context context, String deviceAddress) {
        if (useWorker) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresCharging(false)
                    .build();

            // Set the interval for periodic work (every minute)
            long repeatIntervalMinutes = 1;

            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, repeatIntervalMinutes, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInputData(new Data.Builder().putString("DEVICE_ADDRESS", deviceAddress).build())
                    .build();

            WorkManager.getInstance(context).enqueue(workRequest);
            Log.d(TAG, "Work scheduled");
        } else {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, BluetoothService.class);
            intent.putExtra("DEVICE_ADDRESS", deviceAddress);
            intent.setAction("com.example.hydrohomie.ACTION_TRIGGER_ALARM");

            // Create pending intents for each alarm
            PendingIntent pendingIntent1 = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Set up calendar for the first alarm after 1 minute
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.add(Calendar.SECOND, 15);

            Log.d("SensorDataScheduler", "Calendar 1 time: " + calendar1.getTime() + ", getTimeInMillis: " + calendar1.getTimeInMillis());

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                if (alarmManager.canScheduleExactAlarms()) {
//                    Log.d("SensorDataScheduler", "SensorDataScheduler: Calling setExact called");
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
//                } else {
//                    Log.d("SensorDataScheduler", "SensorDataScheduler: Can't scheduleExact");
//                    // Handle case where exact alarms cannot be scheduled
//                }
//            }
            Log.d("SensorDataScheduler", "setRepeating called");
            // Schedule the first alarm
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
                    60000, pendingIntent1);
        }
    }
}


