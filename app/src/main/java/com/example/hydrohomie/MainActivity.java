

package com.example.hydrohomie;

        import android.os.Bundle;
        import android.view.MenuItem;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentTransaction;

        import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    private home firstFragment = new home();// button for the bottomNavigation
    private details secondFragment = new details();// button for the bottom navigation
    private bluetooth thirdFragment = new bluetooth();// button for the bottom navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        // the home fragement will be defaulted as the first fragement
        // Initial fragment transaction
        loadFragment(firstFragment);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
// menu option based on the icon and which fragement should be reached
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

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragment)
                    .commit();
        }
    }
}

