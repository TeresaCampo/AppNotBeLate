package com.teresa.appnotbelate;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapterNavigationBar extends FragmentStateAdapter {
    public ViewPageAdapterNavigationBar(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Set fragment according to the number associated to the layout
     * UserInfoFragment--> layout 0
     * NewEventFragment--> layout 1
     * LastDetailsFragment--> layout 2
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new UserInfoFragment();
            case 1:
                return new NewEventFragment();
            case 2:
                return new LastDetailsFragment();
            default:
                return new NewEventFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
