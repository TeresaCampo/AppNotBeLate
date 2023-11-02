package com.teresa.appnotbelate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimePickerFragment extends Fragment {
    View view;
    android.widget.NumberPicker en_hour, en_minutes;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    public static TimePickerFragment newInstance(String param1, String param2) {
        TimePickerFragment fragment = new TimePickerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_time_picker, container, false);
        en_hour= v.findViewById(R.id.en_hour);
        en_minutes=v.findViewById(R.id.en_minutes);

        // Set up the hour and minutes picker
        en_hour.setMinValue(0);
        en_hour.setMaxValue(23);
        en_hour.setValue(0);

        en_minutes.setMinValue(0);
        en_minutes.setMaxValue(59);
        en_minutes.setValue(15); // Set the default minute to 0
        view=v;
        return v;
    }

}