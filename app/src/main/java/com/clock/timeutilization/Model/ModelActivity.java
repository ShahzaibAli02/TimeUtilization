package com.clock.timeutilization.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModelActivity
{

    public String getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    private String updatedDate;
    private String Threshold;
    private String MultipleFactorAbove;
    private String MultipleFactorBelow;
    private String TotalTimeSpent;
    public String getTotalActivityTime()
    {
        return TotalActivityTime;
    }
    public void setTotalActivityTime(String totalActivityTime) {
        TotalActivityTime = totalActivityTime;
    }

    private String TotalActivityTime;

    public String getPointsAccumalted() {
        return PointsAccumalted;
    }

    public void setPointsAccumalted(String pointsAccumalted) {
        PointsAccumalted = pointsAccumalted;
    }

    private String PointsAccumalted;



    public  ModelActivity()
    {

        setThreshold("0");
        setID(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        setMultipleFactorAbove("0");
        setMultipleFactorBelow("0");
        setTotalTimeSpent("00");
        setPointsAccumalted("0.0");
        setTotalActivityTime("00");

        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        setUpdatedDate(sdf.format(new Date()));

    }
    public String getThreshold() {
        return Threshold;
    }

    public void setThreshold(String threshold) {
        Threshold = threshold;
    }

    public String getMultipleFactorAbove() {
        return MultipleFactorAbove;
    }

    public void setMultipleFactorAbove(String multipleFactorAbove) {
        MultipleFactorAbove = multipleFactorAbove;
    }

    public String getMultipleFactorBelow() {
        return MultipleFactorBelow;
    }

    public void setMultipleFactorBelow(String multipleFactorBelow) {
        MultipleFactorBelow = multipleFactorBelow;
    }

    public String getTotalTimeSpent() {
        return TotalTimeSpent;
    }

    public void setTotalTimeSpent(String totalTimeSpent) {
        TotalTimeSpent = totalTimeSpent;
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ActivityName;
    private String ID;


}
