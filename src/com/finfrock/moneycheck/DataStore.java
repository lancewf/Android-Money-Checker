package com.finfrock.moneycheck;

import org.json.JSONException;

import com.finfrock.moneycheck.connection.BillTypeBuilder;
import com.finfrock.moneycheck.connection.StoreNameRetirever;
import com.finfrock.moneycheck.data.BillType;

public class DataStore
{
    private static DataStore instance = null;
    
    public static DataStore getInstance(){
        
        if(instance == null){
            instance = new DataStore();
        }
        
        return instance;
    }
    
    private DataStore(){
        
    }
    
    private BillType[] billTypes;
    private String[] storeNames;
    
    public BillType[] getBillTypes(){
        if(billTypes == null){
            BillTypeBuilder builder = new BillTypeBuilder();

            try
            {
                billTypes = builder.build();
            } catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return billTypes;
    }
    
    public String[] getStoreNames(){
        if(storeNames == null){
            StoreNameRetirever storeNameRetirever = new StoreNameRetirever();
            storeNames = storeNameRetirever.getStoreNames();
        }
        return storeNames;
    }
    
    
}
