package com.mcnichols.framework.pages;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.LoginCredentials;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutShippingPage {
	public static final String PROPERTY_FILE = "checkout-shipping-page-config.xml";

	public static String url = "/checkout/shipping.jsp";
	public static String title = "Shipping";
	public static String pageHeader = "SHIPPING";
	public static String pageNamePrefixForLogger = "Checkout - Shipping Page - ";

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
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
			
			Widgets.isKlaviyoFlyoutFormPresent();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public void populateShippingAddressForm() {
		populateShippingAddressForm(false, null);
	}
	
	public void populateShippingAddressForm(LoginCredentials profile) {
		populateShippingAddressForm(false, profile);
	}

	public void populateShippingAddressForm(boolean overwriteFields) {
		populateShippingAddressForm(overwriteFields, null);
	}
	
	public void populateShippingAddressForm(boolean overwriteFields, LoginCredentials profile) {
		Browser.scrollTo(300);
		Browser.waitForSomeTime();

		if (overwriteFields) {
			// Clear fields
			Browser.driver.findElement(By.id("firstName")).clear();
			Browser.driver.findElement(By.id("lastName")).clear();
			Browser.driver.findElement(By.id("address1")).clear();
			Browser.driver.findElement(By.id("city")).clear();
			Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).clear();
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).clear();
			Browser.driver.findElement(By.id("phoneNumber")).clear();

			// Populate
			Browser.driver.findElement(By.id("firstName")).sendKeys(properties.getProperty("guest.first.name"));
			Browser.driver.findElement(By.id("lastName")).sendKeys(properties.getProperty("guest.last.name"));
			Browser.driver.findElement(By.id("address1")).sendKeys(properties.getProperty("guest.address1"));
			Browser.driver.findElement(By.id("city")).sendKeys(properties.getProperty("guest.city"));
			
			try {
				Browser.waitForSomeTime();
				Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).click();				
				Browser.scrollTo(400);
				Browser.waitForSomeTime();
				List<WebElement> stateElements = Browser.driver.findElements(By.cssSelector("ul#state-menu li div"));
				for (WebElement stateElement : stateElements) {
					String state = stateElement.getText();
					
					if (!StringUtil.isEmpty(state) && state.contains(properties.getProperty("guest.state"))) {
						Browser.waitForSomeTime();
						Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + stateElement.getText());
						Browser.scrollToElememnt(stateElement);
						stateElement.click();
						break;
					}
				}
			} catch(Exception e) {
				Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
			}
			
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).clear();
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).click();
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).sendKeys(properties.getProperty("guest.zip.code"));

			Browser.driver.findElement(By.id("phoneNumber")).clear();
			Browser.driver.findElement(By.id("phoneNumber")).click();
			Browser.driver.findElement(By.id("phoneNumber")).sendKeys(properties.getProperty("guest.phone"));
		} else {
			Browser.waitForSomeTime();
			Browser.waitForElementToBeClickable(By.id("firstName")); // Verify form is ready to be interacted with
			if (StringUtil.isEmpty(Browser.driver.findElement(By.id("firstName")).getAttribute("value"))) {
				Browser.driver.findElement(By.id("firstName")).clear();
				if (profile != null) {
					Browser.driver.findElement(By.id("firstName")).sendKeys(profile.getFistName());
				} else {
					Browser.driver.findElement(By.id("firstName")).sendKeys(properties.getProperty("guest.first.name"));	
				}
			}
			if (StringUtil.isEmpty(Browser.driver.findElement(By.id("lastName")).getAttribute("value"))) {
				Browser.driver.findElement(By.id("lastName")).clear();
				if (profile != null) {
					Browser.driver.findElement(By.id("lastName")).sendKeys(profile.getLastName());
				} else {
					Browser.driver.findElement(By.id("lastName")).sendKeys(properties.getProperty("guest.last.name"));	
				}
			}
			if (StringUtil.isEmpty(Browser.driver.findElement(By.id("address1")).getAttribute("value"))) {
				Browser.driver.findElement(By.id("address1")).clear();
				Browser.driver.findElement(By.id("address1")).sendKeys(properties.getProperty("guest.address1"));
			}
			if (StringUtil.isEmpty(Browser.driver.findElement(By.id("city")).getAttribute("value"))) {
				Browser.driver.findElement(By.id("city")).clear();
				Browser.driver.findElement(By.id("city")).sendKeys(properties.getProperty("guest.city"));
			}

			try {
				Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).click();
				Browser.waitForSomeTime();
				List<WebElement> stateElements = Browser.driver.findElements(By.cssSelector("ul#state-menu li div"));
				for (WebElement stateElement : stateElements) {
					String state = stateElement.getText();
					if (!StringUtil.isEmpty(state) && state.contains(properties.getProperty("guest.state"))) {
						Browser.waitForSomeTime();
						Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + stateElement.getText());
						stateElement.click();
						break;
					}
				}
			} catch(Exception e) {
				Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
			}

			if (StringUtil.isEmpty(Browser.driver.findElement(By.cssSelector("#postalCodeContainer #postalCode")).getAttribute("value"))) {
				Browser.driver.findElement(By.cssSelector("#postalCodeContainer #postalCode")).clear();
				Browser.driver.findElement(By.cssSelector("#postalCodeContainer #postalCode")).click();
				Browser.driver.findElement(By.cssSelector("#postalCodeContainer #postalCode")).sendKeys(properties.getProperty("guest.zip.code"));	
			}

			if (StringUtil.isEmpty(Browser.driver.findElement(By.id("phoneNumber")).getAttribute("value"))) {
				Browser.scrollTo(400);
				Browser.waitForSomeTime();
				
				Browser.driver.findElement(By.id("phoneNumber")).clear();
				Browser.click(By.id("phoneNumber"));
				Browser.driver.findElement(By.id("phoneNumber")).sendKeys(properties.getProperty("guest.phone"));	
			}
		}
		
		Logger.info(pageNamePrefixForLogger + "Entered values into address information form.");
	}

	public void selectSpecialHandlingRequired(boolean isRequired) {
		try {
			String cssSelector = isRequired ? "#isSpecialHandleTrue" : "#isSpecialHandleFalse";
			WebElement element = Browser.getWebElement(By.cssSelector(cssSelector));
			element.click();
			Logger.info(pageNamePrefixForLogger + "Selecting special handling.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select special handling!");
		}
	}

	public void selectDeliveryAppoinmentRequired(boolean isRequired) {
		try {
			String cssSelector = isRequired ? "#preNotificationTrue" : "#preNotificationFalse";
			WebElement element = Browser.getWebElement(By.cssSelector(cssSelector));
			element.click();
			Logger.info(pageNamePrefixForLogger + "Selecting delivery appointment.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select delivery appointment!");
		}
	}

	public void selectInstructionsRequired(boolean isRequired) {
		try {
			String cssSelector = isRequired ? "#addInstructionsTrue" : "#addInstructionsFalse";
			WebElement element = Browser.getWebElement(By.cssSelector(cssSelector));
			Browser.scrollToElememnt(element);
			Browser.scrollTo(300);
			Logger.info(pageNamePrefixForLogger + "Selecting special instructions.");
			element.click();
			Browser.waitForSomeTime();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select special instructions!");
		}
	}

	public void continueButton() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#bottomContinueButtonShippingDelivery"));
			Browser.scrollToElememnt(element);
			Browser.waitForSomeTime();
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Continue button selected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select continue button!");
		}
	}

	public boolean isErrorPresent() {
		boolean isErrorPresent = false;

		try {
			Browser.waitForSomeTime();
			if (Browser.isElementPresent(By.cssSelector("#error")) && Browser.isElementVisible(By.cssSelector("#error"))) {
				String errorsText = Browser.driver.findElement(By.cssSelector("#error")).getText();
				if (StringUtil.isNotEmpty(errorsText)) {
					isErrorPresent = true;
					Logger.warning("Checkout Shipping Page - Found the following form validation errors: " + errorsText);
				}
			}
			if (!isErrorPresent) {
				Logger.info(pageNamePrefixForLogger + "No form validatoin errors occured.");
			}
		} catch (Exception e) {
			// Since there is a delay for the form error, check if the element is stale (StaleElementReferenceException) and allow test to move forward.
			Logger.info(pageNamePrefixForLogger + "isErrorPresent failed due to: " + e.getMessage());
		}

		return isErrorPresent;
	}
}
