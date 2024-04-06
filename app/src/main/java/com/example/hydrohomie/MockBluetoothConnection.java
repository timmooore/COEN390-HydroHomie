package com.example.hydrohomie;

public class MockBluetoothConnection {
    private boolean isConnected = false;

    // Method to simulate connecting to a mock Bluetooth device
    public void connect() {
        // Simulate the connection process (e.g., establishing connection)
        // For demonstration purposes, we'll just print a message
        System.out.println("Simulating Bluetooth connection...");
        // Set isConnected flag to true to indicate connection
        isConnected = true;
    }

    // Method to simulate disconnecting from the mock Bluetooth device
    public void disconnect() {
        // Simulate the disconnection process (e.g., closing connection)
        // For demonstration purposes, we'll just print a message
        System.out.println("Simulating Bluetooth disconnection...");
        // Set isConnected flag to false to indicate disconnection
        isConnected = false;
    }

    // Method to check if the mock Bluetooth device is connected
    public boolean isConnected() {
        return isConnected;
    }
}
