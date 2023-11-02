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

public class NewEventFragment extends Fragment {
    GeoApiContext geoApiContext;
    String[] dropdownElements= {"By foot", "By car"};
    private OnFragmentInteractionListener mListener;
    String selectedMeanOfTransport;
    TextView tv_timeToPark;
    TimePickerFragment timePicker;
    AutocompleteSupportFragment acb_meetingPoint, acb_leavingPoint;
    Place origin, destination;
    AutoCompleteTextView ac_meansOfTransport;
    TextView tv_travelTime, tv_meetingPoint, tv_leavingPoint;


    public NewEventFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_new_event, container, false);
        //Set class variables of components that we are goigng to set visible or gone according to the mean of transport
        tv_timeToPark= v.findViewById(R.id.tv_timeToPark);
        timePicker=(TimePickerFragment) getChildFragmentManager().findFragmentById(R.id.fragmentTimePicker);

        tv_meetingPoint=v.findViewById(R.id.tv_meetingPoint);
        tv_travelTime=v.findViewById(R.id.tv_travelTime);
        tv_leavingPoint=v.findViewById(R.id.tv_leavingPoint);

        //Autocomplete bar for meeting point
        acb_meetingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_meeting_point);
        if (acb_meetingPoint != null) {
            acb_meetingPoint.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS)); //what you get from google maps, we only eed the Place name
            acb_meetingPoint.setHint("A public place,address of an house,...");
            acb_meetingPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
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

        //Autocomplete bar for leaving point
        acb_leavingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_leaving_point);
        if (acb_leavingPoint != null) {
            acb_leavingPoint.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS)); //what you get from google maps, correct it
            acb_leavingPoint.setHint("Your house,a public place,...");
            acb_leavingPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
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

        //dropDown menu means of transport and visibility of tv_timeToPark,te_hour,te_minutes
        ac_meansOfTransport= v.findViewById(R.id.auto_complete_txtView);
        ArrayAdapter<String> adapterDropdownMenu= new ArrayAdapter<>(this.getContext(), R.layout.fragment_dropdown_element, dropdownElements);
        ac_meansOfTransport.setAdapter(adapterDropdownMenu);
        ac_meansOfTransport.setHint("By foot"); //defualt value
        ac_meansOfTransport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMeanOfTransport= parent.getItemAtPosition(position).toString();
                if(selectedMeanOfTransport.equals("By car")){
                    tv_timeToPark.setVisibility(View.VISIBLE);
                }
                else{
                    tv_timeToPark.setVisibility(View.INVISIBLE);
                    timePicker.getView().setVisibility(View.INVISIBLE);
                }
                checkTimeDistance();
            }
        });

        //Button "next"
        Button nextButton= v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction();
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
     * Check if both originPlace and destinationPlace are set
     * If so, calculates the time distance and return it
     *
     */
    private void checkTimeDistance(){
        String meanOfTransport= String.valueOf(ac_meansOfTransport.getText());
        String travelMode;
        if(meanOfTransport.equals("By car")){
            travelMode="DRIVING";
        }
        else{
            travelMode="WALKING";
        }
        Toast.makeText(getContext(), "Mean of transport:"+ travelMode, Toast.LENGTH_SHORT).show();
        //check if both Places field contain a place
        if(origin!=null && destination!=null){
            try {
                Toast.makeText(getContext(), "Both source and destination are not null", Toast.LENGTH_SHORT).show();
                DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                        .origin(origin.getAddress())
                        .destination(destination.getAddress())
                        .mode(TravelMode.valueOf(travelMode)) // You can change the travel mode
                        .await();

                // Access the distance and duration from the result
                //String distance = result.routes[0].legs[0].distance.humanReadable;
                String duration = result.routes[0].legs[0].duration.humanReadable;
                tv_travelTime.setText(duration);
                Toast.makeText(getContext(), "Distance calculated", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("TAG", e.toString());
                Toast.makeText(getContext(), "Exception"+ e, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            tv_travelTime.setText("Travel time");
        }
    }
    /*
    public static class AutocompleteBarPlaceSelectionListener implements PlaceSelectionListener{
        @Override
        public void onError(@NonNull Status status) {
            Log.i(TAG, "An error occurred: " + status);
        }

        @Override
        public void onPlaceSelected(@NonNull Place place) {

            Log.i(TAG, "Place: " + place.getName());
        }
    }

     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }




}