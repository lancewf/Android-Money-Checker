package com.finfrock.moneycheck.connection;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.finfrock.moneycheck.data.AllocatedAmount;
import com.finfrock.moneycheck.data.AllocatedAmountCollection;
import com.finfrock.moneycheck.data.BillType;

public class BillTypeBuilder
{
    private List<AllocatedAmount> allocatedAmounts;
    
    public ArrayList<BillType> build() throws JSONException{
        AllocatedAmountsBuilder allocatedAmountsBuilder = 
            new AllocatedAmountsBuilder();
        allocatedAmounts = allocatedAmountsBuilder.build();
        
        HttpRetiever retiever = new HttpRetiever();
        String text = retiever.sendGetMessage("index.php/services/getBillTypes");
        
        JSONArray jsonArray = new JSONArray(text);

        ArrayList<BillType> items = new ArrayList<BillType>(jsonArray.length());
        
        for (int index = 0; index < jsonArray.length(); index++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            BillType item = new BillType();
            
            item.setId(jsonObject.getInt("key"));
            item.setName(jsonObject.getString("name"));
            item.setDescription(jsonObject.getString("description"));
            item.setAllocatedAmountCollection(createAllocatedAmount(jsonObject.getInt("key")));
            items.add(item);
        }
        
        return items;
    }
    
    private AllocatedAmountCollection createAllocatedAmount(int billTypeId){
        List<AllocatedAmount> billTypeAllocatedAmounts = new ArrayList<AllocatedAmount>();
        for(AllocatedAmount allocatedAmount:allocatedAmounts){
            if(allocatedAmount.getBillTypeKey() == billTypeId){
                billTypeAllocatedAmounts.add(allocatedAmount);
            }
        }
        
        AllocatedAmountCollection allocatedAmountCollection = new AllocatedAmountCollection();
        
        allocatedAmountCollection.setAllocatedAmounts(billTypeAllocatedAmounts);
        
        return allocatedAmountCollection;
    }
}
