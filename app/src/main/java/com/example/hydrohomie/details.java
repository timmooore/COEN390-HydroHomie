package com.example.hydrohomie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hydrohomie.R;
//import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;
//import com.harrywhewell.scrolldatepicker.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;

public class details extends Fragment {

//    DayScrollDatePicker dayDatePicker;

    public details() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
//
//        dayDatePicker = rootView.findViewById(R.id.dayDatePicker);
//        dayDatePicker.setStartDate(19, 7, 2021);

//        dayDatePicker.setOnDateSelectedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@Nullable Date date) {
//                if (date != null) {
//                    // Handle the selected date
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(date);
//
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH) + 1; // Note: Month starts from 0
//                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                    // Print or log the selected date
//                    String selectedDate = "Selected Date: " + year + "-" + month + "-" + dayOfMonth;
//                    System.out.println(selectedDate);
//
//                    // Now, you can perform any actions or update UI elements based on the selected date
//                }
//            }
//        });


        return rootView;
    }
}
