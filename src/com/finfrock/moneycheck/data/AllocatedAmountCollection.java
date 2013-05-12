package com.finfrock.moneycheck.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AllocatedAmountCollection implements Parcelable
{
    private List<AllocatedAmount> allocatedAmounts;

    public List<AllocatedAmount> getAllocatedAmounts()
    {
        return allocatedAmounts;
    }

    public void setAllocatedAmounts(List<AllocatedAmount> allocatedAmounts)
    {
        this.allocatedAmounts = allocatedAmounts;
    }
    
    public AllocatedAmount getCurrentAllocatedAmount(){
        Calendar latestDate = Calendar.getInstance();
        latestDate.setTime(new Date(0));
        AllocatedAmount currentAllocatedAmount = null;
        for(AllocatedAmount allocatedAmount : allocatedAmounts){
            if(latestDate.before(allocatedAmount.getEndDate())){
                latestDate = allocatedAmount.getEndDate();
                currentAllocatedAmount = allocatedAmount;
            }
        }
        
        if (currentAllocatedAmount == null)
        {
            return new AllocatedAmount();
        } else
        {
            return currentAllocatedAmount;
        }
    }

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flag) {
		Bundle b = new Bundle();
		b.putParcelableArrayList("allocatedAmounts", new ArrayList<AllocatedAmount>(allocatedAmounts));
		dest.writeBundle(b);
	}
	
	public static final Parcelable.Creator<AllocatedAmountCollection> CREATOR = 
			new Parcelable.Creator<AllocatedAmountCollection>() {
		public AllocatedAmountCollection createFromParcel(Parcel in) {
			AllocatedAmountCollection allocatedAmountCollection = new AllocatedAmountCollection();
			Bundle b = in.readBundle(AllocatedAmount.class.getClassLoader());        
			allocatedAmountCollection.allocatedAmounts = b.getParcelableArrayList("allocatedAmounts");

			return allocatedAmountCollection;
		}

		public AllocatedAmountCollection[] newArray(int size) {
			return new AllocatedAmountCollection[size];
		}
	};
}
