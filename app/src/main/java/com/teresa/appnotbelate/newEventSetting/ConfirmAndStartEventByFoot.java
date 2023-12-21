package com.teresa.appnotbelate.newEventSetting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.R;

public class ConfirmAndStartEventByFoot extends Fragment {
    View v;
    TextView tv_timeToGetReady, tv_timeLeave, tv_timeArrived, tv_isTomorrow;
    Button bn_back, bn_setAlarm;

    public ConfirmAndStartEventByFoot() {
        // Required empty public constructor
    }
    public void insertData(TimeFormatter leavingTime, TimeFormatter meetingTime, TimeFormatter gettingReadyTime, Boolean isTomorrow){
        if(isTomorrow){
            tv_isTomorrow.setVisibility(View.VISIBLE);
        }

        tv_timeToGetReady.setText(leavingTime.toStringAsTime());
        leavingTime.addTimeFormatter(gettingReadyTime);
        tv_timeLeave.setText(leavingTime.toStringAsTime());
        tv_timeArrived.setText(meetingTime.toStringAsTime());
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

        bn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }
}
