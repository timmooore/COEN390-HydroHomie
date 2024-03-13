package com.example.hydrohomie;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BluetoothActivity extends Fragment {
    private static final int PERMISSION_CODE = 1001,
            SCAN_PERMISSION_CODE = 1003,
            REQUEST_ENABLE_BT = 1002;
    private static final String PERMISSION_BLUETOOTH = Manifest.permission.BLUETOOTH,
            PERMISSION_BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN,
            PERMISSION_BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT,
            PERIMSSION_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            PERIMSSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private Set<BluetoothDevice> pairedDevices;

    Button b1, b2, b3, b4;

    ListView lv;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Log.e("activityResultLauncher", result.toString());
                boolean areAllGranted = true;
                for (Boolean b : result.values()) {
                    areAllGranted = areAllGranted && b;
                }
                if (areAllGranted) {
                    // TODO:
                    Toast.makeText(getContext(), "We have permissions", Toast.LENGTH_SHORT).show();
                }
            });
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                assert device != null;
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{PERMISSION_BLUETOOTH_CONNECT}, PERMISSION_CODE);
                    return;
                }

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null) {
                    Toast.makeText(getContext(), deviceName, Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    public BluetoothActivity() {
        // require a empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO:


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check location permissions required for Bluetooth, prompt for permissions from user
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
//                        result -> {
//                            Log.e("activityResultLauncher", result.toString());
//                            boolean areAllGranted = true;
//                            for (Boolean b : result.values()) {
//                                areAllGranted = areAllGranted && b;
//                            }
//                            if (areAllGranted) {
//                                // TODO:
//                                Toast.makeText(getContext(), "We have permissions", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                String[] appPerms;
                appPerms = new String[]{
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                };
                Toast.makeText(getContext(), "This got exec", Toast.LENGTH_SHORT).show();
                requestPermissionLauncher.launch(appPerms);
            }
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "This got exec2", Toast.LENGTH_SHORT).show();
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show an explanation to the user, e.g., using a dialog or Snackbar
                    // Explain to the user why this permission is needed
                    Toast.makeText(requireContext(), "This is why we need location services", Toast.LENGTH_SHORT).show();
                }
                Activity activity = requireActivity();
                String activityClassName = activity.getClass().getName();
                Log.d("ActivityDebug", "Activity class name: " + activityClassName);
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_CODE);
            }
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "This got exec2", Toast.LENGTH_SHORT).show();
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // Show an explanation to the user, e.g., using a dialog or Snackbar
                    // Explain to the user why this permission is needed
                    Toast.makeText(requireContext(), "This is why we need location services", Toast.LENGTH_SHORT).show();
                }
                Activity activity = requireActivity();
                String activityClassName = activity.getClass().getName();
                Log.d("ActivityDebug", "Activity class name: " + activityClassName);
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_CODE);
            }

//            if (ContextCompat.checkSelfPermission(requireContext(),
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getContext(), "ACCESS_BACKGROUND_LOCATION not permitted.", Toast.LENGTH_SHORT).show();
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
//            }
//            if (ContextCompat.checkSelfPermission(requireContext(),
//                    Manifest.permission.BLUETOOTH)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH);
//            }
//            if (ContextCompat.checkSelfPermission(requireContext(),
//                    Manifest.permission.BLUETOOTH_ADMIN)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN);
//            }
//            if (ContextCompat.checkSelfPermission(requireContext(),
//                    Manifest.permission.BLUETOOTH_CONNECT)
//                    != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(getContext(), "BLUETOOTH_CONNECT not permitted.", Toast.LENGTH_SHORT).show();
//                Log.d("Bluetooth Debug", "This code is getting exec");
//                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
//            }
        }

        // Setup Bluetooth adapter and check that Bluetooth is enabled
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getContext(), "Bluetooth not supported.", Toast.LENGTH_SHORT).show();
        }
        assert bluetoothAdapter != null;
        if (!bluetoothAdapter.isEnabled()) {
            ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        Log.d("ActivityResult.getResultCode()", String.valueOf(result.getResultCode()));
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Check the result of the Bluetooth enable request
                            if (bluetoothAdapter.isEnabled()) {
                                // Bluetooth is now enabled, you can proceed with Bluetooth-related operations
                                Toast.makeText(getContext(), "Bluetooth is enabled.", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getContext(), "Bluetooth is still not enabled.", Toast.LENGTH_SHORT).show();
                                // Bluetooth is not enabled, handle accordingly (e.g., show a message)

                            }
                        }
                    });
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        }
        // Query for paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                // TODO: Setup processing of deviceName so that it remembers the device
            }
        }

        // Discover new Bluetooth devices
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        requireActivity().registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        b1 = rootView.findViewById(R.id.button);
        b2 = rootView.findViewById(R.id.button2);
        b3 = rootView.findViewById(R.id.button3);
        b4 = rootView.findViewById(R.id.button4);

        b2.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_SCAN}, SCAN_PERMISSION_CODE);
                Toast.makeText(getContext(), "We don't have BLUETOOTH_SCAN permissions", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getContext(), "Scanning for Bluetooth", Toast.LENGTH_LONG).show();
            bluetoothAdapter.startDiscovery();
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        requireActivity().unregisterReceiver(receiver);
    }

    // Handle the result of the permission request
    private void onPermissionResult(Boolean isGranted) {
        if (isGranted) {
            // Permission granted, you can now perform operations requiring background location
        } else {
            // Permission denied, handle accordingly (e.g., show a message, disable features)
            Toast.makeText(getContext(), "We don't have Bluetooth permissions",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getContext(), "Request code is: " + requestCode, Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_CODE) {
            // Check if the user granted the requested permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now perform operations requiring background location
                // For example, continue with Bluetooth-related operations
                Toast.makeText(getContext(), "Background location permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable features)
                Toast.makeText(getContext(), "Background location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onMultiplePermissionsResult(Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            String permission = entry.getKey();
            Boolean isGranted = entry.getValue();

            if (isGranted) {
                // Permission granted, you can now perform operations requiring the requested permission
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable features)

            }
        }
    }
}
