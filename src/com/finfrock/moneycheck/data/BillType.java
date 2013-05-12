package com.finfrock.moneycheck.data;

import android.os.Parcel;
import android.os.Parcelable;

public class BillType implements Parcelable
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

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
    
}
