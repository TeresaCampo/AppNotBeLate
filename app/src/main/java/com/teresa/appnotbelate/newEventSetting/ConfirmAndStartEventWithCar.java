package com.teresa.appnotbelate.newEventSetting;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;
import com.teresa.appnotbelate.CommunicationActivityFragments;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.R;


public class ConfirmAndStartEventWithCar extends Fragment {
    View v;
    CommunicationActivityFragments communicationListener;
    Button bn_back, bn_setAlarm;
    TextView tv_timeToGetReady, tv_timeLeave, tv_timeStartCar, tv_timePark, tv_timeArrived, tv_isTomorrow;
    String timeToGetReady, leavingTime, startTheCarTime, findTheParkTime, meetingTime;
    Boolean isTomorrow;
    public ConfirmAndStartEventWithCar() {
        // Required empty public constructor
    }
    public void insertData( TimeFormatter leavingTime,TimeFormatter meetingTime,TimeFormatter gettingReadyTime,  TimeFormatter parkTime, TimeFormatter reachCarTime, Boolean isTomorrow) {
        this.isTomorrow = isTomorrow;
        //time to start to get ready
        timeToGetReady = leavingTime.toStringAsTime();
        //leaving time
        leavingTime.addTimeFormatter(gettingReadyTime);
        this.leavingTime = leavingTime.toStringAsTime();
        //time to start the car
        leavingTime.addTimeFormatter(reachCarTime);
        startTheCarTime = leavingTime.toStringAsTime();
        //find the park time
        meetingTime.subtractTimeFormatter(parkTime);
        findTheParkTime = meetingTime.toStringAsTime();
        //meeting time
        meetingTime.addTimeFormatter(parkTime);
        this.meetingTime = meetingTime.toStringAsTime();
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
        bn_back=v.findViewById(R.id.bn_back);
        bn_setAlarm=v.findViewById(R.id.setAlarm);

        bn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    communicationListener.onChangeFragment(3);
                }

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isTomorrow){
            tv_isTomorrow.setVisibility(View.VISIBLE);
        }
        tv_timeToGetReady.setText(timeToGetReady);
        tv_timeLeave.setText(leavingTime);
        tv_timeStartCar.setText(startTheCarTime);
        tv_timePark.setText(findTheParkTime);
        tv_timeArrived.setText(meetingTime);
    }

    /**
     * To set the communicationListener to communicate with MainActivity
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CommunicationActivityFragments) {
            communicationListener = (CommunicationActivityFragments) context;
        } else {
            throw new ClassCastException(context + " must implement OnFragmentInteractionListener");
        }
    }
}