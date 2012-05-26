package com.finfrock.moneycheck.connection;

import java.io.IOException;
import com.finfrock.moneycheck.data.Purchase;

public class DeleteEntrySender
{
    private HttpRetiever httpRetiever = new HttpRetiever();
    
    public String sendDeleteEntry(Purchase purchase){
        HttpPart[] httpParts = new HttpPart[1];
        
        httpParts[0] = new HttpPart();
        httpParts[0].setName("purchaseKey");
        httpParts[0].setValue(purchase.getKey() + "");
        
        try
        {
            return httpRetiever.sendPostMessage(
                    "index.php/services/deletePurchase", 
                    httpParts);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return "Error";
    }
}
