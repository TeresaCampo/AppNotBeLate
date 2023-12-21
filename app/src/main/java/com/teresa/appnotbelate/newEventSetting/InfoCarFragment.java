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
import com.teresa.appnotbelate.CommunicationActivityFragments;
import com.teresa.appnotbelate.R;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.Components.TimePickerFragment;

import java.util.Calendar;

public class InfoCarFragment extends Fragment implements TimePickerFragment.DurationChangeListener {
    private CommunicationActivityFragments communicationListener;
    View v;
    TimePickerFragment reachTheCar, parkTheCar;
    // Data passed from MainActivity
    Place origin, destination;
    Boolean isTomorrow;
    TimeFormatter travelTime, meetingTime, leavingTime, newTravelTime;
    TextView tv_travelTime, tv_leavingPoint, tv_meetingPoint, tv_isTomorrow, tv_message;
    Button back, next;

    public InfoCarFragment() {
        // Required empty public constructor
    }

    public void insertData( Place origin, Place destination,TimeFormatter travelTime, TimeFormatter meetingTime, Boolean isTomorrow){
        this.travelTime=travelTime;
        this.meetingTime=meetingTime;
        this.isTomorrow=isTomorrow;
        this.origin=origin;
        this.destination=destination;

        tv_travelTime.setText(travelTime.toString());
        tv_meetingPoint.setText(destination.getName());
        tv_leavingPoint.setText(origin.getName());
        checkTimeDistance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_info_car, container, false);
        //elements I need
        if(parkTheCar==null) {
            parkTheCar = new TimePickerFragment();
            parkTheCar.setDurationChangeListener(this);
        }
        if(reachTheCar==null) {
            reachTheCar = new TimePickerFragment();
            reachTheCar.setDurationChangeListener(this);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.frg_reachTheCar, reachTheCar).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.frg_parkTheCar, parkTheCar).commit();

        tv_leavingPoint=v.findViewById(R.id.tv_leavingPoint);
        tv_meetingPoint=v.findViewById(R.id.tv_meetingPoint);
        tv_travelTime=v.findViewById(R.id.tv_travelTime);
        tv_isTomorrow= v.findViewById(R.id.tv_isTomorrow);
        tv_message=v.findViewById(R.id.tv_message);
        //buttons
        back=v.findViewById(R.id.back);
        next=v.findViewById(R.id.next);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    communicationListener.onChangeFragment(1);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    communicationListener.onCommunicateInfoCar(leavingTime, newTravelTime, reachTheCar.getTime(), parkTheCar.getTime(), isTomorrow);
                    communicationListener.onChangeFragment(3);

                }
            }
        });
        return v;
    }
    Boolean checkIfTomorrow(TimeFormatter timeToCheck){
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
    }
    /**
    * If so, calculates the time distance and return it
     */
    private void checkTimeDistance(){
        TimeFormatter tmpTravelTime= new TimeFormatter(travelTime.getMinutes(), travelTime.getHours(), travelTime.getDays());
        Log.println(DEBUG, TAG, "(1)tmp travel time is "+ tmpTravelTime + " and meeting time is "+meetingTime);
        tmpTravelTime.addTimeFormatter(parkTheCar.getTime());
        tmpTravelTime.addTimeFormatter(reachTheCar.getTime());
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
            isTomorrow=true;
            tv_isTomorrow.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText("Even leaving now you won't make it in time today, but we can plan it for tomorrow");
        }
        else{
            isTomorrow=false;
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
        checkTimeDistance();
    }
}