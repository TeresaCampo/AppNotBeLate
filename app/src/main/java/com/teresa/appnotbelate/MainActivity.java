package com.teresa.appnotbelate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventByFoot;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventWithCar;
import com.teresa.appnotbelate.newEventSetting.GetReadyFragment;
import com.teresa.appnotbelate.newEventSetting.InfoCarFragment;
import com.teresa.appnotbelate.newEventSetting.NewEventFragment;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CommunicationActivityFragments {
    private static final String TAG = "MyDebug";

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
    //Firebase signin
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    User currentUserInfo;
    UserInfoFragment userInfoFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize what I need
        container = findViewById(R.id.container);
        navigationBar = findViewById(R.id.tabLayout);
        fragmentManager = getSupportFragmentManager();

        mAuth= FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

        //If no one is authenticated, let's authenticate
        if(currentUser==null){
            Log.d(TAG, "No one is authenticated, let's log in");
           Intent loginIntent = new Intent(this, LoginActivity.class );
           startActivity(loginIntent);
        }
        //if a user is already authenticated, load data

        else {
            Log.d(TAG, "Current authenticated user is "+ currentUser.getUid()+" ("+currentUser.getDisplayName()+")");
            //retrieve data about the user
            readCurrentUserInfo();
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
    }

    /**
     * To display a new fragment
     * @param numberOfFolllowingFragment identifier of fragment to be displayed
     */
    @Override
    public void onChangeFragment(int numberOfFolllowingFragment) {
        setCurrentFragmentView(numberOfFolllowingFragment);
    }

    /**
     * To read data about the current user from the database
     */
    void readCurrentUserInfo() {
        db = FirebaseFirestore.getInstance();
        String documentId = mAuth.getCurrentUser().getUid();

        Log.d(TAG, "Reading data about the current user from the database...");
        db.collection("users").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> docData = documentSnapshot.getData();
                        String name = (String) docData.get("name");
                        String email= (String) docData.get("email");
                        currentUserInfo= new User(name, email, documentId);

                        Log.d(TAG, "Data correctly read from the database(name= "+name+", email= "+email+" )");
                    } else {
                        // Document does not exist
                        Log.d(TAG, "Data not read from the database: document "+ documentId+" does not exists");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure
                    Log.e(TAG, "Error getting document", e);
                });
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
                navigationBar.selectTab(navigationBar.getTabAt(0));
                if(userInfoFragment==null){
                    Bundle bundle= new Bundle();
                    bundle.putString("name", currentUserInfo.getName());
                    bundle.putString("email", currentUserInfo.getEmail());

                    userInfoFragment= new UserInfoFragment();
                    userInfoFragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction().replace(R.id.container, userInfoFragment).commit();
                break;
            case 1:
                navigationBar.selectTab(navigationBar.getTabAt(1));
                if(newEventFragment==null){
                    newEventFragment=new NewEventFragment();
                }
                fragmentManager.beginTransaction().replace(container.getId(), newEventFragment).commit();
                break;
            case 2:
                navigationBar.selectTab(navigationBar.getTabAt(1));
                if(infoCarFragment==null){
                    infoCarFragment=new InfoCarFragment();
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
                navigationBar.selectTab(navigationBar.getTabAt(1));
                getReadyFragment= new GetReadyFragment();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(byFoot){
                            getReadyFragment.insertData(origin, destination, travelTimeNewEvent, meetingTime, isTomorrow, true);
                        }
                        else {
                            getReadyFragment.insertData(origin, destination, travelTimeCar, meetingTime, isTomorrow, false);
                        }
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, getReadyFragment).commit();
                break;
            case 4:
                ConfirmAndStartEventWithCar confirmAndStartEventWithCar= new ConfirmAndStartEventWithCar();
                confirmAndStartEventWithCar.insertData(leavingTime, meetingTime, timeToGetReady,timeToParkTheCar,timeToReachTheCar,isTomorrow);
                fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventWithCar).commit();
                break;
            case 5:
                ConfirmAndStartEventByFoot confirmAndStartEventByFoot= new ConfirmAndStartEventByFoot();
                confirmAndStartEventByFoot.insertData(leavingTime, meetingTime, timeToGetReady,isTomorrow);
                fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventByFoot).commit();
                break;
        }
    }

    /**
     * Used by NewEventFragment to communicated data to MainActivity
     */
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
    /**
     * Used by InfoCarFragment to communicated data to MainActivity
     */
    @Override
    public void onCommunicateInfoCar(TimeFormatter leavingTime, TimeFormatter travelTime, TimeFormatter timeToReachTheCar, TimeFormatter timeToPark, Boolean isTomorrow) {
        this.leavingTime=leavingTime;
        this.travelTimeCar=travelTime;
        this.timeToReachTheCar=timeToReachTheCar;
        this.timeToParkTheCar=timeToPark;
        this.isTomorrow=isTomorrow;
    }

    /**
     * Used by GetReadyFragment to communicated data to MainActivity
     */
    @Override
    public void onCommunicateGetReady(TimeFormatter leavingTime, TimeFormatter travelTime, TimeFormatter timeToGetReady, Boolean isTomorrow) {
        this.leavingTime= leavingTime;
        this.travelTimeReady=travelTime;
        this.timeToGetReady=timeToGetReady;
        this.isTomorrow=isTomorrow;
    }
    /**
     * Used by UserInfoFragment to communicated with MainActivity, to log out
     */
    @Override
    public void onLogOut() {
        if(mAuth.getCurrentUser()!=null) {
            Log.d( TAG, "Logging out the user "+ currentUserInfo.getUserId()+" ("+currentUserInfo.getName()+")");
            mAuth.signOut();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        else{
            Log.e( TAG, "Log out even if there's no authenticated user");
        }
    }
}