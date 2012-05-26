package com.finfrock.moneycheck.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AllocatedAmountCollection
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
}
