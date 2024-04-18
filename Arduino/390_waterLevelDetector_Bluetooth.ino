 // US Sensor and LED pins
#define PIN_TRIG 27
#define PIN_ECHO 26
#define GREEN_LED 19
#define YELLOW_LED 18
#define RED_LED 4
#define BLUE_LED 32
#define ADXL345_POWER_PIN 17

#define WH_MAX 16 // Define the maximum height of water (in cm) insdie the bottle
#define WH_OFFSET 2 // Define the distance between sensor and 100% water level (ignored height)
#define WC_TOT 700 // Define the total capacity of water (in L) of the bottle

const int WINDOW_SIZE = 7; // define window size for median filtering

#include <Wire.h>
#include "BluetoothSerial.h"

// Check if bluetooth configs are enabled
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial SerialBT; // bluetooth serial object

char cmd; // variable for reading from bluetooth serial terminal

// Accelerometer variables
int ADXL345 = 0x53; // The ADXL345 sensor I2C address
float X_out, Y_out, Z_out; // Outputs
int X_offset = 0, Y_offset = 0, Z_offset = 0; // Offset values

// US Sensor readings variables
long duration, distance;
double currWaterHeight, waterConsumption;
double prevWaterHeight = 0;

// Water level activity flags
bool waterConsumed = false;
bool waterRefilled = false;
bool acknowledged  = true;
bool idle = false;

long distanceReadings[WINDOW_SIZE]; // Array to store sensor readings
size_t static idx = 0; // index to keep track of current position in the array ranging from 0 to (WINDOW_SIZE - 1)

// Timer: auxiliar variables
unsigned long previousMillis = 0;    // Stores last time reading was published
const long interval = 1000;         // interval at which to publish sensor readings

/****************************************************************************************************************************************************************************************************/

