package com.finfrock.moneycheck.connection;

import java.io.IOException;
import java.util.Calendar;

import com.finfrock.moneycheck.data.BillType;
import com.finfrock.moneycheck.data.Purchase;

public class AddEntrySender
{
    private HttpRetiever httpRetiever = new HttpRetiever();
    
    public String sendAddEntry(Purchase purchase){
        Calendar calendar = purchase.getCalendar();
        return sendAddEntry(purchase.getStore(), purchase.getCost(), calendar.get(Calendar.MONTH)+1, 
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR), purchase.getNote(), 
                purchase.getBillType());
    }
    
    public String sendAddEntry(String store, double cost, int month, int dayOfMonth, 
            int year, String note, BillType billType){
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
        
        try
        {
            return httpRetiever.sendPostMessage(
                    "index.php/services/addPurchase", 
                    httpParts);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return "Error";
    }
}
