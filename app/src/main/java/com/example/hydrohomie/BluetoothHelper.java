package com.example.hydrohomie;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

public class BluetoothHelper {
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    public BluetoothHelper(Context context, Activity myActivity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (ContextCompat.checkSelfPermission(context,
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                        myActivity,
//                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
//                        PERMISSION_CODE);
//            }
//        }
        BA = BluetoothAdapter.getDefaultAdapter();
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(myActivity, turnOn, 0, null);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // TODO: Print error message
        }
        pairedDevices = BA.getBondedDevices();


    }
}
