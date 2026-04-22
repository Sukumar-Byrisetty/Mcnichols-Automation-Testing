package com.mcnichols.framework.config;

import org.json.JSONObject;

public class UserCreditCard {
	private String number;
	private String expirationMonth;
	private String expirationYear;
	private String cvn;
	private String name;
	
	public UserCreditCard(JSONObject creditCardObject) {
		this.number = creditCardObject.getString("number");
		this.expirationMonth = creditCardObject.getString("expirationMonth");
		this.expirationYear = creditCardObject.getString("expirationYear");
		this.cvn = creditCardObject.getString("cvn");
		this.name = creditCardObject.getString("name");
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getExpirationMonth() {
		return expirationMonth;
	}
	
	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}
	
	public String getExpirationYear() {
		return expirationYear;
	}
	
	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}
	
	public String getCvn() {
		return cvn;
	}
	
	public void setCvn(String cvn) {
		this.cvn = cvn;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
