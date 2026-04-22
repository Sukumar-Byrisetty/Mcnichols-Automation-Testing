package com.mcnichols.framework.config;

import org.json.JSONObject;

public class UserProfile {
	private String fistName;
	private String lastName;
	private String colorSecurityAnwser;
	private String phone1;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String country;
	
	public UserProfile(String fistName, String lastName, String colorSecurityAnwser, String phone1, String address,
			String city, String state, String zip, String country) {
		super();
		this.fistName = fistName;
		this.lastName = lastName;
		this.colorSecurityAnwser = colorSecurityAnwser;
		this.phone1 = phone1;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	public UserProfile(JSONObject prfoileObject) {
		this.fistName = prfoileObject.getString("firstName");
		this.lastName = prfoileObject.getString("lastName");
		this.colorSecurityAnwser = prfoileObject.getString("colorSecurityAnwser");
		this.phone1 = prfoileObject.getString("phone1");
		this.address = prfoileObject.getString("address");
		this.city = prfoileObject.getString("city");
		this.state = prfoileObject.getString("state");
		this.zip = prfoileObject.getString("zip");
		this.country = prfoileObject.getString("country");
	}

	public String getFistName() {
		return fistName;
	}
	
	public void setFistName(String fistName) {
		this.fistName = fistName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getColorSecurityAnwser() {
		return colorSecurityAnwser;
	}
	
	public void setColorSecurityAnwser(String colorSecurityAnwser) {
		this.colorSecurityAnwser = colorSecurityAnwser;
	}
	
	public String getPhone1() {
		return phone1;
	}
	
	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
}
