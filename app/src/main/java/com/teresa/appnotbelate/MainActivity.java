package com.teresa.appnotbelate;

import static com.android.volley.VolleyLog.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventByFoot;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventWithCar;
import com.teresa.appnotbelate.newEventSetting.GetReadyFragment;
import com.teresa.appnotbelate.newEventSetting.InfoCarFragment;
import com.teresa.appnotbelate.newEventSetting.NewEventFragment;

public class MainActivity extends AppCompatActivity implements CommunicationActivityFragments {
    //ViewPager2 container;
    FragmentContainerView container;
    FragmentManager fragmentManager;
    NewEventFragment newEventFragment;
    InfoCarFragment infoCarFragment;
    GetReadyFragment getReadyFragment;
    TabLayout navigationBar;

    // info NewEventFragment
    Place origin, destination;
    TimeFormatter meetingTime, travelTimeNewEvent, travelTimeCar, travelTimeReady, leavingTime, timeToGetReady;
    Boolean byFoot=true, isTomorrow;
    //info infoCar
    TimeFormatter timeToReachTheCar, timeToParkTheCar;



    @Override
    public void onChangeFragment(int numberOfFolllowingFragment) {
        setCurrentFragmentView(numberOfFolllowingFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize what I need
        container = findViewById(R.id.container);
        navigationBar = findViewById(R.id.tabLayout);
        fragmentManager = getSupportFragmentManager();
        //display newEvent view as first view
        setCurrentFragmentView(1);

        navigationBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               setCurrentFragmentView(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //initialize Places
        Places.initialize(getApplicationContext(), getResources().getString(R.string.apiKey));


    }
    /**
     * Set fragment according to the number associated to the layout and links the selected tab to the selected fragment
     * @param viewNumber of the fragment to be displayed
     * UserInfoFragment--> layout 0
     * NewEventFragment--> layout 1
     * InfoCarFragment--> layout 2
     * LastDetailsFragment--> layout 3
     */
    private void setCurrentFragmentView(int viewNumber) {
        switch (viewNumber) {
            case 0:
                Log.println(Log.DEBUG, TAG, "Displaying info, number view= "+viewNumber);
                navigationBar.selectTab(navigationBar.getTabAt(0));
                fragmentManager.beginTransaction().replace(R.id.container, new UserInfoFragment()).commit();
                break;
            case 1:
                    Log.println(Log.DEBUG, TAG, "Displaying new event, number view= " + viewNumber);
                    navigationBar.selectTab(navigationBar.getTabAt(1));
                    if(newEventFragment==null){
                        newEventFragment=new NewEventFragment();
                        Log.println(Log.DEBUG,TAG,"Fragment newEventFragment is "+newEventFragment);
                    }

                fragmentManager.beginTransaction().replace(container.getId(), newEventFragment).commit();
                break;
            case 2:
                Log.println(Log.DEBUG, TAG, "Displaying info car, number view= "+viewNumber);
                navigationBar.selectTab(navigationBar.getTabAt(1));
                if(infoCarFragment==null){
                    infoCarFragment=new InfoCarFragment();
                    Log.println(Log.DEBUG,TAG,"Fragment infoCarFragment is "+infoCarFragment);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        infoCarFragment.insertData(origin, destination, travelTimeNewEvent, meetingTime, isTomorrow);
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, infoCarFragment).commit();
                break;
            case 3:
                Log.println(Log.DEBUG, TAG, "Displaying get ready, number view= "+viewNumber);
                navigationBar.selectTab(navigationBar.getTabAt(1));
                getReadyFragment= new GetReadyFragment();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(byFoot==true){
                            getReadyFragment.insertData(origin, destination, travelTimeNewEvent, meetingTime, isTomorrow, byFoot);
                        }
                        else {
                            getReadyFragment.insertData(origin, destination, travelTimeCar, meetingTime, isTomorrow, byFoot);
                        }
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, getReadyFragment).commit();
                break;
            case 4:
                Log.println(Log.DEBUG, TAG, "Displaying recap with car, number view= "+viewNumber);
                ConfirmAndStartEventWithCar confirmAndStartEventWithCar= new ConfirmAndStartEventWithCar();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        confirmAndStartEventWithCar.insertData(leavingTime, meetingTime, timeToGetReady,timeToParkTheCar,timeToReachTheCar,isTomorrow);
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventWithCar).commit();
                break;
            case 5:
                Log.println(Log.DEBUG, TAG, "Displaying recap by foot, number view= "+viewNumber);
                ConfirmAndStartEventByFoot confirmAndStartEventByFoot= new ConfirmAndStartEventByFoot();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        confirmAndStartEventByFoot.insertData(leavingTime, meetingTime, timeToGetReady,isTomorrow);
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventByFoot).commit();
                break;

        }
    }

    @Override
    public void onCommunicateNewEvent(Place origin, Place destination, TimeFormatter meetingTime, Boolean byFoot, TimeFormatter travelTime, TimeFormatter leavingTime, Boolean isTomorrow) {
        this.origin = origin;
        this.destination =destination;
        this.meetingTime = meetingTime;
        this.travelTimeNewEvent=travelTime;
        this.byFoot = byFoot;
        this.isTomorrow=isTomorrow;
        this.leavingTime= leavingTime;
    }

    @Override
    public void onCommunicateInfoCar(TimeFormatter leavingTime, TimeFormatter travelTime, TimeFormatter timeToReachTheCar, TimeFormatter timeToPark, Boolean isTomorrow) {
        this.leavingTime=leavingTime;
        this.travelTimeCar=travelTime;
        this.timeToReachTheCar=timeToReachTheCar;
        this.timeToParkTheCar=timeToPark;
        this.isTomorrow=isTomorrow;
    }

    @Override
    public void onCommunicateGetReady(TimeFormatter leavingTime, TimeFormatter travelTime, TimeFormatter timeToGetReady, Boolean isTomorrow) {
        this.leavingTime= leavingTime;
        this.travelTimeReady=travelTime;
        this.timeToGetReady=timeToGetReady;
        this.isTomorrow=isTomorrow;
    }
}