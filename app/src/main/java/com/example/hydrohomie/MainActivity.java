package com.example.hydrohomie;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;



    private Toolbar toolbar;
    private SettingBonhomme settingBonhomme = new SettingBonhomme();
    private BottomNavigationView bottomNavigationView;

    private home firstFragment = new home();
    private details secondFragment = new details();
    private bluetooth thirdFragment = new bluetooth();

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bonhomme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.sub_action_two) {
            Intent intent = new Intent(this, SettingMenuBonhomme.class);
            startActivity(intent);
            return true;
        }

// signup button might also be useless
        if (itemId == R.id.sub_action_third) {
            Intent intent = new Intent(this, signup.class);
            startActivity(intent);
            return true;
        }
//logout
        if (itemId == R.id.sub_action_forth) {
            mAuth.signOut();
            Intent intent = new Intent(this, signinup.class);
            startActivity(intent);
            return true;
        }

        //login button might be uselesss
        if (itemId == R.id.sub_action_fifth) {
            Intent intent = new Intent(this, signin.class);
            startActivity(intent);
            return true;
        }


        /// goals is a fragment not an activity
        if (itemId == R.id.sub_action_sixth) {

            goals goalsFragment = new goals();


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, goalsFragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
// navigation bar setup menu option
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Menu option based on the icon and which fragment should be reached
        if (itemId == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
            return true;
        } else if (itemId == R.id.details) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
            return true;
        } else if (itemId == R.id.bluetooth) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
            return true;
        }

        return false;
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




}
