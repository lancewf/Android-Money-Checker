package com.finfrock.moneycheck.data;

public class BillType
{
    private int id;
    private String name;
    private String description;
    private AllocatedAmountCollection allocatedAmounts;
    
    
    public AllocatedAmountCollection getAllocatedAmountCollection()
    {
        return allocatedAmounts;
    }
    
    public void setAllocatedAmountCollection(AllocatedAmountCollection allocatedAmounts)
    {
        this.allocatedAmounts = allocatedAmounts;
    }
    
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String toString(){
        return getName();
    }
    
}
