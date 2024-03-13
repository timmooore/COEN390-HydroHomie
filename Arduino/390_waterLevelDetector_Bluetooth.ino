 // US Sensor and LED pins
#define PIN_TRIG 27
#define PIN_ECHO 26
#define LOW_LED 18
#define MID_LED 19
#define HIGH_LED 21

#define WH_MAX 16 // Define the maximum height of water (in cm) insdie the bottle
#define WH_OFFSET 2 // Define the distance between sensor and 100% water level (ignored height)
#define WC_TOT 700 // Define the total capacity of water (in L) of the bottle

#include <Wire.h>
#include "BluetoothSerial.h"

// Check if bluetooth configs are enabled
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT; // bluetooth serial object

// Sensor readings variables
long duration, distance;
double waterHeight, waterLevel, bottleCapacity, waterConsumption;

// Timer: auxiliar variables
unsigned long previousMillis = 0;    // Stores last time temperature was published
const long interval = 1000;         // interval at which to publish sensor readings

void setup() {
  Serial.begin(9600); // Initialize serial communication with a baud rate of 9600 bits per second

  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

  // Specify that LED pins will be used as output pins
  pinMode(LOW_LED, OUTPUT);
  pinMode(MID_LED, OUTPUT);
  pinMode(HIGH_LED, OUTPUT);

  // Set LED pins to high voltage level
  digitalWrite(LOW_LED, LOW);
  digitalWrite(MID_LED, LOW);
  digitalWrite(HIGH_LED, LOW);

  // Specify input/output pins of US sensor
  pinMode(PIN_TRIG, OUTPUT);
  pinMode(PIN_ECHO, INPUT);
}

void loop() {
  // Start a new measurement:
  
  // Sensor is triggered by a HIGH pulse of 10 us or more
  // Provide a short LOW pulse first to ensure a clean HIGH pulse
  digitalWrite(PIN_TRIG, LOW);
  delayMicroseconds(5);
  digitalWrite(PIN_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(PIN_TRIG, LOW);

  // Read the result:
  duration = pulseIn(PIN_ECHO, HIGH);
  distance = (duration/2) / 29.1; // convert time into a distance
  waterHeight = (WH_MAX+WH_OFFSET) - distance; // get the water height in cm
  waterLevel = waterHeight / WH_MAX * 100; // percentage of water height in the bottle
  bottleCapacity = waterLevel/100 * WC_TOT; // Real-time capacity of the bottle
  waterConsumption = WC_TOT - bottleCapacity;

  // Display the results
  Serial.print("Distance: " + String(distance) + " cm\n");
  Serial.print("Height of water: " + String(waterHeight) + " cm\n");
  Serial.print("Water level percentage: " + String(waterLevel) + "%\n");
  Serial.print("Amount of water in bottle: " + String(bottleCapacity) + " mL\n");
  Serial.print("You have consumed : " + String(waterConsumption) + "mL\n");


  // Send readings to bluetooth serial terminal every
  unsigned long currentMillis = millis(); 
  if (currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
    SerialBT.println("You have consumed : " + String(waterConsumption) + "mL");
  }

  // Set conditions for lighting LEDs
  if (waterLevel <= 100 && waterLevel >= 50) // bottle is 50-100 % full
  {
    // turn on red LED
    digitalWrite(LOW_LED, LOW);
    digitalWrite(MID_LED, LOW);
    digitalWrite(HIGH_LED, HIGH);
    Serial.println("Keep hydrating! You've drank less than half of the water bottle!");
  }
  else if (waterLevel < 50 && waterLevel > 0) // bottle is less than 50% full, but not empty
  {
    // turn on yellow LED
    digitalWrite(LOW_LED, LOW);
    digitalWrite(MID_LED, HIGH);
    digitalWrite(HIGH_LED, LOW);
    Serial.println("Keep hydrating. You've drank more than half of the water bottle!");
  }
  else if (waterHeight >= 0 && waterHeight <=1)
  {
    // turn on green LED
    digitalWrite(LOW_LED, HIGH);
    digitalWrite(MID_LED, LOW);
    digitalWrite(HIGH_LED, LOW);
    Serial.println("Congrats! You drank a full water bottle!");
    Serial.println("Please refill the water bottle");
  }

  delay(1000);
}


