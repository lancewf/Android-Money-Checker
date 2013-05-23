package com.finfrock.moneycheck.data;

public class NullBillType extends BillType {

	public String getName(){
		return "None";
	}
	
	public int getId(){
		return -1;
	}
}
