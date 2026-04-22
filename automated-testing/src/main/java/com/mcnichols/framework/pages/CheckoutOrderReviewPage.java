package com.mcnichols.framework.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutOrderReviewPage {
	public static final String PROPERTY_FILE = "checkout-order-review-page-config.xml";

	public static String url = "/checkout/review.jsp";
	public static String title = "Order Review";
	public static String pageHeader = "ORDER REVIEW";
	public static String pageNamePrefixForLogger = "Checkout - Order Review Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isAt() {

		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			String currentUrl = Browser.getCurrentURL();
			if (StringUtil.isNotEmpty(currentUrl) && currentUrl.contains(url)) {
				Logger.info(pageNamePrefixForLogger + "URL verified: " + currentUrl);
				isAt = true;
			}

			String currentTitle = Browser.title();
			if (StringUtil.isNotEmpty(currentTitle)
					&& (currentTitle.toLowerCase().contains(title.toLowerCase())
							|| currentTitle.toLowerCase().contains("review order"))) {
				Logger.info(pageNamePrefixForLogger + "title verified: " + currentTitle);
				isAt = true;
			}

			if (Browser.isElementPresent(By.cssSelector("#submitOrderReviewInfoBottom"))) {
				Logger.info(pageNamePrefixForLogger + "submit order button verified.");
				isAt = true;
			}

			if (Browser.isElementPresent(By.cssSelector("#order-review-info"))) {
				String expectedPageHeader = Browser.driver.findElement(By.cssSelector("#order-review-info")).getText()
						.toLowerCase();
				if (StringUtil.isNotEmpty(expectedPageHeader)
						&& expectedPageHeader.contains(pageHeader.toLowerCase())) {
					Logger.info(pageNamePrefixForLogger + "page header verified.");
					isAt = true;
				}
			}

			if (!isAt) {
				Logger.warning(pageNamePrefixForLogger + "isAt failed. URL: " + currentUrl + " Title: " + currentTitle);
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isErrorPresent() {
		boolean isErrorPresent = false;

		try {
			Browser.waitForSomeTime();
			if (Browser.isElementPresent(By.cssSelector("#error"))
					&& Browser.isElementVisible(By.cssSelector("#error"))) {
				String errorsText = Browser.driver.findElement(By.cssSelector("#error")).getText();
				if (StringUtil.isNotEmpty(errorsText)) {
					isErrorPresent = true;
					Logger.warning(
							pageNamePrefixForLogger + "Found the following form validation errors: " + errorsText);
				}
			}
			if (!isErrorPresent) {
				Logger.info(pageNamePrefixForLogger + "No form validatoin errors occured.");
			}
		} catch (Exception e) {
			// Since there is a delay for the form error, check if the element is stale
			// (StaleElementReferenceException) and allow test to move forward.
			Logger.info(pageNamePrefixForLogger + "isErrorPresent failed due to: " + e.getMessage());
		}
		return isErrorPresent;
	}

	public void selectTermsAndConditions() {
		Browser.waitForSomeTime();
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

		By[] selectors = new By[] {
				By.cssSelector("div.mt-4 > label.checkbox-custom-circle"),
				By.cssSelector(".hidden-xs > .checkbox-custom-circle"),
				By.cssSelector("label.checkbox-custom-circle"),
				By.cssSelector("label[for='termsAndConditions']"),
				By.id("termsAndConditions") };

		WebElement termsElement = null;
		for (By selector : selectors) {
			if (Browser.isElementPresent(selector)) {
				WebElement candidate = Browser.getWebElement(selector);
				if (candidate.isDisplayed()) {
					termsElement = candidate;
					break;
				}
			}
		}

		if (termsElement == null) {
			throw new RuntimeException(pageNamePrefixForLogger + "Terms and Conditions element not found.");
		}

		Browser.scrollToElememnt(termsElement);
		Browser.click(termsElement);

		WebElement termsInput;
		if ("input".equalsIgnoreCase(termsElement.getTagName())) {
			termsInput = termsElement;
		} else {
			termsInput = termsElement.findElement(By.cssSelector("input[type='checkbox']"));
		}

		if (!termsInput.isSelected()) {
			Browser.click(termsInput);
		}

		if (!termsInput.isSelected()) {
			throw new RuntimeException(pageNamePrefixForLogger + "Unable to select Terms and Conditions checkbox.");
		}

		Logger.info(pageNamePrefixForLogger + "Terms and Conditions selected.");
		Browser.waitForSomeTime();
	}

	public void selectSaveForExpressCheckout() {
		WebElement element = Browser.getWebElement(By.cssSelector("#saveForExpressCheckout"));
		element.click();
		Logger.info(pageNamePrefixForLogger + "Save settings for Express Checkout selected.");
	}

	public void submitOrderButton() {
		try {
			WebElement element = Browser.getWebElement(By.cssSelector("#submitOrderReviewInfoBottom"));
			Browser.scrollToElememnt(element);
			Browser.waitForSomeTime();
			element.click();
			Logger.info(pageNamePrefixForLogger + "Submit Order selected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Failed to select the Submit Order button!");
		}
	}
}
