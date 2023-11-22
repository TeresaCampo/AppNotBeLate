package com.teresa.appnotbelate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConfirmAndStartActivityByFoot extends Fragment {
    View v;
    public ConfirmAndStartActivityByFoot() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_confirm_and_start_event_by_foot, container, false);
        return v;
    }
}
