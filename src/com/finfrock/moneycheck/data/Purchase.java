package com.finfrock.moneycheck.data;

import java.util.Calendar;

public class Purchase
{
    private BillType billType;
    private Calendar calendar;
    private String store;
    private double cost;
    private String note;
    private int key;
    
    public int getKey()
    {
        return key;
    }
    public void setKey(int key)
    {
        this.key = key;
    }
    public BillType getBillType()
    {
        return billType;
    }
    public void setBillType(BillType billType)
    {
        this.billType = billType;
    }
    public Calendar getCalendar()
    {
        return calendar;
    }
    public void setCalendar(Calendar calendar)
    {
        this.calendar = calendar;
    }
    public String getStore()
    {
        return store;
    }
    public void setStore(String store)
    {
        this.store = store;
    }
    public double getCost()
    {
        return cost;
    }
    public void setCost(double cost)
    {
        this.cost = cost;
    }
    public String getNote()
    {
        return note;
    }
    public void setNote(String note)
    {
        this.note = note;
    }
}
