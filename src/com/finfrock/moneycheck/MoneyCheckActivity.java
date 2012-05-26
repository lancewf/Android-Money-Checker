package com.finfrock.moneycheck;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.R.layout;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MoneyCheckActivity extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, AllowanceActivity.class);

        spec = tabHost.newTabSpec("allotted").setIndicator("Allotted")
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, AddEntryActivity.class);
        spec = tabHost.newTabSpec("addentry").setIndicator("Add Entry")
                      .setContent(intent);
        
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, MonthlyViewActivity.class);
        spec = tabHost.newTabSpec("monthlyView").setIndicator("View")
                      .setContent(intent);
        
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
    }
}