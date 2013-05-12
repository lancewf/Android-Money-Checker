package com.finfrock.moneycheck;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.StoreNameRetirever;
import com.finfrock.moneycheck.data.BillType;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TabHost;

public class MoneyCheckActivity extends TabActivity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    	
    	addWaitTabs();
        
        new AsyncTask<Void, Void, StartData>() {
            protected StartData doInBackground(Void... urls) {
                StoreNameRetirever storeNameRetirever = new StoreNameRetirever();
            	return new StartData(storeNameRetirever.getStoreNames(), 
            			DataStore.getInstance().getBillTypes());
            }

            protected void onPostExecute(StartData status) {
            	addTabs(status.getStoreNames(), status.getBillTypes());
            }
        }.execute();
    }
    
    private class StartData{
    	private String[] storeNames;
    	private BillType[] billTypes;
    	
    	public StartData(String[] storeNames, 
    			BillType[] billTypes){
    		this.storeNames = storeNames;
    		this.billTypes = billTypes;
    	}
    	
    	public BillType[] getBillTypes(){return billTypes;}
    	public String[] getStoreNames(){return storeNames;}
    }
    
    private void addWaitTabs(){
        TabHost tabHost = getTabHost();  // The activity TabHost
         
        tabHost.addTab(buildWaitTab(tabHost));
    }
    
    private void addTabs(String[] storeNames, BillType[] billTypes){
        TabHost tabHost = getTabHost();  // The activity TabHost
        tabHost.clearAllTabs();
        
        tabHost.addTab(buildAllowanceTab(tabHost));
        tabHost.addTab(buildAddEntryTab(tabHost, storeNames));
        tabHost.addTab(buildMonthlyViewTab(tabHost, storeNames));

        tabHost.setCurrentTab(1);
    }
    
    private TabHost.TabSpec buildWaitTab(TabHost tabHost){
    	Intent intent = new Intent().setClass(this, ProgressActivity.class);
    	TabHost.TabSpec spec = tabHost.newTabSpec("loading").setIndicator("Loading...")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildAddEntryTab(TabHost tabHost, String[] storeNames){
    	Intent intent = new Intent().setClass(this, AddEntryActivity.class);
    	intent.putExtra("storeNames", storeNames);
    	TabHost.TabSpec spec = tabHost.newTabSpec("addentry").setIndicator("Add Entry")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildMonthlyViewTab(TabHost tabHost, String[] storeNames){
    	Intent intent = new Intent().setClass(this, MonthlyViewActivity.class);
    	intent.putExtra("storeNames", storeNames);
    	TabHost.TabSpec spec = tabHost.newTabSpec("monthlyView").setIndicator("View")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildAllowanceTab(TabHost tabHost){
    	Intent intent = new Intent().setClass(this, AllowanceActivity.class);

    	TabHost.TabSpec spec = tabHost.newTabSpec("allotted").setIndicator("Allotted")
                      .setContent(intent);
        return spec;
    }
}