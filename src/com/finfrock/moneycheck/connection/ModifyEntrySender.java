package com.finfrock.moneycheck.connection;

import java.io.IOException;
import java.util.Calendar;

import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

public class ModifyEntrySender
{
    private HttpRetiever httpRetiever = new HttpRetiever();
    
    public String sendModifyEntry(Purchase purchase){
        Calendar calendar = purchase.getCalendar();
        return sendModifyEntry(purchase.getStore(), purchase.getCost(), 
                calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 
                calendar.get(Calendar.YEAR), purchase.getNote(), 
                purchase.getBillType(), purchase.getKey());
    }
    
    public String sendModifyEntry(String store, double cost, int month, int dayOfMonth, 
            int year, String note, BillType billType, int key){
        HttpPart[] httpParts = new HttpPart[8];
        
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
        
        httpParts[7] = new HttpPart();
        httpParts[7].setName("purchaseKey");
        httpParts[7].setValue(key + "");
        
        try
        {
            return httpRetiever.sendPostMessage(
                    "index.php/services/modifyPurchase", 
                    httpParts);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return "Error";
    }
}
