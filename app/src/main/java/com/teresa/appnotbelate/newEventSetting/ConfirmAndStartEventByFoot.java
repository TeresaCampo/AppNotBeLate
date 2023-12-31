package com.teresa.appnotbelate.newEventSetting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.teresa.appnotbelate.AlarmReceiver;
import com.teresa.appnotbelate.CommunicationActivityFragments;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.ExistingEvent;
import com.teresa.appnotbelate.R;

import java.util.Calendar;

public class ConfirmAndStartEventByFoot extends Fragment {
    private static final String TAG = "MyDebug";
    final int BEFORE_GET_READY= 0;
    final int GET_READY= 1;
    final int LEAVE=2;
    final int BEFORE_LEAVE=3;
    CommunicationActivityFragments communicationListener;

    View v;
    TextView tv_timeToGetReady, tv_timeLeave, tv_timeArrived, tv_isTomorrow;
    Button bn_back, bn_setAlarm, bn_deleteEvent;
    String timeToGetReady, leavingTime, meetingTime;
    Boolean isTomorrow=false, existingEvent=false;
    String originName, destinationName;
    TimeFormatter tf_timeToGetReady, tf_timeToLeave;



    public ConfirmAndStartEventByFoot() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_confirm_and_start_event_by_foot, container, false);
        //elements I need
        tv_isTomorrow=v.findViewById(R.id.tv_tomorrow);
        tv_timeToGetReady=v.findViewById(R.id.tv_timeGetReady);
        tv_timeLeave=v.findViewById(R.id.tv_timeLeave);
        tv_timeArrived=v.findViewById(R.id.tv_arrived2);
        bn_back=v.findViewById(R.id.bn_back);
        bn_setAlarm=v.findViewById(R.id.setAlarm);
        bn_deleteEvent=v.findViewById(R.id.bn_deleteEvent);

        bn_back.setOnClickListener(v -> {
            if (communicationListener != null) {
                    communicationListener.onChangeFragment(3);
            }
        });
        bn_setAlarm.setOnClickListener(v -> {
            if (communicationListener != null) {
                setAlarms();
                communicationListener.onStoreEventByFoot();
            }
        });
        bn_deleteEvent.setOnClickListener(v -> {
            if (communicationListener != null) {
                communicationListener.onDeleteByFootEvent();
            }
        });

        return v;
    }

    /**
     * Insert data from new event that is being created
     */
    public void insertData( String originName, String destinationName, TimeFormatter leavingTime, TimeFormatter meetingTime, TimeFormatter gettingReadyTime, Boolean isTomorrow) {
        this.isTomorrow = isTomorrow;
        TimeFormatter tmpLeavingTime =leavingTime.clone();
        //time to start to get ready
        timeToGetReady = tmpLeavingTime.toStringAsTime();
        tf_timeToGetReady=tmpLeavingTime.clone();
        //leaving time
        tmpLeavingTime.addTimeFormatter(gettingReadyTime);
        tf_timeToLeave= tmpLeavingTime.clone();
        this.leavingTime = tmpLeavingTime.toStringAsTime();
        //meeting time
        this.meetingTime = meetingTime.toStringAsTime();
        this.originName=originName;
        this.destinationName=destinationName;
    }
    /**
     * Insert data from existing event
     */
    public  void insertDataExistingEvent(ExistingEvent existingEvent){
        this.timeToGetReady= new TimeFormatter(existingEvent.getGettingReadyTime()).toStringAsTime();
        this.leavingTime= new TimeFormatter(existingEvent.getLeavingTime()).toStringAsTime();
        this.meetingTime= new TimeFormatter(existingEvent.getMeetingTime()).toStringAsTime();
        this.originName=existingEvent.getOriginName();
        this.destinationName= existingEvent.getDestinationName();
        this.existingEvent=true;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(isTomorrow){
            tv_isTomorrow.setVisibility(View.VISIBLE);
        }
        tv_timeToGetReady.setText(timeToGetReady);
        tv_timeLeave.setText(leavingTime);
        tv_timeArrived.setText(meetingTime);

        if(existingEvent){
            bn_deleteEvent.setVisibility(View.VISIBLE);
            bn_setAlarm.setVisibility(View.INVISIBLE);
            bn_back.setVisibility(View.INVISIBLE);
        }
    }
    void setAlarms() {
        //set the alarm when we need to get ready
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        //ALARM WHEN YOU NEED TO GET READY
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("title", "Event: from "+originName+" to "+destinationName);
        intent.putExtra("message", "Start to get ready now!");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), GET_READY, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        secureSetAlarm(alarmManager,pendingIntent, tf_timeToGetReady);

        //ALARM 5 MINUTES BEFORE GET READY
        tf_timeToGetReady.getTimeAndDay().add(Calendar.MINUTE, -5);
        if (tf_timeToGetReady.getTimeAndDay().after(Calendar.getInstance())) {
            intent.putExtra("message", "In 5 minutes you need to start to get ready!");
            pendingIntent = PendingIntent.getBroadcast(requireContext(), BEFORE_GET_READY, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            secureSetAlarm(alarmManager, pendingIntent, tf_timeToGetReady);
        }
        //ALARM LEAVE THE PLACE
        intent.putExtra("message", "Leave the place now!");
        pendingIntent = PendingIntent.getBroadcast(requireContext(), LEAVE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        secureSetAlarm(alarmManager, pendingIntent, tf_timeToLeave);

        //ALARM 5 MINUTES BEFORE LEAVE THE PLACE
        tf_timeToGetReady.getTimeAndDay().add(Calendar.MINUTE, 5);
        tf_timeToLeave.getTimeAndDay().add(Calendar.MINUTE, -5);
        if(tf_timeToLeave.getTimeAndDay().after(tf_timeToGetReady.getTimeAndDay())){
            intent.putExtra("message", "In 5 minutes you need to leave the place!");
            pendingIntent = PendingIntent.getBroadcast(requireContext(), BEFORE_LEAVE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            secureSetAlarm(alarmManager, pendingIntent, tf_timeToLeave);
        }
    }
    void secureSetAlarm(AlarmManager alarmManager,PendingIntent pendingIntent, TimeFormatter time){
        // For API 19 and above, setExact is recommended
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeAndDay().getTimeInMillis(), pendingIntent);
            } catch (SecurityException e) {
                // Handle the exception, possibly by directing the user to the settings
                Intent intentSettings = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intentSettings);
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeAndDay().getTimeInMillis(), pendingIntent);
        }
        Log.d(TAG, "Alarm set at " + time.toStringAsTime());
    }
    /**
     * To set the communicationListener to communicate with MainActivity
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
