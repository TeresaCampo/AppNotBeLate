package com.teresa.appnotbelate.Components;

public class TimeFormatter {
    long totMinutes,minutes, hours, days;

    /**
     * To create an instance given the google maps duration
     * @param googleDuration duration in gooogle maps Duration
     */
   public TimeFormatter(com.google.maps.model.Duration googleDuration) {
        long totalSeconds = googleDuration.inSeconds;
        this.totMinutes = totalSeconds/60;

        long days = totalSeconds / (24 * 60 * 60);
        totalSeconds %= (24 * 60 * 60);
        long hours = totalSeconds / (60 * 60);
        totalSeconds %= (60 * 60);
        long minutes = totalSeconds / 60;

        this.days=days;
        this.hours=hours;
        this.minutes=minutes;
    }

    /**
     * To create an instance given minutes, hours and days as values
     * @param minutes value
     * @param hours value
     * @param days value
     */
    public TimeFormatter(long minutes, long hours, long days) {
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;

        this.totMinutes=(minutes)+(hours*60)+(days*24*60);
    }

    /**
     * To add at the current duration another one
     * @param e duration to add
     */
    public void addTimeFormatter(TimeFormatter e){
        totMinutes= totMinutes+ e.getTotMinutes();
        updateAttributes();
    }

    /**
     * To subtract at the current duration another one
     * @param e duration to subtract
     */
    public void subtractTimeFormatter(TimeFormatter e){
        totMinutes= totMinutes- e.getTotMinutes();
        updateAttributes();
    }

    /**
     * To update the value of minutes, hours and days according to the value of totMinutes
     */
    void updateAttributes(){
        long remaningMinutes;

        //check the new values associated to minutes, hours and days
        days= totMinutes / (60*24);
        remaningMinutes= (totMinutes % (60*24));
        hours= remaningMinutes / 60;
        remaningMinutes= remaningMinutes % 60;
        minutes=remaningMinutes;

    }

    //getter and setter
    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        if(minutes< this.minutes) totMinutes-=(this.minutes-minutes);
        if(minutes> this.minutes) totMinutes+=(minutes-this.minutes);
        this.minutes = minutes;

        updateAttributes();
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        if(hours< this.hours) totMinutes-=(this.hours-hours)*60;
        if(hours> this.hours) totMinutes+=(hours-this.hours)*60;
        this.hours = hours;

        updateAttributes();
    }

    public long getTotMinutes() {
        return totMinutes;
    }

    public void setTotMinutes(long totMinutes) {
        this.totMinutes = totMinutes;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    /**
     * To print the duration as minutes, hours and days
     * @return duration formatted as a string
     */
    @Override
    public String toString() {
        String formattedDuration="";
        if(days!=0){
            formattedDuration= formattedDuration.concat(days+" d ");
        }
        if(hours!= 0){
            formattedDuration= formattedDuration.concat(hours+" h ");
        }
        if(minutes!= 0){
            formattedDuration= formattedDuration.concat(minutes+" min");
        }
        return formattedDuration;
    }

    public String toStringAsTime() {
        String formattedDuration="";
        formattedDuration= formattedDuration.concat(hours+" : ");
        formattedDuration= formattedDuration.concat(String.format("%02d", minutes));
        return formattedDuration;
    }
}
