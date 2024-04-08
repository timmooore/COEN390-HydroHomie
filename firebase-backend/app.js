const express = require('express');
const admin = require('firebase-admin');

const serviceAccount = require('./hydrohomie-84a97-firebase-adminsdk-8t5up-99b3b56f34.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://hydrohomie-84a97-default-rtdb.firebaseio.com"
});

const app = express();

// Define route to compare values
app.get('/', async (req, res) => {
  try {
    // Set up a real-time listener on 'user_data' to react to any changes
    const userDataRef = admin.database().ref('user_data');

    userDataRef.on('child_changed', (snapshot) => {
      const userId = snapshot.key; // Retrieve the user_id that was updated
      const userData = snapshot.val(); // Retrieve the updated user_data for the user_id

      console.log(`Comparison triggered for user_id: ${userId}`);

      // Perform comparison for the updated user_data
      const comparisonResult = performComparison(userId, userData);

      // Send the comparison result to the client (frontend)
      res.write(`Comparison result for ${userId}: ${comparisonResult}\n`);
    });

    res.status(200).json({ message: 'Comparison listener set up successfully' });
  } catch (error) {
    console.error('Error setting up comparison listener:', error);
    res.status(500).json({ error: 'Failed to set up comparison listener' });
  }
});

// Function to perform comparison for a specific user_id and user_data
async function performComparison(userId, userData) {
  try {
    const userIntake = await getUserIntakeRef(userId);
    if (userIntake == {}) {
      console.log('userGoals ref is empty');
    }

    const currentDate = getFormattedDate();
    console.log(currentDate);

    const dateRecord = userData[currentDate];
    if (!dateRecord) {
      console.log(`Data not found for user ${userId} on date: ${currentDate}`);
    }

    console.log(dateRecord);

    const latestTimeSlot = dateRecord['latest_time_slot'];
    const values = dateRecord['values'] || {}; // Ensure 'values' is defined
    const latestReading = values[latestTimeSlot];

    if (latestReading === undefined) {
      console.log(`Latest reading not found for user ${userId} on date: ${currentDate}`);
    }

    const goalsTimestamp = findClosestLowerTimestamp(latestTimeSlot);
    const cumulatedRecommendation = userIntake?.[goalsTimestamp] || 0;

    // Perform comparison logic between userData and userGoals
    const comparisonResult = latestReading >= cumulatedRecommendation ? 'exceeds' : 'does not exceed';
    console.log(`Comparison result for ${userId}: ${comparisonResult}`);

    // Trigger notification based on comparison result
    const message = `Your consumption ${comparisonResult} the goal`;
    await sendNotificationToUser(userId, message);
    return comparisonResult;

  } catch (error) {
    console.error('Error performing comparison:', error);
    // Handle the error (e.g., notify or retry) based on your application's needs
  }
}

// Function to retrieve user_goals data for a specific user_id
async function getUserIntakeRef(userId) {
  try {
    const userIntakeRef = admin.database().ref(`/user_goals/${userId}/incremental_intake_data`);
    const snapshot = await userIntakeRef.once('value');
    return snapshot.val() || {}; // Return the data or an empty object if no data found
  } catch (error) {
    console.error('Error fetching user_goals data:', error);
    throw error; // Throw the error to handle it at the caller's level
  }
}

function getFormattedDate() {
  // Get today's date
  const today = new Date();

  // Subtract 4 hours from the current date
  today.setHours(today.getHours() - 4);
  // Extract year, month, and day components
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0'); // Months are zero-based (0 = January)
  const day = String(today.getDate()).padStart(2, '0');

  // Formatted date in 'yyyy-mm-dd' format
  return `${year}-${month}-${day}`;
}

function findClosestLowerTimestamp(inputTimestamp) {
  // Parse hour (hh), minute (mm), and second (ss) components from input timestamp
  const [hour, minute] = inputTimestamp.split(':').map(Number);

  // Round down minute to nearest lower 15-minute increment
  const roundedMinute = Math.floor(minute / 15) * 15;

  // Construct hh:mm format for the closest lower timestamp
  const closestLowerTimestamp = `${hour}:${roundedMinute.toString().padStart(2, '0')}`;

  return closestLowerTimestamp;
}

async function sendNotificationToUser(userId, message) {
  try {
    const userDeviceToken = await getUserDeviceToken(userId); // Retrieve user's FCM token

    if (!userDeviceToken) {
      console.log(`FCM token not found for user ID: ${userId}`);
      return;
    }

    const payload = {
      notification: {
        title: 'Comparison Result',
        body: message,
      },
    };

    const options = {
      priority: 'high',
      timeToLive: 60 * 60 * 24, // 1 day (in seconds)
    };

    await admin.messaging().sendToDevice(userDeviceToken, payload, options);
    console.log('Notification sent successfully');
  } catch (error) {
    console.error('Error sending notification:', error);
  }
}

async function getUserDeviceToken(userId) {
  try {
    // Implement logic to fetch user's FCM token from your database
    // For example:
    const userFCMTokenRef = admin.database().ref(`/FCM_tokens/${userId}`);
    const snapshot = await userFCMTokenRef.once('value');
    
    if (snapshot.exists()) {
      console.log(snapshot.val());
      const fcmToken = snapshot.val(); // Assuming 'fcmToken' is the field containing FCM token
      if (fcmToken) {
        return fcmToken;
      } else {
        console.log(`FCM token not found for user ID: ${userId}`);
        return null;
      }
    } else {
      console.log(`User not found for user ID: ${userId}`);
      return null;
    }
  } catch (error) {
    console.error('Error fetching user device token:', error);
    return null;
  }
}

// Start the Express.js server
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server is listening on port ${PORT}`);
});
