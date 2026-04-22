package com.mcnichols.framework.pages;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.LoginCredentials;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.config.UserCreditCard;
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
			if (StringUtil.isNotEmpty(expectedPageHeader)
					&& expectedPageHeader.toLowerCase().contains(pageHeader.toLowerCase())) {
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
		try {
			// Current checkout UI uses saved-card radio buttons instead of the legacy
			// #creditCardId selectmenu.
			List<WebElement> savedCardOptions = Browser.driver.findElements(By.xpath(
					"//div[@id='savedCardsBlock']//input[@name='savedInstrumentId' and not(@id='addNewCardRadio')]"));
			for (WebElement option : savedCardOptions) {
				if (option.isDisplayed() && option.isEnabled()) {
					Logger.info(pageNamePrefixForLogger + "Default Credit Card present.");
					return true;
				}
			}

			// Fallback for older page version where selected card text appears in a
			// selectmenu.
			if (Browser.isElementPresent(By.cssSelector("#creditCardId-button .ui-selectmenu-text"))) {
				String defaultText = "Please Select or Add a New Credit Card";
				String creditCardFormText = Browser.driver
						.findElement(By.cssSelector("#creditCardId-button .ui-selectmenu-text")).getText();

				if (StringUtil.isNotEmpty(creditCardFormText)) {
					creditCardFormText = creditCardFormText.trim();
					if (!defaultText.contains(creditCardFormText)) {
						Logger.info(pageNamePrefixForLogger + "Default Credit Card present.");
						return true;
					}
				}
			}
		} catch (Exception e) {
			Logger.warning(
					pageNamePrefixForLogger + "Unable to verify default card selection due to: " + e.getMessage());
		}

		Logger.info(pageNamePrefixForLogger + "Default Credit Card is not present.");
		return false;
	}

	public boolean isCardVerificationNumberFieldPopulated() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#cardVerificationNumber"))) {
				WebElement element = Browser.getWebElement(By.cssSelector("#cardVerificationNumber"));
				String cvnValue = element.getAttribute("value");
				if (StringUtil.isNotEmpty(cvnValue)) {
					Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is populated.");
					return true;
				}
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Unable to verify CVN value: " + e.getMessage());
		}
		Logger.info(pageNamePrefixForLogger + "Credit Card CVN field is not populated.");
		return false;
	}

	public boolean preparePaymentMethod(LoginCredentials loginCredentials) {
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
		Browser.waitForSomeTime(1000);

		if (selectExistingSavedCard()) {
			populateCardVerificationNumberField();
			return true;
		}

		Logger.info(pageNamePrefixForLogger + "No existing saved card selected. Attempting to add a new card.");
		if (!selectAddNewCardOption()) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select Add New Card option.");
			return false;
		}

		togglePopulateAddressCheckboxIfPresent();

		UserCreditCard userCreditCard = loginCredentials == null ? null : loginCredentials.getUserCreditCard();
		String cardNumber = userCreditCard == null ? "4111 1111 1111 1111" : userCreditCard.getNumber();
		String expirationMonth = userCreditCard == null ? "01" : userCreditCard.getExpirationMonth();
		String expirationYear = userCreditCard == null ? "2026" : userCreditCard.getExpirationYear();
		String cvn = properties.getProperty("credit-card.cvn");
		if (StringUtil.isEmpty(cvn) && userCreditCard != null && StringUtil.isNotEmpty(userCreditCard.getCvn())) {
			cvn = userCreditCard.getCvn();
		}

		boolean numberEntered = enterInputInAnyVisibleFrame("number", cardNumber);
		enterInputInAnyVisibleFrame("expirationMonth-autocomplete", expirationMonth);
		enterInputInAnyVisibleFrame("expirationYear-autocomplete", expirationYear);
		enterInputInAnyVisibleFrame("securityCode", cvn);

		setInputIfPresent(By.id("expirationMonth"), expirationMonth);
		setInputIfPresent(By.id("expirationYear"),
				expirationYear.length() > 2 ? expirationYear.substring(2) : expirationYear);

		if (!numberEntered) {
			Logger.warning(
					pageNamePrefixForLogger + "Did not find the card number microform field while adding a new card.");
		}

		clickAddCardModalContinueIfPresent();

		if (!isCardVerificationNumberFieldPopulated()) {
			populateCardVerificationNumberField();
		}

		return true;
	}

	private boolean selectExistingSavedCard() {
		try {
			List<WebElement> savedCardOptions = Browser.driver.findElements(By.xpath(
					"//div[@id='savedCardsBlock']//input[@name='savedInstrumentId' and not(@id='addNewCardRadio')]"));
			if (!savedCardOptions.isEmpty()) {
				WebElement option = savedCardOptions.get(0);
				try {
					option.click();
				} catch (Exception e) {
					WebElement label = option.findElement(By.xpath("./ancestor::label[1]"));
					label.click();
				}
				Logger.info(pageNamePrefixForLogger + "Selected existing saved card.");
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select existing saved card: " + e.getMessage());
		}
		return false;
	}

	private boolean selectAddNewCardOption() {
		try {
			if (Browser.isElementPresent(By.id("addNewCardRow"))) {
				Browser.click(By.id("addNewCardRow"));
			}
			if (Browser.isElementPresent(By.id("addNewCardRadio"))) {
				Browser.click(By.id("addNewCardRadio"));
				Logger.info(pageNamePrefixForLogger + "Selected Add New Card option.");
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select Add New Card option: " + e.getMessage());
		}
		return false;
	}

	private void togglePopulateAddressCheckboxIfPresent() {
		try {
			if (Browser.isElementPresent(By.id("populate-address-checkbox"))) {
				Browser.click(By.id("populate-address-checkbox"));
				Logger.info(pageNamePrefixForLogger + "Selected populate billing address checkbox.");
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Populate address checkbox was not available: " + e.getMessage());
		}
	}

	private boolean enterInputInAnyVisibleFrame(String inputId, String value) {
		if (StringUtil.isEmpty(value)) {
			return false;
		}

		List<WebElement> iframes = Browser.driver.findElements(By.tagName("iframe"));
		for (int index = 0; index < iframes.size(); index++) {
			try {
				Browser.driver.switchTo().defaultContent();
				Browser.driver.switchTo().frame(index);

				List<WebElement> inputs = Browser.driver
						.findElements(By.cssSelector("#" + inputId + ", input[name='" + inputId + "']"));
				if (!inputs.isEmpty()) {
					WebElement input = inputs.get(0);
					if (input.isDisplayed()) {
						input.clear();
						input.sendKeys(value);
						Browser.driver.switchTo().defaultContent();
						Logger.info(pageNamePrefixForLogger + "Entered value in iframe field: " + inputId);
						return true;
					}
				}
			} catch (Exception e) {
				// Ignore individual frame failures and continue searching.
			}
		}

		Browser.driver.switchTo().defaultContent();
		return false;
	}

	private void setInputIfPresent(By locator, String value) {
		if (StringUtil.isEmpty(value)) {
			return;
		}
		try {
			if (Browser.isElementPresent(locator)) {
				WebElement element = Browser.getWebElement(locator);
				element.clear();
				element.sendKeys(value);
			}
		} catch (Exception e) {
			Logger.info(
					pageNamePrefixForLogger + "Unable to populate optional field " + locator + ": " + e.getMessage());
		}
	}

	private void clickAddCardModalContinueIfPresent() {
		try {
			if (Browser.isElementPresent(By.id("continueButtonPaymentCCBtmModal"))) {
				Browser.click(By.id("continueButtonPaymentCCBtmModal"));
				Logger.info(pageNamePrefixForLogger + "Submitted new-card modal.");
				Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to submit new-card modal: " + e.getMessage());
		}
	}

	public void populateCardVerificationNumberField() {
		try {
			// CVN field is inside a CyberSource Flex Microform iframe injected into
			// #cardVerificationNumber
			Browser.scrollTo(400);
			WebDriverWait wait = new WebDriverWait(Browser.driver, 30);
			WebElement cvnIframe = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.cssSelector("#cardVerificationNumber iframe")));
			Browser.driver.switchTo().frame(cvnIframe);
			WebElement cvnInput = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
			cvnInput.clear();
			cvnInput.sendKeys(properties.getProperty("credit-card.cvn"));
			Browser.driver.switchTo().defaultContent();
			Logger.info(pageNamePrefixForLogger + "Entered Credit Card CVN number into iframe form field.");
		} catch (Exception e) {
			try {
				Browser.driver.switchTo().defaultContent();
			} catch (Exception ignored) {
			}
			Logger.warning(pageNamePrefixForLogger + "Unable to enter CVN via iframe, attempting direct field: "
					+ e.getMessage());
			// Fallback: try the element directly (non-iframe case)
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#cardVerificationNumber"));
			element.clear();
			Browser.click(By.cssSelector("#cardVerificationNumber"));
			element.sendKeys(properties.getProperty("credit-card.cvn"));
			Logger.info(pageNamePrefixForLogger + "Entered Credit Card CVN number into form field.");
		}
	}

	public void continueButton() {
		String startUrl = Browser.getCurrentURL();
		WebElement continueElement = Browser.waitForElementToBeClickable(By.cssSelector("#continueButtonPaymentCCBtm"));
		Browser.scrollToElememnt(continueElement);
		Browser.click(continueElement);
		Logger.info(pageNamePrefixForLogger + "Continue button selected.");

		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
		try {
			WebDriverWait wait = new WebDriverWait(Browser.driver, 20);
			wait.until(ExpectedConditions.or(
					ExpectedConditions.urlContains("/checkout/review.jsp"),
					ExpectedConditions.presenceOfElementLocated(By.cssSelector("#submitOrderReviewInfoBottom")),
					ExpectedConditions.presenceOfElementLocated(By.cssSelector("#error"))));
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Continue click outcome wait timed out: " + e.getMessage());
		}

		Logger.info(pageNamePrefixForLogger + "Continue start URL: " + startUrl + " current URL: "
				+ Browser.getCurrentURL());
	}
}
