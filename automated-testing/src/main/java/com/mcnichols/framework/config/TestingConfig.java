package com.mcnichols.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.testng.Reporter;

import com.mcnichols.framework.util.StringUtil;

public class TestingConfig {
	public static final String PAGES_PROPERTY_FILE_LOCATION = "/src/main/resources/framework/pages/";
	public static final String UTIL_PROPERTY_FILE_LOCATION = "/src/main/resources/framework/util/";
	public static final String DEVELOPMENT_BASE_URL = "dev.mcnichols.com";
	public static final String DEVELOPMENT_APP_BASE_URL = "tpcdatgapp";
	public static final String DEVELOPMENT_ENVIRONMENT_KEY = "dev";
	public static final String QA1_ENVIRONMENT_KEY = "qa1";
	public static final String QA2_ENVIRONMENT_KEY = "qa2";
	public static final String PRODUCTION_ENVIRONMENT_KEY = "prod";
	
	private static Properties properties = null;
	private static TestParameters testParameters;

	public static String testingEnvironment = "";

	public static void setTestingEnvironment(String baseUrl, String env) {
		if (StringUtil.isNotEmpty(env)) {
			testingEnvironment = env;
		}
		testParameters = new TestParameters(); 
	}
	

	public static Properties getProperties(String file) {
		properties = null;
		readProperties(PAGES_PROPERTY_FILE_LOCATION, file);        
        return properties;
	}
	
	public static Properties getProperties(String location, String file) {
		if (properties == null) {
           readProperties(location, file);
        }
        return properties;
	}

	private static void readProperties(String location, String file) {
    	String propertiesFile = null;
        if (propertiesFile == null) {
            propertiesFile = System.getProperty("user.dir") + location + file;
        }

        try {
           properties = new Properties();
           FileInputStream xmlStream = new FileInputStream(propertiesFile);
           properties.loadFromXML(xmlStream);
        } catch (IOException e) {
           e.printStackTrace();
        }
     }
	
	public static String getRandomSearchTerm() {
		List<String> searchTerms = testParameters.getSearchTerms();
		Random random = new Random();
		String randomElement = searchTerms.get(random.nextInt(searchTerms.size()));
		Reporter.log("Random Search Term: " + randomElement, true);
		return randomElement;
	}
	
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}

	public static LoginCredentials getRandomLogin() {
		List<LoginCredentials> logins = testParameters.getLogins();
		Random random = new Random();
		LoginCredentials loginElement = logins.get(random.nextInt(logins.size()));

		Reporter.log("Random Username: " + loginElement.getUsername(), true);
		Reporter.log("Random Password: " + "xxxxxxxxx", true);

		return loginElement;
	}

	public static LoginCredentials getLogin(int index) {
		List<LoginCredentials> logins = testParameters.getLogins();
		LoginCredentials loginElement = logins.get(index);

		//Reporter.log("Username: " + loginElement.getUsername(), true);
		//Reporter.log("Password: " + "xxxxxxxxx", true);

		return loginElement;
	}

	public static String getItemForOnlinePurchase() {
		List<String> items = testParameters.getItemsForOnlinePurchase();
		Random random = new Random();
		String randomElement = items.get(random.nextInt(items.size()));
		Reporter.log("Random Item For Online Purchase: " + randomElement, true);
		return randomElement;
	}

	public static String getItemNotForOnlinePurchase() {
		List<String> items = testParameters.getItemsNotForOnlinePurchase();
		Random random = new Random();
		String randomElement = items.get(random.nextInt(items.size()));
		Reporter.log("Random Item Not For Online Purchase: " + randomElement, true);
		return randomElement;
	}
}
