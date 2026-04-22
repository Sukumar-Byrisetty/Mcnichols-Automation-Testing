package com.mcnichols.framework.pages;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutContactInformationPage {
	public static final String PROPERTY_FILE = "checkout-contact-information-page-config.xml";

	public static String url = "/checkout/contact-info.jsp";
	public static String title = "McNichols - Contact Info";
	public static String titleLogin = "McNichols - Login";
	public static String titleLoginUpdate = "Log In or Continue As Guest";
	public static String pageHeader = "CONTACT INFORMATION";
	public static String pageNamePrefixForLogger = "Checkout - Contact Information Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains((title)) || Browser.title().contains(titleLogin) || Browser.title().contains(titleLoginUpdate)) {
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
	
	public void scrollToPageHeader() {
		String contactContainerV3 = ".contact-info-parent";
		if (Browser.isElementPresent(By.cssSelector(contactContainerV3))) {
			//contactContainer = ".contact-info-header";
			Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector(contactContainerV3)));
		}
		
		String contactContainer = ".contact-info-h1";
		if (Browser.isElementPresent(By.cssSelector(contactContainer))) {
			Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector(contactContainer)));
		}
	}

	public void enterContactInformationForm() {
		//Browser.scrollTo(300);
		Browser.waitForSomeTime();
		Browser.driver.findElement(By.id("firstName")).sendKeys(properties.getProperty("guest.first.name"));
		Browser.driver.findElement(By.id("lastName")).sendKeys(properties.getProperty("guest.last.name"));
		Browser.driver.findElement(By.id("phoneNumber")).clear();
		Browser.driver.findElement(By.id("phoneNumber")).click();
		Browser.driver.findElement(By.id("phoneNumber")).sendKeys(properties.getProperty("guest.phone"));
		Browser.driver.findElement(By.id("email")).sendKeys(properties.getProperty("guest.email"));
		Browser.driver.findElement(By.id("address1")).sendKeys(properties.getProperty("guest.address1"));
		Browser.driver.findElement(By.id("city")).sendKeys(properties.getProperty("guest.city"));

		try {
			Browser.waitForSomeTime(10000); // Add time to allow the Intercom modal processing
			Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).click();
			Browser.scrollTo(400);
			Browser.waitForSomeTime();
			List<WebElement> stateElements = Browser.driver.findElements(By.cssSelector("ul#state-menu li div"));
			for (WebElement stateElement : stateElements) {
				String state = stateElement.getText();
				
				if (!StringUtil.isEmpty(state) && state.contains("FL - Florida")) {
					Browser.waitForSomeTime();
					Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + stateElement.getText());
					Browser.click(stateElement);
					break;
				}
			}
		} catch(Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
		}
		
		Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).clear();
		Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).click();
		Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).sendKeys(properties.getProperty("guest.zip.code"));
	}

	public void continueButton() {
		WebElement avsElement = Browser.getWebElement(By.cssSelector(".contact-info-continueBtn"));
		Browser.click(avsElement);
		Logger.info(pageNamePrefixForLogger + "Continue button selected.");
	}
	
	public void backButton() {
		WebElement avsElement = Browser.getWebElement(By.cssSelector(".contact-info-backBtn"));
		avsElement.click();
		Logger.info(pageNamePrefixForLogger + "Back button selected.");
	}
}
