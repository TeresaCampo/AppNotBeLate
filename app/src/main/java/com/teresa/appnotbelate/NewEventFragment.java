package com.teresa.appnotbelate;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Arrays;
import java.util.Calendar;


public class NewEventFragment extends Fragment implements TimePickerFragment.DurationChangeListener {
    View v;
    GeoApiContext geoApiContext;
    String[] dropdownElements= {"By foot", "By car"};
    private CommunicationActivityFragments communicationListener;
    TextView tv_isTomorrow, tv_travelTime, tv_meetingPoint, tv_leavingPoint;
    TimePickerFragment meetingTime;
    TimeFormatter travelTime;
    AutocompleteSupportFragment acb_meetingPoint, acb_leavingPoint;
    Place origin, destination;
    AutoCompleteTextView ac_meansOfTransport;
    Button nextButton;
    Boolean isTomorrow=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_new_event, container, false);

        //Components that I have to work on
        tv_travelTime=v.findViewById(R.id.tv_travelTime);
        tv_isTomorrow=v.findViewById(R.id.tv_isTomorrow);

        //Create autocomplete bar for leaving and meeting point
        createAutocompleteBarMeetingPoint();
        createAutocompleteBarLeavingPoint();

        //Create dropDown menu with means of transport
        createDropDownMenu();

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
                    String meanOfTransport= String.valueOf(ac_meansOfTransport.getText());
                    if(meanOfTransport.equals("By car")){
                        communicationListener.onCommunicateNewEvent(origin, destination,meetingTime.getTime(), false, travelTime );
                        communicationListener.onChangeFragment(2);
                    }
                    else {
                        communicationListener.onCommunicateNewEvent(origin, destination, meetingTime.getTime(), true, travelTime );
                        communicationListener.onChangeFragment(3);
                    }
                }
            }
        });
        //Initialize meetingTime
        meetingTime = new TimePickerFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.frg_meetingTime, meetingTime).commit();
        meetingTime.setCurrentTime();
        meetingTime.durationChangeListener=this;

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
        //Components I need
        ac_meansOfTransport= v.findViewById(R.id.auto_complete_txtView);

        ArrayAdapter<String> adapterDropdownMenu= new ArrayAdapter<>(this.getContext(), R.layout.fragment_dropdown_element, dropdownElements);
        ac_meansOfTransport.setAdapter(adapterDropdownMenu);
        ac_meansOfTransport.setHint("By foot"); //default value
        ac_meansOfTransport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkTimeDistance();
            }
        });
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
    }

    /**
     * Check if both originPlace and destinationPlace are set
     * If so, calculates the time distance and return it
     */
    private void checkTimeDistance(){
        //Check the travel mode
        String travelMode;
        String meanOfTransport= String.valueOf(ac_meansOfTransport.getText());
        if(meanOfTransport.equals("By car")){
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
     * To update isTomorrow when the meetingTime changes
     * @param newTime new time
     */
    @Override
    public void onDurationChanged(TimeFormatter newTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(newTime.getHours()<hour){
            isTomorrow=true;
        }
        else if(newTime.getHours()==hour && newTime.getMinutes()<=minute){
            isTomorrow=true;
        }
        else{
            isTomorrow=false;
        }

        if(isTomorrow==true) {
            tv_isTomorrow.setVisibility(View.VISIBLE);
        }
        else{
            tv_isTomorrow.setVisibility(View.INVISIBLE);
        }
    }
}