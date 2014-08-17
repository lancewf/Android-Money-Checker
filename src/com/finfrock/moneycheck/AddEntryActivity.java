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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddEntryActivity extends Activity {
    private AddEntrySender addEntrySender = new AddEntrySender();
    private String[] storeNames;
    private ArrayList<BillType> billTypes;
    private MatchingEntrySender matchingEntrySender;
    
    @Override 
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addentry);
        
        storeNames = getIntent().getExtras().getStringArray("storeNames");
        
        billTypes = 
        		getIntent().getExtras().getParcelableArrayList("billTypes");
        
        matchingEntrySender = new MatchingEntrySender(billTypes);

        
        Button okButton = (Button)findViewById(R.id.ok);
        okButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                sendEntry();
            }
        });
        
        Button cancelButton = (Button)findViewById(R.id.cancel);
        
        cancelButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                clearFields();
            }
        });
        
        Button splitButton = (Button)findViewById(R.id.split);
        
        splitButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Purchase purchase = getPurchase();
                Intent intent = new Intent().setClass(
                        AddEntryActivity.this, 
                        SplitEntryActivity.class);
                intent.putExtra("store", purchase.getStore());
                intent.putExtra("billTypes", billTypes);
                intent.putExtra("cost", purchase.getCost());
                intent.putExtra("storeNames", storeNames);
                intent.putExtra("month", purchase.getCalendar().get(Calendar.MONTH) + 1);
                intent.putExtra("dayOfMonth", purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
                intent.putExtra("year", purchase.getCalendar().get(Calendar.YEAR));
                clearFields();
                AddEntryActivity.this.startActivity(intent);
            }
        });
        
        AutoCompleteTextView textView = (AutoCompleteTextView) 
        		findViewById(R.id.stores);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		R.layout.list_item, storeNames);
        textView.setAdapter(adapter);
                                            
        Spinner spinner = (Spinner) findViewById(R.id.billType);
        ArrayAdapter<BillType> adapterBillType = new ArrayAdapter<BillType>(this, 
                android.R.layout.simple_spinner_item, billTypes);
        adapterBillType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterBillType);
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
        if (id == 2){
            dialog.setTitle("Checking For Matches");
            dialog.setMessage("Please wait while checking for matching entries...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
        }
        return dialog;
    }
    
    private void sendEntry() {
        showDialog(2);
        
        Purchase purchase = getPurchase();
        
        new CheckMatchingEntryTask().execute(purchase);
    }
    
    private Purchase getPurchase(){
        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker1);
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) findViewById(R.id.stores);
        Spinner spinner = (Spinner) findViewById(R.id.billType);
        EditText noteEditText = (EditText) findViewById(R.id.notes);
        EditText costEditText = (EditText)findViewById(R.id.cost);
        
        double cost = 0.0;
        String costText = costEditText.getText().toString();
        if(costText.length() != 0){
        	cost = Double.parseDouble(costText);
        }
        
        BillType selectedBillType = (BillType)spinner.getSelectedItem();
        String store = storesTextView.getText().toString();
        String note = noteEditText.getText().toString();
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), 
                datePicker.getDayOfMonth());
        
        Purchase purchase = new Purchase();
        
        purchase.setCost(cost);
        purchase.setStore(store);
        purchase.setNote(note);
        purchase.setBillType(selectedBillType);
        purchase.setCalendar(calendar);
        
        return purchase;
    }
    
    private class CheckMatchingEntryTask extends AsyncTask<Purchase, Void, List<Purchase>> {
        
        private Purchase purchase;
        protected List<Purchase> doInBackground(Purchase... purcahses) {
            purchase = purcahses[0];
            return matchingEntrySender.getMatchingEntries(purchase);
        }

        protected void onPostExecute(List<Purchase> purchases) {
            TextView textView = (TextView)findViewById(R.id.textView2);
            String response = "";
            if(purchases.size()> 0){
                response = "Matching entries";
                textView.setText(response);
                AddEntryActivity.this.removeDialog(2);
                Intent intent = new Intent().setClass(
                        AddEntryActivity.this, 
                        MatchingItemViewActivity.class);
                intent.putExtra("billTypes", billTypes);
                intent.putExtra("storeNames", storeNames);
                intent.putExtra("store", purchase.getStore());
                intent.putExtra("cost", purchase.getCost());
                intent.putExtra("month", purchase.getCalendar().get(Calendar.MONTH));
                intent.putExtra("dayOfMonth", purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
                intent.putExtra("year", purchase.getCalendar().get(Calendar.YEAR));
                intent.putExtra("note", purchase.getNote());
                intent.putExtra("billTypeKey", purchase.getBillType().getId());
                intent.putExtra("key", purchase.getKey());
                
                AddEntryActivity.this.startActivity(intent);
            }
            else{
                response = "no matching entries";
                textView.setText(response);
                AddEntryActivity.this.removeDialog(2);
                
                showDialog(1);
                new SendEntryTask().execute(purchase);
            }
        }
    }
    
    private class SendEntryTask extends AsyncTask<Purchase, Void, String> {
        protected String doInBackground(Purchase... purcahses) {
            String reponses = "";
            for(Purchase purcahse:purcahses){
                reponses += addEntrySender.sendAddEntry(purcahse);
            }
            return reponses;
        }

        protected void onPostExecute(String response) {
            TextView textView = (TextView)findViewById(R.id.textView2);
            textView.setText(response);
            clearFields();
            
            AddEntryActivity.this.removeDialog(1);
        }
    }
    
    private void clearFields(){
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) findViewById(R.id.stores);
        EditText noteEditText = (EditText) findViewById(R.id.notes);
        EditText costEditText = (EditText)findViewById(R.id.cost);
        storesTextView.setText("");
        noteEditText.setText("");
        costEditText.setText("");
    }
}