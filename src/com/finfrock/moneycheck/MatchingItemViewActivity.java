package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.AddEntrySender;
import com.finfrock.moneycheck.connection.MatchingEntrySender;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MatchingItemViewActivity extends Activity
{
    private MatchingEntrySender matchingEntrySender = new MatchingEntrySender(
            DataStore.getInstance().getBillTypes());
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matchingitemview);

        Button submitButton = (Button)findViewById(R.id.submitwith);
        submitButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                showDialog(1);
                Purchase purchase = getIntentPurchase();
                new SendEntryTask().execute(purchase);
            }
        });
        
        Button cancelButton = (Button)findViewById(R.id.matchingcancel);
        
        cancelButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                MatchingItemViewActivity.this.finish();
            }
        });
    }
    
    protected void onResume(){
        super.onResume();
        setProgressBar();
        Purchase purchase = getIntentPurchase();
        new CheckMatchingEntryTask().execute(purchase);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        if (id == 1)
        {
            dialog.setTitle("Submitting");
            dialog.setMessage("Please wait while submitting entry...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }
        
        return dialog;
    }
    
    private class SendEntryTask extends AsyncTask<Purchase, Void, String> {
        private AddEntrySender addEntrySender = new AddEntrySender();
        protected String doInBackground(Purchase... purcahses) {
            Purchase purcahse = purcahses[0];
            return addEntrySender.sendAddEntry(purcahse);
        }

        protected void onPostExecute(String response) {
            MatchingItemViewActivity.this.removeDialog(1);
            MatchingItemViewActivity.this.finish();
        }
    }
    
    private class CheckMatchingEntryTask extends AsyncTask<Purchase, Void, List<Purchase>> {
        
        private Purchase purchase;
        protected List<Purchase> doInBackground(Purchase... purcahses) {
            purchase = purcahses[0];
            return matchingEntrySender.getMatchingEntries(purchase);
        }

        protected void onPostExecute(List<Purchase> purchases) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.matchingitemviewscrollview);

            TableLayout tableLayout = new TableLayout(MatchingItemViewActivity.this);
            
            for(final Purchase purchase : purchases){
                List<String> columns = new ArrayList<String>();
                
                columns.add(purchase.getCalendar().get(Calendar.DAY_OF_MONTH) + " ");
                columns.add(purchase.getStore());
                columns.add("$" + roundMoney(purchase.getCost()));
                TableRow tableRow = createRow(columns);
                
                tableRow.setOnClickListener(new OnClickListener(){            
                    public void onClick(View v) {
                        Intent intent = new Intent().setClass(
                                MatchingItemViewActivity.this, 
                                ModifyEntryActivity.class);
                        intent.putExtra("store", purchase.getStore());
                        intent.putExtra("cost", purchase.getCost());
                        intent.putExtra("month", purchase.getCalendar().get(Calendar.MONTH));
                        intent.putExtra("dayOfMonth", purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
                        intent.putExtra("year", purchase.getCalendar().get(Calendar.YEAR));
                        intent.putExtra("note", purchase.getNote());
                        intent.putExtra("billTypeKey", purchase.getBillType().getId());
                        intent.putExtra("key", purchase.getKey());
                        
                        MatchingItemViewActivity.this.startActivity(intent);
                }});
                
                tableLayout.addView(tableRow);
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }
    
    private Purchase getIntentPurchase()
    {
        String store = getIntent().getExtras().getString("store");
        double cost = getIntent().getExtras().getDouble("cost");
        String note = getIntent().getExtras().getString("note");
        int year = getIntent().getExtras().getInt("year");
        int month = getIntent().getExtras().getInt("month");
        int dayOfMonth = getIntent().getExtras().getInt("dayOfMonth");
        
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        
        Purchase purchase = new Purchase();

        purchase.setCost(cost);
        purchase.setStore(store);
        purchase.setNote(note);
        purchase.setBillType(findBillType());
        purchase.setCalendar(calendar);
        
        return purchase;
    }
    
    private void setProgressBar(){
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        ScrollView scrollView = (ScrollView) findViewById(R.id.matchingitemviewscrollview);
        
        scrollView.removeAllViews();
        scrollView.addView(progressBar);
    }
    
    private int getBillTypeId(){
        return getIntent().getExtras().getInt("billTypeKey");
    }
    
    private BillType findBillType(){
        int billTypeId = getBillTypeId();
        for (BillType billType : DataStore.getInstance().getBillTypes())
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
