package com.teresa.appnotbelate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExistingEvent {
    String originName, originAddress, destinationName, destinationAddress;
    Date gettingReadyTime, leavingTime, startTheCarTime, parkTheCarTime, meetingTime;
    Boolean byCar;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, EEEE dd");


    public ExistingEvent(String originName, String originAddress, String destinationName, String destinationAddress, Date gettingReadyTime, Date leavingTime, Date meetingTime, Boolean byCar) {
        this.originName = originName;
        this.originAddress = originAddress;
        this.destinationName = destinationName;
        this.destinationAddress = destinationAddress;
        this.gettingReadyTime = gettingReadyTime;
        this.leavingTime = leavingTime;
        this.meetingTime = meetingTime;
        this.byCar = byCar;
    }

    public void setCarDetails(Date startTheCarTime, Date parkTheCarTime) {
        this.startTheCarTime = startTheCarTime;
        this.parkTheCarTime=parkTheCarTime;
    }

    public Boolean getByCar() {
        return byCar;
    }

    public Date getGettingReadyTime() {
        return gettingReadyTime;
    }

    public Date getLeavingTime() {
        return leavingTime;
    }

    public Date getStartTheCarTime() {
        return startTheCarTime;
    }

    public Date getParkTheCarTime() {
        return parkTheCarTime;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestinationName() {
        return destinationName;
    }
}
