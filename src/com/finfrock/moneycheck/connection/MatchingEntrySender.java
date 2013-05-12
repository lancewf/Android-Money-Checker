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

public class MatchingEntrySender
{
    private HttpRetiever httpRetiever = new HttpRetiever();
    
    private ArrayList<BillType> billTypes;
    
    public MatchingEntrySender(ArrayList<BillType> billTypes){
        this.billTypes = billTypes;
    }
    
    public List<Purchase> getMatchingEntries(Purchase purchase){
        Calendar calendar = purchase.getCalendar();
        return getMatchingEntries(purchase.getStore(), purchase.getCost(), 
                calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 
                calendar.get(Calendar.YEAR), purchase.getNote(), 
                purchase.getBillType());
    }
    
    public List<Purchase> getMatchingEntries(String store, double cost, int month, 
            int dayOfMonth, int year, String note, BillType billType){
        HttpPart[] httpParts = new HttpPart[7];
        
        httpParts[0] = new HttpPart();
        httpParts[0].setName("store");
        httpParts[0].setValue(store);
        
        httpParts[1] = new HttpPart();
        httpParts[1].setName("cost");
        httpParts[1].setValue(cost + "");
        
        httpParts[2] = new HttpPart();
        httpParts[2].setName("month");
        httpParts[2].setValue("" + month);
        
        httpParts[3] = new HttpPart();
        httpParts[3].setName("dayOfMonth");
        httpParts[3].setValue("" + dayOfMonth);
        
        httpParts[4] = new HttpPart();
        httpParts[4].setName("year");
        httpParts[4].setValue(""+ year);
        
        httpParts[5] = new HttpPart();
        httpParts[5].setName("note");
        httpParts[5].setValue(note);
        
        httpParts[6] = new HttpPart();
        httpParts[6].setName("billTypeKey");
        httpParts[6].setValue(billType.getId() + "");
        
        List<Purchase> purchases = new ArrayList<Purchase>();
        try
        {
            String text = httpRetiever.sendPostMessage(
                    "index.php/services/getMatchingPuchases", 
                    httpParts);

            purchases = createPurchases(text);
            
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException ex){
            ex.printStackTrace();
        }
        
        return purchases;
    }
    
    private List<Purchase> createPurchases(String text) throws JSONException{
        JSONArray jsonArray = new JSONArray(text);
        List<Purchase> purchases = new ArrayList<Purchase>();
        for (int index = 0; index < jsonArray.length(); index++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            Purchase purchase = new Purchase();

            int dayOfMonth = jsonObject.getInt("dayofmonth");
            int month = jsonObject.getInt("month");
            int year = jsonObject.getInt("year");
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);

            purchase.setCalendar(calendar);
            purchase.setBillType(findBillType(jsonObject
                    .getInt("billtypekey")));
            purchase.setNote(jsonObject.getString("notes"));
            purchase.setCost(jsonObject.getDouble("cost"));
            purchase.setKey(jsonObject.getInt("key"));
            purchase.setStore(jsonObject.getString("store"));

            purchases.add(purchase);
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
