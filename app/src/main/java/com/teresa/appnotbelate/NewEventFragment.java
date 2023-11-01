package com.teresa.appnotbelate;

import static com.android.volley.VolleyLog.TAG;

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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class NewEventFragment extends Fragment {
    String[] dropdownElements= {"By foot", "By car"};
    private OnFragmentInteractionListener mListener;
    String selectedMeanOfTransport;
    TextView tv_timeToPark;
    EditText te_minutes;
    EditText te_hour;


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
        te_minutes= v.findViewById(R.id.te_minutes);
        te_hour= v.findViewById(R.id.te_hour);

        //Autocomplete bar for meeting point
        AutocompleteSupportFragment acb_meetingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_meeting_point);
        if (acb_meetingPoint != null) {
            acb_meetingPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME)); //what you get from google maps, correct it
            acb_meetingPoint.setHint("A public place,address of an house,...");
            acb_meetingPoint.setOnPlaceSelectedListener(new AutocompleteBarPlaceSelectionListener());
        }
        else{
            Log.d("TAG", "Il frammento era nullo");
        }

        //Autocomplete bar for leaving point
        AutocompleteSupportFragment acb_leavingPoint= (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.ac_leaving_point);
        if (acb_leavingPoint != null) {
            acb_leavingPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME)); //what you get from google maps, correct it
            acb_leavingPoint.setHint("Your house,a public place,...");
            acb_leavingPoint.setOnPlaceSelectedListener(new AutocompleteBarPlaceSelectionListener());
        }
        else{
            Log.d("TAG", "Il frammento era nullo");
        }

        //dropDown menu means of transport and visibility of tv_timeToPark,te_hour,te_minutes
        AutoCompleteTextView ac_meansOfTransport= v.findViewById(R.id.auto_complete_txtView);
        ArrayAdapter<String> adapterDropdownMenu= new ArrayAdapter<>(this.getContext(), R.layout.fragment_dropdown_element, dropdownElements);
        ac_meansOfTransport.setAdapter(adapterDropdownMenu);

        ac_meansOfTransport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMeanOfTransport= parent.getItemAtPosition(position).toString();
                if(selectedMeanOfTransport.equals("By car")){
                    tv_timeToPark.setVisibility(View.VISIBLE);
                    te_hour.setVisibility(View.VISIBLE);
                    te_minutes.setVisibility(View.VISIBLE);
                }
                else{
                    tv_timeToPark.setVisibility(View.GONE);
                    te_hour.setVisibility(View.GONE);
                    te_minutes.setVisibility(View.GONE);
                }
            }
        });

        //button on action
        Button nextButton= v.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentInteraction();
                }

            }
        });

        return v;
    }
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