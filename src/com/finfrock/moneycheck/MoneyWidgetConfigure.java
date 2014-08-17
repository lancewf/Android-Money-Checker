package com.finfrock.moneycheck;

import java.util.ArrayList;

import org.json.JSONException;

import com.finfrock.moneycheck.connection.BillTypeBuilder;
import com.finfrock.moneycheck.data.BillType;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MoneyWidgetConfigure extends Activity {
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
    private int appWidgetId = -9999;
    
	// -------------------------------------------------------------------------
	// Activity Member
	// -------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_config);
		setResult(RESULT_CANCELED);
		
		if (getAppWidgetId() != AppWidgetManager.INVALID_APPWIDGET_ID) {
			createBillTypeSpiner();
			
			findViewById(R.id.widgetConfigSaveButton).setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View v) {
							saveButtonPressed();
						}
					});
		} else {
			finish();
		}
	}

	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------

	private void createBillTypeSpiner() {
        new AsyncTask<Void, Void, ArrayList<BillType>>() {
            protected  ArrayList<BillType> doInBackground(Void... urls) {
            	ArrayList<BillType> billTypes = new ArrayList<BillType>();
                try{
                	BillTypeBuilder builder = new BillTypeBuilder();
        			billTypes = builder.build();
                }
                catch(JSONException ex){
                	
                }
                
            	return billTypes;
            }

            protected void onPostExecute(ArrayList<BillType> billTypes) {
            	Spinner spinner = (Spinner) findViewById(R.id.widgetBillTypeSpinner);
    			ArrayAdapter<BillType> adapterBillType = new ArrayAdapter<BillType>(
    					MoneyWidgetConfigure.this, android.R.layout.simple_spinner_item,
    					billTypes);
    			adapterBillType
    					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    			spinner.setAdapter(adapterBillType);
            }
        }.execute();
	}
	
	private BillType getSelectedBillType(){
		Spinner spinner = (Spinner) 
				findViewById(R.id.widgetBillTypeSpinner);
		BillType selectedBillType = (BillType) spinner
				.getSelectedItem();
		
		return selectedBillType;
	}
	
	private int getAppWidgetId() {
		if (appWidgetId == -9999) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				appWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			} else {
				appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			}
		}

		return appWidgetId;
	}

	private void saveButtonPressed() {
		BillType billType = getSelectedBillType();
		sharedStorage.saveInformation(this, getAppWidgetId(), billType.getId());
		 
		updateAppWidget();

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				getAppWidgetId());
		setResult(RESULT_OK, resultValue);
		finish();
	}

	private void updateAppWidget() {
		int[] ids = new int[] { getAppWidgetId() };

		Intent updateIntent = new Intent();
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(MoneyWidgetDisplay.WIDGET_IDS_KEY, ids);
		sendBroadcast(updateIntent);
	}
}
