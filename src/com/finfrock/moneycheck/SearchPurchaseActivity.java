package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.SearchPurchaseBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.NullBillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SearchPurchaseActivity extends Activity {
	private String[] storeNames;
	private ArrayList<BillType> billTypes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_puchase);

		storeNames = getIntent().getExtras().getStringArray("storeNames");

		billTypes = getIntent().getExtras().getParcelableArrayList("billTypes");

		Spinner costSpinner = (Spinner) findViewById(R.id.searchCostSpinner);
		ArrayAdapter<String> adapterCostCompare = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, new String[] {
						"None", "Equal", "Greater", "Less", "Range" });
		adapterCostCompare
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		costSpinner.setAdapter(adapterCostCompare);

		LinearLayout costAreaLinearLayout = (LinearLayout) findViewById(R.id.searchCostArea);
		costAreaLinearLayout.setVisibility(View.GONE);

		costSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String selected = parent.getItemAtPosition(pos).toString();
				LinearLayout costAreaLinearLayout = (LinearLayout) findViewById(R.id.searchCostArea);
				if (selected.equals("None")) {
					costAreaLinearLayout.setVisibility(View.GONE);
				} else {
					costAreaLinearLayout.setVisibility(View.VISIBLE);
					EditText costEditText1 = (EditText) findViewById(R.id.searchCost1);
					LinearLayout costRangeAreaLinearLayout = (LinearLayout) findViewById(R.id.searchCostRangeArea);
					if (selected.equals("Range")) {
						costEditText1.setVisibility(View.VISIBLE);
						costRangeAreaLinearLayout.setVisibility(View.VISIBLE);
					} else {
						costEditText1.setVisibility(View.VISIBLE);
						costRangeAreaLinearLayout.setVisibility(View.GONE);
					}
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});

		// checkBox1
		LinearLayout dateAreaLinearLayout = (LinearLayout) findViewById(R.id.searchDateArea);
		dateAreaLinearLayout.setVisibility(View.GONE);
		CheckBox dateCheckbox = (CheckBox) findViewById(R.id.checkBox1);
		dateCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				LinearLayout dateAreaLinearLayout = (LinearLayout) findViewById(R.id.searchDateArea);

				if (isChecked) {
					dateAreaLinearLayout.setVisibility(View.VISIBLE);
				} else {
					dateAreaLinearLayout.setVisibility(View.GONE);
				}
			}
		});

		AutoCompleteTextView storesTextView = (AutoCompleteTextView) findViewById(R.id.searchStores);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item, storeNames);
		storesTextView.setAdapter(adapter);

		ArrayList<BillType> nullAddedBillTypes = new ArrayList<BillType>(
				billTypes);
		nullAddedBillTypes.add(0, new NullBillType());
		Spinner spinner = (Spinner) findViewById(R.id.searchBillTypeSpinner);
		ArrayAdapter<BillType> adapterBillType = new ArrayAdapter<BillType>(
				this, android.R.layout.simple_spinner_item, nullAddedBillTypes);
		adapterBillType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapterBillType);

		Button searchButton = (Button) findViewById(R.id.searchButton);
		
		searchButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				BillType billType = null;
				Spinner spinner = (Spinner) findViewById(R.id.searchBillTypeSpinner);
		        BillType selectedBillType = (BillType)spinner.getSelectedItem();
		        
		        if(!selectedBillType.getName().equals("None")){
		        	billType = selectedBillType;
		        }
		     
				Calendar startDate = null; 
				Calendar endDate = null; 
		        CheckBox dateCheckbox = (CheckBox) findViewById(R.id.checkBox1);
		        
		        if(dateCheckbox.isChecked()){
		        	DatePicker datePicker1 = (DatePicker)findViewById(R.id.searchDatePicker1);
		        	startDate = Calendar.getInstance();
		        	startDate.set(datePicker1.getYear(), datePicker1.getMonth(), 
		        			datePicker1.getDayOfMonth());
		        	
		        	DatePicker datePicker2 = (DatePicker)findViewById(R.id.searchDatePicker2);
		        	endDate = Calendar.getInstance();
		        	endDate.set(datePicker2.getYear(), datePicker2.getMonth(), 
		        			datePicker2.getDayOfMonth());
		        }
		        
				String storeName = null; 
				
				AutoCompleteTextView storesTextView = (AutoCompleteTextView) findViewById(R.id.searchStores);
				System.out.println("store: " + storesTextView.getText().toString());
				if(storesTextView.getText().toString().length() != 0){
					storeName = storesTextView.getText().toString();
				}
				
				Spinner costSpinner = (Spinner) findViewById(R.id.searchCostSpinner);
				
				Double cost = null;
				String costComparison = null; 
				Double costRange = null;
				costComparison = (String)costSpinner.getSelectedItem();
				if(!costSpinner.getSelectedItem().equals("None")){
			        EditText costEditText = (EditText)findViewById(R.id.searchCost1);
			        String costText = costEditText.getText().toString();
			        cost = Double.parseDouble(costText);
					if(costComparison.equals("Range")){
				        EditText cost2EditText = (EditText)findViewById(R.id.searchCost2);
				        String cost2Text = cost2EditText.getText().toString();
				        costRange = Double.parseDouble(cost2Text);
					}
				}
				else{
					costComparison = null;
				}
				
				new PurchaseTask(startDate, endDate, billType, 
						storeName, cost, costComparison, costRange).execute();
			}
		});
	}
    
	private class PurchaseTask extends
			AsyncTask<Void, Void, ArrayList<Purchase>> {

		private Calendar startDate = null;
		private Calendar endDate = null;
		private BillType billType = null;
		private String storeName = null;
		private Double cost = null;
		private String costComparison = null;
		private Double costRange = null;

		public PurchaseTask(Calendar startDate, Calendar endDate,
				BillType billType, String storeName, Double cost,
				String costComparison, Double costRange) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.billType = billType;
			this.storeName = storeName;
			this.cost = cost;
			this.costComparison = costComparison;
			this.costRange = costRange;
			
			System.out.println(
//					"startDate: " + startDate.get(Calendar.DAY_OF_MONTH) + 
//					" endDate: " + endDate.get(Calendar.DAY_OF_MONTH) + 
					" billType: " + billType + " storeName: " +  storeName + 
					" cost: " + cost + " costComparison: " + costComparison + 
					" costRange: " + costRange);
		}

		protected ArrayList<Purchase> doInBackground(Void... nothing) {
			ArrayList<Purchase> purchases = new ArrayList<Purchase>();
			try {
				SearchPurchaseBuilder purchaseBuilder = new SearchPurchaseBuilder(
						billTypes);
				purchases = new ArrayList<Purchase>(purchaseBuilder.getPurchases(
						startDate, endDate, billType, storeName, cost,
						costComparison, costRange));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return purchases;
		}

		protected void onPostExecute(
				ArrayList<Purchase> purchases) {
            Intent intent = new Intent().setClass(
            		SearchPurchaseActivity.this, 
            		PurchaseSearchViewActivity.class);
            intent.putExtra("billTypes", billTypes);
            intent.putExtra("storeNames", storeNames);
            intent.putExtra("purchases", purchases);
            
            SearchPurchaseActivity.this.startActivity(intent);
		}
	}
}