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
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeParcelable(allocatedAmounts, flags);
	}
    
	public static final Parcelable.Creator<BillType> CREATOR = new Parcelable.Creator<BillType>() {
		public BillType createFromParcel(Parcel in) {
			BillType billType = new BillType();
			billType.id = in.readInt();
			billType.name = in.readString();
			billType.description = in.readString();
			billType.allocatedAmounts = 
					in.readParcelable(
				AllocatedAmountCollection.class.getClassLoader());

			return billType;
		}

		public BillType[] newArray(int size) {
			return new BillType[size];
		}
	};
}
