package com.teresa.appnotbelate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NewEventFragment.OnFragmentInteractionListener{
    ViewPager2 container;
    TabLayout navigationBar;

    @Override
    public void onFragmentInteraction() {
        container.setCurrentItem(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up navigation bar
        container= findViewById(R.id.container);
        navigationBar= findViewById(R.id.tabLayout);
        container.setAdapter(new ViewPageAdapterNavigationBar(this));

        navigationBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                container.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        //initialize Places
        Places.initialize(getApplicationContext(), getResources().getString(R.string.apiKey));


    }
}