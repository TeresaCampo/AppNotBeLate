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
        Log.println(DEBUG,TAG,"origin place is "+origin);
        // Inflate the layout for this fragment
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
            meetingTime= meetingTimeFrag.getTime();
            meetingTimeFrag.setDurationChangeListener(this);
        }
        else{
            meetingTimeFrag.setSameTime(meetingTime);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.frg_meetingTime, meetingTimeFrag).commit();

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
                    communicationListener.onCommunicateNewEvent(origin, destination,meetingTimeFrag.getTime(), byFoot, travelTime,leavingTime, isTomorrow );

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
        getChildFragmentManager().beginTransaction().replace(R.id.ac_meansOfTransport, ac_meansOftransport).commit();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                // Code to be executed after the delay
                ac_meansOftransport.insertElements(dropdownElements, context);
                if(byFoot== true){
                    ac_meansOftransport.setHint("By foot");
                }
                else{
                    ac_meansOftransport.setHint("By car");
                }
                ac_meansOftransport.getTextView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Log.println(DEBUG, TAG, "Selected mean of transport is "+position);
                            if(position==1){
                                byFoot=false;
                            }
                            else{
                                byFoot=true;
                            }
                            checkTimeDistance();
                    }
                });
            }
        }, 150);
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
                    destination=null;
                    tv_meetingPoint.setText("Meeting point");
                }
            });
        }
        else{
            Log.d("TAG", "Il frammento era nullo");
        }
        //display place already chosen
        if(destination!=null){
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
                    origin=null;
                    tv_leavingPoint.setText("Leaving point");
                }
            });
        }
        else{
            Log.d("TAG", "Il frammento era nullo");
        }
        //display place already chosen
        if(origin!=null){
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
                        .mode(TravelMode.valueOf(travelMode)) // You can change the travel mode
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
                Log.println(DEBUG, TAG, "leavingTime is "+ leavingTime + " and meeting time is "+meetingTimeFrag.getTime());
                if(checkIfTomorrow(leavingTime)) {
                    tv_isTomorrow.setVisibility(View.VISIBLE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Even leaving now you won't make it in time today, but we can plan it for tomorrow");
                    //Toast.makeText(getContext(), "Even leaving now you won't make it in time today, but we can plan it for tomorrow", Toast.LENGTH_LONG).show();
                }
                else{
                    tv_isTomorrow.setVisibility(View.INVISIBLE);
                    tv_message.setVisibility(View.INVISIBLE);
                }


                tv_travelTime.setText(travelTime.toString());
            } catch (Exception e) {
                Log.d("TAG", e.toString());
                Toast.makeText(getContext(), "Exception"+ e, Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(getContext(), "The arrival time is before the current time", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            tv_isTomorrow.setVisibility(View.INVISIBLE);
            tv_message.setVisibility(View.INVISIBLE);
        }
        meetingTime=meetingTimeFrag.getTime();
        checkTimeDistance();
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
}