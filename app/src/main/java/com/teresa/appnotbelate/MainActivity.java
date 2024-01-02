package com.teresa.appnotbelate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventByFoot;
import com.teresa.appnotbelate.newEventSetting.ConfirmAndStartEventWithCar;
import com.teresa.appnotbelate.newEventSetting.GetReadyFragment;
import com.teresa.appnotbelate.newEventSetting.InfoCarFragment;
import com.teresa.appnotbelate.newEventSetting.NewEventFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CommunicationActivityFragments {
    private static final String TAG = "MyDebug";
    //fragments
    FragmentContainerView container;
    FragmentManager fragmentManager;
    NewEventFragment newEventFragment;
    InfoCarFragment infoCarFragment;
    GetReadyFragment getReadyFragment;
    TabLayout navigationBar;

    // info NewEventFragment
    Place origin, destination;
    TimeFormatter meetingTime, travelTimeNewEvent, travelTimeCar, travelTimeReady, leavingTime;
    TimeFormatter timeToReachTheCar, timeToParkTheCar, timeToGetReady;
    Boolean byFoot=true, isTomorrow;

    //Firebase
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    User currentUserInfo;
    UserInfoFragment userInfoFragment;
    //general
    Context context;
    //alarms
    final int BEFORE_GET_READY= 0;
    final int GET_READY= 1;
    final int LEAVE=2;
    final int BEFORE_LEAVE=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        //first time in which I lunch the application
        if (isFirstTime()) {
            // Show the notification settings prompt and create the notification channel
            setUpTheApplication();
            // Set the 'firstTime' flag to false
            setFirstTimeFlag(false);
        }

        //initialize what I need
        container = findViewById(R.id.container);
        navigationBar = findViewById(R.id.tabLayout);
        fragmentManager = getSupportFragmentManager();

        mAuth= FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

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
            //check if there's an event already planned
            checkExistingEvent();
            //display newEvent view as first view
            setCurrentFragmentView(1);


            //initialize Places
            Places.initialize(getApplicationContext(), getResources().getString(R.string.apiKey));
        }
    }


    /**
     * To check if it is the first time the application is opened
     */
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getBoolean("firstTime", true);
    }

    /**
     * To update the flag that indicates if it is the first time the application is opened
     */
    private void setFirstTimeFlag(boolean isFirstTime) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstTime", isFirstTime);
        editor.apply();
    }
    public void setAlarmsFlag(boolean value) {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("alarmsFlag", value);
        editor.apply();
    }

    private boolean isAlarmsFlagTrue() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return preferences.getBoolean("alarmsFlag", false);
    }

    /**
     * To create the communication channel, to show notification settings
     */
    private void setUpTheApplication() {
        // For Android Oreo and above, notification channels are required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel("alarm_channel", "Alarm Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // Show the notification permission settings
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
        context.startActivity(intent);
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
        if(currentUserInfo!=null) return;
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
                checkExistingEvent();
                break;
            case 2:
                navigationBar.selectTab(navigationBar.getTabAt(1));
                if(infoCarFragment==null){
                    infoCarFragment=new InfoCarFragment();
                }
                new Handler().postDelayed(() -> infoCarFragment.insertData(origin, destination, travelTimeNewEvent, meetingTime, isTomorrow), 200);
                fragmentManager.beginTransaction().replace(R.id.container, infoCarFragment).commit();
                break;
            case 3:
                navigationBar.selectTab(navigationBar.getTabAt(1));
                getReadyFragment= new GetReadyFragment();
                new Handler().postDelayed(() -> {
                    if(byFoot){
                        getReadyFragment.insertData(origin, destination, travelTimeNewEvent, meetingTime, isTomorrow, true);
                    }
                    else {
                        getReadyFragment.insertData(origin, destination, travelTimeCar, meetingTime, isTomorrow, false);
                    }
                }, 200);
                fragmentManager.beginTransaction().replace(R.id.container, getReadyFragment).commit();
                break;
            case 4:
                Log.d(TAG, "MAIN DEBUG-> meetingTime is (calendar):"+meetingTime.getTimeAndDay().getTime());
                Log.d(TAG, "MAIN DEBUG-> leavingTime is (calendar):"+leavingTime.getTimeAndDay().getTime());
                ConfirmAndStartEventWithCar confirmAndStartEventWithCar= new ConfirmAndStartEventWithCar();
                confirmAndStartEventWithCar.insertData(origin.getName(), destination.getName(),leavingTime, meetingTime, timeToGetReady,timeToParkTheCar,timeToReachTheCar,isTomorrow);
                fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventWithCar).commit();
                break;
            case 5:
                ConfirmAndStartEventByFoot confirmAndStartEventByFoot= new ConfirmAndStartEventByFoot();
                confirmAndStartEventByFoot.insertData(origin.getName(), destination.getName(),leavingTime, meetingTime, timeToGetReady,isTomorrow);
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

        Log.d(TAG, "MAIN DEBUG-> meetingTime is (calendar):"+meetingTime.getTimeAndDay().getTime());
        Log.d(TAG, "MAIN DEBUG-> leavingTime is (calendar):"+leavingTime.getTimeAndDay().getTime());
    }
    /**
     * Used by InfoCarFragment to communicated data to MainActivity
     */
    @Override
    public void onCommunicateInfoCar(TimeFormatter leavingTime,TimeFormatter meetingTime, TimeFormatter travelTime, TimeFormatter timeToReachTheCar, TimeFormatter timeToPark, Boolean isTomorrow) {
        this.leavingTime=leavingTime;
        this.meetingTime=meetingTime;
        this.travelTimeCar=travelTime;
        this.timeToReachTheCar=timeToReachTheCar;
        this.timeToParkTheCar=timeToPark;
        this.isTomorrow=isTomorrow;

        Log.d(TAG, "MAIN DEBUG-> meetingTime is (calendar):"+meetingTime.getTimeAndDay().getTime());
        Log.d(TAG, "MAIN DEBUG-> leavingTime is (calendar):"+leavingTime.getTimeAndDay().getTime());
    }

    /**
     * Used by GetReadyFragment to communicated data to MainActivity
     */
    @Override
    public void onCommunicateGetReady(TimeFormatter leavingTime,TimeFormatter meetingTime, TimeFormatter travelTime, TimeFormatter timeToGetReady, Boolean isTomorrow) {
        this.meetingTime=meetingTime;
        this.leavingTime= leavingTime;
        this.travelTimeReady=travelTime;
        this.timeToGetReady=timeToGetReady;
        this.isTomorrow=isTomorrow;

        Log.d(TAG, "MAIN DEBUG-> meetingTime is (calendar):"+meetingTime.getTimeAndDay().getTime());
        Log.d(TAG, "MAIN DEBUG-> leavingTime is (calendar):"+leavingTime.getTimeAndDay().getTime());
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

    /**
     * To check if an existing event exists and eventually load the data
     */
    void checkExistingEvent() {
        //if the existing event has been already loaded
        /*if(currentUserInfo!=null) {
            ExistingEvent existingEvent= currentUserInfo.getExistingEvent();
            if (existingEvent != null) {
                Log.d(TAG, "Existing event is "+existingEvent);
                if (existingEvent.getByCar() != null && existingEvent.getByCar()) {
                    ConfirmAndStartEventWithCar confirmAndStartEventWithCar = new ConfirmAndStartEventWithCar();
                    confirmAndStartEventWithCar.insertDataExistingEvent(existingEvent);
                    fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventWithCar).commit();
                } else {
                    ConfirmAndStartEventByFoot confirmAndStartEventByFoot= new ConfirmAndStartEventByFoot();
                    confirmAndStartEventByFoot.insertDataExistingEvent(existingEvent);
                    fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventByFoot).commit();
                }
                return;
            }
        }

         */

        //if there's no existing event loaded, look for it in the database
        db.collection("users").document(mAuth.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Directly extract all data from the document
                        String originName = documentSnapshot.getString("originName");
                        String originAddress = documentSnapshot.getString("originAddress");
                        String destinationName = documentSnapshot.getString("destinationName");
                        String destinationAddress = documentSnapshot.getString("destinationAddress");
                        Date gettingReadyTime = documentSnapshot.getDate("gettingReadyTime");
                        Date leavingTime = documentSnapshot.getDate("leavingTime");
                        Date startTheCarTime = documentSnapshot.getDate("startTheCarTime");
                        Date parkTheCarTime = documentSnapshot.getDate("parkTheCarTime");
                        Date meetingTime = documentSnapshot.getDate("meetingTime");
                        Boolean byCar = documentSnapshot.getBoolean("byCar");

                        ExistingEvent existingEvent= new ExistingEvent(originName, originAddress, destinationName, destinationAddress, gettingReadyTime, leavingTime, meetingTime, byCar);
                        existingEvent.setCarDetails(startTheCarTime, parkTheCarTime);
                        currentUserInfo.setExistingEvent(existingEvent);


                        // Check if 'byCar' is true and then act accordingly
                        if (byCar != null && byCar) {
                            if(existingEvent.getMeetingTime().before(Calendar.getInstance().getTime())){
                                onDeleteCarEvent();
                            }
                            ConfirmAndStartEventWithCar confirmAndStartEventWithCar= new ConfirmAndStartEventWithCar();
                            confirmAndStartEventWithCar.insertDataExistingEvent(existingEvent);
                            if(isAlarmsFlagTrue()){
                                confirmAndStartEventWithCar.setAlarms();
                                setAlarmsFlag(false);
                            }
                            fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventWithCar).commit();
                        }
                        if(byCar!=null && !byCar){
                            if(existingEvent.getMeetingTime().before(Calendar.getInstance().getTime())){
                                onDeleteByFootEvent();
                            }
                            ConfirmAndStartEventByFoot confirmAndStartEventByFoot= new ConfirmAndStartEventByFoot();
                            confirmAndStartEventByFoot.insertDataExistingEvent(existingEvent);
                            if(isAlarmsFlagTrue()){
                                confirmAndStartEventByFoot.setAlarms();
                                setAlarmsFlag(false);
                            }
                            fragmentManager.beginTransaction().replace(R.id.container, confirmAndStartEventByFoot).commit();
                        }

                    } else {
                        Log.d(TAG, "No  existing event, let's schedule a new one");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    @Override
    public void onDeleteCarEvent() {
        // delete the alarms
        deleteAlarms();

        Map<String, Object> updates = new HashMap<>();
        updates.put("originName", FieldValue.delete());
        updates.put("originAddress", FieldValue.delete());
        updates.put("destinationName", FieldValue.delete());
        updates.put("destinationAddress", FieldValue.delete());
        updates.put("gettingReadyTime", FieldValue.delete());
        updates.put("leavingTime", FieldValue.delete());
        updates.put("startTheCarTime", FieldValue.delete());
        updates.put("parkTheCarTime", FieldValue.delete());
        updates.put("meetingTime", FieldValue.delete());
        updates.put("byCar", FieldValue.delete());

        db.collection("users").document(currentUserInfo.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    currentUserInfo.setExistingEvent(null);
                    Log.d(TAG, "Event successfully deleted from the database");
                })
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
        //get ready to schedule a new event
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDeleteByFootEvent() {
        // delete the alarms
        deleteAlarms();

        Map<String, Object> updates = new HashMap<>();
        updates.put("originName", FieldValue.delete());
        updates.put("originAddress", FieldValue.delete());
        updates.put("destinationName", FieldValue.delete());
        updates.put("destinationAddress", FieldValue.delete());
        updates.put("gettingReadyTime", FieldValue.delete());
        updates.put("leavingTime", FieldValue.delete());
        updates.put("meetingTime", FieldValue.delete());
        updates.put("byCar", FieldValue.delete());

        db.collection("users").document(currentUserInfo.getUserId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    currentUserInfo.setExistingEvent(null);
                    Log.d(TAG, "Event successfully deleted from the database");
                })
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
        //get ready to schedule a new event
        startActivity(new Intent(this, MainActivity.class));
    }
    void deleteAlarms(){
        // delete the alarms
        //GET READY
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,GET_READY, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        //BEFORE GET READY
        pendingIntent = PendingIntent.getBroadcast(this,BEFORE_GET_READY, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        //LEAVE
        pendingIntent = PendingIntent.getBroadcast(this,LEAVE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        //BEFORE LEAVE
        pendingIntent = PendingIntent.getBroadcast(this,BEFORE_LEAVE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * To store information about the event (by car)
     */
    @Override
    public void onStoreEventByCar() {

            FirebaseFirestore db= FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(currentUserInfo.getUserId());
            docRef.get().addOnCompleteListener(task -> {
                //try to access to the file
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If document exists, add information about the alarm
                    if (document != null && document.exists()) {
                        //store data
                        TimeFormatter tmpLeavingTime= leavingTime.clone();
                        TimeFormatter tmpMeetingTime= meetingTime.clone();

                        String originName = origin.getName();
                        String originAddress = origin.getAddress();
                        String destinationName = destination.getName();
                        String destinationAddress = destination.getAddress();
                        Date gettingReadyTime = tmpLeavingTime.getTimeAndDay().getTime();
                        tmpLeavingTime.addTimeFormatter(timeToGetReady);
                        Date leavingTime = tmpLeavingTime.getTimeAndDay().getTime();
                        tmpLeavingTime.addTimeFormatter(timeToReachTheCar);
                        Date startTheCarTime = tmpLeavingTime.getTimeAndDay().getTime();
                        tmpMeetingTime.subtractTimeFormatter(timeToParkTheCar);
                        Date parkTheCarTime =tmpMeetingTime.getTimeAndDay().getTime();
                        Date meetingTime = this.meetingTime.getTimeAndDay().getTime();
                        Boolean byCar = true;

                        //info for the database
                        Map<String, Object> user = new HashMap<>();
                        user.put("originName", originName);
                        user.put("originAddress",originAddress);
                        user.put("destinationName", destinationName);
                        user.put("destinationAddress", destinationAddress);
                        user.put("gettingReadyTime",gettingReadyTime);
                        user.put("leavingTime",leavingTime);
                        user.put("startTheCarTime",startTheCarTime);
                        user.put("parkTheCarTime",parkTheCarTime);
                        user.put("meetingTime",meetingTime);
                        user.put("byCar", true);

                        //info for the currentUserInfo
                        ExistingEvent existingEvent= new ExistingEvent(originName, originAddress, destinationName, destinationAddress, gettingReadyTime, leavingTime, meetingTime, byCar);
                        existingEvent.setCarDetails(startTheCarTime, parkTheCarTime);
                        currentUserInfo.setExistingEvent(existingEvent);

                        db.collection("users").document(currentUserInfo.getUserId())
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "New event successfully saved"))
                                .addOnFailureListener(e -> Log.e(TAG, "New event, error while storing it", e));
                        startActivity(new Intent(this, MainActivity.class));
                        currentUserInfo.setExistingEvent(null);
                    }
                    //if document doesn't exist, there an error
                    else {
                        Log.e(TAG, "New event, error: document about the user doesn't exists");
                        currentUserInfo.setExistingEvent(null);
                    }
                } else {
                    if (task.getException() != null) {
                        Log.e(TAG, "Error while checking the existence of the user(Firestone document)", task.getException());
                    }
                }
            });
    }

    @Override
    public void onStoreEventByFoot() {
            FirebaseFirestore db= FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(currentUserInfo.getUserId());
            docRef.get().addOnCompleteListener(task -> {
                //try to access to the file
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If document exists, add information about the alarm
                    if (document != null && document.exists()) {
                        //store data
                        TimeFormatter tmpLeavingTime= leavingTime.clone();
                        TimeFormatter tmpMeetingTime= meetingTime.clone();

                        String originName = origin.getName();
                        String originAddress = origin.getAddress();
                        String destinationName = destination.getName();
                        String destinationAddress = destination.getAddress();
                        Date gettingReadyTime = tmpLeavingTime.getTimeAndDay().getTime();
                        tmpLeavingTime.addTimeFormatter(timeToGetReady);
                        Date leavingTime = tmpLeavingTime.getTimeAndDay().getTime();
                        Date meetingTime = tmpMeetingTime.getTimeAndDay().getTime();
                        Boolean byCar = true;

                        //info for the database
                        Map<String, Object> user = new HashMap<>();
                        user.put("originName", originName);
                        user.put("originAddress",originAddress);
                        user.put("destinationName", destinationName);
                        user.put("destinationAddress", destinationAddress);
                        user.put("gettingReadyTime",gettingReadyTime);
                        user.put("leavingTime",leavingTime);
                        user.put("meetingTime",meetingTime);
                        user.put("byCar", false);

                        //info for the currentUserInfo
                        ExistingEvent existingEvent= new ExistingEvent(originName, originAddress, destinationName, destinationAddress, gettingReadyTime, leavingTime, meetingTime, byCar);
                        currentUserInfo.setExistingEvent(existingEvent);

                        db.collection("users").document(currentUserInfo.getUserId())
                                .set(user, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "New event successfully saved"))
                                .addOnFailureListener(e -> Log.e(TAG, "New event, error while storing it", e));
                        startActivity(new Intent(this, MainActivity.class));
                    }
                    //if document doesn't exist, there an error
                    else {
                        Log.e(TAG, "New event, error: document about the user doesn't exists");
                        currentUserInfo.setExistingEvent(null);
                    }
                } else {
                    if (task.getException() != null) {
                        Log.e(TAG, "Error while checking the existence of the user(Firestone document)", task.getException());
                    }
                }
            });

    }
}