package com.mcnichols.framework.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutOrderReviewExpressCheckoutPage {
	public static final String PROPERTY_FILE = "checkout-order-review-express-checkout-page-config.xml";

	public static String url = "/checkout/review-express.jsp";
	public static String title = "McNichols - Review Order";
	public static String pageHeader = "EXPRESS CHECKOUT";
	public static String pageNamePrefixForLogger = "Checkout - Order Review Express Checkout Page - ";

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

			if (Browser.title().contains((title))) {
				Logger.info(pageNamePrefixForLogger + "title verified.");
				isAt = true;
			}

			By[] pageHeaderSelectors = new By[] {
					By.cssSelector("#order-review-info"),
					By.cssSelector(".order-review-info"),
					By.cssSelector("h1"),
					By.cssSelector(".cart-title") };

			for (By headerSelector : pageHeaderSelectors) {
				if (Browser.isElementPresent(headerSelector)) {
					String expectedPageHeader = Browser.driver.findElement(headerSelector).getText();
					if (StringUtil.isNotEmpty(expectedPageHeader)
							&& (expectedPageHeader.toUpperCase().contains(pageHeader)
									|| expectedPageHeader.toUpperCase().contains("EXPRESS")
									|| expectedPageHeader.toUpperCase().contains("REVIEW"))) {
						Logger.info(pageNamePrefixForLogger + "page header verified by selector: " + headerSelector);
						isAt = true;
						break;
					}
				}
			}

			if (!isAt) {
				Logger.warning(pageNamePrefixForLogger + "isAt failted by page header!");
				Logger.warning(pageNamePrefixForLogger + "Current URL: " + Browser.getCurrentURL());
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
			if (Browser.isElementPresent(By.cssSelector("#main-page-content #error"))
					&& Browser.isElementVisible(By.cssSelector("#main-page-content #error"))) {
				String errorsText = Browser.driver.findElement(By.cssSelector("#main-page-content #error")).getText();
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
				By.xpath("//div[@id='main-page-content']/div[2]/div[3]/div/div[5]/label"),
				By.cssSelector("#main-page-content label.checkbox-custom-circle"),
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
	}

	public boolean isCardVerificationNumberFieldPopulated() {
		WebElement element = Browser.getWebElement(By.cssSelector("#cardVerificationNumber"));
		String cvnValue = element.getAttribute("value");
		if (StringUtil.isNotEmpty(cvnValue)) {
			Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is populated.");
			return true;
		}
		Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is not populated.");
		return false;
	}

	public void populateCardVerificationNumberField() {
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
		Browser.waitForSomeTime();

		WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#cardVerificationNumber"));
		element.isDisplayed();

		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber")).clear();
		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber")).click();
		Browser.driver.findElement(By.cssSelector("#cardVerificationNumber"))
				.sendKeys(properties.getProperty("credit-card.cvn"));
		Logger.info(pageNamePrefixForLogger + "Entered Credit Card CVN number into form field.");

		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
	}

	public void submitOrderTopButton() {
		try {
			WebElement element = Browser.getWebElement(By.cssSelector("#submitOrderReviewInfo1"));
			Browser.scrollToElememnt(element);
			Browser.waitForSomeTime();
			element.click();
			Logger.info(pageNamePrefixForLogger + "Submit Order top button elected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Failed to select the Submit Order top button!");
		}
	}

	public void submitOrderBottomButton() {
		try {
			WebElement element = Browser
					.getWebElement(By.cssSelector("#submitBtnReviewPagesBottom > button#submitOrderReviewInfo2"));
			Browser.scrollToElememnt(element);
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			Browser.waitForSomeTime();
			element.click();
			Logger.info(pageNamePrefixForLogger + "Submit Order bottom button elected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Failed to select the Submit Order bottom button!");
		}
	}
}
