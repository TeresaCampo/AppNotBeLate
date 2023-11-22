package com.teresa.appnotbelate;

import static com.android.volley.VolleyLog.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements CommunicationActivityFragments {
    //ViewPager2 container;
    FragmentContainerView container;
    FragmentManager fragmentManager;
    TabLayout navigationBar;

    // info NewEventFragment
    Place origin, destination;
    TimeFormatter meetingTime;
    Boolean byFoot;


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
                    fragmentManager.beginTransaction().replace(container.getId(), new NewEventFragment()).commit();
                break;
            case 2:
                Log.println(Log.DEBUG, TAG, "Displaying info car, number view= "+viewNumber);
                navigationBar.selectTab(navigationBar.getTabAt(1));
                fragmentManager.beginTransaction().replace(R.id.container, new InfoCarFragment()).commit();
                break;
            case 3:
                Log.println(Log.DEBUG, TAG, "Displaying get ready, number view= "+viewNumber);
                navigationBar.selectTab(navigationBar.getTabAt(1));
                fragmentManager.beginTransaction().replace(R.id.container, new GetReadyFragment()).commit();
                break;

        }
    }

    @Override
    public void onCommunicateNewEvent(Place origin, Place destination, TimeFormatter meetingTime, Boolean byFoot, TimeFormatter travelTime) {
        this.origin = origin;
        this.destination =destination;
        this.meetingTime = meetingTime;
        this.byFoot = byFoot;
    }
}