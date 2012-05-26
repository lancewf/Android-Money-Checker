package com.finfrock.moneycheck.connection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.finfrock.moneycheck.data.AllocatedAmount;

public class AllocatedAmountsBuilder
{
    public List<AllocatedAmount> build() throws JSONException{
        HttpRetiever retiever = new HttpRetiever();
        String text = retiever.sendGetMessage("index.php/services/getAllocatedAmounts");
        
        JSONArray jsonArray = new JSONArray(text);

        List<AllocatedAmount> allocatedAmounts = new ArrayList<AllocatedAmount>();
        
        for (int index = 0; index < jsonArray.length(); index++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            AllocatedAmount allocatedAmount = new AllocatedAmount();
            
            allocatedAmount.setKey(jsonObject.getInt("key"));
            allocatedAmount.setAmount(jsonObject.getDouble("amount"));
            allocatedAmount.setBillTypeKey(jsonObject.getInt("billtypekey"));
            
            int startDayOfMonth = jsonObject.getInt("startdayofmonth");
            int startMonth = jsonObject.getInt("startmonth");
            int startYear = jsonObject.getInt("startyear");
            
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(startYear, startMonth, startDayOfMonth);
            allocatedAmount.setStartDate(startCalendar);
            
            int endDayOfMonth = jsonObject.getInt("enddayofmonth");
            int endMonth = jsonObject.getInt("endmonth");
            int endYear = jsonObject.getInt("endyear");
            
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(endYear, endMonth, endDayOfMonth);
            allocatedAmount.setEndDate(endCalendar);
            
            allocatedAmounts.add(allocatedAmount);
        }
        
        return allocatedAmounts;
    }
}
