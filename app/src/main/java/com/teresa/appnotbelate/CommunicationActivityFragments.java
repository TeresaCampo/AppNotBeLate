package com.teresa.appnotbelate;

import com.google.android.libraries.places.api.model.Place;
import com.teresa.appnotbelate.Components.TimeFormatter;

public interface CommunicationActivityFragments {
    void onChangeFragment(int numberOfFolllowingFragment);
    void onCommunicateGetReady(TimeFormatter leavingTime,TimeFormatter meetingTime, TimeFormatter travelTime, TimeFormatter timeToGetReady, Boolean isTomorrow);
    void onLogOut();

    void onCommunicateInfoCar(TimeFormatter leavingTime,TimeFormatter meetingTime, TimeFormatter travelTime, TimeFormatter timeToReachTheCar, TimeFormatter timeToPark, Boolean isTomorrow);
    void onCommunicateNewEvent(Place origin, Place destination, TimeFormatter meetingTime, Boolean byFoot, TimeFormatter travelTime, TimeFormatter leavingTime, Boolean isTomorrow);
    void onStoreEventByCar();
    void onStoreEventByFoot();
    void onDeleteCarEvent();
    void onDeleteByFootEvent();
    }
