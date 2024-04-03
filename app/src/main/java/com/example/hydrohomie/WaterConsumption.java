package com.example.hydrohomie;

public class WaterConsumption {
    private int year;
    private int month;
    private int day;
    private double amount; // Amount of water consumed for the day


    public WaterConsumption(int year, int month, int day, double amount) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.amount = amount;
    }

    // Getter methods
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public double getAmount() {
        return amount;
    }
}
