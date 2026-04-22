package com.mcnichols.framework.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutPaymentPage {
	public static final String PROPERTY_FILE = "checkout-payment-page-config.xml";

	public static String url = "/checkout/payment.jsp";
	public static String title = "Payment";
	public static String pageHeader = "PAYMENT METHOD";
	public static String pageNamePrefixForLogger = "Checkout - Payment Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains((title))) {
				Logger.info(pageNamePrefixForLogger + "title verified.");
				isAt = true;
			}

			String expectedPageHeader = Browser.driver.findElement(By.cssSelector(".payment-title")).getText();
			if (StringUtil.isNotEmpty(expectedPageHeader) && expectedPageHeader.toLowerCase().contains(pageHeader.toLowerCase())) {
				Logger.info(pageNamePrefixForLogger + "page header verified.");
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}

		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isDefaultCreditCardSelected() {
		String defaultText = "Please Select or Add a New Credit Card";
		String creditCardFormText = Browser.driver.findElement(By.cssSelector("#creditCardId-button .ui-selectmenu-text")).getText();

		if (StringUtil.isNotEmpty(creditCardFormText)) {
			creditCardFormText = creditCardFormText.trim();
			if (!defaultText.contains(creditCardFormText)) {
				Logger.info(pageNamePrefixForLogger + "Default Credit Card present.");
				return true;
			}
		}
		Logger.info(pageNamePrefixForLogger + "Default Credit Card is not present.");
		return false;
	}

	public boolean isCardVerificationNumberFieldPopulated() {
		WebElement element = Browser.getWebElement(By.cssSelector("#cardVerificationNumber"));
		String cvnValue = element.getText();
		if (StringUtil.isNotEmpty(cvnValue)) {
			Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is populated.");
			return true;
		}
		Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is not populated.");
		return false;
	}

	public void populateCardVerificationNumberField() {
		WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#cardVerificationNumber"));
		element.isDisplayed();

		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber")).clear();
		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber")).click();
		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber")).sendKeys(properties.getProperty("credit-card.cvn"));
		Logger.info(pageNamePrefixForLogger + "Entered Credit Card CVN number into form field.");
	}
	
	public void continueButton() {
		WebElement avsElement = Browser.getWebElement(By.cssSelector("#continueButtonPaymentCCBtm"));
		avsElement.click();
		Logger.info(pageNamePrefixForLogger + "Continue button selected.");
	}
}
