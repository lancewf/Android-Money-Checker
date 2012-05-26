package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.MonthlyPurchaseBuilder;
import com.finfrock.moneycheck.connection.YearlyPurchaseBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.BillTypePurchaseCollection;

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

public class MonthlyViewActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchaseview);
        
        List<ViewItem> list = new ArrayList<ViewItem>();
        
        Calendar currentCalendar = Calendar.getInstance();
        
        for (int count = 0; count < 4; count++)
        {
            list.add(new ViewItem((Calendar)currentCalendar.clone(), MONTHLY_TYPE));
            currentCalendar.add(Calendar.MONTH, -1);
        }
        
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

            @Override
            public void onNothingSelected(AdapterView parent){
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
            new MonthlyPurchaseTask().execute(viewItem.getCalendar());
        }
        else if(viewItem.getType().equals(YEARLY_TYPE)){
            new YearlyPurchaseTask().execute(viewItem.getCalendar());
        }
    }
    
    private void setProgressBar(){
        ProgressBar progressBar = new ProgressBar(MonthlyViewActivity.this);
        progressBar.setIndeterminate(true);
        ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
        
        scrollView.removeAllViews();
        scrollView.addView(progressBar);
    }
    private static String MONTHLY_TYPE = "Monthly";
    private static String YEARLY_TYPE = "Yearly";
    
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
            } else
            {
                return "All " + calendar.get(Calendar.YEAR);
            }
        }
    }
    
    private class YearlyPurchaseTask extends AsyncTask<Calendar, Void, List<BillTypePurchaseCollection>> {
        private Calendar calendar;
        protected List<BillTypePurchaseCollection> doInBackground(Calendar... dates) {
            try
            {
                YearlyPurchaseBuilder yearlyPurchaseBuilder = new YearlyPurchaseBuilder(
                        DataStore.getInstance().getBillTypes());
                calendar = dates[0];
                return yearlyPurchaseBuilder.getBillTypePurchaseCollection(calendar);
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return new ArrayList<BillTypePurchaseCollection>();
        }

        protected void onPostExecute(List<BillTypePurchaseCollection> billTypePurchaseCollections) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
            TableLayout tableLayout = new TableLayout(MonthlyViewActivity.this);
            for (BillTypePurchaseCollection billTypePurchaseCollection : billTypePurchaseCollections)
            {
                final BillType billtype = billTypePurchaseCollection.getBillType();
                List<String> columns = new ArrayList<String>();
                columns.add(billtype.getName());
                columns.add("$" + roundMoney(billTypePurchaseCollection.sum()));

                TableRow tableRow = createRow(columns);
                tableLayout.addView(tableRow);
            }
            scrollView.removeAllViews();
            scrollView.addView(tableLayout);
        }
    }

    private class MonthlyPurchaseTask extends AsyncTask<Calendar, Void, List<BillTypePurchaseCollection>> {
        private Calendar calendar;
        protected List<BillTypePurchaseCollection> doInBackground(Calendar... dates) {
            try
            {
                MonthlyPurchaseBuilder monthlyPurchaseBuilder = new MonthlyPurchaseBuilder(
                        DataStore.getInstance().getBillTypes());
                calendar = dates[0];
                return monthlyPurchaseBuilder.getBillTypePurchaseCollection(calendar);
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return new ArrayList<BillTypePurchaseCollection>();
        }

        protected void onPostExecute(List<BillTypePurchaseCollection> billTypePurchaseCollections) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseviewscrollview);
            TableLayout tableLayout = new TableLayout(MonthlyViewActivity.this);
            for (BillTypePurchaseCollection billTypePurchaseCollection : billTypePurchaseCollections)
            {
                final BillType billtype = billTypePurchaseCollection.getBillType();
                List<String> columns = new ArrayList<String>();
                columns.add(billtype.getName());
                columns.add("$" + roundMoney(billTypePurchaseCollection.sum()));

                TableRow tableRow = createRow(columns);
                tableRow.setOnClickListener(new OnClickListener(){            
                    public void onClick(View v) {
                        Intent intent = new Intent().setClass(MonthlyViewActivity.this, 
                                MonthlyItemViewActivity.class);
                        intent.putExtra("year", calendar.get(Calendar.YEAR));
                        intent.putExtra("month", calendar.get(Calendar.MONTH) + 1);
                        intent.putExtra("billTypeId", billtype.getId());
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
