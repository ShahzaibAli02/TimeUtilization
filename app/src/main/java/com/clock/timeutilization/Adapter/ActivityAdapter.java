package com.clock.timeutilization.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.clock.timeutilization.AdapterHandler;
import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.R;
import com.clock.timeutilization.SharedPref.SharedPref;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ActivityAdapter extends BaseAdapter
{
    private static final String DATE_FORMAT = "dd-MMMM-yyyy";;
    ArrayList<ModelActivity> data;
    Context context;
    Handler timerHandler = new Handler();
    Runnable timerRunnable;
    boolean blink=true;
    public AdapterHandler adapterhandler;
    String toResumeID=null;
    /////////////////////////////////////////////////////
    long time=0;
    int i=0;
    boolean isResume=false;
    long TotalActivityTime=0;
    long totalDurationOfallActivites=0;
    double previousActivityPoints=0.0;
    double currentpoints=0.0;
    ModelActivity currentRunningact=null;
     TextView textViewTotalTimeofCurrentAct,textViewDailyTimeofCurrentAct;
    long timeElpasedAfterAppClosed=00;
    long timeElpasedAfterAppClosed2=00;




    int index=0;
    static  String id;




    ///////////////////////////////////////////////////////

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
    public static String getCurrentDateTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    public ActivityAdapter(Context context, ArrayList<ModelActivity> data)
    {
        this.context = context;
        this.data = data;
        totalDurationOfallActivites=SharedPref.getTotalTime(getActivity());
        previousActivityPoints=SharedPref.getpoint(getActivity());
        String dateInString = SharedPref.getDateInString(getActivity());
        if(dateInString!=null)
        {
                timeElpasedAfterAppClosed2=timeElpasedAfterAppClosed=getTimeDifference(dateInString);
                totalDurationOfallActivites=totalDurationOfallActivites+timeElpasedAfterAppClosed;
                toResumeID=SharedPref.getToResumeID(getActivity());
                isResume=true;
        }
    }
    public  void ondestroy(Activity ac)
    {
             if(currentRunningact!=null)
             {
                 currentRunningact.setTotalTimeSpent(String.valueOf(time));
                 currentRunningact.setPointsAccumalted(String.valueOf(currentpoints));
                 System.out.println("CURRENT POINTS : "+currentpoints);
                 currentRunningact.setTotalActivityTime(String.valueOf(TotalActivityTime));
                 SharedPref.updatlist(ac,currentRunningact,1);
                 SharedPref.saveDate(getActivity(),getCurrentDateTime());
                 SharedPref.setToResumeID(ac,currentRunningact.getID());
                 SharedPref.setisMainRunning(getActivity(),true);
             }
             else
                 SharedPref.setisMainRunning(getActivity(),false);
        SharedPref.storeTotalTime(ac,totalDurationOfallActivites);
        SharedPref.storepoint(ac,previousActivityPoints+currentpoints);
    }
    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }




    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder")
        View row = inflater.inflate(R.layout.lyt_activity, parent, false);
        final ModelActivity Data = data.get(position);

        try
        {
            final Activity ac= (Activity) context;
            TextView txtName=row.findViewById(R.id.txtName);
            Button btnStart=row.findViewById(R.id.btnStart);
            Button btnStop=row.findViewById(R.id.btnStop);
            Button btnDelete=row.findViewById(R.id.btnDelet);
            final EditText editTextThreshold=row.findViewById(R.id.edttxtThreshold);
            final EditText editMulFactorAbove=row.findViewById(R.id.edttxtMulFactAbv);
            final EditText editMulFactorBellow=row.findViewById(R.id.edttxtMulFactBelow);
            final TextView txtTotalTime=row.findViewById(R.id.txtTotalTime);
            final TextView txtDailyTime=row.findViewById(R.id.txtDailyTime);
            txtName.setText(Data.getActivityName());
            editMulFactorAbove.setText(Data.getMultipleFactorAbove());
            editMulFactorBellow.setText(Data.getMultipleFactorBelow());
            editTextThreshold.setText(Data.getThreshold());
            try{  txtDailyTime.setText(formate(Long.parseLong(Data.getTotalTimeSpent())));} catch (NumberFormatException ignored){}
            try{  txtTotalTime.setText(formate(Long.parseLong(Data.getTotalActivityTime())));} catch (NumberFormatException ignored){}
            btnStart.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    if(notEmpty(editMulFactorAbove,editMulFactorBellow,editTextThreshold))
                    {

                        Data.setThreshold(editTextThreshold.getText().toString());
                        Data.setMultipleFactorBelow(editMulFactorBellow.getText().toString());
                        Data.setMultipleFactorAbove(editMulFactorAbove.getText().toString());
                        data.set(position,Data);
                        SharedPref.updatlist(ac,Data,1);
                        if(currentRunningact!=null)
                            SaveOldState(position,Data,false);
                       ModelActivity Data=data.get(position);
                       try
                       {
                           adapterhandler.updateActivityNameText(Data.getActivityName());
                           adapterhandler.updateMfaboveText(Data.getMultipleFactorAbove());
                           adapterhandler.updateMfBelowText(Data.getMultipleFactorBelow());
                           adapterhandler.updatethresholdText(Data.getThreshold());
                           time=Long.parseLong(Data.getTotalTimeSpent());
                           TotalActivityTime=Long.parseLong(Data.getTotalActivityTime());
                           currentRunningact=Data;
                           startTimer(txtTotalTime,txtDailyTime);
                       }
                       catch (Exception e)
                       {
                           Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                           return;
                       }

                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    if(currentRunningact!=null)
                    {
                        toResumeID=currentRunningact.getID();
                        if(currentRunningact.getID().equals(Data.getID()))
                            SaveOldState(position,Data,false);
                    }
                    SharedPref.updatlist(ac,Data,-1);
                    data.remove(position);
                    //ActivityAdapter.this.notifyDataSetChanged();
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());
                }
            });

            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(currentRunningact!=null)
                    {
                        if(currentRunningact.getID().equals(Data.getID()))
                              SaveOldState(position,Data,true);
                    }
                }
            });
            if(toResumeID!=null)
            {
                if(Data.getID().equals(toResumeID))
                {
                   if(i!=3)
                   {
                       Data.setTotalActivityTime(String.valueOf(Long.parseLong(Data.getTotalActivityTime())+timeElpasedAfterAppClosed));
                       Data.setTotalTimeSpent(String.valueOf(Long.parseLong(Data.getTotalTimeSpent())+timeElpasedAfterAppClosed));
                       try{  txtDailyTime.setText(formate(Long.parseLong(Data.getTotalTimeSpent())));} catch (NumberFormatException ignored){}
                       try{  txtTotalTime.setText(formate(Long.parseLong(Data.getTotalActivityTime())));} catch (NumberFormatException ignored){}
                       ++i;
                       timeElpasedAfterAppClosed=00;
                       SharedPref.setToResumeID(getActivity(),null);
                       SharedPref.saveDate(getActivity(),null);
                       btnStart.performClick();
                   }
                   else
                   {
                       toResumeID=null;
                   }

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return row;
    }

    private boolean isSame(ModelActivity data)
    {
            if(currentRunningact==null)
                return false;
            else
                return currentRunningact.getID().equalsIgnoreCase(data.getID()) && currentRunningact.getThreshold().equals(data.getThreshold()) && currentRunningact.getMultipleFactorAbove().equals(data.getMultipleFactorAbove()) && currentRunningact.getMultipleFactorBelow().equals(data.getMultipleFactorBelow());
    }

    private void SaveOldState(int position, ModelActivity Modeldata,boolean isStop)
    {

        long oldtime = Long.parseLong(currentRunningact.getTotalTimeSpent());
        currentRunningact.setTotalTimeSpent(String.valueOf(time));
        currentRunningact.setPointsAccumalted(String.valueOf(currentpoints));
        currentRunningact.setTotalActivityTime(String.valueOf(TotalActivityTime));
        if(Modeldata.getID().equalsIgnoreCase(currentRunningact.getID()))
        {
            Modeldata.setTotalTimeSpent(String.valueOf(time));
            Modeldata.setPointsAccumalted(String.valueOf(currentpoints));
            data.set(position,Modeldata);
        }
        previousActivityPoints=previousActivityPoints+currentpoints;
        SharedPref.updatlist(getActivity(),currentRunningact,1);
        SharedPref.storeTotalTime(getActivity(),totalDurationOfallActivites);
        resetVals(isStop);
    }

    private void resetVals(boolean isStop)
    {
        currentRunningact=null;
        textViewTotalTimeofCurrentAct=null;
        time=0;
        currentpoints=0;
        TotalActivityTime=0;
        if(timerRunnable!=null)
        {
            timerHandler.removeCallbacks(timerRunnable);
            timerRunnable=null;
            if(!isStop)
            {
                adapterhandler.updateTotalDailyActivityDurationText("00:00:00");
                adapterhandler.updateMfBelowText("0");
                adapterhandler.updateMfaboveText("0");
                adapterhandler.updatethresholdText("0");
                adapterhandler.updateActivityNameText("");
                adapterhandler.updateDailyPointsText("0",Color.WHITE,Color.BLACK);
                adapterhandler.updateTotalActivityDurationText("0");
            }

          //  adapterhandler.updateTotalActivitiesDurationText("00:00:00");

        }}
    Activity getActivity()
    {
        return (Activity) context;
    }
    private void startTimer(final TextView textViewTotalTimeofCurrentAct, final TextView textViewDailyTimeofCurrentAct)
    {
        final  double threshold = Double.parseDouble(currentRunningact.getThreshold());
        final double MfBelow=Double.parseDouble(currentRunningact.getMultipleFactorBelow());
        final double MfAbove=Double.parseDouble(currentRunningact.getMultipleFactorAbove());
        if(!isResume)
        {
            time=time-1000;
            totalDurationOfallActivites=totalDurationOfallActivites-1000;
            TotalActivityTime=TotalActivityTime-1000;
            long totalActivitySeconds=(time+1000)/1000;
            Double toMinus;
            if(totalActivitySeconds<=(threshold*3600))
            {
                toMinus=+(totalActivitySeconds/(double)3600)*MfBelow;
            }
            else
            {
                toMinus=+(threshold*MfBelow)+((((totalActivitySeconds/(double)3600))-threshold)*MfAbove);
            }

            previousActivityPoints=previousActivityPoints-(toMinus);
        }
        else
        {

            time=(time-timeElpasedAfterAppClosed2)-1000;
            totalDurationOfallActivites=totalDurationOfallActivites-1000;
            TotalActivityTime=TotalActivityTime-1000;
            long totalActivitySeconds=(time+1000)/1000;
            Double toMinus;
            if(totalActivitySeconds<=(threshold*3600))
            {
                toMinus=+(totalActivitySeconds/(double)3600)*MfBelow;
            }
            else
            {
                toMinus=+(threshold*MfBelow)+((((totalActivitySeconds/(double)3600))-threshold)*MfAbove);
            }
            previousActivityPoints=previousActivityPoints-(toMinus);
            isResume=false;
            time=time+timeElpasedAfterAppClosed2;
            timeElpasedAfterAppClosed2=00;
        }
        timerRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                time+=1000;
                totalDurationOfallActivites+=1000;
                TotalActivityTime+=1000;
                long totalActivitySeconds=time/1000;
                String formate = formate(time);
                String formate2 = formate(TotalActivityTime);
                double oldpoints=currentpoints+previousActivityPoints;
                if(totalActivitySeconds<=(threshold*3600))
                {
                    currentpoints=(totalActivitySeconds/(double)3600)*MfBelow;
                }
                else
                {
                    currentpoints=(threshold*MfBelow)+((((totalActivitySeconds/(double)3600))-threshold)*MfAbove);
                }
                System.out.println("TOMIPLUS 1 "+currentpoints+" TIme = "+time);
                currentRunningact.setTotalTimeSpent(String.valueOf(time));
                currentRunningact.setPointsAccumalted(String.valueOf(currentpoints+previousActivityPoints));
                adapterhandler.updateTotalActivitiesDurationText(formate(totalDurationOfallActivites));
                adapterhandler.updateTotalDailyActivityDurationText(formate);;
                textViewTotalTimeofCurrentAct.setText(formate2);
                textViewDailyTimeofCurrentAct.setText(formate);
                int back;
                int txt;
                if(oldpoints<currentpoints+previousActivityPoints)
                {
                    back=Color.parseColor("#03f0fc");
                }
                else
                {

                    back=Color.parseColor("#f7a714");
                }
                if(currentpoints+previousActivityPoints>0)
                   txt=Color.parseColor("#1f9906");
                else
                    txt=Color.RED;
                DecimalFormat df = new DecimalFormat("##.####");
                adapterhandler.updateDailyPointsText(String.valueOf(df.format(currentpoints+previousActivityPoints)),back,txt);
                adapterhandler.updateTotalActivityDurationText(formate2);
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerRunnable.run();
    }

    private double getPReviousActiviytPointsAfterResume(ModelActivity currentRunningact)
    {

        ArrayList<ModelActivity> list = SharedPref.getList(getActivity());
        double points=0.0;
        for(ModelActivity singleData:list)
        {
            if(!currentRunningact.getID().equals(singleData.getID()))
            {
                points+=Double.parseDouble(singleData.getPointsAccumalted());
            }

        }
        return points;
    }

    private String formate(long time)
    {
        @SuppressLint("DefaultLocale")
        String format = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        return  format;
    }

    private boolean notEmpty(EditText editMulFactorAbove, EditText editMulFactorBellow, EditText editTextThreshold)
    {
        for(EditText toCheck:new EditText[]{editMulFactorAbove,editMulFactorBellow,editTextThreshold})
        {
            if(TextUtils.isEmpty(toCheck.getText()))
            {
                toCheck.setError("Required Field");
                toCheck.requestFocus();
                return false;
            }
        }
        return true;
    }


    private void showMenu(final String id)
    {

        final Activity ac= (Activity) context;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Delete");
        builder1.setMessage("Are You Sure To Delete?");
        builder1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // DO NOTHING
            }
        });
        builder1.show();

    }


}