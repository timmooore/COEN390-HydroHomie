package com.example.hydrohomie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
protected EditText name;
protected TextView read;
protected Button save,read1;
private DatabaseReference root,root1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.name);
        save=findViewById(R.id.button);
        read=findViewById(R.id.read);
        read1=findViewById(R.id.button2);

        /// making reference to the database , the child() is a branch(subfile) of the main branch
        root1= FirebaseDatabase.getInstance().getReference().child("SUBDATA");

/// method that help user read from the database directly
        read1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                root1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String data=snapshot.getValue().toString();
                            read.setText(data);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        /// method that help the user save into the data base
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Data=name.getText().toString();
                root1.setValue(Data);
            }
        });
    }
}