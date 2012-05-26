package com.finfrock.moneycheck.connection;

public class StoreNameRetirever
{
    public String[] getStoreNames(){
        HttpRetiever retiever = new HttpRetiever();
        String text = retiever.sendGetMessage("index.php/services/getStores");
        
        return text.split(";");
    }
}
