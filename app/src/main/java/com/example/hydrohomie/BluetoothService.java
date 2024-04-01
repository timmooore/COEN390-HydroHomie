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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
            Toast.makeText(this, "Device address not provided", Toast.LENGTH_SHORT).show();
            stopSelf(); // Stop the service
            return START_NOT_STICKY;
        }

        tester(deviceAddress);


        // Connect to the Bluetooth device
//        try {
//            connectToDevice(deviceAddress);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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

            Toast.makeText(this, "Selected device" + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
            socket.connect();

            mmOutputStream = socket.getOutputStream();
            mmInputStream = socket.getInputStream();

            beginListenForData();

        } catch (IOException | SecurityException e) {
            // Error occurred while connecting to the device
            Toast.makeText(this, "Error connecting to device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "connectToDevice: Error connecting to device: ", e);
            stopSelf(); // Stop the service
            return;
        }
    }

    void beginListenForData() {
        // TODO: Test if this runs in the background
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // TODO: Test
        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        // TODO: Help Anto modify Arduino code with compact format
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

                                    // handler.post(() -> textView.setText(data));
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

    private void tester(String deviceAddress) {
        Toast.makeText(this,"Service doing work with address", Toast.LENGTH_LONG).show();
    }
}

