package com.teresa.appnotbelate;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.libraries.places.api.model.Place;

import java.sql.Time;

public class InfoCarFragment extends Fragment {
    View v;
    TimePickerFragment reachTheCar, parkTheCar;
    // Data passed from MainActivity
    Place origin, destination;
    TimeFormatter travelTime;

    public InfoCarFragment() {
        // Required empty public constructor
    }


    /*
    private void checkTimeDistance(){
        TimeFormatter totalTravelTime= new TimeFormatter(0,0,0);
        Log.println(Log.INFO,TAG,"DEBUG1-totalTravelTime just created->"+ totalTravelTime+ ", tot minutes "+totalTravelTime.getTotMinutes()); //debug
        String travelMode;

        //Check if there's extra time to park to be considered
        String meanOfTransport= String.valueOf(ac_meansOfTransport.getText());
        if(meanOfTransport.equals("By car")){
            travelMode="DRIVING";
            Log.println(Log.INFO,TAG,"DEBUG2.1-totalTravelTime is updated with parking time->"+ totalTravelTime+ ", tot minutes "+totalTravelTime.getTotMinutes()); //debug
            totalTravelTime.addTimeFormatter(timePicker.getTime());
            Log.println(Log.INFO,TAG,"DEBUG2.2-totalTravelTime is updated with parking time->"+ totalTravelTime+ ", tot minutes "+totalTravelTime.getTotMinutes()); //debug
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
                //String distance = result.routes[0].legs[0].distance.humanReadable;
                //String duration = result.routes[0].legs[0].duration.humanReadable;
                com.google.maps.model.Duration duration = result.routes[0].legs[0].duration;
                TimeFormatter travelDuration=new TimeFormatter(duration);
                Log.println(Log.INFO,TAG,"DEBUG3-travelDuration just created->"+ travelDuration+ ", tot minutes "+travelDuration.getTotMinutes()); //debug

                totalTravelTime.addTimeFormatter(travelDuration);
                Log.println(Log.INFO,TAG,"DEBUG4-sum of totalTravelTime and travel duration->"+ totalTravelTime+ ", tot minutes "+totalTravelTime.getTotMinutes()); //debug

                tv_travelTime.setText(totalTravelTime.toString());
            } catch (Exception e) {
                Log.d("TAG", e.toString());
                Toast.makeText(getContext(), "Exception"+ e, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            tv_travelTime.setText("Travel time");
        }
    }

     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_info_car, container, false);
        //elements I need
        parkTheCar=new TimePickerFragment();
        reachTheCar=new TimePickerFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.frg_reachTheCar, reachTheCar).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.frg_parkTheCar, parkTheCar).commit();

        return v;
    }
}