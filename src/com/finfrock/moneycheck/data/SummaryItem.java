package com.finfrock.moneycheck.data;

public class SummaryItem
{
    private BillType billType;
    private double allotted;
    private double spent;
    private double amountLeft;
    private double average;
    private double amountLeftOfAverage;
    
    public BillType getBillType()
    {
        return billType;
    }
    public void setBillType(BillType billType)
    {
        this.billType = billType;
    }
    public double getAllotted()
    {
        return allotted;
    }
    public void setAllotted(double allotted)
    {
        this.allotted = allotted;
    }
    public double getSpent()
    {
        return spent;
    }
    public void setSpent(double spent)
    {
        this.spent = spent;
    }
    public double getAmountLeft()
    {
        return amountLeft;
    }
    public void setAmountLeft(double amountLeft)
    {
        this.amountLeft = amountLeft;
    }
    public double getAverage()
    {
        return average;
    }
    public void setAverage(double average)
    {
        this.average = average;
    }
    public double getAmountLeftOfAverage()
    {
        return amountLeftOfAverage;
    }
    public void setAmountLeftOfAverage(double amountLeftOfAverage)
    {
        this.amountLeftOfAverage = amountLeftOfAverage;
    }
}
