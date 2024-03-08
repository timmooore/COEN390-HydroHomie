package com.example.hydrohomie;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class home extends Fragment {
    private Toolbar toolbar;// the toolbar at the top with the icon
    private SettingBonhomme settingBonhomme = new SettingBonhomme();
    protected ProgressBar simpleProgressBar;// the progress bar
    protected Button refreshButton;// the refresh button that shows what we should expect to see from having touched the button
    protected TextView titleNotif;// the notifcation button on what is expected to be seen
    protected int progress;// the progress that is connected to the xml code for the progress bar adding value to it
    public home() {
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        simpleProgressBar = view.findViewById(R.id.pb);

        refreshButton = view.findViewById(R.id.refresh);
        titleNotif=view.findViewById(R.id.titleNotif);
        // Enable options menu in the fragment
        setHasOptionsMenu(true);
        updateText();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress=progress+10;
                simpleProgressBar.setProgress(progress);
                updateText();
            }
        });


        return view;
    }

    private void updateText() {
        if (progress >= 0 && progress < 50) {
            titleNotif.setText("LOW CONSUMPTION, DRINK WATER :(");
        } else if (progress >= 50 && progress < 75) {
            titleNotif.setText("MODERATE CONSUMPTION, KEEP HYDRATING :/");
        } else if (progress >= 75 && progress <= 100) {
            titleNotif.setText("GOOD CONSUMPTION, STAY HYDRATED! :)");
        } else {
            // Handle any other values outside the specified ranges
            titleNotif.setText("Keep hydrating!");
        }
    }





    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bonhomme, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.sub_action_two) {
            Intent intent = new Intent(requireContext(), SettingMenuBonhomme.class);
            startActivity(intent);
            return true;
        }

        if (itemId == R.id.sub_action_third) {
            Intent intent = new Intent(requireContext(), signinup.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
