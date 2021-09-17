package com.clock.timeutilization.SharedPref;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.app.ActivityCompat;

import com.clock.timeutilization.Adapter.ActivityAdapter;
import com.clock.timeutilization.Model.ModelActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SharedPref
{
    public static  void updatlist(Context ac,ModelActivity toUpdate, int code)
    {

        ArrayList<ModelActivity> fvrtList = getList(ac);
        boolean isRepeat=false;
        for(int i=0;i<fvrtList.size();i++)
        {
            ModelActivity Fvrt=fvrtList.get(i);
            if(Fvrt.getID().equals(toUpdate.getID()))
            {
                isRepeat=true;
                if(code==-1)
                {
                    fvrtList.remove(i);
                    break;
                }
                if(code==1)
                {
                    fvrtList.set(i,toUpdate);
                    break;
                }
            }
        }
        if(!isRepeat)
            fvrtList.add(toUpdate);

        SharedPreferences sharedPreferences = ac.getSharedPreferences("list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(fvrtList);
        editor.putString("list", json);
        editor.apply();
    }


    public  static  void ResetActivities(Context ac,ArrayList<ModelActivity> newList)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("list", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(newList);
        editor.putString("list", json);
        editor.apply();
    }
    public  static  void setToResumeID(Context ac,String  Id)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("toResume", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("toResume",Id);
        editor.apply();

    }
    public  static  void setFirstLaunch(Context ac,Boolean  val)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("firstLaunch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstLaunch",val);
        editor.apply();

    }
    public  static  Boolean getFirstLaunch(Context ac)
    {
      SharedPreferences sharedPreferences = ac.getSharedPreferences("firstLaunch", Context.MODE_PRIVATE);
      return   sharedPreferences.getBoolean("firstLaunch",true);
    }

    public  static  String  getToResumeID(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("toResume", Context.MODE_PRIVATE);
        String toResume = sharedPreferences.getString("toResume", null);
       return toResume;

    }

    public  static  void storeTotalTime(Context ac,long time)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("totalTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("totalTime",time);
        editor.apply();

    }
    public  static  long getTotalTime(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("totalTime", Context.MODE_PRIVATE);
        return  sharedPreferences.getLong("totalTime", 0);
    }



    public static void StoreDailyRecords(Context ac,ArrayList<ArrayList<ModelActivity>> toSave)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("RecordList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toSave);
        editor.putString("RecordList", json);
        editor.apply();
    }

    public static ArrayList<ArrayList<ModelActivity>> getRecordList(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("RecordList",  Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("RecordList", null);
        Type type = new TypeToken<ArrayList<ArrayList<ModelActivity>>>() {}.getType();
        ArrayList<ArrayList<ModelActivity>> FvrtList = gson.fromJson(json, type);
        if (FvrtList == null) {
            FvrtList = new ArrayList<>();
        }
        return FvrtList;
    }



    public  static  double getpoint(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("totalpoints", Context.MODE_PRIVATE);

        String totalpoints = sharedPreferences.getString("totalpoints", "00");


        return   Double.parseDouble(totalpoints);
    }

    public  static  void storepoint(Context ac,double points)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("totalpoints", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("totalpoints", String.valueOf(points));
        editor.apply();

    }


    //////////////////////////////








    public  static  double getOldpoint(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("oldpoints", Context.MODE_PRIVATE);

        String totalpoints = sharedPreferences.getString("oldpoints", "00");


        return   Double.parseDouble(totalpoints);
    }

    public  static  void storeOldpoint(Context ac,double points)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("oldpoints", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("oldpoints", String.valueOf(points));
        editor.apply();

    }



    ////////////

    public  static  boolean getisMainRunning(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("Main", Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean("Main", false);

    }
    public  static  void setisMainRunning(Context ac,Boolean val)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("Main", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Main",val);
        editor.apply();

    }


    public  static  void saveDate(Context ac,String Date)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("Datee", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Datee", String.valueOf(Date));
        editor.apply();

    }
    public  static Date getDate(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("Datee", Context.MODE_PRIVATE);

        String date = sharedPreferences.getString("Datee", "0000");

        if(date.equals("0000"))
            return null;

        DateFormat format = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
        Date dateobj = null;
        try
        {
            dateobj = format.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
            System.out.println("PARSE ERROR");
        }
        return   dateobj;
    }
    public  static String getDateInString(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("Datee", Context.MODE_PRIVATE);

        String date = sharedPreferences.getString("Datee", null);

        try
        {
            DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
            Date dateobj = null;
            if (date != null && !date.equals("null")) {
                dateobj = format.parse(date);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            if (dateobj != null) {
                return sdf.format(dateobj);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("PARSE ERROR");
        }
        return null;
    }




    public static ArrayList<ModelActivity> getList(Context ac)
    {
        SharedPreferences sharedPreferences = ac.getSharedPreferences("list",  Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<ModelActivity>>() {}.getType();
        ArrayList<ModelActivity> FvrtList = gson.fromJson(json, type);
        if (FvrtList == null) {
            FvrtList = new ArrayList<>();
        }
        return FvrtList;
    }





}
