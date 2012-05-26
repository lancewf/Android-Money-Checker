package com.finfrock.moneycheck.data;

import java.util.ArrayList;
import java.util.List;

public class BillTypePurchaseCollection
{
    private BillType billType;
    private List<Purchase> purchases;
    
    public BillType getBillType()
    {
        return billType;
    }
    
    public void setBillType(BillType billType)
    {
        this.billType = billType;
    }
    
    public List<Purchase> getPurchases()
    {
        return purchases;
    }
    
    public void setPurchases(List<Purchase> purchases)
    {
        this.purchases = purchases;
    }
    
    public void addPurchase(Purchase purchase){
        if(purchases == null){
            purchases = new ArrayList<Purchase>();
        }
        
        purchases.add(purchase);
    }
    
    public double sum(){
        if(purchases == null){
            return 0;
        }
        else{
            double sum = 0;
            for(Purchase purchase : purchases){
                sum += purchase.getCost();
            }
            
            return sum;
        }
    }
}
