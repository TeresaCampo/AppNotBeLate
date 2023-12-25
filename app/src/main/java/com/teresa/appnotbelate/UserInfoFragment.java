package com.teresa.appnotbelate;

import static android.content.ContentValues.TAG;
import static android.util.Log.DEBUG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyLog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {
    View v;
    Button bn_logOut;
    TextView tv_name, tv_email;
    String name, email;

    private CommunicationActivityFragments communicationListener;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_user_info, container, false);
        tv_name=v.findViewById(R.id.tv_name);
        tv_email=v.findViewById(R.id.tv_email);
        bn_logOut=v.findViewById(R.id.logOut);

        Bundle bundle=getArguments();
        tv_name.setText(bundle.getString("name"));
        tv_email.setText(bundle.getString("email"));

        bn_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (communicationListener != null) {
                communicationListener.onLogOut();
                }

            }
        });
        return v;
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
    void insertData(String name, String email){
        this.name=name;
        this.email=email;
    }
    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.println(DEBUG, VolleyLog.TAG,"Inserisco i dati ");
        getActivity().runOnUiThread(() -> {
            this.tv_name.setText(name);
            this.tv_email.setText(email);
        });
    }

     */
}