package com.teresa.appnotbelate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GetReadyFragment extends Fragment implements TimePickerFragment.DurationChangeListener {
    View v;
    TimePickerFragment timePicker1;
    TimePickerFragment timePicker2;
    CommunicationActivityFragments communicationListener;
    Button next,back;
    TextView tv_areYouSure;

    public GetReadyFragment() {
        // Required empty public constructor
    }
    public static GetReadyFragment newInstance(String param1, String param2) {
        GetReadyFragment fragment = new GetReadyFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_get_ready, container, false);
        //set the first timePiker
        timePicker1= new TimePickerFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.firstReadyTime, timePicker1).commit();

        //set the second timePicker blank
        timePicker2= new TimePickerFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.secondReadyTime, timePicker2).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.secondReadyTime, new BlankFragment()).commit();
        //create the buttons
        back=v.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    communicationListener.onChangeFragment(1);
                }
            }
        });
        next=v.findViewById(R.id.next);
        tv_areYouSure=v.findViewById(R.id.tv_areYouSure);
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
                        communicationListener.onChangeFragment(1);
                    }
                }
            }
        });


        return v;
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
        //fai cose
    }
}