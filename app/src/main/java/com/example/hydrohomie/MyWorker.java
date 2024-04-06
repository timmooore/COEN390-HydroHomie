package com.example.hydrohomie;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Perform background work here
        // This method runs on a background thread
        // Start your service here
        String deviceAddress = getInputData().getString("DEVICE_ADDRESS");
        Intent serviceIntent = new Intent(getApplicationContext(), BluetoothService.class);
        serviceIntent.putExtra("DEVICE_ADDRESS", deviceAddress);
        getApplicationContext().startService(serviceIntent);
        Log.d("MyWorker", "Finished starting service");
        return Result.success();
    }
}
