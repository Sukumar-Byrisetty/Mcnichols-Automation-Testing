package com.mcnichols.framework.pages.account;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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

			String expectedPageHeader = Browser.driver.findElement(By.cssSelector("#create-an-account-form h1"))
					.getText();
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
		Browser.scrollTo(350);

		// First name
		Browser.driver.findElement(By.cssSelector("input#firstName")).clear();
		Browser.driver.findElement(By.cssSelector("input#firstName")).sendKeys(userProfile.getFistName());

		// Last name
		Browser.driver.findElement(By.cssSelector("input#lastName")).clear();
		Browser.driver.findElement(By.cssSelector("input#lastName")).sendKeys(userProfile.getLastName());

		// Confirm email address
		Browser.driver.findElement(By.cssSelector("input#confirmEmail")).clear();
		Browser.driver.findElement(By.cssSelector("input#confirmEmail")).sendKeys(userEmailAddress);

		// Password
		Browser.driver.findElement(By.cssSelector("input#password")).clear();
		Browser.driver.findElement(By.cssSelector("input#password")).sendKeys(userPassword);

		// Confirm Password
		Browser.driver.findElement(By.cssSelector("input#confirmPassword")).clear();
		Browser.driver.findElement(By.cssSelector("input#confirmPassword")).sendKeys(userPassword);

		// Select security question
		try {
			if (Browser.isElementPresent(By.cssSelector("#security-questions-button > .ui-selectmenu-text"))) {
				Browser.driver.findElement(By.cssSelector("#security-questions-button > .ui-selectmenu-text")).click();
			} else {
				Browser.driver.findElement(By.cssSelector("#security-questions-button")).click();
			}
			Browser.waitForSomeTime();

			boolean didSelectSecurityQuestion = false;
			List<WebElement> securityQuestionElements = Browser.driver.findElements(By.cssSelector(
					"ul#security-questions-menu li div, ul#security-questions-menu li span, ul#security-questions-menu li .ui-menu-item-wrapper"));
			for (WebElement securityQuestionElement : securityQuestionElements) {
				String question = securityQuestionElement.getText();
				if (!StringUtil.isEmpty(question) && question.contains(securityQuestionSelect)) {
					Browser.waitForSomeTime();
					Logger.info(pageNamePrefixForLogger + "Selecting security question dropdown value: " + question);
					securityQuestionElement.click();
					didSelectSecurityQuestion = true;
					break;
				}
			}

			if (!didSelectSecurityQuestion && Browser.isElementPresent(By.cssSelector("select#security-questions"))) {
				Select securityQuestionDropdown = new Select(
						Browser.getWebElement(By.cssSelector("select#security-questions")));
				for (WebElement option : securityQuestionDropdown.getOptions()) {
					String question = option.getText();
					if (!StringUtil.isEmpty(question) && question.contains(securityQuestionSelect)) {
						securityQuestionDropdown.selectByVisibleText(question);
						didSelectSecurityQuestion = true;
						Logger.info(
								pageNamePrefixForLogger + "Selecting security question dropdown value: " + question);
						break;
					}
				}
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select security question from dropdown form!");
		}

		// Security Answer
		Browser.driver.findElement(By.cssSelector("input#securityAnswer")).clear();
		Browser.driver.findElement(By.cssSelector("input#securityAnswer"))
				.sendKeys(userProfile.getColorSecurityAnwser());
	}

	public void enterOtherInformation(int accountType) {
		UserProfile userProfile = TestingConfig.getLogin(accountType).getUserProfile();

		// Scroll to profile section
		Browser.scrollToElememnt(Browser.getWebElement(By.id("account-info")));
		Browser.scrollTo(350);

		// Address 1
		Browser.driver.findElement(By.cssSelector("input#address1")).clear();
		Browser.driver.findElement(By.cssSelector("input#address1")).sendKeys(userProfile.getAddress());

		// City
		Browser.driver.findElement(By.cssSelector("input#city")).clear();
		Browser.driver.findElement(By.cssSelector("input#city")).sendKeys(userProfile.getCity());

		// State
		try {
			if (Browser.isElementPresent(By.cssSelector("#state-button > .ui-selectmenu-text"))) {
				Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).click();
			} else {
				Browser.driver.findElement(By.cssSelector("#state-button")).click();
			}
			Browser.waitForSomeTime();

			boolean didSelectState = false;
			List<WebElement> stateElements = Browser.driver.findElements(By.cssSelector(
					"ul#state-menu li div, ul#state-menu li span, ul#state-menu li .ui-menu-item-wrapper"));
			for (WebElement stateElement : stateElements) {
				String state = stateElement.getText();
				if (!StringUtil.isEmpty(state) && state.contains(userProfile.getState())) {
					Browser.waitForSomeTime();
					Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + state);
					Browser.scrollToElememnt(stateElement);
					stateElement.click();
					didSelectState = true;
					break;
				}
			}

			if (!didSelectState && Browser.isElementPresent(By.cssSelector("select#state"))) {
				Select stateDropdown = new Select(Browser.getWebElement(By.cssSelector("select#state")));
				for (WebElement option : stateDropdown.getOptions()) {
					String state = option.getText();
					if (!StringUtil.isEmpty(state) && state.contains(userProfile.getState())) {
						stateDropdown.selectByVisibleText(state);
						didSelectState = true;
						Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + state);
						break;
					}
				}
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
		}

		// Zip
		Browser.driver.findElement(By.cssSelector("input#postalCode")).clear();
		Browser.driver.findElement(By.cssSelector("input#postalCode")).sendKeys(userProfile.getZip());

		// Phone number
		Browser.driver.findElement(By.cssSelector("input#phoneNumber")).clear();
		Browser.driver.findElement(By.cssSelector("input#phoneNumber")).sendKeys(userProfile.getPhone1());
	}

	public void selectTermsAndConditions() {
		try {
			Browser.waitForSomeTime();
			WebElement element = Browser
					.getWebElement(By.cssSelector("#terms-and-conditions-accountcreation span.checkmark"));

			Browser.scrollToElememnt(element);
			Browser.scrollTo(200);
			element.click();
			Logger.info(pageNamePrefixForLogger + "Terms and Conditions selected.");
			Browser.waitForSomeTime();
		} catch (Exception e) {
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
			if (Browser.isElementPresent(By.cssSelector("#error"))
					&& Browser.isElementVisible(By.cssSelector("#error"))) {
				String errorsText = Browser.driver.findElement(By.cssSelector("#error")).getText();
				if (StringUtil.isNotEmpty(errorsText)) {
					isErrorPresent = true;
					Logger.warning(
							pageNamePrefixForLogger + "- Found the following form validation errors: " + errorsText);
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
}
