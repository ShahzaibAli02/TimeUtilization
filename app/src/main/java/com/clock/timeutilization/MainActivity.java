package com.clock.timeutilization;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clock.timeutilization.Adapter.ActivityAdapter;
import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.SharedPref.SharedPref;
import com.clock.timeutilization.Widget.WidgetProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnActivityName,btnshowRecords;
    private GridView list_activites;
    ArrayList<ModelActivity> activities=new ArrayList<>();
    public ActivityAdapter adapter;
    private    TextView txtviewActivityDuration,textviewActivityName,txtmfbelow,txtmfabove,txtpoints,txtTotalActivitesDuration,txtviewTotalActivityDuration,txtThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activities=SharedPref.getList(getActivity());
        init();
        SharedPref.setisMainRunning(getActivity(),true);
        if(SharedPref.getFirstLaunch(getActivity()))
        {
            setAlarm();

        }
        UpdateWidget();
    }
    public  void UpdateWidget()
    {


        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
    private void init()
    {

        btnActivityName=findViewById(R.id.btnActivityName);
        list_activites=findViewById(R.id.list_activites);
        txtviewActivityDuration=findViewById(R.id.txtviewActivityDuration);
        textviewActivityName=findViewById(R.id.textviewActivityName);
        txtmfbelow=findViewById(R.id.txtmfbelow);
        txtviewTotalActivityDuration=findViewById(R.id.txtviewTotalActivityDuration);
        txtTotalActivitesDuration=findViewById(R.id.txtTotalActivitesDuration);
        txtThreshold=findViewById(R.id.txtthreshold);
        btnshowRecords=findViewById(R.id.btnshowRecords);

        txtmfabove=findViewById(R.id.txtmfabove);
        txtpoints=findViewById(R.id.txtpoints);
        btnActivityName.setOnClickListener(this);
        btnshowRecords.setOnClickListener(this);
        adapter=new ActivityAdapter(this,activities);
        list_activites.setAdapter(adapter);
        list_activites.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                if ( view.hasFocus()){
                    view.clearFocus(); //we can put it inside the second if as well, but it makes sense to do it to all scraped views
                    //Optional: also hide keyboard in that case
                    if ( view instanceof EditText) {
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        adapter.adapterhandler = new AdapterHandler()
        {
            @Override
            public void updateTotalDailyActivityDurationText(String text)
            {
                MainActivity.this.txtviewActivityDuration.setText(text);


            }

            @Override
            public void updateActivityNameText(String text) {
                MainActivity.this.textviewActivityName.setText(text);
            }

            @Override
            public void updateDailyPointsText(String text, int back,int txt) {
                MainActivity.this.txtpoints.setText(text);
                MainActivity.this.txtpoints.setBackgroundColor(back);
                MainActivity.this.txtpoints.setTextColor(txt);
            }

            @Override
            public void updateMfBelowText(String text) {
                MainActivity.this.txtmfbelow.setText(text);
            }

            @Override
            public void updateMfaboveText(String text) {
                MainActivity.this.txtmfabove.setText(text);
            }

            @Override
            public void updateTotalActivitiesDurationText(String text)
            {
                MainActivity.this.txtTotalActivitesDuration.setText(text);
            }

            @Override
            public void updateTotalActivityDurationText(String text)
            {
                MainActivity.this.txtviewTotalActivityDuration.setText(text);
            }

            @Override
            public void updatethresholdText(String text) {
                MainActivity.this.txtThreshold.setText(text);
            }

            @Override
            public void updateList() {

                activities=SharedPref.getList(getActivity());
                adapter=new ActivityAdapter(MainActivity.this,activities);
                list_activites.setAdapter(adapter);
                list_activites.setRecyclerListener(new AbsListView.RecyclerListener() {
                    @Override
                    public void onMovedToScrapHeap(View view) {
                        if ( view.hasFocus()){
                            view.clearFocus(); //we can put it inside the second if as well, but it makes sense to do it to all scraped views
                            //Optional: also hide keyboard in that case
                            if ( view instanceof EditText) {
                                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    }
                });
            }
        };
    }



    public  void setAlarm()
    {

        AlarmManager objAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar objCalendar = Calendar.getInstance();
        objCalendar.set(Calendar.HOUR_OF_DAY, 23);
        objCalendar.set(Calendar.MINUTE, 59);
        objCalendar.set(Calendar.SECOND, 40);
        Intent alamShowIntent = new Intent(this,saveActivtity.class);
        PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0,alamShowIntent,0 );
        objAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        SharedPref.setFirstLaunch(MainActivity.this,false);
    }
    @Override
    public void onClick(View v)
    {

        if(v.getId()==R.id.btnActivityName)
        {
            AddActivity();
            return;
        }
        if(v.getId()==R.id.btnshowRecords)
        {
            startActivity(new Intent(this,Records.class));

        }

    }
    private void AddActivity()
    {

        AlertDialog.Builder DialogBox= new AlertDialog.Builder(this);
        DialogBox.setMessage("Enter Activity Name");
        final EditText editTextMsg=new EditText(this);
        DialogBox.setView(editTextMsg);
        DialogBox.setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {


                if(!TextUtils.isEmpty(editTextMsg.getText()))
                {
                    ModelActivity modelActivity = new ModelActivity();
                    modelActivity.setActivityName(editTextMsg.getText().toString());
                    SharedPref.updatlist(getActivity(),modelActivity,0);
                    activities.add(modelActivity);
                    getActivity().finish();
                    getActivity().startActivity(getActivity().getIntent());

                }

            }
        });
        DialogBox.setNegativeButton("Cancel",null);
        DialogBox.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.ondestroy(MainActivity.this);
    }

    public Activity getActivity()
    {
        return  MainActivity.this;
    }
}

