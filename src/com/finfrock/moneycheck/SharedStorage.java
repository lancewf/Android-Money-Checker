package com.finfrock.moneycheck;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStorage {

	private static final String BILL_TYPE_KEY = "bill_type_";
    private static final String PREFS_NAME = "com.finfrock.moneycheck";
    
    public void saveInformation(Context context, int appWidgetId, int billTypeId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(BILL_TYPE_KEY + appWidgetId, billTypeId);
        prefs.commit();
    }
    
    public int getBillTypeId(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			return prefs.getInt(BILL_TYPE_KEY + appWidgetId, 0);
		} else {
			return 0;
		}
    }
}