void setup() {
  Serial.begin(9600); // Initialize serial communication with a baud rate of 9600 bits per second

  SerialBT.begin("HydroHomieBottle"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

  // Specify that LED pins will be used as output pins
  pinMode(GREEN_LED, OUTPUT);
  pinMode(YELLOW_LED, OUTPUT);
  pinMode(RED_LED, OUTPUT);
  pinMode(BLUE_LED, OUTPUT);

  // Set LED pins to low voltage level
  digitalWrite(GREEN_LED, LOW);
  digitalWrite(YELLOW_LED, LOW);
  digitalWrite(RED_LED, HIGH);
  digitalWrite(BLUE_LED, LOW);

  // Specify input/output pins of US sensor
  pinMode(PIN_TRIG, OUTPUT);
  pinMode(PIN_ECHO, INPUT);

  // Specify pin connected to VCC of ADXL345
  pinMode(ADXL345_POWER_PIN, OUTPUT);
  digitalWrite(ADXL345_POWER_PIN, HIGH);

  // Setup ADXL345
  Wire.begin();
  configureADXL345(); // Configure the sensor
  calibrateADXL345(); // Calibrate the sensor

  // configSensor(); // start a new US sensor measurement
  // // get initial distance readings to populate the array
  // while ( (sizeof(distanceReadings)) / (sizeof(distanceReadings[0])) != WINDOW_SIZE) {
  //   getDistanceReading();
  // }

}

void loop() {
  receiveAcknowledgement();

  if (verticallyOriented()) {

    calculateWaterLevel();

    if (waterConsumed || acknowledged == false) {
      if (acknowledged) {
        Serial.print("Ack true: You have consumed: " + String(waterConsumption) + "mL\n");
      } else {
        Serial.print("Ack false: You have consumed: " + String(waterConsumption) + "mL\n");
      }
    
      // Send amount of water consumed to bluetooth serial terminal
      SerialBT.println("You have consumed: " + String(waterConsumption) + "mL\n");
    } else {
      Serial.print("True: You have consumed: " + String(0.0) + "mL\n");
    
      // Send amount of water consumed to bluetooth serial terminal
      SerialBT.println("You have consumed: " + String(0.0) + "mL\n");
    }
  
  // get water level measurements (old)
  // waterHeight = (WH_MAX+WH_OFFSET) - medianDistance; // get the water height in cm
  // waterLevel = waterHeight / WH_MAX * 100; // percentage of water height in the bottle
  // bottleCapacity = waterLevel/100 * WC_TOT; // Real-time capacity of the bottle
  // waterConsumption = WC_TOT - bottleCapacity;

    // Display the results
    Serial.print("Previous height of water: " + String(prevWaterHeight) + " cm" + ", Current height of water: " + String(currWaterHeight) + " cm\n");
 

  // Send readings to bluetooth serial terminal every second
  // unsigned long currentMillis = millis(); 
  // if (currentMillis - previousMillis >= interval){
  //   previousMillis = currentMillis;
  //   SerialBT.println("You have consumed : " + String(waterConsumption) + "mL");
  // }

    // TODO: Update conditions for lighting LEDs
    if (currWaterHeight <= WH_MAX && currWaterHeight >= (WH_MAX/2)) // bottle is 50-100 % full
    {
      // turn on red LED
      // digitalWrite(GREEN_LED, LOW);
      // digitalWrite(YELLOW_LED, LOW);
      digitalWrite(BLUE_LED,LOW);
      // digitalWrite(RED_LED, HIGH);
      // Serial.println("Keep hydrating! You've drank less than half of the water bottle!");
    }
    else if (currWaterHeight < (WH_MAX/2) && currWaterHeight > 1) // bottle is less than 50% full, but not empty
    {
      // turn on yellow LED
      // digitalWrite(GREEN_LED, LOW);
      // digitalWrite(YELLOW_LED, HIGH);
      // digitalWrite(RED_LED, LOW);
      digitalWrite(BLUE_LED,LOW);
      Serial.println("Keep hydrating. You've drank more than half of the water bottle!");
    }
    else if (currWaterHeight >= 0 && currWaterHeight <=1)
    {
      // turn on green LED
      // digitalWrite(GREEN_LED, HIGH);
      // digitalWrite(YELLOW_LED, LOW);
      // digitalWrite(RED_LED, LOW);
      digitalWrite(BLUE_LED,LOW);
      Serial.println("Congrats! You drank a full water bottle!");
      Serial.println("Please refill the water bottle");
    }
// TODO: REVERT
    delay(1000);

  } // end if vertically oriented
  else {
    Serial.println("Not vertically oriented...");
  }

} // end loop

/****************************************************************************************************************************************************************************************************/

/* US Sensor Helper Functions */

void configSensor() {
  // Start a new US sensor measurement:
  digitalWrite(PIN_TRIG, LOW);  // Provide a short LOW pulse first to ensure a clean HIGH pulse
  delayMicroseconds(5);
  digitalWrite(PIN_TRIG, HIGH);  // Sensor is triggered by a HIGH pulse of 10 us or more
  delayMicroseconds(10);
  digitalWrite(PIN_TRIG, LOW);
}

void getDistanceReading() {

  configSensor();

  // Read the result from sensor
  duration = pulseIn(PIN_ECHO, HIGH);
  distance = (duration/2) / 29.1; // convert time into a distance

  distanceReadings[idx] = distance; // add distance reading to array
  idx = (idx+1) % WINDOW_SIZE; // update array index and wrap back to zero if necessary

  // Serial.print("Distance: " + String(distance) + " cm, idx: " + String(idx) + "\n");
}

void calculateWaterLevel() {
  int numValidReadings = 0;

  // take required number of valid readings
  while (numValidReadings < WINDOW_SIZE) {
    Serial.println("Measuring water level...");

    getDistanceReading(); // add distance reading to distanceReadings array
    
    // Check if reading is in the acceptable range
    if (idx != 0) { // array index is non-zero
      if (distanceReadings[idx-1] <= (WH_MAX + WH_OFFSET) && distanceReadings[idx-1] >= WH_OFFSET) { // latest reading is stored at array index (idx-1)
        numValidReadings++; // update the count of valid readings
      }
    }
    else { // array index is zero
      if (distanceReadings[WINDOW_SIZE - 1] <= (WH_MAX + WH_OFFSET) && distanceReadings[WINDOW_SIZE - 1] >= WH_OFFSET) { // latest reading is stored at array index (WINDOW_SIZE - 1)
        numValidReadings++; // update the count of valid readings
      }
    }

    delay(10);
  }

  Serial.println("Water level measured...");

  long medianDistance = medianFilter(); // get the filtered distance reading
  currWaterHeight = (WH_MAX+WH_OFFSET) - medianDistance; // get the water height in cm

  if (currWaterHeight < prevWaterHeight) { // water is consumed from the bottle
  
    if (acknowledged == false) {
      waterConsumption += calculateWaterConsumption(prevWaterHeight, currWaterHeight); // get amount of water consumed (in mL)
    } else {
      waterConsumption = calculateWaterConsumption(prevWaterHeight, currWaterHeight); // get amount of water consumed (in mL)
      acknowledged = false;
    }

    prevWaterHeight = currWaterHeight;

    // update water activity flags 
    waterConsumed = true;
    waterRefilled = false;
    idle = false;
  }
  else if (currWaterHeight > prevWaterHeight) { // water is refilled
    prevWaterHeight = currWaterHeight;

    // update water activity flags 
    waterConsumed = false;
    waterRefilled = true;
    idle = false;

  }
  else if (currWaterHeight == prevWaterHeight) { // no change in water level
    // update water activity flags 
    waterConsumed = false;
    waterRefilled = false;
    idle = true;
  }

}

double calculateWaterConsumption(double prev, double curr) {
  double diff = prev - curr; // get the difference in water height
  return diff/WH_MAX * WC_TOT; // convert height (in cm) to amount in (mL)
}

void insertionSort(long arr[], int n) {
    int i, j;
    long key;
    for (i = 1; i < n; i++) {
        key = arr[i];
        j = i - 1;

        // Move elements of array up to i-1 that are greater than key, to one position ahead of their current position
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j = j - 1;
        }
        arr[j + 1] = key;
    }
}

