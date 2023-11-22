package com.teresa.appnotbelate;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.database.Observable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.time.LocalTime;
import java.util.Calendar;

/**
 * TimePicker Fragment is used to select a hours and minutes
 * User can interact with it and change the values
 * It is used both to select a duration both to select time of the day
 *
 */
public class TimePickerFragment extends Fragment {
    TimeFormatter time=new TimeFormatter(15,0,0);
    View v;
    android.widget.NumberPicker en_hour, en_minutes;

    // To do something if the value changes
    DurationChangeListener durationChangeListener;
    // To update the UI if I change the value
    private Handler handler = new Handler();
    private boolean isValueChanged = false;

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_time_picker, container, false);
        en_hour= v.findViewById(R.id.en_hour);
        en_minutes=v.findViewById(R.id.en_minutes);

        // Set up the hour and minutes picker
        en_hour.setMinValue(0);
        en_hour.setMaxValue(23);
        en_hour.setValue(0);
        en_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        // If the value of hours changes, call to function onDurationChangeListener if it is implemented
        en_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Remove any existing callbacks to avoid triggering multiple updates
                handler.removeCallbacksAndMessages(null);

                // Set a flag indicating that the value has changed
                isValueChanged = true;

                // Delay the callback by 300 milliseconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the value has changed again before performing the update
                        if (isValueChanged) {
                            // Perform the update here
                            time.setHours(newVal);
                            if (durationChangeListener != null) {
                                durationChangeListener.onDurationChanged(time);
                                Log.println(Log.DEBUG, TAG, "DEBUG->call durationChangeListener");
                            }

                            // Reset the flag
                            isValueChanged = false;
                        }
                    }
                }, 300);
            }
        });

        en_minutes.setMinValue(0);
        en_minutes.setMaxValue(59);
        en_minutes.setValue(15); // Set the default minute to 0
        en_minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        // If the value of minutes changes, call to function onDurationChangeListener if it is implemented
        en_minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Remove any existing callbacks to avoid triggering multiple updates
                handler.removeCallbacksAndMessages(null);

                // Set a flag indicating that the value has changed
                isValueChanged = true;

                // Delay the callback by 300 milliseconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the value has changed again before performing the update
                        if (isValueChanged) {
                            // Perform the update here
                            time.setMinutes(newVal);
                            if (durationChangeListener != null) {
                                durationChangeListener.onDurationChanged(time);
                                Log.println(Log.DEBUG, TAG, "DEBUG->call durationChangeListener");
                            }

                            // Reset the flag
                            isValueChanged = false;
                        }
                    }
                }, 300);
            }
        });
        return v;
    }

    /**
     * Interface that has to be implemented to do something if the value of minutes or hours changes
     */
    public interface DurationChangeListener {
        void onDurationChanged(TimeFormatter newDuration);
    }

    /**
     * To set the fragment not selectable
     */
    public void setNotSelectable(){
        en_minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        en_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        Log.println(Log.DEBUG, TAG, "WATCH ME not selectable--> "+this.en_hour);

        en_minutes.setEnabled(false);
        en_hour.setEnabled(false);
    }

    /**
     * To set in the UI and in the time attribute the current time
     */
    public void setCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); //
        int minute = calendar.get(Calendar.MINUTE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                time.setHours(hour);
                time.setMinutes(minute);

                setEn_hour(hour);
                setEn_minutes(minute);            }
        }, 100); // 100 milliseconds delay
    }

    /**
     * To set in the UI and in the time attribute the same time of another fragment
     */
    public void setSameTime(TimePickerFragment frag){
        //handler to force the UI to update the NumberPicker graphic content
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                en_hour.setMinValue(frag.getEn_hour());
                en_minutes.setMinValue(frag.getEn_minutes());

                time.setHours(frag.getEn_hour());
                time.setMinutes(frag.getEn_minutes());

                setEn_hour(frag.getEn_hour());
                setEn_minutes(frag.getEn_minutes());
            }
        }, 100); // 100 milliseconds delay
    }

    //getter and setter
    public int getEn_hour() {
        return en_hour.getValue();
    }

    public void setEn_hour(int en_hour) {
        this.en_hour.setValue(en_hour);
    }

    public int getEn_minutes() {
        return en_minutes.getValue();
    }

    public void setEn_minutes(int en_minutes) {
        this.en_minutes.setValue(en_minutes);
    }

    public TimeFormatter getTime() {
        return time;
    }
}