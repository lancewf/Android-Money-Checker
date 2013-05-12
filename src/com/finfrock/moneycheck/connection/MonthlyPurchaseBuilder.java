package com.finfrock.moneycheck.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.BillTypePurchaseCollection;
import com.finfrock.moneycheck.data.Purchase;

public class MonthlyPurchaseBuilder
{
    private ArrayList<BillType> billTypes;
    
    public MonthlyPurchaseBuilder(ArrayList<BillType> billTypes){
        this.billTypes = billTypes;
    }
    
    public List<BillTypePurchaseCollection> getBillTypePurchaseCollection(Calendar date) throws JSONException{
        List<BillTypePurchaseCollection> billTypePurchaseCollections = 
            new ArrayList<BillTypePurchaseCollection>();
        
        List<Purchase> purchases = getPurchasesForMonth(date);
        
        for(Purchase purchase: purchases){
            BillTypePurchaseCollection billTypePurchaseCollection = 
                findBillTypePurchaseCollection(purchase.getBillType(), 
                        billTypePurchaseCollections);
            
            if(billTypePurchaseCollection == null){
                billTypePurchaseCollection = new BillTypePurchaseCollection();
                billTypePurchaseCollection.setBillType(purchase.getBillType());
                
                billTypePurchaseCollections.add(billTypePurchaseCollection);
            }
            billTypePurchaseCollection.addPurchase(purchase);
        }
        
        return billTypePurchaseCollections;
    }
    
    private BillTypePurchaseCollection findBillTypePurchaseCollection(BillType billType, 
            List<BillTypePurchaseCollection> billTypePurchaseCollections){
        for(BillTypePurchaseCollection billTypePurchaseCollection: billTypePurchaseCollections){
            if(billTypePurchaseCollection.getBillType().getId() == billType.getId()){
                return billTypePurchaseCollection;
            }
        }
        
        return null;
    }
    
    public List<Purchase> getPurchasesForMonth(Calendar date) throws JSONException{
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
        startDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
        startDate.set(Calendar.DAY_OF_MONTH, 1);
//        startDate.add(Calendar.DAY_OF_MONTH, -1);
        
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.YEAR, date.get(Calendar.YEAR));
        endDate.set(Calendar.MONTH, date.get(Calendar.MONTH));
        endDate.set(Calendar.DAY_OF_MONTH, 1);
        endDate.add(Calendar.MONTH, 1);
//        endDate.add(Calendar.DAY_OF_MONTH, -1);
        
        return getPurchases(startDate, endDate);
    }
    
    public List<Purchase> getPurchases(Calendar startDate, Calendar endDate) throws JSONException{
        HttpRetiever retiever = new HttpRetiever();
        HttpPart[] httpParts = new HttpPart[6];
        
        httpParts[0] = new HttpPart();
        httpParts[0].setName("startmonth");
        httpParts[0].setValue((startDate.get(Calendar.MONTH)+1) + "");

        httpParts[1] = new HttpPart();
        httpParts[1].setName("startdaymonth");
        httpParts[1].setValue(startDate.get(Calendar.DAY_OF_MONTH) + "");

        httpParts[2] = new HttpPart();
        httpParts[2].setName("startyear");
        httpParts[2].setValue(startDate.get(Calendar.YEAR) + "");

        httpParts[3] = new HttpPart();
        httpParts[3].setName("endmonth");
        httpParts[3].setValue((endDate.get(Calendar.MONTH)+1) + "");

        httpParts[4] = new HttpPart();
        httpParts[4].setName("enddaymonth");
        httpParts[4].setValue(endDate.get(Calendar.DAY_OF_MONTH) + "");

        httpParts[5] = new HttpPart();
        httpParts[5].setName("endyear");
        httpParts[5].setValue(endDate.get(Calendar.YEAR) + "");
        
        List<Purchase> purchases = new ArrayList<Purchase>();
        
        try
        {
            String text = retiever.sendPostMessage(
                    "index.php/services/getPurchases", httpParts);
            
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
