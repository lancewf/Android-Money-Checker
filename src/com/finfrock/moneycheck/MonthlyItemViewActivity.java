package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.MonthlyPurchaseBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.BillTypePurchaseCollection;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MonthlyItemViewActivity extends Activity {
	
    private String[] storeNames;
    private ArrayList<BillType> billTypes;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchaseitemview);
        
        storeNames = getIntent().getExtras().getStringArray("storeNames");
        billTypes = 
        		getIntent().getExtras().getParcelableArrayList("billTypes");
        
        TextView billTypeName = (TextView) findViewById(R.id.purchaseitembilltype);
        TextView yearMonthName = (TextView) findViewById(R.id.purchaseitemyearmonth);
        
        BillType billtype = findBillType();
        billTypeName.setText(billtype.getName());
        
        yearMonthName.setText(formateCalendar(createCalendar()));
    }
    
    private Calendar createCalendar(){
        int year = getIntent().getExtras().getInt("year");
        int month = getIntent().getExtras().getInt("month");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, 1);
        return calendar;
    }
    
    protected void onResume(){
        super.onResume();
        setProgressBar();
        new GetPurchaseTask(createCalendar()).execute();
    }
    
    private void setProgressBar(){
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseitemviewscrollview);
        
        scrollView.removeAllViews();
        scrollView.addView(progressBar);
    }
    
    private int getBillTypeId(){
        return getIntent().getExtras().getInt("billTypeId");
    }

    private class GetPurchaseTask extends AsyncTask<Void, Void, BillTypePurchaseCollection> {
    	private Calendar calendar;
    	public GetPurchaseTask(Calendar calendar){
    		this.calendar = calendar;
    	}
        protected BillTypePurchaseCollection doInBackground(Void... ntohting) {
            try
            {
                MonthlyPurchaseBuilder monthlyPurchaseBuilder = new MonthlyPurchaseBuilder(
                		billTypes);
                List<BillTypePurchaseCollection> billTypePurchaseCollectionList = 
                		monthlyPurchaseBuilder.getBillTypePurchaseCollection(calendar);
                
                BillTypePurchaseCollection billTypePurchaseCollection = 
                        findBillTypePurchaseCollection(billTypePurchaseCollectionList);
                
                return billTypePurchaseCollection;
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(
                BillTypePurchaseCollection billTypePurchaseCollection)
        {
            ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseitemviewscrollview);

            TableLayout tableLayout = new TableLayout(MonthlyItemViewActivity.this);
            
            List<String> columns = new ArrayList<String>();
            columns.add("Date");
            columns.add("Store");
            columns.add("Cost");
            columns.add("Note");
            TableRow tableRow = createRow(columns);
            tableLayout.addView(tableRow);
            
            for(final Purchase purchase : billTypePurchaseCollection.getPurchases()){
                columns = new ArrayList<String>();
                
                columns.add(purchase.getCalendar().get(Calendar.DAY_OF_MONTH) + " ");
                columns.add(purchase.getStore());
                columns.add("$" + roundMoney(purchase.getCost()));
                columns.add(purchase.getNote());
                tableRow = createRow(columns);
                
                tableRow.setOnClickListener(new OnClickListener(){            
                    public void onClick(View v) {
                        Intent intent = new Intent().setClass(
                                MonthlyItemViewActivity.this, 
                                ModifyEntryActivity.class);
                        intent.putExtra("billTypes", billTypes);
                        intent.putExtra("storeNames", storeNames);
                        intent.putExtra("store", purchase.getStore());
                        intent.putExtra("cost", purchase.getCost());
                        intent.putExtra("month", purchase.getCalendar().get(Calendar.MONTH) + 1);
                        intent.putExtra("dayOfMonth", purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
                        intent.putExtra("year", purchase.getCalendar().get(Calendar.YEAR));
                        intent.putExtra("note", purchase.getNote());
                        intent.putExtra("billTypeKey", purchase.getBillType().getId());
                        intent.putExtra("key", purchase.getKey());
                        
                        MonthlyItemViewActivity.this.startActivity(intent);
                }});
                
                tableLayout.addView(tableRow);
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }
    
    private String formateCalendar(Calendar calendar){
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + 
        calendar.get(Calendar.YEAR);
    }
    
    private BillTypePurchaseCollection findBillTypePurchaseCollection(
            List<BillTypePurchaseCollection> billTypePurchaseCollections){
        int billTypeId = getBillTypeId();
        for (BillTypePurchaseCollection billTypePurchaseCollection : billTypePurchaseCollections)
        {
            if (billTypeId == billTypePurchaseCollection.getBillType()
                    .getId()){
                return billTypePurchaseCollection;
            }
        }
        return null;
    }
    
    private BillType findBillType(){
        int billTypeId = getBillTypeId();
        for (BillType billType : billTypes)
        {
            if (billTypeId == billType.getId()){
                return billType;
            }
        }
        return null;
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
