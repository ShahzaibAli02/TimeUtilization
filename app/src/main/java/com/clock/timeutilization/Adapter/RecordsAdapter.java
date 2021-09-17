package com.clock.timeutilization.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.clock.timeutilization.Model.ModelActivity;
import com.clock.timeutilization.R;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RecordsAdapter extends BaseAdapter {
    ArrayList<ArrayList<ModelActivity>> data;
    Context context;

    public RecordsAdapter(Context context, ArrayList<ArrayList<ModelActivity>> data) {
        this.context = context;
        this.data = data;
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
    public int getViewTypeCount() {

        return getCount();
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder")
        View row = inflater.inflate(R.layout.lyt_records, parent, false);
        final ArrayList<ModelActivity> Data = data.get(position);
        try
        {

            DecimalFormat df = new DecimalFormat("##.####");
            TextView txtDate=row.findViewById(R.id.txtDate);
            TextView txttotalpoints=row.findViewById(R.id.txttotalpoints);
            txtDate.setText(Data.get(0).getUpdatedDate());
            Double totalPoints=0.0;
            LinearLayout linearLayout = row.findViewById(R.id.linearlayout);
              for(ModelActivity data:Data)
              {
                  totalPoints+=Double.parseDouble(data.getPointsAccumalted());
                  linearLayout.addView(getTextView(data));
              }
            txttotalpoints.setText(df.format(totalPoints));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return row;
    }

    private TextView getTextView(ModelActivity modelActivity)
    {


        TextView v=new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        DecimalFormat df = new DecimalFormat("##.####");
        params.setMargins(0, 10, 0, 10);
        v.setLayoutParams(params);
        v.setBackground(context.getDrawable(R.drawable.table_content_cell_bg));
        StringBuilder string=new StringBuilder();
        string.append("Name   : ").append(modelActivity.getActivityName());
        string.append("\nTotal Daily Activity Duration   : ").append(formate(Long.parseLong(modelActivity.getTotalTimeSpent())));
        string.append("\nTotal Activity Duration    : ").append(formate(Long.parseLong(modelActivity.getTotalActivityTime())));
        string.append("\nTotal Daily Activity Points   : ").append(String.valueOf(df.format(Double.parseDouble(modelActivity.getPointsAccumalted()))));
        v.setText(string);

        return v;




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


}