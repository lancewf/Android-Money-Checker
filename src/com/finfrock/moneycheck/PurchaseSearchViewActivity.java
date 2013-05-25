package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;

import com.finfrock.moneycheck.R;
import com.finfrock.moneycheck.connection.SearchPurchaseBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PurchaseSearchViewActivity extends Activity {

	private ArrayList<BillType> billTypes;
	private String[] storeNames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchaseitemview2);

		billTypes = getIntent().getExtras().getParcelableArrayList("billTypes");
		storeNames = getIntent().getExtras().getStringArray("storeNames");
		Calendar startDate = (Calendar)getIntent().getExtras().getSerializable("startDate"); 
		Calendar endDate = (Calendar)getIntent().getExtras().getSerializable("endDate");
		BillType billType = getIntent().getExtras().getParcelable("billType");
		String storeName = getIntent().getExtras().getString("storeName");
		Double cost = getIntent().getExtras().getDouble("cost"); 
		String costComparison = getIntent().getExtras().getString("costComparison");
		Double costRange = getIntent().getExtras().getDouble("costRange");
		
		new PurchaseTask(startDate, endDate, billType, 
				storeName, cost, costComparison, costRange).execute();
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
		}

		protected ArrayList<Purchase> doInBackground(Void... nothing) {
			ArrayList<Purchase> purchases = new ArrayList<Purchase>();
			try {
				SearchPurchaseBuilder purchaseBuilder = new SearchPurchaseBuilder(
						billTypes);
				purchases = new ArrayList<Purchase>(
						purchaseBuilder.getPurchases(startDate, endDate,
								billType, storeName, cost, costComparison,
								costRange));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return purchases;
		}

		protected void onPostExecute(ArrayList<Purchase> purchases) {
			ScrollView scrollView = (ScrollView) findViewById(R.id.purchaseitemviewscrollview2);
			TableLayout tableLayout = new TableLayout(
					PurchaseSearchViewActivity.this);
			TableRow tableRow = createRow(createHeader());
			tableLayout.addView(tableRow);

			for (final Purchase purchase : purchases) {
				tableRow = createRow(createColumns(purchase));
				tableRow.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent().setClass(
								PurchaseSearchViewActivity.this,
								ModifyEntryActivity.class);
						intent.putExtra("billTypes", billTypes);
						intent.putExtra("storeNames", storeNames);
						intent.putExtra("store", purchase.getStore());
						intent.putExtra("cost", purchase.getCost());
						intent.putExtra("month",
								purchase.getCalendar().get(Calendar.MONTH) + 1);
						intent.putExtra("dayOfMonth", purchase.getCalendar()
								.get(Calendar.DAY_OF_MONTH));
						intent.putExtra("year",
								purchase.getCalendar().get(Calendar.YEAR));
						intent.putExtra("note", purchase.getNote());
						intent.putExtra("billTypeKey", purchase.getBillType()
								.getId());
						intent.putExtra("key", purchase.getKey());

						startActivity(intent);
					}
				});
				tableLayout.addView(tableRow);
			}

			scrollView.removeAllViews();
			scrollView.addView(tableLayout);
		}
	}

	private List<String> createHeader() {
		List<String> columns = new ArrayList<String>();

		columns.add("Date");
		columns.add("Bill Type");
		columns.add("Cost");
		columns.add("Store");

		return columns;
	}

	private List<String> createColumns(Purchase purchase) {
		List<String> columns = new ArrayList<String>();

		String month = String.format("%02d",
				purchase.getCalendar().get(Calendar.MONTH) + 1);
		String day = String.format("%02d",
				purchase.getCalendar().get(Calendar.DAY_OF_MONTH));
		columns.add(purchase.getCalendar().get(Calendar.YEAR) + "/" + month
				+ "/" + day);
		columns.add(purchase.getBillType().getName());
		columns.add("$" + roundMoney(purchase.getCost()));
		columns.add(purchase.getStore());

		return columns;
	}

	private TableRow createRow(List<String> texts) {
		TableRow tableRow = new TableRow(this);
		for (String text : texts) {
			tableRow.addView(createTextView(text));

		}
		return tableRow;
	}

	private TextView createTextView(String text) {
		TextView tv = new TextView(this);
		tv.setPadding(10, 10, 10, 10);
		tv.setText(text);
		return tv;
	}

	private double roundMoney(double value) {
		int x = (int) (value * 100.0);
		return ((double) x) / 100.0;
	}
}
