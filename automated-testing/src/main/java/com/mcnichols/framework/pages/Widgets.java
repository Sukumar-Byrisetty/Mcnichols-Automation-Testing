package com.mcnichols.framework.pages;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.framework.util.TestUtil;

public class Widgets {
	public static final String PROPERTY_FILE = "widgets-config.xml";
	public static String pageNamePrefixForLogger = "Widget - ";
	static Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isBackToTopVisibile() {
		try {
			WebElement element = Browser.driver.findElement(By.cssSelector("#back-top i"));
			if (element.isDisplayed()) {
				Logger.info(pageNamePrefixForLogger + "Back to top - Is visible.");
				return true;
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Back to top - Is not visible.");
		}
		return false;
	}

	public void selectBackToTopElement() {
		try {
			WebElement element = Browser.driver.findElement(By.cssSelector("#back-top i"));
			element.click();
			Logger.info(pageNamePrefixForLogger + "Back to top - Selected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Back to top - Unable to Select!");
		}
	}

	public boolean isAddressVerificationDisplay() {
		try {
			Logger.info(pageNamePrefixForLogger + "Address Verification - Checking if modal is present...");
			WebElement avsElement = Browser.waitForElementToBeVisible(By.cssSelector("#avsModalContainer"));
			if (avsElement.isDisplayed()) {
				Logger.info(pageNamePrefixForLogger + "Address Verification - Is present.");
				return true;	
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Address Verification - is not present.");
		}
		return false;
	}

	public void selectAddressVerificationContinue() {
		Browser.waitForSomeTime();
		
		// Shipping page continue button
		By submitButton = By.cssSelector("#avsModalContainer button.avs-submit");
		
		if (!Browser.isElementPresent(submitButton)) {
			// Create an account continue button
			submitButton = By.cssSelector("#avsModalContainer button.button-dark");	
		}
		
		Browser.waitForElementToBeClickable(submitButton, 45);

		try {
			TestUtil.takeScreenshot("address-verification");
			Browser.click(submitButton);
			
			Logger.info(pageNamePrefixForLogger + "Address Verification - Selected continue button.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Address Verification - Issue clicking Continue!!!");
		}
	}

	public boolean isAnnouncementModalVisibile() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#announcementModalContainer"), 5);
			if (element.isDisplayed()) {
				Logger.info(pageNamePrefixForLogger + "Announcement Modal - Is visible.");
				return true;
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Announcement Modal - Is not visible.");
		}
		return false;
	}
	
	public boolean isPromoBarVisibile() {
		try {
			// Click on page to initiate the lazy load of Intercom
			String searchBoxSelector = ".elementor-element-82c56dc #headerSearchQuestionFromHeader";
			if (Browser.isElementVisible(By.cssSelector(searchBoxSelector))) {
				searchBoxSelector = ".elementor-element-82c56dc #headerSearchQuestionFromHeader";
				Browser.driver.findElement(By.cssSelector(searchBoxSelector)).click();
			} else {
				WebElement elementToHover = Browser.driver.findElement(By.id("menu-header-menu"));
				Actions actions = new Actions(Browser.driver);
				// Perform the hover action
				actions.moveToElement(elementToHover).perform();
			}
			
			Browser.waitForSomeTime(5000);
			Browser.driver.switchTo().frame("intercom-banner-frame");
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#intercom-container-body"), 5);
			if (element.isDisplayed()) {
				Logger.info(pageNamePrefixForLogger + "Promo Bar - Is visible.");
				Browser.driver.switchTo().defaultContent();
				return true;
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Promo Bar - Is not visible.");
			Browser.driver.switchTo().defaultContent();
		}
		
		return false;
	}
	
	public void closePromoBar() {
		try {
			WebElement element = Browser.getWebElement(By.cssSelector("#intercom-container-body"));
			if (element.isDisplayed()) {
				Logger.info(pageNamePrefixForLogger + "Promo Bar - Is visible.");
			}
		} catch (Exception e) {
			Browser.driver.switchTo().frame("intercom-banner-frame");
			Logger.info(pageNamePrefixForLogger + "Promo Bar - Is not visible.");
		}
		
		Browser.driver.findElement(By.cssSelector("polygon")).click();
		Browser.driver.switchTo().defaultContent();
	}
	
	public void closeAnnouncementModal() {
		Browser.click(By.cssSelector("#announcementModalContainer .announcement-modal-close"));
	}
	
	public static void isKlaviyoFlyoutFormPresent() {
		boolean isKlaviyoFlyoutFormEnabled = StringUtil.isNotEmpty(properties.getProperty("isKlaviyoFlyoutFormEnabled")) ? Boolean.parseBoolean((String) properties.getProperty("isKlaviyoFlyoutFormEnabled")) : false;
		boolean isKlaviyoFormPresent = Browser.isElementPresent(By.cssSelector(".klaviyo-form .go3176171171"));
		
		if (isKlaviyoFlyoutFormEnabled || isKlaviyoFormPresent) {
			try {
				//WebElement klaviyoForm = Browser.waitForElementToBeVisible(By.cssSelector(".klaviyo-form"), 5);
				WebElement klaviyoForm = Browser.getWebElement(By.cssSelector(".klaviyo-form .go3176171171"));
				
				if (klaviyoForm.isDisplayed()) {
					Logger.info(pageNamePrefixForLogger + "Klaviyo Flyout Form - Is visible.");
					
					WebElement klaviyoFormCloseElement = Browser.getWebElement(By.cssSelector(".klaviyo-close-form"));
					Browser.click(klaviyoFormCloseElement);
					Browser.waitForSomeTime();
				}
			} catch (Exception e) {
				Logger.info(pageNamePrefixForLogger + "Klaviyo Flyout Form - Is not visible.");
			}
		}
	}
	
	public boolean isAccountConfirmationModalPresent() {
		boolean isAt = false;
		String modalHeader = "Account Confirmation";
		
		try {
			Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - Checking if modal is present...");
			WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#regModalContainer h2"));
			String expectedHeader = Browser.driver.findElement(By.cssSelector("#regModalContainer h2")).getText();
			
			if (element.isDisplayed() && StringUtil.isNotEmpty(expectedHeader) && expectedHeader.contains(modalHeader)) {
				Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - modal header verified.");
				isAt = true;
			}
			
			if (isAt) {
				Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - Is present.");
			} else {
				Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - is not present.");	
			}
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - is not present.");
		}
		return isAt;
	}
	
	public void selectAccountConfirmationButtonForModal(boolean isMyAccount) {
		Browser.waitForSomeTime();

		try {
			TestUtil.takeScreenshot("account-confirmation-modal");
			if (isMyAccount) {
				// This is My Account - Select the first account match
				WebElement firstAccountMatchElement = Browser.getWebElement(By.cssSelector("#regModalContainer > div > div > div.modal-body.modal-body-content.pb-4 > div > form > div > label:nth-child(1)"));
				firstAccountMatchElement.click();
				
				By submitButton = By.cssSelector("#regModalContainer #confirm-account-btn");
				Browser.waitForElementToBeClickable(submitButton, 5);
				Browser.click(submitButton);
				
				Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - Selected This is My Account button.");
			} else {
				// This is Not My Account
				By submitButton = By.cssSelector("#regModalContainer #setup-new-account-btn");
				Browser.waitForElementToBeClickable(submitButton, 5);
				Browser.click(submitButton);
				
				Logger.info(pageNamePrefixForLogger + "Account Confirmation Modal - Selected This is Not My Account button.");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Account Confirmation Modal - Issue clicking Button!!!");
		}
	}
}
