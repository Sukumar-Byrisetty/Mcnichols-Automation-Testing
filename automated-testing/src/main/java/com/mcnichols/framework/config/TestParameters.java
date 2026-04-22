package com.mcnichols.framework.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.JSONUtil;
import com.mcnichols.framework.util.StringUtil;

public class TestParameters {
	
	public static final String SEARCH_TERMS_KEY = "SearchTerms";
	public static final String ITEMS_FOR_ONLINE_PURCHASE_KEY = "ItemsEligibleForOnlinePurchase";
	public static final String ITEMS_NOT_FOR_ONLINE_PURCHASE_KEY = "ItemsNotEligibleForOnlinePurchase";
	public static final String TEST_PARAMETERS_PROPERTY_FILE = "test-parameters-config.json";
	public static final String UTIL_PROPERTY_DIRECTORY_LOCATION = "/src/main/resources/framework/util/";

	public static String file = System.getProperty("user.dir") + UTIL_PROPERTY_DIRECTORY_LOCATION + TEST_PARAMETERS_PROPERTY_FILE;

	private List<LoginCredentials> logins = new ArrayList<LoginCredentials>();
	private List<String> searchTerms = new ArrayList<String>();
	private List<String> itemsForOnlinePurchase = new ArrayList<String>();
	private List<String> itemsNotForOnlinePurchase = new ArrayList<String>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public static boolean isDevelopmentEnvironment() {
		if (StringUtil.isNotEmpty(TestingConfig.testingEnvironment) 
				&& TestingConfig.testingEnvironment.equals(TestingConfig.DEVELOPMENT_ENVIRONMENT_KEY)) {
			return true;
		}
		return false;
	}
	
	public static boolean isProductionEnvironment() {
		if (StringUtil.isNotEmpty(TestingConfig.testingEnvironment) 
				&& TestingConfig.testingEnvironment.equals(TestingConfig.PRODUCTION_ENVIRONMENT_KEY)) {
			return true;
		}
		return false;
	}

	public TestParameters() {
		JSONObject jsonObject = JSONUtil.parseJSONFile(file);

		// Set search terms
		JSONArray searchTermsJsonArray = jsonObject.getJSONArray(SEARCH_TERMS_KEY);
		for (int i = 0; i < searchTermsJsonArray.length(); i++) {
			searchTerms.add(searchTermsJsonArray.getString(i));
		}

		// Set logins
		JSONArray loginsJsonArray = jsonObject.getJSONArray(LoginCredentials.LOGINS_KEY);
		if (isDevelopmentEnvironment()) {
			loginsJsonArray = jsonObject.getJSONArray(LoginCredentials.DEVELOPMENT_LOGINS_KEY);
		} else if (isProductionEnvironment()) {
			loginsJsonArray = jsonObject.getJSONArray(LoginCredentials.PRODUCTION_LOGINS_KEY);
		}
		
		 for (int i = 0; i < loginsJsonArray.length(); i++) {
			 JSONObject object = loginsJsonArray.getJSONObject(i);
			 LoginCredentials login = new LoginCredentials();
			 if (object.get(LoginCredentials.USERNAME_KEY) != null && object.get(LoginCredentials.PASSWORD_KEY) != null) {
				 login.setUsername(object.get(LoginCredentials.USERNAME_KEY).toString());
				 login.setPassword(object.get(LoginCredentials.PASSWORD_KEY).toString());
				 
				 // Get Profile
				 JSONArray profile = (JSONArray) object.get("Profile");
				 JSONObject prfoileObject = profile.getJSONObject(0);
				 String firstName = prfoileObject.getString("firstName");
				 String lastName = prfoileObject.getString("lastName");
				 
				 login.setFistName(firstName);
				 login.setLastName(lastName);
				 
				 UserProfile userProfile = new UserProfile(prfoileObject);
				 login.setUserProfile(userProfile);
				 
				 // Get user credit card
				 JSONArray creditCards = (JSONArray) object.get("CreditCards");
				 JSONObject creditCardObject = creditCards.getJSONObject(0);
				 UserCreditCard userCreditCard = new UserCreditCard(creditCardObject);
				 login.setUserCreditCard(userCreditCard);
				 
				 logins.add(login);
			 }
		 }

		 // Set items for and not for purchase
		 JSONArray itemsForOnlinePurchaseJsonArray = jsonObject.getJSONArray(ITEMS_FOR_ONLINE_PURCHASE_KEY);
		 for (int i = 0; i < itemsForOnlinePurchaseJsonArray.length(); i++) {
			 itemsForOnlinePurchase.add(itemsForOnlinePurchaseJsonArray.getString(i));
		 }
		 JSONArray itemsNotForOnlinePurchaseJsonArray = jsonObject.getJSONArray(ITEMS_NOT_FOR_ONLINE_PURCHASE_KEY);
		 for (int i = 0; i < itemsNotForOnlinePurchaseJsonArray.length(); i++) {
			 itemsNotForOnlinePurchase.add(itemsNotForOnlinePurchaseJsonArray.getString(i));
		 }
	}

	public List<LoginCredentials> getLogins() {
		return logins;
	}

	public void setLogins(List<LoginCredentials> logins) {
		this.logins = logins;
	}

	public List<String> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(List<String> searchTerms) {
		this.searchTerms = searchTerms;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	public List<String> getItemsForOnlinePurchase() {
		return itemsForOnlinePurchase;
	}

	public void setItemsForOnlinePurchase(List<String> itemsForOnlinePurchase) {
		this.itemsForOnlinePurchase = itemsForOnlinePurchase;
	}

	public List<String> getItemsNotForOnlinePurchase() {
		return itemsNotForOnlinePurchase;
	}

	public void setItemsNotForOnlinePurchase(List<String> itemsNotForOnlinePurchase) {
		this.itemsNotForOnlinePurchase = itemsNotForOnlinePurchase;
	}
}
