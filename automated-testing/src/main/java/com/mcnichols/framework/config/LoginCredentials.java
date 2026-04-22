package com.mcnichols.framework.config;

import java.util.HashMap;
import java.util.Map;

public class LoginCredentials {

	public static final String LOGINS_KEY = "Logins";
	public static final String DEVELOPMENT_LOGINS_KEY = "Dev-Logins";
	public static final String PRODUCTION_LOGINS_KEY = "Prod-Logins";
	public static final String USERNAME_KEY = "Username";
	public static final String PASSWORD_KEY = "Password";
	private String username;
	private String password;
	private String fistName;
	private String lastName;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private UserProfile userProfile;
	private UserCreditCard userCreditCard;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
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

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public UserCreditCard getUserCreditCard() {
		return userCreditCard;
	}

	public void setUserCreditCard(UserCreditCard userCreditCard) {
		this.userCreditCard = userCreditCard;
	}
}
