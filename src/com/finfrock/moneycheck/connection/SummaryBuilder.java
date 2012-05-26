package com.finfrock.moneycheck.connection;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.SummaryItem;

public class SummaryBuilder
{
    private BillType[] billTypes;
    
    public SummaryBuilder(BillType[] billTypes){
        this.billTypes = billTypes;
    }
    
    public List<SummaryItem> build() throws JSONException{
        HttpRetiever retiever = new HttpRetiever();
        String text = retiever.sendGetMessage("index.php/services/getCurrentViewItems");
        
        JSONArray jsonArray = new JSONArray(text);

        List<SummaryItem> items = new ArrayList<SummaryItem>(jsonArray.length());
        
        for (int index = 0; index < jsonArray.length(); index++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            SummaryItem item = new SummaryItem();
            
            item.setAllotted(jsonObject.getDouble("allotted"));
            item.setBillType(findBillType(jsonObject.getInt("billType")));
            item.setSpent(jsonObject.getDouble("spent"));
            item.setAmountLeft(jsonObject.getDouble("amountLeft"));
            item.setAverage(jsonObject.getDouble("average"));
            item.setAmountLeftOfAverage(jsonObject.getDouble("amountLeftOfAverage"));
            
            items.add(item);
        }
        
        return items;
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
