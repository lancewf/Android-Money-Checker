package com.finfrock.moneycheck.data;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Purchase implements Parcelable
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
    
	public int describeContents() {
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(billType, flags);
		dest.writeSerializable(calendar);
		dest.writeString(store);
		dest.writeDouble(cost);
		dest.writeString(note);
		dest.writeInt(key);
	}
	
	public static final Parcelable.Creator<Purchase> CREATOR = new Parcelable.Creator<Purchase>() {
		public Purchase createFromParcel(Parcel in) {
			Purchase purchase = new Purchase();
			purchase.billType = in.readParcelable(
					BillType.class.getClassLoader());
			purchase.calendar = (Calendar)in.readSerializable();
			purchase.store = in.readString();
			purchase.cost = in.readDouble();
			purchase.note = in.readString();
			purchase.key = in.readInt();

			return purchase;
		}

		public Purchase[] newArray(int size) {
			return new Purchase[size];
		}
	};
}
