package com.teresa.appnotbelate.newEventSetting;

import static android.util.Log.DEBUG;
import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.maps.DirectionsApi;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.teresa.appnotbelate.CommunicationActivityFragments;
import com.teresa.appnotbelate.Components.DropDownMenu;
import com.teresa.appnotbelate.R;
import com.teresa.appnotbelate.Components.TimeFormatter;
import com.teresa.appnotbelate.Components.TimePickerFragment;

import java.util.Arrays;
import java.util.Calendar;


public class NewEventFragment extends Fragment implements TimePickerFragment.DurationChangeListener {
    private static final String TAG = "MyDebug";
    View v;
    GeoApiContext geoApiContext;
    String[] dropdownElements= {"By foot", "By car"};
    private CommunicationActivityFragments communicationListener;
    TextView tv_isTomorrow, tv_travelTime, tv_meetingPoint, tv_leavingPoint, tv_message;
    TimePickerFragment meetingTimeFrag;
    TimeFormatter travelTime, leavingTime, meetingTime;
    DropDownMenu ac_meansOftransport;
    AutocompleteSupportFragment acb_meetingPoint, acb_leavingPoint;
    Place origin, destination;
    Button nextButton;
    Boolean isTomorrow=false;
    Boolean byFoot=true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_new_event, container, false);

        //Components that I have to work on
        tv_travelTime=v.findViewById(R.id.tv_travelTime);
        tv_isTomorrow=v.findViewById(R.id.tv_isTomorrow);
        tv_message=v.findViewById(R.id.tv_message);

        //Create autocomplete bar for leaving and meeting point
        createAutocompleteBarMeetingPoint();
        createAutocompleteBarLeavingPoint();
        
        //Create dropDown menu with means of transport
        createDropDownMenu();

        //Initialize meetingTimeFrag
        if(meetingTimeFrag==null) {
            meetingTimeFrag = new TimePickerFragment();
            meetingTimeFrag.setCurrentTime();
            getChildFragmentManager().beginTransaction().replace(R.id.frg_meetingTime, meetingTimeFrag).commit();
            meetingTime= meetingTimeFrag.getTime();
            meetingTimeFrag.setDurationChangeListener(this);
        }
        else{
            meetingTimeFrag.setSameTime(meetingTime);
            getChildFragmentManager().beginTransaction().replace(R.id.frg_meetingTime, meetingTimeFrag).commit();

            if(checkIfTomorrow(meetingTime)) {
                tv_isTomorrow.setVisibility(View.VISIBLE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText("Even leaving now you won't make it in time today, but we can plan it for tomorrow");
                //Toast.makeText(getContext(), "Even leaving now you won't make it in time today, but we can plan it for tomorrow", Toast.LENGTH_LONG).show();
            }
        }
        //getChildFragmentManager().beginTransaction().replace(R.id.frg_meetingTime, meetingTimeFrag).commit();

        //Create "next" button
        nextButton= v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                    //check if all the field are compiled
                    if(origin== null || destination==null) {
                        Toast.makeText(getContext(), "Please, insert all the data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TimeFormatter updatedMeetingTime= new TimeFormatter(meetingTime.getMinutes(),meetingTime.getHours(), 0);
                    TimeFormatter updatedLeavingTime= new TimeFormatter(meetingTime.getMinutes(),meetingTime.getHours(), 0);
                    if(isTomorrow){
                        updatedMeetingTime.setDayTomorrow();
                        updatedLeavingTime.setDayTomorrow();
                    }
                    updatedLeavingTime.subtractTimeFormatter(travelTime);
                    Log.d(TAG, "leavingTime is (calendar):"+updatedLeavingTime.getTimeAndDay().getTime()+", (timeFormatter):"+updatedLeavingTime.toString());
                    Log.d(TAG, "meetingTime is (calendar):"+updatedMeetingTime.getTimeAndDay().getTime()+", (timeFormatter):"+updatedMeetingTime.toString());

                    communicationListener.onCommunicateNewEvent(origin, destination,updatedMeetingTime, byFoot, travelTime,updatedLeavingTime, isTomorrow );

                    if(byFoot==false){
                        communicationListener.onChangeFragment(2);
                    }
                    else {
                        communicationListener.onChangeFragment(3);
                    }
                }
            }
        });

        //Initialize Class to calculate time distance between two places
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.apiKey))
                .build();

        return v;
    }

    /**
     * Create a drop down menu for to choose the mean of transport
     */
    private void createDropDownMenu(){
        Context context= this.getContext();
        ac_meansOftransport = new DropDownMenu() ;
        ac_meansOftransport.setContext(context);
        ac_meansOftransport.setDropdownElements(dropdownElements);
        ac_meansOftransport.setClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    byFoot=false;
                }
                else{
                    byFoot=true;
                }
                checkTimeDistance();
            }
        });
        if(byFoot== true){
            ac_meansOftransport.setHint("By foot");
        }
        else{
            ac_meansOftransport.setHint("By car");
        }
        getChildFragmentManager().beginTransaction().replace(R.id.ac_meansOfTransport, ac_meansOftransport).commit();
    }


    /**
     * Create an Google autocomplete bar to choose the meeting point
     */
    private void createAutocompleteBarMeetingPoint(){
        //Components I need
        tv_meetingPoint=v.findViewById(R.id.tv_meetingPoint);

        acb_meetingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_meeting_point);
        if (acb_meetingPoint != null) {
            acb_meetingPoint.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS)); //what you get from google maps, we only eed the Place name
            acb_meetingPoint.setHint("Place you need to go");
            acb_meetingPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    destination=place;
                    tv_meetingPoint.setText(destination.getName());
                    checkTimeDistance();
                }
                @Override
                public void onError(@NonNull Status status) {
                    Log.e( TAG, "Problem with Google API "+status);
                    destination=null;
                    tv_meetingPoint.setText("Meeting point");
                }
            });
        }
        else{
            Log.e(TAG, "Error while creating meeting point autocomplete bar(null fragment)");
        }
        //eventually initialize
        if(destination!=null){
            tv_meetingPoint.setText(destination.getName());
            acb_meetingPoint.setText(destination.getName());
            checkTimeDistance();
        }
    }
    /**
     * Create an Google autocomplete bar to choose the leaving point
     */
    private void createAutocompleteBarLeavingPoint(){
        //Components I need
        tv_leavingPoint=v.findViewById(R.id.tv_leavingPoint);

        acb_leavingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_leaving_point);
        if (acb_leavingPoint != null) {
            acb_leavingPoint.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS)); //what you get from google maps, correct it
            acb_leavingPoint.setHint("Place you are leaving from");

            acb_leavingPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    origin=place;
                    tv_leavingPoint.setText(origin.getName());
                    checkTimeDistance();
                }
                @Override
                public void onError(@NonNull Status status) {
                    Log.e( TAG, "Problem with Google API "+status);
                    origin=null;
                    tv_leavingPoint.setText("Leaving point");
                }
            });
        }
        else{
            Log.e(TAG, "Error while creating meeting point autocomplete bar(null fragment)");
        }
        //eventually initialize
        if(origin!=null){
            tv_leavingPoint.setText(origin.getName());
            acb_leavingPoint.setText(origin.getName());
            checkTimeDistance();
        }
    }


    /**
     * Check if both originPlace and destinationPlace are set
     * If so, calculates the time distance and return it
     */
    private void checkTimeDistance(){
        //Check the travel mode
        String travelMode;
        if(byFoot==false){
            travelMode="DRIVING";
        }
        else{
            travelMode="WALKING";
        }
        //Check if both origin and destination contain a place
        if(origin!=null && destination!=null){
            try {
                DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                        .origin(origin.getAddress())
                        .destination(destination.getAddress())
                        .mode(TravelMode.valueOf(travelMode))
                        .await();

                // Access the distance and duration from the result
                com.google.maps.model.Duration duration = result.routes[0].legs[0].duration;
                travelTime=new TimeFormatter(duration);
                //the travel should last less than one day
                if(travelTime.getDays()!=0){
                    Toast.makeText(getContext(), "More than one day to reach the meeting point...choose another leaving point", Toast.LENGTH_LONG).show();
                    // to make the UI reactive
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            acb_leavingPoint.setText("");
                        }
                    }, 1);
                    origin=null;
                    tv_leavingPoint.setText("Leaving point");
                    return;
                }
                leavingTime= new TimeFormatter(meetingTimeFrag.getEn_minutes(), meetingTimeFrag.getEn_hour(),meetingTimeFrag.getTime().getDays());

                leavingTime.subtractTimeFormatter(travelTime);
                if(checkIfTomorrow(leavingTime)) {
                    tv_isTomorrow.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Even leaving now you won't make it in time today, but we can plan it for tomorrow");
                }
                else{
                    tv_isTomorrow.setVisibility(View.INVISIBLE);
                    tv_message.setVisibility(View.INVISIBLE);
                }
                Log.d(TAG, "Leaving point= "+origin.getName()+", meeting point is "+destination.getName()+", mean of transport is "+travelMode+", travel time is "+travelTime.toString());
                tv_travelTime.setText(travelTime.toString());
            }
            catch (Exception e) {
                Log.d(TAG, "Error with google API while trying to calculate time distance between origin and destination");
                Log.d(TAG, e.toString());
            }
        }
        else{
            tv_travelTime.setText("Travel time");
        }
    }

    /**
     * To set the communicationListener to communicate with MainActivity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommunicationActivityFragments) {
            communicationListener = (CommunicationActivityFragments) context;
        } else {
            throw new ClassCastException(context + " must implement CommunicationActivityFragments");
        }
    }

    /**
     * To update isTomorrow when the meetingTimeFrag changes
     * @param newTime new time
     */
    @Override
    public void onDurationChanged(TimeFormatter newTime) {
        isTomorrow=checkIfTomorrow(newTime);

        if(isTomorrow==true) {
            tv_isTomorrow.setVisibility(View.VISIBLE);
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText("The arrival time is before the current time");
            return;
        }
        else{
            tv_isTomorrow.setVisibility(View.INVISIBLE);
            tv_message.setVisibility(View.INVISIBLE);
        }
        meetingTime=meetingTimeFrag.getTime();
        checkTimeDistance();
    }

    /**
     * To check if the meeting time is tomorrow or today
     * @param timeToCheck meeting time
     * @return true if it is tomorrow
     */
    Boolean checkIfTomorrow(TimeFormatter timeToCheck){
        Calendar calendar = Calendar.getInstance();
        isTomorrow= timeToCheck.getTimeAndDay().before(calendar);
        return isTomorrow;
        /*int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(timeToCheck.getHours()<hour){
            return true;
        }
        else if(timeToCheck.getHours()==hour && timeToCheck.getMinutes()<=minute){
            return true;
        }
        else{
            return false;
        }*/
    }
}