long calculateMedianValue(long arr[]) {
  // calculate the median value
  long median;
  if (WINDOW_SIZE % 2 == 0) { // Window size is even
    median = (arr[WINDOW_SIZE / 2 - 1] + arr[WINDOW_SIZE / 2]) / 2;
  } 
  else { // Window size is odd
    median = arr[WINDOW_SIZE / 2]; // get middle value
  }

  return median;
}

long medianFilter() {
  // return the filtered distance

  // sort the array of distance readings
  long sortedDistanceReadings[WINDOW_SIZE]; 
  memcpy(sortedDistanceReadings, distanceReadings, sizeof(distanceReadings)); // create a copy of array to be sorted
  insertionSort(sortedDistanceReadings, WINDOW_SIZE);

  // calculate the median value
  return calculateMedianValue(sortedDistanceReadings);

}

/* Accelerometer helper functions */

void calibrateADXL345() {
  // calibration with the positive x-axis pointed upwards against gravity
 
  float numReadings = 500;
  float xSum = 0;
  // float ySum = 0;
  // float zSum = 0;

  // take 500 readings
  for (int i = 0; i < numReadings; i++) {
    Wire.beginTransmission(ADXL345);
    Wire.write(0x32); // Start with register 0x32 (ACCEL_XOUT_H)
    Wire.endTransmission(false);
    Wire.requestFrom(ADXL345, 6, true);
    // read raw values
    X_out = (Wire.read() | Wire.read() << 8);
    Y_out = (Wire.read() | Wire.read() << 8);
    Z_out = (Wire.read() | Wire.read() << 8);
    // add to running sum
    xSum += X_out;
    // ySum += Y_out;
    // zSum += Z_out;
  }
  // 256 is the raw value for 1g: x-axis calibrated at 1g, y and z-axis at 0

  // calculate offsets
  X_offset = (256 - xSum / numReadings) / 4;

  // Y_offset = (0-ySum / numReadings) / 4;

  // Z_offset = (0-zSum / numReadings) / 4;
 
  delay(1000);
  
  // Write offsets to respective offset registers
  Wire.beginTransmission(ADXL345);
  Wire.write(0x1E); // x-axis offset register
  Wire.write(X_offset);
  Wire.endTransmission();

  // Wire.beginTransmission(ADXL345);
  // Wire.write(0x1F); // y-axis offset register
  // Wire.write(Y_offset);
  // Wire.endTransmission();

  // Wire.beginTransmission(ADXL345);
  // Wire.write(0x20); // z-axis offset register
  // Wire.write(Z_offset); // 
  // Wire.endTransmission();
}

