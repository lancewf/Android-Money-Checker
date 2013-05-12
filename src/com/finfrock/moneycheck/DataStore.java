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
    
    private DataStore(){}
    
    private BillType[] billTypes;
    
    public BillType[] getBillTypes(){
        if(billTypes == null){
            BillTypeBuilder builder = new BillTypeBuilder();

            try
            {
                billTypes = builder.build();
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        
        return billTypes;
    }
}
