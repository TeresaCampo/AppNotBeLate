package com.teresa.appnotbelate.newEventSetting;

import static android.util.Log.DEBUG;
import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;
import com.teresa.appnotbelate.Components.BlankFragment;
import com.teresa.appnotbelate.CommunicationActivityFragments;
import com.teresa.appnotbelate.R;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.Components.TimePickerFragment;

import java.util.Calendar;

public class GetReadyFragment extends Fragment implements TimePickerFragment.DurationChangeListener {
    View v;
    TimePickerFragment timePicker1;
    TimePickerFragment timePicker2;
    CommunicationActivityFragments communicationListener;
    Button next,back;
    TextView tv_areYouSure, tv_travelTime, tv_meetingPoint, tv_leavingPoint, tv_isTomorrow, tv_message;
    Place origin, destination;
    TimeFormatter travelTime, meetingTime, newTravelTime, leavingTime;
    Boolean isTomorrow, byFoot;

    public GetReadyFragment() {
        // Required empty public constructor
    }
    public void insertData(Place origin, Place destination, TimeFormatter travelTime, TimeFormatter meetingTime, Boolean isTomorrow, Boolean byFoot){
        this.travelTime=travelTime;
        this.meetingTime=meetingTime;
        this.isTomorrow=isTomorrow;
        this.origin=origin;
        this.destination=destination;
        this.byFoot=byFoot;

        tv_travelTime.setText(travelTime.toString());
        tv_meetingPoint.setText(destination.getName());
        tv_leavingPoint.setText(origin.getName());
        checkTimeDistance(timePicker1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_get_ready, container, false);
        //elements I need
        tv_travelTime=v.findViewById(R.id.tv_travelTime);
        tv_areYouSure=v.findViewById(R.id.tv_areYouSure);
        tv_isTomorrow=v.findViewById(R.id.tv_isTomorrow);
        tv_message= v.findViewById(R.id.tv_message);
        tv_leavingPoint=v.findViewById(R.id.tv_leavingPoint);
        tv_meetingPoint=v.findViewById(R.id.tv_meetingPoint);


        //set the first timePiker
        timePicker1= new TimePickerFragment();
        timePicker1.setDurationChangeListener(this);
        getChildFragmentManager().beginTransaction().replace(R.id.firstReadyTime, timePicker1).commit();

        //set the second timePicker blank
        timePicker2= new TimePickerFragment();
        timePicker2.setDurationChangeListener(this);
        getChildFragmentManager().beginTransaction().replace(R.id.secondReadyTime, timePicker2).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.secondReadyTime, new BlankFragment()).commit();
        //create the buttons
        back=v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    if(byFoot==true){
                        communicationListener.onChangeFragment(1);
                    }
                    else{
                        communicationListener.onChangeFragment(2);
                    }
                }
            }
        });
        next=v.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_areYouSure.getVisibility()==View.INVISIBLE){
                    timePicker1.setNotSelectable();


                    tv_areYouSure.setVisibility(View.VISIBLE);
                    timePicker2.setSameTime(timePicker1);
                    getChildFragmentManager().beginTransaction().replace(R.id.secondReadyTime, timePicker2).commit();

                }
                else {
                    if (communicationListener != null) {
                        //Choose what to display next
                        TimeFormatter updatedMeetingTime= new TimeFormatter(meetingTime.getMinutes(),meetingTime.getHours(), 0);
                        TimeFormatter updatedLeavingTime= new TimeFormatter(meetingTime.getMinutes(),meetingTime.getHours(), 0);
                        if( tv_isTomorrow.getVisibility()==View.VISIBLE){
                            updatedMeetingTime.setDayTomorrow();
                            updatedLeavingTime.setDayTomorrow();
                            isTomorrow=true;
                        }

                        updatedLeavingTime.subtractTimeFormatter(newTravelTime);
                        Log.d(TAG, "leavingTime is (calendar):"+updatedLeavingTime.getTimeAndDay().getTime()+", (timeFormatter):"+updatedLeavingTime.toString());
                        Log.d(TAG, "meetingTime is (calendar):"+updatedMeetingTime.getTimeAndDay().getTime()+", (timeFormatter):"+updatedMeetingTime.toString());

                        communicationListener.onCommunicateGetReady(updatedLeavingTime, updatedMeetingTime, newTravelTime,timePicker2.getTime(),isTomorrow);
                        if(byFoot==true){
                            communicationListener.onChangeFragment(5);
                        }
                        else{
                            communicationListener.onChangeFragment(4);
                        }
                    }
                }
            }
        });


        return v;
    }
    Boolean checkIfTomorrow(TimeFormatter timeToCheck){
        Calendar calendar = Calendar.getInstance();
        return timeToCheck.getTimeAndDay().before(calendar);
        /*
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(timeToCheck.getHours()<hour){
            return true;
        }
        else if(timeToCheck.getHours()==hour && timeToCheck.getMinutes()<=minute){
            return true;
        }
        else{
            return false;
        }

         */
    }
    /**
     * If so, calculates the time distance and return it
     */
    private void checkTimeDistance(TimePickerFragment timeToGetReady){
        TimeFormatter tmpTravelTime= new TimeFormatter(travelTime.getMinutes(), travelTime.getHours(), travelTime.getDays());
        Log.println(DEBUG, TAG, "(1)tmp travel time is "+ tmpTravelTime + " and meeting time is "+meetingTime);
        tmpTravelTime.addTimeFormatter(timeToGetReady.getTime());
        Log.println(DEBUG, TAG, "(1)tmp travel time is "+ tmpTravelTime + " and meeting time is "+meetingTime);

        //the travel should last less than one day
        if(tmpTravelTime.getDays()!=0){
            Toast.makeText(getContext(), "More than one day to reach the meeting point...choose another leaving point", Toast.LENGTH_LONG).show();
            if (communicationListener != null) {
                communicationListener.onChangeFragment(1);
            }
        }
        newTravelTime= tmpTravelTime;
        //check if tomorrow
        leavingTime= new TimeFormatter(meetingTime.getMinutes(), meetingTime.getHours(), meetingTime.getDays());
        leavingTime.subtractTimeFormatter(newTravelTime);
        Log.println(DEBUG, TAG, "leavingTime is "+ leavingTime + " and meeting time is "+meetingTime);
        if(checkIfTomorrow(leavingTime)) {
            tv_isTomorrow.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText("Even leaving now you won't make it in time today, but we can plan it for tomorrow");
        }
        else{
            tv_isTomorrow.setVisibility(View.INVISIBLE);
            tv_message.setVisibility(View.INVISIBLE);
        }

        tv_travelTime.setText(newTravelTime.toString());
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

    @Override
    public void onDurationChanged(TimeFormatter newDuration) {
        Log.println(DEBUG, TAG, "check time distance");
        if(tv_areYouSure.getVisibility()==View.INVISIBLE) {
            checkTimeDistance(timePicker1);
        }
        else{
            checkTimeDistance(timePicker2);
        }
    }
}