package com.mcnichols.framework;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.framework.util.TestUtil;

public class BrowserOperations {
	
	
	private enum BrowserViewport {
		EXTRA_LARGE, LARGE, SMALL, EXTRA_SMALL
	}

	public static final String CHROME_BROWSER = "chrome";
	private static final String FIREFOX_BROWSER = "firefox";

	private static final String BROWSER_VIEWPORT_EXTRA_LARGE = "XL";
	private static final String BROWSER_VIEWPORT_LARGE = "L";
	private static final String BROWSER_VIEWPORT_SMALL = "S";
	private static final String BROWSER_VIEWPORT_EXTRA_SMALL = "XS";

	public static String testingBrowserName = "chrome";
	public static String testingBrowserViewport = "";
	public static final String PROPERTY_FILE = "test-base-config.xml";

	private static Properties properties = TestingConfig.getProperties(TestingConfig.UTIL_PROPERTY_FILE_LOCATION, PROPERTY_FILE);

	public static void launchBrowser(String browserName, String environment) throws Exception {
		try {
			if (CHROME_BROWSER.equals(browserName)) {
				Browser.driver = getChromeDriver(environment);
			} else if (FIREFOX_BROWSER.equals(browserName)) {
				Browser.driver = getFirefoxDriver();
			}
			testingBrowserName = browserName;
		} catch (Exception e) {
			String message = "An error occurred getting the browser possibly due to invalid browser location: ";
			throw new Exception(message + e);
		}
	}

	private static WebDriver getChromeDriver(String environment) {
		System.setProperty("webdriver.chrome.driver", "." + properties.getProperty("web.driver.location.chrome"));
		ChromeOptions options = new ChromeOptions();
		
		// Set logging preferences
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
		options.setCapability("goog:loggingPrefs", logPrefs);

		boolean useHeadlessMode = Boolean.valueOf(properties.getProperty("use.headless.mode", "false"));
		boolean useChromeBestPractices = Boolean.valueOf(properties.getProperty("use.chrome.best.practices", "false"));
		boolean useChromeIncognitoMode = Boolean.valueOf(properties.getProperty("use.chrome.incognito.mode", "false"));
		
		if (useHeadlessMode) {
			// Headless mode (use new mode for better compatibility)
			options.addArguments("--headless=new"); // or "--headless" for older versions
		}
		
		String chromeUserDataDir = properties.getProperty("chrome.user.data.dir");
		if (StringUtil.isNotEmpty(chromeUserDataDir) && doesFolderExists(chromeUserDataDir)) {
			chromeUserDataDir += "/" + environment;
			if (!doesFolderExists(chromeUserDataDir)) {
				TestUtil.createDirectory(chromeUserDataDir);
			}
			options.addArguments("user-data-dir=" + chromeUserDataDir); // Use a temporary profile (isolated environment)
			options.addArguments("profile-directory=Default"); // Use the default profile
		}
		
        options.addArguments("--disable-gpu"); // Disable GPU (mostly for Windows, safe to include)
        options.addArguments("--ignore-certificate-errors"); // Work around for the certificate issue on the new staging web-site. This argument allows Selenium to deal with the error.
        
        if (useChromeBestPractices) {
            // General best practices
            options.addArguments("--disable-extensions"); // Prevents Chrome from loading extensions.
            options.addArguments("--disable-popup-blocking"); // Prevents Chrome from blocking popups.
            options.addArguments("--disable-notifications"); // Prevents Chrome from showing notifications.
            options.addArguments("--disable-infobars"); // Prevents Chrome from showing the 'Chrome is being controlled by automated test software' infobar.
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"); // Sets the user agent to a specific value.
            
            options.setExperimentalOption("excludeSwitches", new String[] {
        		"enable-automation" // Prevents Chrome from showing the 'Chrome is being controlled by automated test software' infobar.
    		});
        }
        
        if (useChromeIncognitoMode) {
        	options.addArguments("--incognito"); // Use incognito mode to prevent caching and cookies from affecting tests	
        }
        
		return new ChromeDriver(options);
	}

	private static WebDriver getFirefoxDriver() {
		System.setProperty("webdriver.gecko.driver", "." + properties.getProperty("web.driver.location.firefox"));
		return new FirefoxDriver();
	}
	
	public static void setBrowserViewport(String browserViewport) {
		String widthHeightDimensions = "";
		switch (browserViewport) {
		case BROWSER_VIEWPORT_EXTRA_LARGE:
			//Browser.driver.manage().window().maximize();
			//widthHeightDimensions = "Window Maximized";
			//Browser.driver.manage().window().setSize(new Dimension(1450, 1000));
			//widthHeightDimensions = "Width: 1450 x Height: 1000";
			
			Browser.driver.manage().window().setSize(new Dimension(1300, 1000));
			widthHeightDimensions = "Width: 1300 x Height: 1000";
			break;
		case BROWSER_VIEWPORT_LARGE:
			Browser.driver.manage().window().setSize(new Dimension(1200, 900));
			widthHeightDimensions = "Width: 1200 x Height: 900";
			break;
		case BROWSER_VIEWPORT_SMALL:
			Browser.driver.manage().window().setSize(new Dimension(900, 900));
			widthHeightDimensions = "Width: 900 x Height: 900";
			break;
		case BROWSER_VIEWPORT_EXTRA_SMALL:
			Browser.driver.manage().window().setSize(new Dimension(375, 812));
			widthHeightDimensions = "Width: 375 x Height: 812";
			break;
		default: 
			Browser.driver.manage().window().maximize();
			widthHeightDimensions = "Defaulted to Window Maximized";
			testingBrowserViewport = BROWSER_VIEWPORT_EXTRA_LARGE;
			break;
		}
		
		testingBrowserViewport = browserViewport;

		Logger.info("Browser viewport setting: " + browserViewport + " - " + widthHeightDimensions);
	}
	
	private static boolean doesFolderExists(String folderPath) {
		boolean doesExists = false;
		try {
			File folder = new File(folderPath);

	        if (folder.exists() && folder.isDirectory()) {
	            //System.out.println("Folder exists: " + folderPath);
	            doesExists = true;
	        } else {
	            //System.out.println("Folder does not exist: " + folderPath);
	            doesExists = false;
	        }
		} catch (Exception e) {
			e.getMessage();
		}
		
		return doesExists;
	}
}
