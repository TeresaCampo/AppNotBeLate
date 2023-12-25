package com.teresa.appnotbelate.Components;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.teresa.appnotbelate.R;

public class DropDownMenu extends Fragment  {
    private static final String TAG = "MyDebug";

    View v;
    public AutoCompleteTextView textView;

    Context context;
    String[] dropdownElements;
    String hint;
    AdapterView.OnItemClickListener clickListener;
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_drop_down_menu, container, false);
        textView= v.findViewById(R.id.auto_complete_txtView);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(clickListener==null || hint == null || dropdownElements== null || context==null){
            Log.e(TAG, "Error while setting up the dropdown menu, some parameters are null");
            return;
        }
        ArrayAdapter<String> adapterDropdownMenu = new ArrayAdapter<>(context, R.layout.fragment_dropdown_element, dropdownElements);
        textView.setAdapter(adapterDropdownMenu);
        textView.setHint(hint);
        textView.setOnItemClickListener(clickListener);
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setDropdownElements(String[] dropdownElements) {
        this.dropdownElements = dropdownElements;
    }
    public void setClickListener(AdapterView.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
    public void setHint(String hint) {
        this.hint = hint;
    }
}