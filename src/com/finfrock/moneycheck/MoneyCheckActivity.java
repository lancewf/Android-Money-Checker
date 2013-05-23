package com.finfrock.moneycheck;

import java.util.ArrayList;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.BillTypeBuilder;
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
                BillTypeBuilder builder = new BillTypeBuilder();
                ArrayList<BillType> billTypes = new ArrayList<BillType>();
                String[] storeNames = new String[0];
                boolean connectionGood = true;
                try{
                	billTypes = builder.build();
                	storeNames = storeNameRetirever.getStoreNames();
                }
                catch(Exception ex){
                	connectionGood = false;
                }
                
            	return new StartData(storeNames, 
            			billTypes, connectionGood);
            }

            protected void onPostExecute(StartData status) {
            	if(status.isConnectionGood()){
            		addTabs(status.getStoreNames(), status.getBillTypes());
            	}
            	else{
            		//TODO: create a bad connection activity
            	}
            }
        }.execute();
    }
    
    private class StartData{
    	private String[] storeNames;
    	private ArrayList<BillType> billTypes;
    	private boolean connectionGood;
    	
    	public StartData(String[] storeNames, 
    			ArrayList<BillType> billTypes, 
    			boolean connectionGood){
    		this.storeNames = storeNames;
    		this.billTypes = billTypes;
    		this.connectionGood = connectionGood;
    	}
    	
    	public ArrayList<BillType> getBillTypes(){return billTypes;}
    	public String[] getStoreNames(){return storeNames;}
    	public boolean isConnectionGood(){return connectionGood;}
    }
    
    private void addWaitTabs(){
        TabHost tabHost = getTabHost();  // The activity TabHost
         
        tabHost.addTab(buildWaitTab(tabHost));
    }
    
    private void addTabs(String[] storeNames, ArrayList<BillType> billTypes){
        TabHost tabHost = getTabHost();  // The activity TabHost
        tabHost.clearAllTabs();
        
        tabHost.addTab(buildAllowanceTab(tabHost, billTypes));
        tabHost.addTab(buildAddEntryTab(tabHost, storeNames, billTypes));
        tabHost.addTab(buildMonthlyViewTab(tabHost, storeNames, billTypes));
        tabHost.addTab(buildSearchTab(tabHost, storeNames, billTypes));

        tabHost.setCurrentTab(1);
    }
    
    private TabHost.TabSpec buildWaitTab(TabHost tabHost){
    	Intent intent = new Intent().setClass(this, ProgressActivity.class);
    	TabHost.TabSpec spec = tabHost.newTabSpec("loading").setIndicator("Loading...")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildSearchTab(TabHost tabHost, String[] storeNames, 
    		ArrayList<BillType> billTypes){
    	Intent intent = new Intent().setClass(this, SearchPurchaseActivity.class);
    	intent.putExtra("storeNames", storeNames);
    	intent.putExtra("billTypes", billTypes);
    	TabHost.TabSpec spec = tabHost.newTabSpec("search").setIndicator("Search")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildAddEntryTab(TabHost tabHost, String[] storeNames, 
    		ArrayList<BillType> billTypes){
    	Intent intent = new Intent().setClass(this, AddEntryActivity.class);
    	intent.putExtra("storeNames", storeNames);
    	intent.putExtra("billTypes", billTypes);
    	TabHost.TabSpec spec = tabHost.newTabSpec("addentry").setIndicator("Add Entry")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildMonthlyViewTab(TabHost tabHost, String[] storeNames, 
    		ArrayList<BillType> billTypes){
    	Intent intent = new Intent().setClass(this, MonthlyViewActivity.class);
    	intent.putExtra("storeNames", storeNames);
    	intent.putExtra("billTypes", billTypes);
    	TabHost.TabSpec spec = tabHost.newTabSpec("monthlyView").setIndicator("View")
                      .setContent(intent);
        return spec;
    }
    
    private TabHost.TabSpec buildAllowanceTab(TabHost tabHost, 
    		ArrayList<BillType> billTypes){
    	Intent intent = new Intent().setClass(this, AllowanceActivity.class);
    	intent.putExtra("billTypes", billTypes);
    	
    	TabHost.TabSpec spec = tabHost.newTabSpec("allotted").setIndicator("Allotted")
                      .setContent(intent);
        return spec;
    }
}