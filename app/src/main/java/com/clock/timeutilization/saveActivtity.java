package com.clock.timeutilization;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.SharedPref.SharedPref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class saveActivtity extends AppCompatActivity {
    long   timeElpasedAfterAppClosed=00;
    String toResumeID=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_save_activtity);
         storeDailyPoints();
    }

    private void storeDailyPoints()
    {

        if(SharedPref.getList(getActivity()).size()<1)
        {
            if(SharedPref.getisMainRunning(getActivity()))
            {
                ActivityCompat.finishAffinity(this);
                startActivity(new Intent(this,MainActivity.class));
            }
            else
                ActivityCompat.finishAffinity(this);

            return;
        }

        String dateInString = SharedPref.getDateInString(getActivity());
        if(dateInString!=null)
        {
            timeElpasedAfterAppClosed=getTimeDifference(dateInString);
            toResumeID=SharedPref.getToResumeID(getActivity());
            System.out.println("TIME DIFFERENCE : "+timeElpasedAfterAppClosed);
            ArrayList<ModelActivity> list = SharedPref.getList(getActivity());
            ArrayList<ModelActivity> NewList=new ArrayList<>();
            for(ModelActivity Data:list)
            {
               if(Data.getID().equals(toResumeID))
               {
                   Data.setTotalActivityTime(String.valueOf(Long.parseLong(Data.getTotalActivityTime())+timeElpasedAfterAppClosed));
                   Data.setTotalTimeSpent(String.valueOf(Long.parseLong(Data.getTotalTimeSpent())+timeElpasedAfterAppClosed));
                   long totalActivitySeconds=Long.parseLong(Data.getTotalTimeSpent())/1000;

                   System.out.println("Activity Seconds : "+totalActivitySeconds);
                   Double points=calculatePoints(totalActivitySeconds,Data);
                   Data.setPointsAccumalted(String.valueOf(points));
                   timeElpasedAfterAppClosed=00;
                   toResumeID=null;
               }
                NewList.add(Data);
            }

            ArrayList<ArrayList<ModelActivity>> recordList = SharedPref.getRecordList(getActivity());
            recordList.add(NewList);
            SaveList(recordList);
            clear(NewList);
        }
        else
        {
            ArrayList<ArrayList<ModelActivity>> recordList = SharedPref.getRecordList(getActivity());
            ArrayList<ModelActivity> list = SharedPref.getList(getActivity());
            recordList.add(list);
            SaveList(recordList);

            clear(list);

        }


        clearSharedPref();
    }

    private void clearSharedPref()
    {
        SharedPref.storeTotalTime(getActivity(),00);
        SharedPref.storepoint(getActivity(),00);
        SharedPref.saveDate(getActivity(),getCurrentDateTime());
    }

    public static String getCurrentDateTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    private Double calculatePoints(long totalActivitySeconds,ModelActivity data)
    {

        final  double threshold = Double.parseDouble(data.getThreshold());
        final double MfBelow=Double.parseDouble(data.getMultipleFactorBelow());
        final double MfAbove=Double.parseDouble(data.getMultipleFactorAbove());
        Double toReturn;
        if(totalActivitySeconds<=(threshold*3600))
        {
            toReturn=+(totalActivitySeconds/(double)3600)*MfBelow;
        }
        else
        {
            toReturn=+(threshold*MfBelow)+((((totalActivitySeconds/(double)3600))-threshold)*MfAbove);
        }
        return  toReturn;
    }

    public  void SaveList(ArrayList<ArrayList<ModelActivity>> list)
    {
        SharedPref.StoreDailyRecords(getActivity(),list);
    }

    private void clear(ArrayList<ModelActivity> list)
    {

        ArrayList<ModelActivity> NewList=new ArrayList<>();
        for(ModelActivity ac:list)
        {
            ac.setTotalTimeSpent("00");
            ac.setPointsAccumalted("00");
            String myFormat = "MM-dd-yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            ac.setUpdatedDate(sdf.format(new Date()));
            NewList.add(ac);
        }
        Toast.makeText(saveActivtity.this,"Reset Done",Toast.LENGTH_SHORT).show();
        SharedPref.ResetActivities(getActivity(),NewList);

        System.out.println("MAIN RUNNING : "+SharedPref.getisMainRunning(getActivity()));


        if(SharedPref.getisMainRunning(getActivity()))
        {
            ActivityCompat.finishAffinity(this);
            startActivity(new Intent(this,MainActivity.class));
        }
        else
            ActivityCompat.finishAffinity(this);

    }

    public long getTimeDifference(String RequestDate)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            Date date1 = sdf.parse(currentDateandTime);  //This is Current Date

            Date date2 = sdf.parse(RequestDate);  //This Is Request Data

            long diff = date1.getTime()-date2.getTime();  //Time Difference Between These Dates
            // long seconds = diff / 1000; //Converting Time Diffence From Milis to Seconds
            //long minutes = seconds / 60;  //Converting Seconds To Minutes (Because We Need Time Differnece In Minutes)

            return diff;

        } catch (ParseException e)
        {

            e.printStackTrace();
            return 0;
        }

    }

    private Activity getActivity() {

        return  saveActivtity.this;
    }
}
