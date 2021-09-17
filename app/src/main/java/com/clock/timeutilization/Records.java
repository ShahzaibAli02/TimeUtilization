package com.clock.timeutilization;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListView;

import com.clock.timeutilization.Adapter.RecordsAdapter;
import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.SharedPref.SharedPref;

import java.util.ArrayList;

public class Records extends AppCompatActivity {


    ListView list_activites;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        init();
    }

    private void init()
    {

        list_activites=findViewById(R.id.list_activites);

        ArrayList<ArrayList<ModelActivity>> recordList = SharedPref.getRecordList(this);
        if(recordList.size()<1)
        {
            setContentView(R.layout.lyt_empty);
            return;
        }
        RecordsAdapter adapter=new RecordsAdapter(this,recordList);
        list_activites.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
