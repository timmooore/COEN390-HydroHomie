package com.example.hydrohomie;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.yuana.itemsettingview.ItemSettingView;


public class AppSetting extends Fragment {
    private FirebaseAuth mAuth;
    private ItemSettingView itemNotif1;
    private ItemSettingView itemNotif2;
    private ItemSettingView itemNotif3;
    private ItemSettingView itemNotif4;
    private ItemSettingView itemNotif5;
    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_setting, container, false);

        itemNotif1 = view.findViewById(R.id.itemNotif1);
        itemNotif2 = view.findViewById(R.id.itemNotif2);
        itemNotif3 = view.findViewById(R.id.itemNotif3);
        itemNotif4 = view.findViewById(R.id.itemNotif4);
        itemNotif5 = view.findViewById(R.id.itemNotif5);
        mAuth = FirebaseAuth.getInstance();
        initItemNotif1();
        initItemNotif2();
        initItemNotif3();
        initItemNotif4();
        initItemNotif5();

        return view;
    }

    private void initItemNotif1() {
        itemNotif1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment with SettingBonhomme fragment
                SettingBonhomme settingBonhommeFragment = new SettingBonhomme();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, settingBonhommeFragment);
                transaction.addToBackStack(null); // Add transaction to the back stack
                transaction.commit();
            }
        });
    }


    private void initItemNotif2() {
        itemNotif2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BluetoothFragment Bluetooth = new BluetoothFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, Bluetooth);
                transaction.addToBackStack(null); // Add transaction to the back stack
                transaction.commit();
            }
        });
    }

    private void initItemNotif3() {
        itemNotif3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment with SettingBonhomme fragment
                goals goals = new goals();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, goals);
                transaction.addToBackStack(null); // Add transaction to the back stack
                transaction.commit();
            }
        });
    }


    private void initItemNotif4() {
        itemNotif4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), signinup.class);
                startActivity(intent);
            }
        });
    }


    private void initItemNotif5() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();

                itemNotif5.setLabel(userEmail);
        }

    }
}