package com.clock.timeutilization;

import android.graphics.Color;
import android.widget.GridView;
import android.widget.ListView;

public abstract class AdapterHandler
{
    public void updateTotalDailyActivityDurationText(String text) {}
    public void updateDailyPointsText(String text, int clr,int txt) {}
    public void updateMfBelowText(String text) {}
    public void updateMfaboveText(String text) {}
    public void updateActivityNameText(String text) {}
    public void updateTotalActivitiesDurationText(String text) {}
    public void updateTotalActivityDurationText(String text) {}
    public void updatethresholdText(String text) {}
    public void updateList() {}

    public GridView getlistview() {
        return null;
    }
}
