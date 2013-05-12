package com.finfrock.moneycheck;

import java.util.Calendar;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.DeleteEntrySender;
import com.finfrock.moneycheck.connection.ModifyEntrySender;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

public class ModifyEntryActivity extends Activity {
    private ModifyEntrySender modifyEntrySender = new ModifyEntrySender();
    private DeleteEntrySender deleteEntrySender = new DeleteEntrySender();
    private int key;
    private String[] storeNames;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifyentry);
        
        storeNames = getIntent().getExtras().getStringArray("storeNames");
        
        Button saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                saveEntry();
            }
        });
        
        Button deleteButton = (Button)findViewById(R.id.delete);
        
        deleteButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                deletePurchase();
            }
        });
        
        AutoCompleteTextView textView = (AutoCompleteTextView) 
            findViewById(R.id.modifyStores);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		R.layout.list_item, storeNames);
        textView.setAdapter(adapter);
                                            
        Spinner spinner = (Spinner) findViewById(R.id.modifyBillType);
        ArrayAdapter<BillType> adapterBillType = new ArrayAdapter<BillType>(this, 
                android.R.layout.simple_spinner_item, 
                DataStore.getInstance().getBillTypes());
        adapterBillType.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterBillType);
        
        setIntent();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Submitting");
        dialog.setMessage("Please wait while submitting entry...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
    
    private void saveEntry() {
        showDialog(1);
        Purchase purchase = getPurchase();
        new ModifyEntryTask().execute(purchase);
    }
    
    private Purchase getPurchase(){
        DatePicker datePicker = (DatePicker)findViewById(R.id.modifyDatePicker1);
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) 
            findViewById(R.id.modifyStores);
        Spinner spinner = (Spinner) findViewById(R.id.modifyBillType);
        EditText noteEditText = (EditText) findViewById(R.id.modifyNotes);
        EditText costEditText = (EditText)findViewById(R.id.modifyCost);
        
        String costText = costEditText.getText().toString();
        double cost = Double.parseDouble(costText);
        BillType selectedBillType = (BillType)spinner.getSelectedItem();
        String store = storesTextView.getText().toString();
        String note = noteEditText.getText().toString();
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), 
                datePicker.getDayOfMonth());
        
        Purchase purchase = new Purchase();
        
        purchase.setKey(key);
        purchase.setCost(cost);
        purchase.setStore(store);
        purchase.setNote(note);
        purchase.setBillType(selectedBillType);
        purchase.setCalendar(calendar);
        
        return purchase;
    }
    
    private class DeleteEntryTask extends AsyncTask<Purchase, Void, String> {
        protected String doInBackground(Purchase... purcahses) {
            String reponses = "";
            for(Purchase purcahse:purcahses){
                reponses += deleteEntrySender.sendDeleteEntry(purcahse);
            }
            return reponses;
        }

        protected void onPostExecute(String response) {
            TextView textView = (TextView)findViewById(R.id.modifyTextView2);
            textView.setText(response);
            clearFields();
            
            ModifyEntryActivity.this.removeDialog(1);
            ModifyEntryActivity.this.finish();
        }
    }
    
    private class ModifyEntryTask extends AsyncTask<Purchase, Void, String> {
        protected String doInBackground(Purchase... purcahses) {
            String reponses = "";
            for(Purchase purcahse:purcahses){
                reponses += modifyEntrySender.sendModifyEntry(purcahse);
            }
            return reponses;
        }

        protected void onPostExecute(String response) {
            TextView textView = (TextView)findViewById(R.id.modifyTextView2);
            textView.setText(response);
            clearFields();
            
            ModifyEntryActivity.this.removeDialog(1);
            ModifyEntryActivity.this.finish();
        }
    }
    
    private void deletePurchase(){
        showDialog(1);
        Purchase purchase = getPurchase();
        new DeleteEntryTask().execute(purchase);
    }
    
    private void setIntent()
    {
        String store = getIntent().getExtras().getString("store");
        double cost = getIntent().getExtras().getDouble("cost");
        String note = getIntent().getExtras().getString("note");
        int billTypeKey = getIntent().getExtras().getInt("billTypeKey");
        key = getIntent().getExtras().getInt("key");
        int year = getIntent().getExtras().getInt("year");
        int month = getIntent().getExtras().getInt("month");
        int dayOfMonth = getIntent().getExtras().getInt("dayOfMonth");

        DatePicker datePicker = (DatePicker)findViewById(R.id.modifyDatePicker1);
        Spinner spinner = (Spinner) findViewById(R.id.modifyBillType);
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) 
            findViewById(R.id.modifyStores);
        EditText costEditText = (EditText) findViewById(R.id.modifyCost);
        EditText noteEditText = (EditText) findViewById(R.id.modifyNotes);
        
        for(int index = 0; index < spinner.getCount(); index++){
            BillType billType = (BillType)spinner.getItemAtPosition(index);
            
            if(billType.getId() == billTypeKey){
                spinner.setSelection(index);
                break;
            }
        }

        storesTextView.setText(store);
        noteEditText.setText(note);
        costEditText.setText(cost + "");
        datePicker.updateDate(year, month - 1, dayOfMonth);
    }
    
    private void clearFields(){
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) 
            findViewById(R.id.modifyStores);
        EditText noteEditText = (EditText) findViewById(R.id.modifyNotes);
        EditText costEditText = (EditText)findViewById(R.id.modifyCost);
        
        storesTextView.setText("");
        noteEditText.setText("");
        costEditText.setText("");
    }
}