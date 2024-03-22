package com.example.hydrohomie;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends Fragment {
    // TODO: Delete this
//    private static final int PERMISSION_CODE = 1001,
//            SCAN_PERMISSION_CODE = 1003,
//            REQUEST_ENABLE_BT = 1002;
//    private static final String PERMISSION_BLUETOOTH = Manifest.permission.BLUETOOTH,
//            PERMISSION_BLUETOOTH_ADMIN = Manifest.permission.BLUETOOTH_ADMIN,
//            PERMISSION_BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT,
//            PERIMSSION_BACKGROUND_LOCATION = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//            PERIMSSION_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private Set<BluetoothDevice> pairedDevices;
    private final ArrayList<String> devicesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    Button b1, b2, b3, b4;

    ListView lv;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                assert device != null;
                discoveredDevicesList.add(device);

                String deviceName;
                try {
                    deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }

                if (deviceName != null) {
                    Toast.makeText(getContext(), deviceName, Toast.LENGTH_LONG).show();
                    devicesList.add(deviceName);
                    // Notify the adapter that the dataset has changed
                    // This should be a member variable of your Activity/Fragment
                    // Adapter initialization and setting to the list view should be done elsewhere
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };
    private final ArrayList<BluetoothDevice> discoveredDevicesList = new ArrayList<>();

    public BluetoothActivity() {
        // require a empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check location permissions required for Bluetooth, prompt for permissions from user
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Log.i("activityResultLauncher", result.toString());
                            boolean areAllGranted = true;
                            for (Boolean b : result.values()) {
                                areAllGranted = areAllGranted && b;
                                beginBluetoothPairing();
                            }
                            if (areAllGranted) {
                                // TODO:
                                Toast.makeText(getContext(), "We have permissions", Toast.LENGTH_SHORT).show();
                            }
                        });
                String[] appPerms;
                appPerms = new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                };
                Toast.makeText(getContext(), "This got exec", Toast.LENGTH_SHORT).show();
                requestPermissionLauncher.launch(appPerms);
            }
        }
    }

    private void beginBluetoothPairing() {
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
        Set<BluetoothDevice> pairedDevices;
        try {
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (!pairedDevices.isEmpty()) {
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
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        b1 = rootView.findViewById(R.id.button);
        b2 = rootView.findViewById(R.id.button2);
        b3 = rootView.findViewById(R.id.button3);
        b4 = rootView.findViewById(R.id.button4);
        // Initialize the adapter and set it to your list view;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, devicesList);
        ListView listView = new ListView(getContext());
        listView.setAdapter(adapter);

        // Create a dialog and set the list view
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a device to pair");
        builder.setView(listView);

        // Create dialog
        AlertDialog dialog = builder.create();

        beginBluetoothPairing();
        // Set a click listener for list items to handle pairing
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected device from the list
                String selectedDeviceName = devicesList.get(position);
                // Find the device object using its name
                BluetoothDevice selectedDevice = findDeviceByName(selectedDeviceName);
                assert selectedDevice != null;
                try {
                    Toast.makeText(requireContext(), "You selected: " + selectedDevice.getName(), Toast.LENGTH_LONG).show();
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }
                dialog.hide();
                // Now you can initiate pairing with this device
                // For example:
                initiatePairing(selectedDevice);
            }
        });

        b2.setOnClickListener(v -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            try {
                bluetoothAdapter.startDiscovery();
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }

            dialog.show();
        });
        return rootView;
    }

    private void initiatePairing(BluetoothDevice selectedDevice) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        requireActivity().unregisterReceiver(receiver);
    }

    private BluetoothDevice findDeviceByName(String deviceName) {
        try {
            for (BluetoothDevice device : discoveredDevicesList) {
                if (device.getName() != null && device.getName().equals(deviceName)) {
                    return device;
                }
            }
        } catch (SecurityException e) {
            // Handle the SecurityException, such as requesting permission or logging an error
            throw new RuntimeException(e);
        }
        return null; // Device not found or permission not granted
    }
}
