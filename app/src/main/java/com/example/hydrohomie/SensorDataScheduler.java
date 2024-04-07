package com.example.hydrohomie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

public class SensorDataScheduler {
    private static final String TAG = "SensorDataScheduler";

    // Schedule alarms to trigger the SensorDataService every hour at 00 and 30 minutes
    public static void scheduleSensorDataReading(Context context, String deviceAddress) {
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

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(context, BluetoothService.class);
//        intent.putExtra("DEVICE_ADDRESS", deviceAddress);
//        intent.setAction("com.example.hydrohomie.ACTION_TRIGGER_ALARM");
//
//        // Create pending intents for each alarm
//        PendingIntent pendingIntent1 = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        PendingIntent pendingIntent2 = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//        PendingIntent pendingIntent3 = PendingIntent.getService(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        // Set up calendar for the first alarm after 1 minute
//        Calendar calendar1 = Calendar.getInstance();
//        calendar1.setTimeInMillis(System.currentTimeMillis());
//        calendar1.add(Calendar.SECOND, 15);
//
//        // Set up calendar for the second alarm after 5 minutes
//        Calendar calendar2 = Calendar.getInstance();
//        calendar2.setTimeInMillis(System.currentTimeMillis());
//        calendar2.add(Calendar.MINUTE, 5);
//
//        // Set up calendar for the third alarm after 10 minutes
//        Calendar calendar3 = Calendar.getInstance();
//        calendar3.setTimeInMillis(System.currentTimeMillis());
//        calendar3.add(Calendar.MINUTE, 10);
//
//        Log.d("SensorDataScheduler", "Calendar 1 time: " + calendar1.getTime() + ", getTimeInMillis: " + calendar1.getTimeInMillis());
//        Log.d("SensorDataScheduler", "Calendar 2 time: " + calendar2.getTime());
//        Log.d("SensorDataScheduler", "Calendar 3 time: " + calendar3.getTime());
//        Log.d("SensorDataScheduler", "SensorDataScheduler: Calling setInexactRepeating called");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (alarmManager.canScheduleExactAlarms()) {
//                Log.d("SensorDataScheduler", "SensorDataScheduler: Calling setExact called");
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
//            } else {
//                Log.d("SensorDataScheduler", "SensorDataScheduler: Can't scheduleExact");
//                // Handle case where exact alarms cannot be scheduled
//            }
//        }
//
//        // Schedule the first alarm
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
//                60000, pendingIntent1);

//        // Schedule the second alarm
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent2);
//
//        // Schedule the third alarm
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(),
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent3);

//        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Set up calendar for the next top of the hour
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        // Schedule the first alarm
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_HOUR, pendingIntent);
//
//        // Schedule the second alarm after 30 minutes
//        calendar.add(Calendar.MINUTE, 30);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}


