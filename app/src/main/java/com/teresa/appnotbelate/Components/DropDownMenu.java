package com.teresa.appnotbelate.Components;

import static android.util.Log.DEBUG;
import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.teresa.appnotbelate.R;


public  class DropDownMenu extends Fragment {
public AutoCompleteTextView textView;
View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_drop_down_menu, container, false);
        textView= v.findViewById(R.id.auto_complete_txtView);
        Log.println(DEBUG,TAG, "DEBUGGGGhhhhhhhhhhhhhhhhhhhh--> textview found is "+ textView);
        return v;
    }
    public void insertElements(String[] dropdownElements, Context context){
        ArrayAdapter<String> adapterDropdownMenu= new ArrayAdapter<>(context, R.layout.fragment_dropdown_element, dropdownElements);
        Log.println(DEBUG,TAG, "DEBUGGGGhhhhhhhhhhhhhhhhhhhh 2--> textview found is "+ textView);

        textView.setAdapter(adapterDropdownMenu);

    }
    public void setHint(String hint){
        textView.setHint(hint);
    }

    public AutoCompleteTextView getTextView() {
        return textView;
    }

    public void setTextView(AutoCompleteTextView textView) {
        this.textView = textView;
    }


}