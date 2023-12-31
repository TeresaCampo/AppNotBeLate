package com.teresa.appnotbelate.Components;

import androidx.annotation.NonNull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeFormatter {
    private static final String TAG = "MyDebug";
    //for timeGap
    long totMinutes,minutes, hours, days;
    //forTimeOfADay
    Calendar timeAndDay;

    public TimeFormatter(Date timeAndDay) {
        Calendar tmpTimeAndDay=Calendar.getInstance();
        tmpTimeAndDay.setTime(timeAndDay);

        this.timeAndDay = tmpTimeAndDay;

    }

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
        //time of a day
       Calendar calendar=Calendar.getInstance();
       calendar.set(Calendar.HOUR_OF_DAY,(int) hours); // 15 for 3 PM
       calendar.set(Calendar.MINUTE, (int)minutes); // 30 minutes
       this.timeAndDay= calendar;
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
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,(int) hours); // 15 for 3 PM
        calendar.set(Calendar.MINUTE, (int)minutes); // 30 minutes
        this.timeAndDay= calendar;
    }

    /**
     * To add at the current duration another one
     * @param e duration to add
     */
    public void addTimeFormatter(TimeFormatter e){
        //time of a day
        timeAndDay.add(Calendar.HOUR, e.getTimeAndDay().get(Calendar.HOUR));
        timeAndDay.add(Calendar.MINUTE, e.getTimeAndDay().get(Calendar.MINUTE));
        //timeGap
        totMinutes= totMinutes+ e.getTotMinutes();
        updateAttributes();
    }

    /**
     * To subtract at the current duration another one
     * @param e duration to subtract
     */
    public void subtractTimeFormatter(TimeFormatter e){
        //time of a day
        timeAndDay.add(Calendar.HOUR, -e.getTimeAndDay().get(Calendar.HOUR));
        timeAndDay.add(Calendar.MINUTE, -e.getTimeAndDay().get(Calendar.MINUTE));
        //timeGap
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
        //time of a day
        timeAndDay.set(Calendar.MINUTE, (int) minutes);
        //timeGap
        if(minutes< this.minutes) totMinutes-=(this.minutes-minutes);
        if(minutes> this.minutes) totMinutes+=(minutes-this.minutes);
        this.minutes = minutes;

        updateAttributes();
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        //time of a day
        timeAndDay.set(Calendar.HOUR_OF_DAY, (int) hours);
        //timeGap
        if(hours< this.hours) totMinutes-=(this.hours-hours)*60;
        if(hours> this.hours) totMinutes+=(hours-this.hours)*60;
        this.hours = hours;

        updateAttributes();
    }

    public long getTotMinutes() {
        return totMinutes;
    }

    public long getDays() {
        return days;
    }

    @NonNull
    @Override
    public TimeFormatter clone(){
        return new TimeFormatter(this.timeAndDay.getTime());
    }

    /**
     * To print the duration as minutes, hours and days
     * @return duration formatted as a string
     */
    @NonNull
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, EEEE dd", Locale.ENGLISH);
        return sdf.format(timeAndDay.getTime());

    }

    public void setDayToday() {
        Calendar calendar= Calendar.getInstance();
        this.timeAndDay.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR));
    }
    public void setDayTomorrow() {
        timeAndDay.add(Calendar.DAY_OF_YEAR, 1);
    }

    public Calendar getTimeAndDay() {
        return timeAndDay;
    }

}
