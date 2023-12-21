package com.teresa.appnotbelate.newEventSetting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.R;


public class ConfirmAndStartEventWithCar extends Fragment {
    View v;
    TextView tv_timeToGetReady, tv_timeLeave, tv_timeStartCar, tv_timePark, tv_timeArrived, tv_isTomorrow;

    public ConfirmAndStartEventWithCar() {
        // Required empty public constructor
    }
    public void insertData( TimeFormatter leavingTime,TimeFormatter meetingTime,TimeFormatter gettingReadyTime,  TimeFormatter parkTime, TimeFormatter reachCarTime, Boolean isTomorrow){
        if(isTomorrow){
            tv_isTomorrow.setVisibility(View.VISIBLE);
        }

        tv_timeToGetReady.setText(leavingTime.toStringAsTime());
        leavingTime.addTimeFormatter(gettingReadyTime);
        tv_timeLeave.setText(leavingTime.toStringAsTime());
        leavingTime.addTimeFormatter(reachCarTime);
        tv_timeStartCar.setText(leavingTime.toStringAsTime());
        meetingTime.subtractTimeFormatter(parkTime);
        tv_timePark.setText(meetingTime.toStringAsTime());
        meetingTime.addTimeFormatter(parkTime);
        tv_timeArrived.setText(meetingTime.toStringAsTime());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_confirm_and_start_event_with_car, container, false);
        //elements I need
        tv_isTomorrow=v.findViewById(R.id.tv_tomorrow);
        tv_timeToGetReady=v.findViewById(R.id.tv_timeGetReady);
        tv_timeLeave=v.findViewById(R.id.tv_timeLeave);
        tv_timeStartCar=v.findViewById(R.id.tv_timeStartCar);
        tv_timePark=v.findViewById(R.id.tv_timePark);
        tv_timeArrived=v.findViewById(R.id.tv_timeArrived);

        return v;
    }
}