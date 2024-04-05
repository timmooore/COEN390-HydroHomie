package com.example.hydrohomie;

import android.app.ActivityManager;
import android.content.Context;
import static androidx.core.content.ContextCompat.startForegroundService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;



    private Toolbar toolbar;
    private SettingBonhomme settingBonhomme = new SettingBonhomme();
    private BottomNavigationView bottomNavigationView;
    private home firstFragment = new home();
    private WaterConsumptionHistory waterConsumption = new WaterConsumptionHistory();
    private BluetoothFragment thirdFragment = new BluetoothFragment();
    private goals forthFragment = new goals();
    private SettingBonhomme fifthFragment = new SettingBonhomme();
    private int selectedMenuItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The home fragment will be defaulted as the first fragment
        // Initial fragment transaction
        loadFragment(firstFragment);
        selectedMenuItemId = R.id.home; // Set the default selected menu item
     //   SensorReaderData.pushDummyDataToFirebase();



    public static class GoalsSignup extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_goals_signup);

        }
    }



//        // Check if BluetoothService is running and run it if not
//        if (!isServiceRunning()) {
//            // If not running, start the service
//            Intent serviceIntent = new Intent(this, BluetoothService.class);
//            startService(serviceIntent);
//        }
    }

    // navigation bar setup menu option
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Menu option based on the icon and which fragment should be reached
        if (itemId == R.id.home) {
            selectedMenuItemId = R.id.home;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
            return true;
        } else if (itemId == R.id.details) {

            selectedMenuItemId = R.id.details;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,waterConsumption).commit();
            return true;


        } else if (itemId == R.id.bluetooth) {
            selectedMenuItemId = R.id.bluetooth;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
            return true;
        } else if (itemId == R.id.goals) {
            selectedMenuItemId = R.id.goals;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, forthFragment).commit();
            return true;
        } else if (itemId == R.id.setting) {
            selectedMenuItemId = R.id.setting;
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, fifthFragment).commit();
            return true;
        }

        return false;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bonhomme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // Update the selected menu item and synchronize it with the bottom navigation view
        selectedMenuItemId = itemId;
        bottomNavigationView.setSelectedItemId(itemId);
        if (itemId == R.id.sub_action_one) {


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, thirdFragment)
                    .addToBackStack(null)  // Optional: This allows the user to navigate back to the previous fragment
                    .commit();

            return true;
        }


        if (itemId == R.id.sub_action_two) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, fifthFragment)
                    .addToBackStack(null)  // Optional: This allows the user to navigate back to the previous fragment
                    .commit();

            return true;
        }

//// signup button might also be useless
//        if (itemId == R.id.sub_action_third) {
//            Intent intent = new Intent(this, signup.class);
//            startActivity(intent);
//            return true;
//        }
//logout
        if (itemId == R.id.sub_action_forth) {
            mAuth.signOut();
            Intent intent = new Intent(this, signinup.class);
            startActivity(intent);
            return true;
        }

//        //login button might be uselesss
//        if (itemId == R.id.sub_action_fifth) {
//            Intent intent = new Intent(this, signin.class);
//            startActivity(intent);
//            return true;
//        }


        /// goals is a fragment not an activity
        if (itemId == R.id.sub_action_sixth) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, forthFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //  smth to do with navigation bar bottom
    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in during the app's lifecycle (e.g., when returning from background)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User is not signed in, navigate to signinup activity
            Intent intent = new Intent(this, signinup.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent the user from coming back to it
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "MainActivity onDestroy()");
    }

    // Method to start the BluetoothService
    public void startBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
    }

    // Method to stop the BluetoothService
    public void stopBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        stopService(serviceIntent);
    }

    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices != null) {
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (BluetoothService.class.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

}