void configureADXL345() {
  // Wire.beginTransmission(ADXL345);
  // Wire.write(0x2C); // BW_RATE register
  // Wire.write(0x08); // set data rate (D3 bit high)
  // Wire.endTransmission();

  // Wire.beginTransmission(ADXL345);
  // Wire.write(0x31); // DATA_FORMAT register
  // Wire.write(0x08); // Enable FULL_RES (D3 bit high)
  // Wire.endTransmission();

  Wire.beginTransmission(ADXL345);
  Wire.write(0x2D); // POWER_CTL Register
  Wire.write(8); // Enable measurement (D3 bit high)
  Wire.endTransmission();


  delay(10);
}

void readAccelData() {
  // Read accelerometer data
  Wire.beginTransmission(ADXL345);
  Wire.write(0x32); // Start with register 0x32 (ACCEL_XOUT_H)
  Wire.endTransmission(false);
  Wire.requestFrom(ADXL345, 6, true); // Read 6 registers total, each axis value stored in 2 registers
  X_out = (Wire.read() | Wire.read() << 8); // Raw x-axis value
  X_out = X_out / 256; // For default sensitivity (+-2g), divide by 256 
  Y_out = (Wire.read() | Wire.read() << 8); // Raw y-axis value
  Y_out = Y_out / 256;
  Z_out = (Wire.read() | Wire.read() << 8); // Raw z-axis value
  Z_out = Z_out / 256;
}

bool verticallyOriented() {
  // Averaging of ADXL345
  float averageX_out = 0;
  float numReadings = 16;
  for (int i = 0; i < numReadings; i++) {
    readAccelData();
    averageX_out += X_out;
    delay(1);
  }
  averageX_out /= numReadings;

  Serial.print("Average X_out = ");
  Serial.println(averageX_out);
  // delay(1000);

  if (averageX_out <= 1.1 && averageX_out >= 0.98) {
    return true;
  }

  // if not vertically oriented, turn on blue LED
  digitalWrite(BLUE_LED,HIGH);
  digitalWrite(GREEN_LED, LOW);
  digitalWrite(YELLOW_LED, LOW);
  digitalWrite(RED_LED, LOW);
  return false;

}

/* LED helper functions */

void updateLEDs(char color) {

  if (color == 'r') {
    // turn on red LED
    digitalWrite(GREEN_LED, LOW);
    digitalWrite(YELLOW_LED, LOW);
    digitalWrite(BLUE_LED,LOW);
    digitalWrite(RED_LED, HIGH);
    Serial.println("Keep hydrating! You have not yet reached half of your hydration goal!");
  }
  else if (color == 'y') {
    // turn on yellow LED
    digitalWrite(GREEN_LED, LOW);
    digitalWrite(YELLOW_LED, HIGH);
    digitalWrite(RED_LED, LOW);
    digitalWrite(BLUE_LED,LOW);
    Serial.println("Keep hydrating. You've drank more than half of the water bottle!");
  }
  else if (color == 'g') {
    // turn on green LED
    digitalWrite(GREEN_LED, HIGH);
    digitalWrite(YELLOW_LED, LOW);
    digitalWrite(RED_LED, LOW);
    digitalWrite(BLUE_LED,LOW);
    Serial.println("Congrats! You have met your hydration goal");
  }

}

void receiveAcknowledgement() {
  // Read command from bluetooth serial terminal
  if (SerialBT.available()) {
    Serial.println("SerialBT.available() == true");
    cmd = SerialBT.read();
    if (cmd != -1) {
      if (cmd == 'a') {
        acknowledged = true;
        Serial.println("ACK received");
      }
      else {
        // call updateLED()
        Serial.println("ACK not received");
      }
    }

    bool updateLED = true;

    // Flush the input buffer by reading and discarding any remaining bytes
    while (SerialBT.available()) {
    
      char c = SerialBT.read(); // Read and discard the remaining bytes in the input buffer
      if (updateLED) {
        updateLEDs(c);
        updateLED = false;
      }
    }
  }
}
