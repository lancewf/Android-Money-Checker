package com.finfrock.moneycheck.data;

import java.util.Calendar;

public class AllocatedAmount
{
    private int key;
    private double amount;
    private int billTypeKey;
    private Calendar startDate;
    private Calendar endDate;
    public int getKey()
    {
        return key;
    }
    public void setKey(int key)
    {
        this.key = key;
    }
    public double getAmount()
    {
        return amount;
    }
    public void setAmount(double amount)
    {
        this.amount = amount;
    }
    public int getBillTypeKey()
    {
        return billTypeKey;
    }
    public void setBillTypeKey(int billTypeKey)
    {
        this.billTypeKey = billTypeKey;
    }
    public Calendar getStartDate()
    {
        return startDate;
    }
    public void setStartDate(Calendar startDate)
    {
        this.startDate = startDate;
    }
    public Calendar getEndDate()
    {
        return endDate;
    }
    public void setEndDate(Calendar endDate)
    {
        this.endDate = endDate;
    }
}
