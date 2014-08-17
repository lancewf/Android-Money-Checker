package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.AddEntrySender;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public class SplitEntryActivity extends Activity {
    private AddEntrySender addEntrySender = new AddEntrySender();
    private ArrayList<BillType> billTypes;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splitentry);
        
        String store = getIntent().getExtras().getString("store");
        billTypes = 
        		getIntent().getExtras().getParcelableArrayList("billTypes");
        
        double cost = getIntent().getExtras().getDouble("cost");
        int year = getIntent().getExtras().getInt("year");
        int month = getIntent().getExtras().getInt("month");
        int dayOfMonth = getIntent().getExtras().getInt("dayOfMonth");
        
        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.updateDate(year, month - 1, dayOfMonth);
        
        
        EditText stores = (EditText)findViewById(R.id.stores);
        stores.setText(store);
        
        EditText totalCost = (EditText)findViewById(R.id.totalCost);
        totalCost.setText("" + cost);
        totalCost.setOnFocusChangeListener(new OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                adjustLastCell();
            }
        });
        
        Button addRowButton = (Button)findViewById(R.id.AddRowbutton);
        addRowButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                addRow();
            }
        });
        
        Button submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                submitData();
            }
        });
        
        addRow();
        addRow();
        
        adjustLastCell();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Submitting");
        dialog.setMessage("Please wait while submitting entries...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
    
    private void submitData(){
        showDialog(1);
        List<Purchase> purchases = getPurchases(); 
        new SendEntryTask().execute(purchases.toArray(new Purchase[0]));
    }
    
    private List<Purchase> getPurchases(){
        DatePicker datePicker = (DatePicker)findViewById(R.id.datePicker);
        AutoCompleteTextView storesTextView = (AutoCompleteTextView) findViewById(R.id.stores);

        String store = storesTextView.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), 
                datePicker.getDayOfMonth());
        
        List<Purchase> purchases = new ArrayList<Purchase>();
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);
        for(int index = 0; index < tableLayout.getChildCount(); index++){
            TableRow tableRow = (TableRow)tableLayout.getChildAt(index);
            EditText notEditText = (EditText)tableRow.getChildAt(1);
            Spinner spinner = (Spinner) tableRow.getChildAt(2);
            EditText costEditText = (EditText)tableRow.getChildAt(0);
            
            BillType selectedBillType = (BillType)spinner.getSelectedItem();
            String note = notEditText.getText().toString();
            double cost = Double.parseDouble(costEditText.getText().toString());
            Purchase purchase = new Purchase();
            
            purchase.setCost(cost);
            purchase.setStore(store);
            purchase.setNote(note);
            purchase.setBillType(selectedBillType);
            purchase.setCalendar((Calendar)calendar.clone());
            
            purchases.add(purchase);
        }

        Purchase largestPurchase = findLargestPurchase(purchases);
        largestPurchase.setCost(getTotatlCost());
        purchases.remove(largestPurchase);
        List<Purchase> finalPurchases = new ArrayList<Purchase>();
        finalPurchases.add(largestPurchase);
        for(Purchase subtractedPurchase : purchases){
            Purchase purchase = new Purchase();
            
            purchase.setCost((-1)*subtractedPurchase.getCost());
            purchase.setStore(store);
            purchase.setNote("Subtracting other purchases");
            purchase.setBillType(largestPurchase.getBillType());
            purchase.setCalendar((Calendar)calendar.clone());
            
            finalPurchases.add(subtractedPurchase);
            finalPurchases.add(purchase);
        }
        
        return finalPurchases;
    }
    
    private Purchase findLargestPurchase(List<Purchase> purchases){
        double largest = -100;
        Purchase largestPurchase = null;
        for(Purchase purchase : purchases){
            if(largest < purchase.getCost()){
                largest = purchase.getCost();
                largestPurchase = purchase;
            }
        }
        
        return largestPurchase;
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
            SplitEntryActivity.this.removeDialog(1);
            SplitEntryActivity.this.finish();
        }
    }

    private double roundMoney(double value){
        int x = (int)(value * 100.0);
        return ((double)x)/100.0;
    }
    
    private void adjustLastCell(){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);
        
        TableRow lastTableRow = (TableRow)tableLayout.getChildAt(
                tableLayout.getChildCount()-1);
        EditText cost = (EditText)lastTableRow.getChildAt(0);
        
        double currentCost = parseDouble(cost.getText().toString());
        double lastCell = roundMoney(getTotatlCost() - sum() + currentCost);
        cost.setText(lastCell + "");
    }
    
    private double getTotatlCost(){
        EditText totalCost = (EditText)findViewById(R.id.totalCost);
        
        return parseDouble(totalCost.getText().toString());
    }
    
    private double parseDouble(String rawTest){
        try{
            return Double.parseDouble(rawTest);
        }
        catch(Exception ex){
            return 0.0;
        }
    }
    
    private void addRow(){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        final TableRow tableRow = new TableRow(this);

        EditText cost = new EditText(this);
        cost.setPadding(10, 10, 10, 10);
        cost.setText("0.0");
        cost.setWidth(120);
        cost.setKeyListener(DigitsKeyListener.getInstance("-0123456789."));
        cost.setOnFocusChangeListener(new OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                adjustLastCell();
            }
        });
        
        tableRow.addView(cost);
        EditText note = new EditText(this);
        note.setPadding(10, 10, 10, 10);
        note.setWidth(150);
        note.setText("");
        tableRow.addView(note);
        
        tableRow.addView(createSpinner());
        
        Button deleteButton = new Button(this);
        deleteButton.setText("X");
        deleteButton.setPadding(10, 10, 10, 10);
        deleteButton.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);
                tableLayout.removeView(tableRow);
            }
        });
        tableRow.addView(deleteButton);
        
        tableLayout.addView(tableRow, 0);
    }
    
    private double sum(){
        double sum = 0;
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);
        for(int index = 0; index < tableLayout.getChildCount(); index++){
            TableRow tableRow = (TableRow)tableLayout.getChildAt(index);
            EditText cost = (EditText)tableRow.getChildAt(0);
            
            sum += Double.parseDouble(cost.getText().toString());
        }
        return roundMoney(sum);
    }
    
    private Spinner createSpinner(){
        Spinner spinner = new Spinner(this);
        spinner.setPadding(10, 10, 10, 10);
        ArrayAdapter<BillType> adapterBillType = new ArrayAdapter<BillType>(this, 
                android.R.layout.simple_spinner_item, billTypes);
        adapterBillType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        System.out.println("billtype: " + billTypes.size());
        spinner.setAdapter(adapterBillType);
        return spinner;
    }
}