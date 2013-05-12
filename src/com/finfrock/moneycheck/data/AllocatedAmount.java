package com.finfrock.moneycheck.data;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class AllocatedAmount implements Parcelable
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
    
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(key);
		dest.writeDouble(amount);
		dest.writeInt(billTypeKey);
		dest.writeSerializable(startDate);
		dest.writeSerializable(endDate);
	}
	
	public static final Parcelable.Creator<AllocatedAmount> CREATOR = new Parcelable.Creator<AllocatedAmount>() {
		public AllocatedAmount createFromParcel(Parcel in) {
			AllocatedAmount allocatedAmount = new AllocatedAmount();
			allocatedAmount.key = in.readInt();
			allocatedAmount.amount = in.readDouble();
			allocatedAmount.billTypeKey = in.readInt();
			allocatedAmount.startDate = (Calendar)in.readSerializable();
			allocatedAmount.endDate = (Calendar)in.readSerializable();

			return allocatedAmount;
		}

		public AllocatedAmount[] newArray(int size) {
			return new AllocatedAmount[size];
		}
	};
}
