package com.clock.timeutilization.Widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.R;
import com.clock.timeutilization.SharedPref.SharedPref;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Created by mustafa.urgupluoglu on 02/10/17.
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String LIST_ITEM_CLICKED_ACTION = "LIST_ITEM_CLICKED_ACTION";
    private static final String REFRESH_WIDGET_ACTION = "REFRESH_WIDGET_ACTION";
    public static final String EXTRA_CLICKED_FILE = "EXTRA_CLICKED_FILE";

    public static final String ACTION_WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";


    Context context;
    int[] appWidgetIds;
    AppWidgetManager appWidgetManager;
    double oldPoints=00;


    /**
     * "This is called to update the App Widget at intervals defined by the updatePeriodMillis attribute in the
     * AppWidgetProviderInfo (see Adding the AppWidgetProviderInfo Metadata above). This method is also called when the
     * user adds the App Widget, so it should perform the essential setup, such as define event handlers for Views and
     * start a temporary Service, if necessary. However, if you have declared a configuration Activity, this method is
     * not called when the user adds the App Widget, but is called for the subsequent updates. It is the responsibility
     * of the configuration Activity to perform the first update when configuration is done. (See Creating an App Widget
     * Configuration Activity below.)"
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context=context;
        this.appWidgetIds=appWidgetIds;
        this.appWidgetManager=appWidgetManager;
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            setRepeating();
             long totalDurationOfallActivites= SharedPref.getTotalTime(getContext());
            double previousActivityPoints=SharedPref.getpoint(getContext());
            String dateInString = SharedPref.getDateInString(getContext());
            if(dateInString!=null)
            {
                long timeElpasedAfterAppClosed=getTimeDifference(dateInString);
                totalDurationOfallActivites=totalDurationOfallActivites+timeElpasedAfterAppClosed;
                String  toResumeID=SharedPref.getToResumeID(getContext());
                ModelActivity activity=getModelofRunnindID(toResumeID);
                if(activity!=null)
                {
                    long totalActivityTime = timeElpasedAfterAppClosed+Long.parseLong(activity.getTotalActivityTime());
                    long totalActivityDailyTime = timeElpasedAfterAppClosed+Long.parseLong(activity.getTotalTimeSpent());
                    String formateTotalTime = formate(totalActivityTime);
                    String formateTotalDailyTime = formate(timeElpasedAfterAppClosed+Long.parseLong(activity.getTotalTimeSpent()));

                    rv.setTextViewText(R.id.textviewActivityName,activity.getActivityName());
                    rv.setTextViewText(R.id.txtviewActivityDuration,formateTotalTime);
                    rv.setTextViewText(R.id.txtviewTotalActivityDuration,formateTotalDailyTime);


                    final  double threshold = Double.parseDouble(activity.getThreshold());
                    final double MfBelow=Double.parseDouble(activity.getMultipleFactorBelow());
                    final double MfAbove=Double.parseDouble(activity.getMultipleFactorAbove());
                    long totalActivitySeconds=totalActivityDailyTime/1000;
                    double currentpoints=0.0;
                    if(totalActivitySeconds<=(threshold*3600))
                    {
                        currentpoints=(totalActivitySeconds/(double)3600)*MfBelow;
                    }
                    else
                    {
                        currentpoints=(threshold*MfBelow)+((((totalActivitySeconds/(double)3600))-threshold)*MfAbove);
                    }

                    DecimalFormat df = new DecimalFormat("##.####");
                    rv.setTextViewText(R.id.txtActivitypoints, df.format(currentpoints));
                    long l = Long.parseLong(activity.getTotalActivityTime());
                    l=l/1000;
                    if(l<=(threshold*3600))
                    {
                        previousActivityPoints-=(l/(double)3600)*MfBelow;
                    }
                    else
                    {
                        previousActivityPoints-=(threshold*MfBelow)+((((l/(double)3600))-threshold)*MfAbove);
                    }
                    rv.setTextViewText(R.id.txtpoints, df.format(currentpoints + previousActivityPoints));
                    int txt=0;
                    if(currentpoints>0)
                        txt= Color.parseColor("#1f9906");
                    else
                        txt=Color.RED;

                    rv.setTextColor(R.id.txtActivitypoints,txt);

                    int back=0;
                    oldPoints=SharedPref.getOldpoint(context);
                    if(oldPoints<currentpoints+previousActivityPoints)
                    {
                        back=Color.parseColor("#03f0fc");
                    }
                    else
                    {
                        back=Color.parseColor("#f7a714");
                    }
                    rv.setInt(R.id.txtpoints, "setBackgroundColor",back);
                    int txt2=0;
                    if(currentpoints+previousActivityPoints>=0)
                        txt2= Color.parseColor("#1f9906");
                    else
                        txt2=Color.RED;
                    rv.setTextColor(R.id.txtpoints,txt2);
                    SharedPref.storeOldpoint(context,currentpoints+previousActivityPoints);

                }
                else
                {
                    rv.setTextViewText(R.id.textviewActivityName,"Not Found Activity ");
                    rv.setTextViewText(R.id.txtviewTotalActivityDuration,"00:00:00");
                    rv.setTextViewText(R.id.txtviewActivityDuration,"00:00:00");
                    rv.setTextViewText(R.id.txtpoints,String.valueOf("00"));
                }

            }
            else
            {
                rv.setTextViewText(R.id.txtviewActivityDuration,"00:00:00");
                rv.setTextViewText(R.id.textviewActivityName,"No Activity Running");
                rv.setTextViewText(R.id.txtviewTotalActivityDuration,"00:00:00");
                rv.setTextViewText(R.id.txtpoints,String.valueOf("00"));
            }


            Intent refreshIntent = new Intent(context, WidgetProvider.class);
            refreshIntent.setAction(WidgetProvider.REFRESH_WIDGET_ACTION);
            refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            refreshIntent.setData(Uri.parse(refreshIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.btnRef, refreshPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    private ModelActivity getModelofRunnindID(String toResumeID)
    {

        ArrayList<ModelActivity> list = SharedPref.getList(getContext());
        for(ModelActivity ac:list)
        {
            if(ac.getID().equals(toResumeID))
                return ac;
        }

        return  null;
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


    private Context getContext() {

        return context;
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
    private void setRepeating()
    {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarm = new Intent(context, WidgetProvider.class);
        alarm.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); // Set appwidget update action
        alarm.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds); // Set appwidget ids to be updated
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm,
                PendingIntent.FLAG_CANCEL_CURRENT); // get the broadcast pending intent
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000, pendingIntent); // set repeat alarm
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
      if(intent.getAction()==REFRESH_WIDGET_ACTION)
      {
          UpdateWidget();
      }


    }
    public  void UpdateWidget()
    {


        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }



}
