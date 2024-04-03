package com.example.hydrohomie;

import static android.content.Context.BIND_AUTO_CREATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothFragment extends Fragment {
    private static final String TAG = "MY_BLUETOOTH_FRAGMENT_DEBUG_TAG";
    private final ArrayList<BluetoothDevice> discoveredDevicesList = new ArrayList<>();
    private final ArrayList<String> devicesList = new ArrayList<>(),
                                    pairedDevicesList = new ArrayList<>();
    private ListView pairedDevicesListView, lv;
    private ArrayAdapter<String> devicesListAdapter;
    private Button b1, b2, b3, b4;
    private TextView textView;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> adapter;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    
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
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    assert device != null;
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDED");
                    }
                    if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                    }
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                    }
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    public BluetoothFragment() {
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
                            }
                            if (areAllGranted) {
                                Toast.makeText(getContext(), "We have permissions", Toast.LENGTH_SHORT).show();
                                initializeBluetoothPairing();
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
                enableBluetooth();
            } else {
                enableBluetooth();
                initializeBluetoothPairing();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Replace with your Bluetooth device address


        View rootView = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        b1 = rootView.findViewById(R.id.button);
        b2 = rootView.findViewById(R.id.scanButton);
        b3 = rootView.findViewById(R.id.refreshButton);
//        b4 = rootView.findViewById(R.id.button4);
        textView = rootView.findViewById(R.id.thirdFragment);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize the adapter and set it to your list view;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, devicesList);
        ListView listView = new ListView(getContext());
        listView.setAdapter(adapter);
        // Create a dialog and set the list view
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a device to pair");
        builder.setView(listView);

        // Get a reference to the ListView
        pairedDevicesListView = rootView.findViewById(R.id.pairedDevicesListView);
        // Create an adapter for the ListView
        devicesListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, pairedDevicesList);
        pairedDevicesListView.setAdapter(devicesListAdapter);

        // Create dialog
        AlertDialog dialog = builder.create();

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
                    // TODO: Pairing handshake
                    Toast.makeText(requireContext(), "You selected: " + selectedDevice.getName(), Toast.LENGTH_LONG).show();
                    bluetoothAdapter.cancelDiscovery();
                    initiatePairing(selectedDevice);
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }
                dialog.hide();
            }
        });

        // Set item click listener to the ListView
        pairedDevicesListView.setOnItemClickListener((parent, view1, position, id) -> {
            // Get the selected device name
            String deviceName = (String) parent.getItemAtPosition(position);

            // Call the function to connect to the selected Bluetooth device
            try {
                connectToDevice(deviceName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        b2.setOnClickListener(v -> {
            try {
                boolean b = bluetoothAdapter.startDiscovery();
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }

            dialog.show();
        });
        return rootView;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        requireActivity().unregisterReceiver(receiver);
    }

    private void enableBluetooth() {
        // Setup Bluetooth adapter and check that Bluetooth is enabled
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getContext(), "Bluetooth not supported.", Toast.LENGTH_SHORT).show();
        }
        assert bluetoothAdapter != null;
        if (!bluetoothAdapter.isEnabled()
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {
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
    }

    private void initializeBluetoothPairing() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                    pairedDevicesList.add(deviceName);
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

    private void connectToDevice(String deviceName) throws IOException {
        Toast.makeText(getContext(), "Trying to connect to " + deviceName, Toast.LENGTH_SHORT).show();
        BluetoothDevice selectedDevice = null;
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            pairedDevices = bluetoothAdapter.getBondedDevices();

            if (!pairedDevices.isEmpty()) {
                Toast.makeText(getContext(), "pairedDevices is not empty", Toast.LENGTH_SHORT).show();
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(deviceName)) {
                        selectedDevice = device;
                        break;
                    }
                }
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID\
                assert selectedDevice != null;
                BluetoothSocket socket = selectedDevice.createRfcommSocketToServiceRecord(uuid);
                Toast.makeText(getContext(), "Selected device" + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                socket.connect();

                mmOutputStream = socket.getOutputStream();
                mmInputStream = socket.getInputStream();

                beginListenForData();
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            textView.setText(data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
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

    private void initiatePairing(BluetoothDevice selectedDevice) {
        try {
            Log.d(TAG, "initiatePairing: deviceName: " + selectedDevice.getName());
            Log.d(TAG, "initiatePairing: deviceAddress: " + selectedDevice.getAddress());
            selectedDevice.createBond();
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
