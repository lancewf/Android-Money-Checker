package com.finfrock.moneycheck;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.finfrock.moneycheck.connection.BillTypeBuilder;
import com.finfrock.moneycheck.connection.SummaryBuilder;
import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.SummaryItem;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class MoneyWidgetDisplay extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY = "moneywidgetproviderwidgetids";
	private SharedStorage sharedStorage = new SharedStorage();
	
	// -------------------------------------------------------------------------
	// AppWidgetProvider Members
	// -------------------------------------------------------------------------

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.hasExtra(WIDGET_IDS_KEY)){
			int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
			onUpdate(context, AppWidgetManager.getInstance(context), ids);
		} else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
        
		update(context, appWidgetManager, appWidgetIds);
	}
	
	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------
	
	private void update(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			int billTypeId = sharedStorage.getBillTypeId(context, appWidgetId);
			Log.i("info", "billType: " + billTypeId);
	        startBackgroundRequest(context, appWidgetId, appWidgetManager, 
	        		appWidgetIds, billTypeId);
		}
	}
	
	/**
	 * A background thread is created because any network request needs to be off the main thread
	 */
	private void startBackgroundRequest(final Context context,
			final int appWidgetId, final AppWidgetManager appWidgetManager,
			final int[] appWidgetIds, final int billTypeId) {
		(new AsyncTask<Object, Void, SummaryItem>() {
			protected SummaryItem doInBackground(Object... args) {
				try {
					SummaryItem summaryItem = getSummaryItem(billTypeId);

					return summaryItem;
				} catch (Exception e) {
					return null;
				}
			}

			protected void onPostExecute(SummaryItem summaryItem) {
				updateWidget(context, appWidgetIds, appWidgetId,
						appWidgetManager, summaryItem);
			}
		}).execute();
	}
	
	private void updateWidget(Context context, int[] appWidgetIds,
			int appWidgetId, AppWidgetManager appWidgetManager, 
			SummaryItem summaryItem) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		
		if(summaryItem != null){
			remoteViews.setTextViewText(R.id.dataTextView, "$" + 
					(int)summaryItem.getAmountLeftOfAverage() );
			
			remoteViews.setTextViewText(R.id.dateText, "Monthly Avg: $" + (int)summaryItem.getAverage());
			remoteViews.setTextViewText(R.id.nameLabel, summaryItem.getBillType().getName());
		}

		// When we click the widget, we want to open our main activity.
		Intent defineIntent2 = new Intent(context, MoneyCheckActivity.class);
		defineIntent2
				.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0,
				defineIntent2, 0);
		remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent2);
		remoteViews
				.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent2);

		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	private SummaryItem getSummaryItem(int billTypeId) throws JSONException{
		BillTypeBuilder builder = new BillTypeBuilder();
		ArrayList<BillType> billTypes = builder.build();
		SummaryBuilder summaryBuilder = new SummaryBuilder(billTypes);
		List<SummaryItem> summaryItems = summaryBuilder.build();
		
		SummaryItem foundSummaryItem = null;
		for(SummaryItem summaryItem : summaryItems){
			if(summaryItem.getBillType().getId() == billTypeId){
				foundSummaryItem = summaryItem;
				break;
			}
		}
		
		return foundSummaryItem;
	}
}
