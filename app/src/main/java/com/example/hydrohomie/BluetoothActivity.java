package com.example.hydrohomie;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;
import java.util.Set;

public class BluetoothActivity extends Fragment {
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;

    Button b1,b2,b3,b4;

    ListView lv;
    public BluetoothActivity(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        b1 = rootView.findViewById(R.id.button);
        b2 = rootView.findViewById(R.id.button2);
        b3 = rootView.findViewById(R.id.button3);
        b4 = rootView.findViewById(R.id.button4);

        BA = BluetoothAdapter.getDefaultAdapter();
        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // startActivityForResult(turnOn, 0);
//        // registerForActivityResult(ActivityResultContract<turnOn, 0>,)
        if (ActivityCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // TODO: Print error message
            Toast.makeText(getContext(), "We don't have Bluetooth permissions",Toast.LENGTH_SHORT).show();
        }
        else {
            //pairedDevices = BA.getBondedDevices();
            Toast.makeText(getContext(), "We have permissions",Toast.LENGTH_SHORT).show();

        }
        return rootView;
    }

}
