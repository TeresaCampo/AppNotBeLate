package com.teresa.appnotbelate;

import com.google.android.libraries.places.api.model.Place;

public interface CommunicationActivityFragments {
    void onChangeFragment(int numberOfFolllowingFragment);
    void onCommunicateNewEvent(Place origin, Place destination, TimeFormatter meetingTime, Boolean byFoot, TimeFormatter travelTime);

}
