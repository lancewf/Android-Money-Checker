package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PurchaseSearchViewActivity extends Activity {
	
    private ArrayList<BillType> billTypes;
    private ArrayList<Purchase> purchases;
    private String[] storeNames;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchaseitemview2);
        
        billTypes =	getIntent().getExtras().getParcelableArrayList("billTypes");
        purchases = getIntent().getExtras().getParcelableArrayList("purchases");
        storeNames = getIntent().getExtras().getStringArray("storeNames");
        
		ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseitemviewscrollview2);
		TableLayout tableLayout = new TableLayout(this);
		for (final Purchase purchase : purchases) {
			TableRow tableRow = createRow(createColumns(purchase));
			tableRow.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
                    Intent intent = new Intent().setClass(PurchaseSearchViewActivity.this, 
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
                    
                    startActivity(intent);
				}
			});
			tableLayout.addView(tableRow);
		}
		scrollView.removeAllViews();
		scrollView.addView(tableLayout);
    }
    
    private List<String> createColumns(Purchase purchase){
		List<String> columns = new ArrayList<String>();

		String month = String.format("%02d", purchase.getCalendar().get(Calendar.MONTH)+1);
		String day = String.format("%02d", purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
		columns.add(purchase.getCalendar().get(Calendar.YEAR) + "/" +
				month + "/" + day);
		columns.add(purchase.getBillType().getName());
		columns.add("$" + roundMoney(purchase.getCost()));
		columns.add(purchase.getStore());
		
		return columns;
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
