package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.finfrock.moneycheck.connection.SummaryBuilder;
import com.finfrock.moneycheck.data.SummaryItem;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AllowanceActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    
    protected void onResume(){
        super.onResume();
        show();
    }
    
    // -------------------------------------------------------------------------
    // Private Members
    // -------------------------------------------------------------------------
    
    private void show()
    {
        ProgressBar progressBar = new ProgressBar(AllowanceActivity.this);
        
        progressBar.setIndeterminate(true);
        setContentView(progressBar);
        new GetSummaryItemsTask().execute();
    }
    
    private class GetSummaryItemsTask extends AsyncTask<Void, Void, List<List<String>>> {
        protected List<List<String>> doInBackground(Void... nothing) {
            List<List<String>> columnsCollection = new ArrayList<List<String>>();
            try{
                SummaryBuilder summaryBuilder = new SummaryBuilder(
                        DataStore.getInstance().getBillTypes());
            	List<SummaryItem> summaryItems = summaryBuilder.build();
                
                for (SummaryItem item : summaryItems){
                    List<String> columns = new ArrayList<String>();
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
            ScrollView scrollView = new ScrollView(AllowanceActivity.this);
            TableLayout tableLayout = new TableLayout(
                    AllowanceActivity.this);
            for (List<String> columns : columnsCollection){
                tableLayout.addView(createRow(columns));
            }
            scrollView.addView(tableLayout);
            setContentView(scrollView);
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
        tv.setPadding(5, 5, 10, 10);
        tv.setText(text);
        return tv;
    }
    
    private double roundMoney(double value){
        int x = (int)(value * 100.0);
        return ((double)x)/100.0;
    }
}
