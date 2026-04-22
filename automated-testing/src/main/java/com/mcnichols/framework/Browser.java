package com.mcnichols.framework;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.mcnichols.framework.util.JavaScriptWaiter;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class Browser {
	private static final long SECONDS_TO_WAIT = 30;

	public static WebDriver driver;
	public static JavascriptExecutor javascriptExecutor;

	public static void goTo(String url) {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		driver.get(baseUrl + url);
	}

	public static String title() {
		return driver.getTitle();
	}
	
	public static String getCurrentURL() {
		return driver.getCurrentUrl();
	}

	public static void close() {
		driver.close();
	}

	public static String getBaseUrl(String currentUrl) {
		String baseUrl = "";
		URL url = null;
		try {
			url = new URL(currentUrl);
			baseUrl = url.getProtocol() + "://" + url.getHost();

			if (url.getPort() != -1) {
				baseUrl += ":" + url.getPort(); 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return baseUrl;
	}

	public static void goToByURL(String url) {
		try {
			if (url.startsWith("http") || url.contains(".com")) {
				driver.get(url);	
			} else {
				String baseUrl = getBaseUrl(driver.getCurrentUrl());
				driver.get(baseUrl + url);	
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void click(By by) {
		boolean result = false;
		int attempts = 0;
		WebElement webElement = Browser.getWebElement(by);

		while (attempts < 2) {
			try {
				click(webElement);
				result = true;
				break;
			} catch (StaleElementReferenceException e) {
				Logger.warning("Issue making click, due to StaleElementReferenceException for element: " + webElement.toString()
					+ "\r\n Exception: " + e.getMessage());
				waitForSomeTime();
			}
			attempts++;
		}

		if (!result) {
			Logger.fail("Issue making click, due to StaleElementReferenceException for element: " + webElement.toString());
		}
	}

	public static void click(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			Logger.warning("Issue making click, attempting to use JavaScriptExecuter to send click to element for: " + element.toString());

			// Element is present but having issue with overlays or multiple elements.
			// Using JavaScriptExecuter to send click directly on the element.
			JavascriptExecutor executor = (JavascriptExecutor) Browser.driver;
			executor.executeScript("arguments[0].click();", element);
		}
	}

	//Write Text
    public static void writeText(By elementLocation, String text) {
        driver.findElement(elementLocation).sendKeys(text);
    }

    //Read Text
    public static String readText(By elementLocation) {
        return driver.findElement(elementLocation).getText();
    }

	public static boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public static boolean isElementVisible(By by) {
		WebElement element = Browser.driver.findElement(by);
		return element.isDisplayed();
	}
	
	public static WebElement getWebElement(By by) {
		WebElement element = Browser.driver.findElement(by);
		return element;
	}
	
	public static WebElement waitForElementToBeClickable(By attribute) {
		return waitForElementToBeClickable(attribute, SECONDS_TO_WAIT);
	}
	
	public static WebElement waitForElementToBeClickable(By attribute, long secondsToWait) {
		WebElement element;
		try {
			WebDriverWait wait = new WebDriverWait(driver, secondsToWait);
			element = wait.until(ExpectedConditions.elementToBeClickable(attribute));
		} catch (StaleElementReferenceException e) {
        	Logger.warning("The Element is not Attached to the DOM due to Stale Element! Due to: " + attribute.toString());
        	element = driver.findElement(attribute);
	    }
		return element;
	}

	public static WebElement waitForElementToBeVisible(By attribute) {
		return waitForElementToBeVisible(attribute, SECONDS_TO_WAIT);
	}
	
	public static WebElement waitForElementToBeVisible(By attribute, long secondsToWait) {
		WebDriverWait wait = new WebDriverWait(driver, secondsToWait);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(attribute));
		return element;
	}

	public static boolean didElementDisappearWhileWaiting(By attribute) {
		WebDriverWait wait = new WebDriverWait(driver, SECONDS_TO_WAIT);
		return wait.until(ExpectedConditions.invisibilityOfElementLocated(attribute));
	}

	public static WebElement waitForPresenceOfElement(By locator) {
		return waitForPresenceOfElement(locator, 10);
	}
	
	public static WebElement waitForPresenceOfElement(By locator, long secondsToWait) {
		// times out after 5 seconds
		WebDriverWait wait = new WebDriverWait(driver, secondsToWait);

		// while the following loop runs, the DOM changes - 
		// page is refreshed, or element is removed and re-added
		WebElement element = wait.until(presenceOfElementLocated(locator));
		return element;
	}
	private static Function<WebDriver,WebElement> presenceOfElementLocated(final By locator) {
	    return new Function<WebDriver, WebElement>() {
	        @Override
	        public WebElement apply(WebDriver driver) {
	            return driver.findElement(locator);
	        }
	    };
	}
	
	// Wait for JavaScript Dependencies to be loaded and ready!
	public static void waitForJavaScriptDependencies() {
		try {
			JavaScriptWaiter.waitUntilJSReady();
			JavaScriptWaiter.waitUntilJQueryReady();
			Browser.setupJavaScriptErrorChecker();
		} catch (Exception e) {
			Logger.warning("JavaScript dependencies took too long loading due to: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void waitForSomeTime() {
		waitForSomeTime(2000, "");
	}

	public static void waitForSomeTime(String explanationMessage) {
		waitForSomeTime(2000, explanationMessage);
	}

	public static void waitForSomeTime(long timeoutInMillis) {
		waitForSomeTime(timeoutInMillis, "");
	}

	public static void waitForSomeTime(long timeoutInMillis, String explanationMessage) {
		try {
			Thread.sleep(timeoutInMillis);
			if (!StringUtil.isEmpty(explanationMessage)) {
				Logger.info("Waiting for: " + explanationMessage);
			}
		} catch (InterruptedException e) {
			Logger.warning("An issue occurred with the explicit wait due to: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void waitForTheLoadingOverlayToDisappear(String pageNamePrefixForLogger) {
		try {
			if (Browser.isElementVisible(By.cssSelector("#preloader"))) {
				pageNamePrefixForLogger =+ pageNamePrefixForLogger.length() > 1 ? pageNamePrefixForLogger + " " : "";  
				Logger.info(pageNamePrefixForLogger + "The loading overlay is present.  Going to wait until the page finishes loading...");
				Browser.didElementDisappearWhileWaiting(By.cssSelector("#preloader"));
			}	
		} catch (Exception e) {
			Logger.info("The loading overlay is not present.");
		}
	}

	// Scroll to web element
	public static void scrollToElememnt(WebElement element) {
		Coordinates coordinate = ((Locatable) element).getCoordinates();
		coordinate.onPage(); 
		coordinate.inViewPort();
		
		Logger.info("Scroll to element: " + element.getTagName());
	}

	//This will scroll down the page by x amount of pixel vertical
	public static void scrollTo(int pixelAmount) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0," + Integer.toString(pixelAmount) + ")");
        
        Logger.info("Scroll by " + pixelAmount + " pixel(s)");
	}

	public static void setupJavaScriptErrorChecker() {
		javascriptExecutor = (JavascriptExecutor) driver;

		//Run the JavascriptExecutor to define the jserror and attach it to the page body source code for lookup later.
        javascriptExecutor.executeScript("window.onerror=function(msg){ $('body').attr('JSError',msg); }");
	}

	public static Object getJavaScriptErrors() {
		Object errorJS = null;
		try {
			errorJS = javascriptExecutor.executeScript("return $('body').attr('jserror');");
		} catch(Exception e) {
			e.getMessage();
		}	
        return errorJS;
	}

	public static List<String> getPageLinks() {
		Set<String> uniqueLinks = new HashSet<String>();
		List<WebElement> links = Browser.driver.findElements(By.tagName("a"));
		//List<WebElement> links = Browser.driver.findElements(By.cssSelector("#productsplp a"));
		
		System.out.println("URLs found!!!!!");
		for (int i = 0; i < links.size(); i++) {
			WebElement element = links.get(i);
			// By using "href" attribute, we could get the url of the requried link
			String url = element.getAttribute("href");
			if (!StringUtil.isEmpty(url) && !url.contains("tel:")) {
				uniqueLinks.add(url);
				System.out.println("URL: " + url);
			}
		}

		System.out.println("Total links are " + uniqueLinks.size());

		return new ArrayList<String>(uniqueLinks);
	}

	public static void checkForBrokenLinks() {
		checkForBrokenLinks(new ArrayList<String>());
	}

	public static void checkForBrokenLinks(List<String> urlLinks) {
		if (urlLinks.isEmpty()) {
			urlLinks = getPageLinks();
		}
		
		for (String urlLink : urlLinks) {
			Browser.verifyLink(urlLink);
		}
	}

	// The below function verifyLink(String urlLink) verifies any broken links and return the server status. 
	public static void verifyLink(String urlLink) {
        //Sometimes we may face exception "java.net.MalformedURLException". Keep the code in try catch block to continue the broken link analysis
        try {
			//Use URL Class - Create object of the URL Class and pass the urlLink as parameter 
			URL link = new URL(urlLink);
			// Create a connection using URL object (i.e., link)
			HttpURLConnection httpConn =(HttpURLConnection)link.openConnection();
			//Set the timeout for 2 seconds
			httpConn.setConnectTimeout(2000);
			//connect using connect method
			httpConn.connect();

			if(httpConn.getResponseCode() == 200) {	
				System.out.println(urlLink+ " - " + httpConn.getResponseMessage());
				
				for (Entry<String, List<String>> header : httpConn.getHeaderFields().entrySet()) {
					System.out.println(header.getKey() + "=" + header.getValue());
				}
			}
			if(httpConn.getResponseCode() >= 400 && httpConn.getResponseCode() <= 503) {
				System.out.println(urlLink + " - " + httpConn.getResponseMessage());
				Reporter.log("Verify Link - Response Code: " + httpConn.getResponseCode() + " Issue occured for: " + urlLink, true);
			}

			String resolvedPath = httpConn.getURL().getPath();
			if (httpConn.getURL().getHost().contains("mcnichols.com")
					&& !urlLink.contains(resolvedPath)) {
				Reporter.log("Verify Link - Redirect occured for: " + urlLink, true);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public static void checkJavaScriptErrorLog() {
		// Capture all JS errors but log only the severe ones
		LogEntries logEntries = Browser.driver.manage().logs().get(LogType.BROWSER);
		
		for (LogEntry entry : logEntries) {
			if (Level.SEVERE.getName().equals(entry.getLevel().getName())) {
				Logger.warning("JavaScript Error: " + entry.getMessage());
			} else if (Level.WARNING.getName().equals(entry.getLevel().getName())) {
				Logger.warning("JavaScript Warning: " + entry.getMessage());
			} else {
				Logger.info("JavaScript Info: " + entry.getMessage());
			}
		}
	}
	
	public static void getPageEnvironmentDetails() {
		try {
			String pageType = JavaScriptWaiter.getValueFromConsole("MCN.page.pageType");
			String sourceName = JavaScriptWaiter.getValueFromConsole("MCN.page.source.name");
			String sourceIdentifier = JavaScriptWaiter.getValueFromConsole("MCN.page.source.identifier");
			
			if (StringUtil.isNotEmpty(pageType)) {
				Logger.info("Page Type: " + pageType);	
			}
			
			if (StringUtil.isNotEmpty(sourceName)) {
				Logger.info("Page Source Name: " + sourceName);	
			}
			
			if (StringUtil.isNotEmpty(sourceIdentifier)) {
				Logger.info("Page Source Identifier: " + sourceIdentifier);	
			}
		} catch (Exception e) {
			//e.printStackTrace();
			e.getMessage();
		}
	}
	
	public static int getCartedItemsCount() {
		int numberOfCartedItems = 0;
		try {
			String itemCount = JavaScriptWaiter.getValueFromConsole("MCN.order.itemCount");
			
			if (StringUtil.isNotEmpty(itemCount)) {
				numberOfCartedItems = Integer.parseInt(itemCount);
				Logger.info("MCN object - Count of carted items: " + itemCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberOfCartedItems;
	}
	
	public static String getPageNameFromMCNObject() {
		String pageName = "";
		try {
			pageName = JavaScriptWaiter.getValueFromConsole("MCN.page.pageName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageName;
	}
}
