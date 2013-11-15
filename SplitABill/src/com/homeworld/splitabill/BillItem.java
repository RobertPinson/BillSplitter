package com.homeworld.splitabill;

public class BillItem {
	private String Name;
	private String Amount;
	
	public BillItem(String name, String amount){
		super();
		this.Name = name;
		this.Amount = amount;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}
}