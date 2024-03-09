package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class goals extends Fragment {

    protected EditText info1, info2, info3;
    protected TextView infO1, infO2, infO3;
    protected Button save,edit;
    protected Toolbar toolbar;
    protected boolean toggle = false;
    private FirebaseAuth mAuth;

    public goals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        mAuth = FirebaseAuth.getInstance();
      //  toolbar = view.findViewById(R.id.toolbar);

        info1 = view.findViewById(R.id.info1);
        info2 = view.findViewById(R.id.info2);
        info3 = view.findViewById(R.id.info3);
        infO1 = view.findViewById(R.id.infO1);
        infO2 = view.findViewById(R.id.infO2);
        infO3 = view.findViewById(R.id.infO3);
        save = view.findViewById(R.id.Save);
        edit = view.findViewById(R.id.edit);

        // Set up the toolbar for the fragment

        //enableText();

        // Retrieve and display data when the fragment is created
        retrieveAndDisplayData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
                disableText();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableText();
            }
        });

        return view;
    }

    private void retrieveAndDisplayData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the user's goals in the database
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            userGoalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve data from the database
                        String dbValue1 = dataSnapshot.child("info1").getValue(String.class);
                        String dbValue2 = dataSnapshot.child("info2").getValue(String.class);
                        String dbValue3 = dataSnapshot.child("info3").getValue(String.class);

                        info1.setText(dbValue1);
                        info2.setText(dbValue2);
                        info3.setText(dbValue3);

                        // Disable or enable text based on data presence
                        if (dbValue1 != null && dbValue2 != null && dbValue3 != null) {
                            disableText();
                        } else {
                            enableText();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }



    private void saveInformation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            String value1 = info1.getText().toString();
            String value2 = info2.getText().toString();
            String value3 = info3.getText().toString();

            // Create a reference to the user's goals in the database
            DatabaseReference userGoalsRef = FirebaseDatabase.getInstance().getReference("user_goals").child(userId);

            // Save the information to the user's goals
            userGoalsRef.child("info1").setValue(value1);
            userGoalsRef.child("info2").setValue(value2);
            userGoalsRef.child("info3").setValue(value3);

            // Retrieve data from userGoalsRef and update TextViews
            userGoalsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve data from the database
                        String dbValue1 = dataSnapshot.child("info1").getValue(String.class);
                        String dbValue2 = dataSnapshot.child("info2").getValue(String.class);
                        String dbValue3 = dataSnapshot.child("info3").getValue(String.class);
                        info1.setText(dbValue1);
                        info2.setText(dbValue2);
                        info3.setText(dbValue3);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }




    private void disableText(){
        info1.setEnabled(false);
        info2.setEnabled(false);
        info3.setEnabled(false);
        save.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
    }
    private void enableText(){
        info1.setEnabled(true);
        info2.setEnabled(true);
        info3.setEnabled(true);
        save.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
    }







}
