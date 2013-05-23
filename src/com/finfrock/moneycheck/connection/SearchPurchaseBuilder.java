package com.finfrock.moneycheck.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

public class SearchPurchaseBuilder
{
    private ArrayList<BillType> billTypes;
    
    public SearchPurchaseBuilder(ArrayList<BillType> billTypes){
        this.billTypes = billTypes;
    } 
    
    public List<Purchase> getPurchases(Calendar startDate, Calendar endDate, 
    		BillType billType, String storeName, Double cost, String costComparison, 
    		Double costRange) throws JSONException{
    	
    	ArrayList<HttpPart> httpParts = new ArrayList<HttpPart>();
    	
    	if(costComparison != null){
   			httpParts.add(new HttpPart("costcomparison", costComparison));
    		if(costComparison.equalsIgnoreCase("Range")){
    			httpParts.add(new HttpPart("cost", cost.toString()));
    			httpParts.add(new HttpPart("costrange", costRange.toString()));
    		} else{
    			httpParts.add(new HttpPart("cost", cost.toString()));
    		}
    	}
    	
    	if(billType != null){
    		httpParts.add(new HttpPart("billtypekey", billType.getId() + ""));
    	}
    	
    	if(storeName != null){
    		httpParts.add(new HttpPart("storename", storeName));
    	}
        
    	if(startDate != null){
    		httpParts.add(new HttpPart("startmonth", (startDate.get(Calendar.MONTH)+1) + ""));
    		httpParts.add(new HttpPart("startdaymonth", startDate.get(Calendar.DAY_OF_MONTH) + ""));
    		httpParts.add(new HttpPart("startyear", startDate.get(Calendar.YEAR) + ""));
    	}
    	
    	if(endDate != null){
    		httpParts.add(new HttpPart("endmonth", (endDate.get(Calendar.MONTH)+1) + ""));
    		httpParts.add(new HttpPart("enddaymonth", endDate.get(Calendar.DAY_OF_MONTH) + ""));
    		httpParts.add(new HttpPart("endyear", endDate.get(Calendar.YEAR) + ""));
    	}
    	
        List<Purchase> purchases = new ArrayList<Purchase>();
        
        try
        {
            HttpRetiever retiever = new HttpRetiever();
            String text = retiever.sendPostMessage(
                    "index.php/services/searchpurchases", httpParts);
            
            JSONArray jsonArray = new JSONArray(text);
            
            for (int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                Purchase purchase = new Purchase();
                
                int dayOfMonth = jsonObject.getInt("dayofmonth");
                int month = jsonObject.getInt("month");
                int year = jsonObject.getInt("year");
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month -1, dayOfMonth);
                
                purchase.setCalendar(calendar);
                purchase.setBillType(findBillType(jsonObject.getInt("billtypekey")));
                purchase.setNote(jsonObject.getString("notes"));
                purchase.setCost(jsonObject.getDouble("cost"));
                purchase.setKey(jsonObject.getInt("key"));
                purchase.setStore(jsonObject.getString("store"));
                
                
                purchases.add(purchase);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        
        return purchases;
    }
    
    
    private BillType findBillType(int id){
        
        for(BillType billType: billTypes){
            if(billType.getId() == id){
                return billType;
            }
        }
        return null;
    }
}
