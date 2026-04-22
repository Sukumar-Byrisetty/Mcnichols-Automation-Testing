package com.mcnichols.framework.pages;

import java.util.Properties;

import org.openqa.selenium.By;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutOrderConfirmationPage {
	public static final String PROPERTY_FILE = "checkout-order-confirmation-page-config.xml";

	public static String url = "/checkout/confirmation.jsp";
	public static String title = "Order Confirmation";
	public static String pageHeader = "ITEMS ORDERED";
	public static String pageNamePrefixForLogger = "Checkout - Order Confirmation Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isAt() {
		return isAt(false);
	}
	
	public boolean isAt(boolean allowAdditionalAttempt) {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();			
			isAt = isAtOrderConfirmationPageVerified();
			
			if (!isAt && allowAdditionalAttempt) {
				Logger.warning("Time exceeded to verify page.  Trying again...");
				isAt = isAtOrderConfirmationPageVerified();
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}
	
	private boolean isAtOrderConfirmationPageVerified() {
		boolean isAt = false;
		Browser.waitForJavaScriptDependencies();
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

		if (Browser.title().contains((title))) {
			Logger.info(pageNamePrefixForLogger + "title verified.");

			String expectedPageHeader = Browser.driver.findElement(By.cssSelector(".confirm-title-view .confirmation-items-ordered-label")).getText().toLowerCase();
			if (StringUtil.isNotEmpty(expectedPageHeader) && expectedPageHeader.contains(pageHeader.toLowerCase())) {
				Logger.info(pageNamePrefixForLogger + "page header verified.");
				isAt = true;
			} else {
				Logger.warning(pageNamePrefixForLogger + "isAt failted by page header!");
			}
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isOrderNumberPresent() {
		if (Browser.isElementPresent(By.cssSelector("#confirmation-page .confirm-order-id"))) {
			String orderNumberText = Browser.driver.findElement(By.cssSelector("#confirmation-page .confirm-order-id")).getText();
			Logger.info(pageNamePrefixForLogger + "Confirmed Web Reference #: " + orderNumberText);
			return true;
		}
		Logger.warning(pageNamePrefixForLogger + "Web Reference # is missing!");
		return false;
	}
}
