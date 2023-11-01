package com.teresa.appnotbelate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LastDetailsFragment extends Fragment {
    public LastDetailsFragment() {
        // Required empty public constructor
    }
    public static LastDetailsFragment newInstance(String param1, String param2) {
        LastDetailsFragment fragment = new LastDetailsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_last_details, container, false);
    }
}