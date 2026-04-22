package com.mcnichols.framework.pages.account;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.config.UserProfile;
import com.mcnichols.framework.pages.Widgets;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class RegistrationPage {
	public static String pageNamePrefixForLogger = "Registration Page - ";
	public static String url = "/account/create-an-account.jsp";
	public static String title = "Create Account";
	public static String pageHeader = "Enter Account Information";
	
	public boolean isAt() {
		Browser.waitForJavaScriptDependencies();
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			String browserPageTitle = Browser.driver.getTitle();
			if (browserPageTitle.contains(title)) {
				Logger.info(pageNamePrefixForLogger + "Title verified.");
				isAt = true;
			}
			
			String expectedPageHeader = Browser.driver.findElement(By.cssSelector("#create-an-account-form h1")).getText();
			if (!isAt && StringUtil.isNotEmpty(expectedPageHeader) && expectedPageHeader.contains(pageHeader)) {
				Logger.info(pageNamePrefixForLogger + "page header verified.");
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
	
	public void enterProfileInformation(int accountType) {
		String securityQuestionSelect = "What is your favorite color?"; // ToDo: Move to JSON config
		
		String userEmailAddress = TestingConfig.getLogin(accountType).getUsername();
		String userPassword = TestingConfig.getLogin(accountType).getPassword();
		UserProfile userProfile = TestingConfig.getLogin(accountType).getUserProfile();
		
		// Scroll to profile section
		Browser.scrollToElememnt(Browser.getWebElement(By.id("personal-info")));
		
		// First name
		Browser.getWebElement(By.cssSelector("input#firstName")).click();
		Browser.driver.findElement(By.cssSelector("input#firstName")).clear();
		Browser.driver.findElement(By.cssSelector("input#firstName")).sendKeys(userProfile.getFistName());
		
		// Last name
		Browser.getWebElement(By.cssSelector("input#lastName")).click();
		Browser.driver.findElement(By.cssSelector("input#lastName")).clear();
		Browser.driver.findElement(By.cssSelector("input#lastName")).sendKeys(userProfile.getLastName());
		
		// Confirm email address
		Browser.getWebElement(By.cssSelector("input#confirmEmail")).click();
		Browser.driver.findElement(By.cssSelector("input#confirmEmail")).clear();
		Browser.driver.findElement(By.cssSelector("input#confirmEmail")).sendKeys(userEmailAddress);
		
		// Password
		Browser.getWebElement(By.cssSelector("input#password")).click();
		Browser.driver.findElement(By.cssSelector("input#password")).clear();
		Browser.driver.findElement(By.cssSelector("input#password")).sendKeys(userPassword);
		
		// Confirm Password
		Browser.getWebElement(By.cssSelector("input#confirmPassword")).click();
		Browser.driver.findElement(By.cssSelector("input#confirmPassword")).clear();
		Browser.driver.findElement(By.cssSelector("input#confirmPassword")).sendKeys(userPassword);
		
		// Select security question
		try {
			Browser.driver.findElement(By.cssSelector("#security-questions-button > .ui-selectmenu-text")).click();
			Browser.waitForSomeTime();
			List<WebElement> securityQuestionElements = Browser.driver.findElements(By.cssSelector("ul#security-questions-menu li div"));
			for (WebElement securityQuestionElement : securityQuestionElements) {
				String question = securityQuestionElement.getText();
				if (!StringUtil.isEmpty(question) && question.contains(securityQuestionSelect)) {
					Browser.waitForSomeTime();
					Logger.info(pageNamePrefixForLogger + "Selecting security question dropdown value: " + securityQuestionElement.getText());
					securityQuestionElement.click();
					break;
				}
			}
		} catch(Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select security question from dropdown form!");
		}
		
		// Security Answer
		Browser.getWebElement(By.cssSelector("input#securityAnswer")).click();
		Browser.driver.findElement(By.cssSelector("input#securityAnswer")).clear();
		Browser.driver.findElement(By.cssSelector("input#securityAnswer")).sendKeys(userProfile.getColorSecurityAnwser());
	}
	
	public void enterOtherInformation(int accountType) {
		UserProfile userProfile = TestingConfig.getLogin(accountType).getUserProfile();
		
		// Scroll to profile section
		Browser.scrollToElememnt(Browser.getWebElement(By.id("account-info")));
		
		// Address 1
		Browser.getWebElement(By.cssSelector("input#address1")).click();
		Browser.driver.findElement(By.cssSelector("input#address1")).clear();
		Browser.driver.findElement(By.cssSelector("input#address1")).sendKeys(userProfile.getAddress());
		
		// City
		Browser.getWebElement(By.cssSelector("input#city")).click();
		Browser.driver.findElement(By.cssSelector("input#city")).clear();
		Browser.driver.findElement(By.cssSelector("input#city")).sendKeys(userProfile.getCity());
		
		// State
		try {
			Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).click();
			Browser.waitForSomeTime();
			List<WebElement> stateElements = Browser.driver.findElements(By.cssSelector("ul#state-menu li div"));
			for (WebElement stateElement : stateElements) {
				String state = stateElement.getText();
				if (!StringUtil.isEmpty(state) && state.contains(userProfile.getState())) {
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
		
		// Zip
		Browser.getWebElement(By.cssSelector("input#postalCode")).click();
		Browser.driver.findElement(By.cssSelector("input#postalCode")).clear();
		Browser.driver.findElement(By.cssSelector("input#postalCode")).sendKeys(userProfile.getZip());
		
		// Phone number
		Browser.getWebElement(By.cssSelector("input#phoneNumber")).click();
		Browser.driver.findElement(By.cssSelector("input#phoneNumber")).clear();
		Browser.driver.findElement(By.cssSelector("input#phoneNumber")).sendKeys(userProfile.getPhone1());
	}
	
	public void selectTermsAndConditions() {
		try {
			Browser.waitForSomeTime();
			WebElement element = Browser.getWebElement(By.cssSelector("#terms-and-conditions-accountcreation span.checkmark"));
			
			Browser.scrollToElememnt(element);
			Browser.scrollTo(200);
			element.click();
			Logger.info(pageNamePrefixForLogger + "Terms and Conditions selected.");
			Browser.waitForSomeTime();
		} catch(Exception e) {
			Logger.info(pageNamePrefixForLogger + "Failed to select the Terms and Conditions! ");
		}
	}
	
	public void selectSubmitButton() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#createAccountBtn"));
			Browser.scrollToElememnt(element);
			Browser.waitForSomeTime();
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Submit button selected.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select Submit button!");
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
					Logger.warning(pageNamePrefixForLogger + "- Found the following form validation errors: " + errorsText);
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
