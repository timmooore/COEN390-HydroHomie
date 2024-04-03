package com.example.hydrohomie;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";

    // Interval for Bluetooth communication (30 minutes)
    private static final long INTERVAL_MILLIS = 30 * 60 * 1000;

    // Input and output streams for serial Bluetooth
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    // Define a flag to indicate whether data collection is complete
    private boolean dataCollectionComplete = false;

    // Define an object for synchronization
    private final Object lock = new Object();

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private String firebaseUserId;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BluetoothService onCreate()");
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BluetoothService onStartCommand()");
        String deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");

        if (deviceAddress == null) {
            // Device address is not provided, cannot proceed
            Log.e(TAG, "Device address not provided");
            Toast.makeText(this, "Device address not provided", Toast.LENGTH_SHORT).show();
            stopSelf(); // Stop the service
            return START_NOT_STICKY;
        }

        // Initialize Firebase Database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Could not get Firebase user");
            Toast.makeText(this, "Could not get Firebase user", Toast.LENGTH_SHORT).show();
            stopSelf(); // Stop the service
            return START_NOT_STICKY;
        } else {
            firebaseUserId = currentUser.getUid();
            firebaseDatabase = FirebaseDatabase.getInstance();
        }

        // tester(deviceAddress);


        // Connect to the Bluetooth device
        try {
            connectToDevice(deviceAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stopSelf();
        // Return START_NOT_STICKY, no need to restart if service is killed
        return START_NOT_STICKY;
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
    private void connectToDevice(String deviceAddress) throws IOException {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions required for Bluetooth functionality are disabled", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        Toast.makeText(this, "Trying to connect to Bluetooth address: " + deviceAddress, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Trying to connect to Bluetooth address: " + deviceAddress);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

        if (selectedDevice == null) {
            // Device with the provided address not found
            Toast.makeText(this, "Device not found with address: " + deviceAddress, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "connectToDevice: Device not found with address: " + deviceAddress);
            stopSelf(); // Stop the service
            return;
        }

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID\
        try (BluetoothSocket socket = selectedDevice.createRfcommSocketToServiceRecord(uuid)) {
            Log.d(TAG, "Selected device " + selectedDevice.getName());
            Toast.makeText(this, "Selected device " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
            socket.connect();

            mmOutputStream = socket.getOutputStream();
            mmInputStream = socket.getInputStream();

            beginListenForData();

            // Wait until data collection is complete
            synchronized (lock) {
                while (!dataCollectionComplete) {
                    try {
                        lock.wait(); // Wait until notified
                    } catch (InterruptedException e) {
                        Log.e(TAG, "connectToDevice: Interrupted", e);
                    }
                }
            }
        } catch (IOException | SecurityException e) {
            // Error occurred while connecting to the device
            Toast.makeText(this, "Error connecting to device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "connectToDevice: Error connecting to device: ", e);
            stopSelf(); // Stop the service
        }
    }

    void beginListenForData() {
        // TODO: Test if this runs in the background
        Log.d(TAG, "beginListenForData: called");
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // TODO: Test
        Thread workerThread = new Thread(() -> {
            Log.d(TAG, "beginListenForData: workerThread running");
            int numReads = 0;
            while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                try {
                    // TODO: Help Anto modify Arduino code with compact format
                    int bytesAvailable = mmInputStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        int numBytesRead = mmInputStream.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetBytes[i];
                            if (b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                readBufferPosition = 0;
                                // TODO: Send data to Firebase
                                handler.post(() -> {
                                    Log.d(TAG, "Data: " + data);
                                    // Regular expression pattern to match the double value
                                    Pattern pattern = Pattern.compile("\\d+\\.\\d+");

                                    // Matcher to find the pattern in the text
                                    Matcher matcher = pattern.matcher(data);

                                    // Check if the pattern is found
                                    if (matcher.find()) {
                                        // Extract the matched value and convert it to double
                                        double value = Double.parseDouble(matcher.group());

                                        // Print the extracted double value
                                        Log.d("ParsedValue", "Extracted double value: " + value);
                                        DatabaseReference databaseReference =
                                                firebaseDatabase.getReference("user_data").child(firebaseUserId);
                                        // Write data to the database
                                        databaseReference.setValue(value)
                                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data written successfully to Firebase"))
                                                .addOnFailureListener(e -> Log.e(TAG, "Error writing data to Firebase", e));
                                    } else {
                                        Log.d("ParsedValue", "No double value found in the text.");
                                    }
                                });
//                                Log.d(TAG, "Data: " + data);
                                ++numReads;
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                        if (numReads == 3) {
                            stopWorker = true;
                            synchronized (lock) {
                                dataCollectionComplete = true;
                                lock.notifyAll(); // Notify the waiting thread
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "threadWorker Runnable: ", e);
                    stopWorker = true;
                }
            }
        });
        workerThread.start();
    }

    private void tester(String deviceAddress) {
        Log.d(TAG, "Service doing work with address" + deviceAddress);
        Toast.makeText(this,"Service doing work with address" + deviceAddress, Toast.LENGTH_LONG).show();
    }
}

