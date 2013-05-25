package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.MonthlyPurchaseBuilder;
import com.finfrock.moneycheck.connection.SummaryBuilder;
import com.finfrock.moneycheck.connection.YearlyPurchaseBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.BillTypePurchaseCollection;
import com.finfrock.moneycheck.data.SummaryItem;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MonthlyViewActivity extends Activity {
    private String[] storeNames;
    private ArrayList<BillType> billTypes;
    private static String MONTHLY_TYPE = "Monthly";
    private static String YEARLY_TYPE = "Yearly";
    private static String ALLOWANCE_TYPE = "Allowance";
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchaseview);
        
        storeNames = getIntent().getExtras().getStringArray("storeNames");
        billTypes = 
        		getIntent().getExtras().getParcelableArrayList("billTypes");
        
        List<ViewItem> list = new ArrayList<ViewItem>();
        
        Calendar currentCalendar = Calendar.getInstance();
        
        list.add(new ViewItem(currentCalendar, ALLOWANCE_TYPE));
        
        for (int count = 0; count < 12; count++){
            list.add(new ViewItem((Calendar)currentCalendar.clone(), MONTHLY_TYPE));
            currentCalendar.add(Calendar.MONTH, -1);
        }
        
        //Same month as current but a couple years back. 
        currentCalendar = Calendar.getInstance();
        for (int count = 0; count < 2; count++)
        {
            currentCalendar.add(Calendar.YEAR, -1);
            list.add(new ViewItem((Calendar)currentCalendar.clone(), MONTHLY_TYPE));
        }
        
        currentCalendar = Calendar.getInstance();
        for (int count = 0; count < 2; count++)
        {
            list.add(new ViewItem((Calendar)currentCalendar.clone(), YEARLY_TYPE));
            currentCalendar.add(Calendar.YEAR, -1);
        }

        Spinner spinner = (Spinner) findViewById(R.id.viewList);
        ArrayAdapter<ViewItem> adapterView = new ArrayAdapter<ViewItem>(this, 
                android.R.layout.simple_spinner_item, list );
        adapterView.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterView);
        
        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                    int pos, long id){
                setData();
            }

            public void onNothingSelected(AdapterView<?> parent){
                // Do nothing.
            }
        });
    }
    
    protected void onResume(){
        super.onResume();
        setData();
    }
    
    private void setData(){
        setProgressBar();
        Spinner spinner = (Spinner) findViewById(R.id.viewList);
        ViewItem viewItem = (ViewItem)spinner.getSelectedItem();
        if(viewItem.getType().equals(MONTHLY_TYPE)){
            new MonthlyPurchaseTask(viewItem.getCalendar()).execute();
        } else if(viewItem.getType().equals(YEARLY_TYPE)){
            new YearlyPurchaseTask(viewItem.getCalendar()).execute();
        } else if(viewItem.getType().equals(ALLOWANCE_TYPE)){
        	new AllowanceItemsTask().execute();
        }
    }
    
    private void setProgressBar(){
        ProgressBar progressBar = new ProgressBar(MonthlyViewActivity.this);
        progressBar.setIndeterminate(true);
        ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
        
        scrollView.removeAllViews();
        scrollView.addView(progressBar);
    }
    
    private class ViewItem{
        
        private Calendar calendar;
        private String type;
        
        public ViewItem(Calendar calendar, String type){
            this.calendar = calendar;
            this.type = type;
        }
        
        public String getType(){
            return type;
        }
        
        public Calendar getCalendar(){
            return calendar;
        }
        
        public String toString()
        {
            if (type.equals(MONTHLY_TYPE))
            {
                return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.US) + " " + calendar.get(Calendar.YEAR);
            } else if(type.equals(ALLOWANCE_TYPE)){
            	return "Allowance";
            }
            else if(type.equals(YEARLY_TYPE))
            {
                return "All " + calendar.get(Calendar.YEAR);
            }
            else{
            	return "Not known";
            }
        }
    }
    
    private class AllowanceItemsTask extends AsyncTask<Void, Void, List<List<String>>> {
        protected List<List<String>> doInBackground(Void... nothing) {
            List<List<String>> columnsCollection = new ArrayList<List<String>>();
            try{
                SummaryBuilder summaryBuilder = new SummaryBuilder(billTypes);
            	List<SummaryItem> summaryItems = summaryBuilder.build();
                
                List<String> columns = new ArrayList<String>();
                columns.add("Bill Type");
                columns.add("Left");
                columns.add("Average");
                columns.add("Allotted");
                
                columnsCollection.add(columns);
                for (SummaryItem item : summaryItems){
                    columns = new ArrayList<String>();
                    columns.add(item.getBillType().getName());
                    columns.add("$"
                            + roundMoney(item.getAmountLeftOfAverage()));
                    columns.add("$"
                            + roundMoney(item.getAverage()));
                    columns.add("$"
                            + roundMoney(item.getAllotted()));
                    
                    columnsCollection.add(columns);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            
            return columnsCollection;
        }

        protected void onPostExecute(List<List<String>> columnsCollection) {
        	ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
            TableLayout tableLayout = new TableLayout(MonthlyViewActivity.this);
            for (List<String> columns : columnsCollection){
                tableLayout.addView(createRow(columns));
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }
    
    private class YearlyPurchaseTask extends 
    	AsyncTask<Void, Void, List<List<String>>> {
        private Calendar calendar;
        
        public YearlyPurchaseTask(Calendar calendar){
        	this.calendar = calendar;
        }
        
        protected List<List<String>> doInBackground(Void... nothing) {
        	List<List<String>> billTypeColumnsCollection = new ArrayList<List<String>>();
            try
            {
                YearlyPurchaseBuilder yearlyPurchaseBuilder = new YearlyPurchaseBuilder(
                		billTypes);
                List<BillTypePurchaseCollection> billTypePurchaseCollections = 
                		yearlyPurchaseBuilder.getBillTypePurchaseCollection(calendar);
                
                for (BillTypePurchaseCollection billTypePurchaseCollection : billTypePurchaseCollections)
                {
                    final BillType billtype = billTypePurchaseCollection.getBillType();
                    List<String> columns = new ArrayList<String>();
                    columns.add(billtype.getName());
                    columns.add("$" + roundMoney(billTypePurchaseCollection.sum()));
                    
                    billTypeColumnsCollection.add(columns);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            return billTypeColumnsCollection;
        }

        protected void onPostExecute(List<List<String>> billTypeColumnsCollection) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
            TableLayout tableLayout = new TableLayout(MonthlyViewActivity.this);
            
            List<String> columns = new ArrayList<String>();
            columns.add("Bill Type");
            columns.add("Sum");
            tableLayout.addView(createRow(columns));
            
            for (List<String> billTypeColumns : billTypeColumnsCollection){
                TableRow tableRow = createRow(billTypeColumns);
                tableLayout.addView(tableRow);
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }
    
    private class MonthlyPurchaseTaskData{
    	private List<String> columns;
    	private int billTypeId;
    	public MonthlyPurchaseTaskData(List<String> columns, int billTypeId){
    		this.columns = columns;
    		this.billTypeId = billTypeId;
    	}
    	public List<String> getColumns(){return columns;}
    	public int getBillTypeId(){return billTypeId;}
    }
    
    private class MonthlyPurchaseTask extends 
    	AsyncTask<Void, Void, List<MonthlyPurchaseTaskData>> {

    	private Calendar calendar;
    	
    	public MonthlyPurchaseTask(Calendar calendar){
    		this.calendar = calendar;
    	}
    	
        protected List<MonthlyPurchaseTaskData> doInBackground(Void... nothing) {
        	List<MonthlyPurchaseTaskData> monthlyPurchaseTaskDataCollection = 
        			new ArrayList<MonthlyPurchaseTaskData>();
            try
            {
                MonthlyPurchaseBuilder monthlyPurchaseBuilder = new MonthlyPurchaseBuilder(
                		billTypes);
                List<BillTypePurchaseCollection> billTypePurchaseCollections = 
                		monthlyPurchaseBuilder.getBillTypePurchaseCollection(calendar);
                
                for (BillTypePurchaseCollection billTypePurchaseCollection : billTypePurchaseCollections)
                {
                    BillType billType = billTypePurchaseCollection.getBillType();
                    List<String> columns = new ArrayList<String>();
                    columns.add(billType.getName());
                    columns.add("$" + roundMoney(billTypePurchaseCollection.sum()));
                    
                    monthlyPurchaseTaskDataCollection.add(new MonthlyPurchaseTaskData(columns, 
                    		billType.getId()));
                }
                
            } catch (JSONException e){
                e.printStackTrace();
            }
            return monthlyPurchaseTaskDataCollection;
        }

        protected void onPostExecute(List<MonthlyPurchaseTaskData> billTypePurchaseCollections) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
            TableLayout tableLayout = new TableLayout(MonthlyViewActivity.this);
            List<String> columns = new ArrayList<String>();
            columns.add("Bill Type");
            columns.add("Sum");
            tableLayout.addView(createRow(columns));
            
            for (MonthlyPurchaseTaskData monthlyPurchaseTaskData : billTypePurchaseCollections){
                final int billTypeId = monthlyPurchaseTaskData.getBillTypeId();

                TableRow tableRow = createRow(monthlyPurchaseTaskData.getColumns());
                tableRow.setOnClickListener(new OnClickListener(){            
                    public void onClick(View v) {
                        Intent intent = new Intent().setClass(MonthlyViewActivity.this, 
                                MonthlyItemViewActivity.class);
                        intent.putExtra("billTypes", billTypes);
                        intent.putExtra("storeNames", storeNames);
                        intent.putExtra("year", calendar.get(Calendar.YEAR));
                        intent.putExtra("month", calendar.get(Calendar.MONTH) + 1);
                        intent.putExtra("billTypeId", billTypeId);
                        MonthlyViewActivity.this.startActivity(intent);
                }});
                tableLayout.addView(tableRow);
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }
    
    private TableRow createRow(List<String> texts){
        TableRow tableRow = new TableRow(this);
        for(String text : texts){
            tableRow.addView(createTextView(text));

        }
        return tableRow;
    }
    
    private TextView createTextView(String text){
        TextView tv = new TextView(this);
        tv.setPadding(10, 10, 10, 10);
        tv.setText(text);
        return tv;
    }
    
    private double roundMoney(double value){
        int x = (int)(value * 100.0);
        return ((double)x)/100.0;
    }
}
