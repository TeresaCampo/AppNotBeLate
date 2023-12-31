package com.teresa.appnotbelate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class UserInfoFragment extends Fragment {
    View v;
    Button bn_logOut;
    TextView tv_name, tv_email;
    String name, email;

    private CommunicationActivityFragments communicationListener;

    public UserInfoFragment() {
        // Required empty public constructor
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
        name=bundle.getString("name");
        email=bundle.getString("email");
        tv_name.setText(name);
        tv_email.setText(email);

        bn_logOut.setOnClickListener(v -> {
            if (communicationListener != null) {
            communicationListener.onLogOut();
            }
        });
        return v;
    }
    /**
     * To set the communicationListener to communicate with MainActivity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CommunicationActivityFragments) {
            communicationListener = (CommunicationActivityFragments) context;
        } else {
            throw new ClassCastException(context + " must implement CommunicationActivityFragments");
        }
    }
}