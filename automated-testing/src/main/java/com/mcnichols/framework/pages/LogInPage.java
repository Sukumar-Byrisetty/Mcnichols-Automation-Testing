package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class LogInPage {

	public static final String PROPERTY_FILE = "login-page-config.xml";

	public static String url = "/my-account-login-register";
	public static String title = "Log In to Your Account";
	public static String pageHeader = "ACCOUNT LOGIN";
	public static String pageNamePrefixForLogger = "Log In Page - ";

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

			String expectedPageHeader = Browser.driver.findElement(By.cssSelector("#accountLogin h1")).getText();
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

	public void logInAsRegisteredUser() {
		logInAsRegisteredUser(TestingConfig.getLogin(0).getUsername(), TestingConfig.getLogin(0).getPassword());
	}

	public void logInAsExpressCheckoutUser() {
		logInAsRegisteredUser(TestingConfig.getLogin(1).getUsername(), TestingConfig.getLogin(1).getPassword());
	}

	public void logIn(int accountType) {
		logInAsRegisteredUser(TestingConfig.getLogin(accountType).getUsername(),
				TestingConfig.getLogin(accountType).getPassword());
	}

	public void logInAsRegisteredUser(String username, String password) {
		String loginButton = "button.login-page-button";
		String emailField = "input#login";
		String passwordField = "input#password";

		// The development environment differs from production.
		if (Browser.isElementPresent(By.cssSelector("button.login-btn"))) {
			loginButton = "button.login-btn";
			emailField = "#accountLoginForm .row input#login"; // ToDo: There is more than one element ID on page. Needs
																// to be resolved!
			passwordField = "#accountLoginForm .row input#password";

			Browser.scrollToElememnt(Browser.getWebElement(By.id("new-account-tab")));
			Browser.click(By.id("new-account-tab"));
			Logger.info("Log In Page - Selected tab new account.");
			Browser.waitForSomeTime();
			Browser.click(By.id("login-tab"));
			Logger.info("Log In Page - Selected tab login.");
			Browser.waitForSomeTime();
			Browser.click(By.id("login"));
			Browser.waitForSomeTime();
		}

		WebElement loginWebElement = Browser.waitForElementToBeClickable(By.cssSelector(loginButton));

		Browser.click(By.cssSelector(emailField));
		Browser.driver.findElement(By.cssSelector(emailField)).clear();
		Browser.driver.findElement(By.cssSelector(emailField)).sendKeys(username);
		Logger.info(pageNamePrefixForLogger + "Form Login - Selected username field and entered: " + username);
		Browser.waitForSomeTime();
		Browser.click(By.cssSelector(passwordField));
		Browser.driver.findElement(By.cssSelector(passwordField)).clear();
		Browser.driver.findElement(By.cssSelector(passwordField)).sendKeys(password);
		Logger.info(pageNamePrefixForLogger + "Form Login - Selected password field and entered: ***********");
		Browser.waitForSomeTime();

		Browser.click(loginWebElement);
		Logger.info(pageNamePrefixForLogger + "Form Login - Selected the login button.");
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

	public boolean doesEmailExistsError() {
		String eamilExistsMessage = "User with specified email address already exists.";

		String errorsText = Browser.driver.findElement(By.cssSelector("#error")).getText();
		if (errorsText.contains(eamilExistsMessage)) {
			Logger.warning(pageNamePrefixForLogger + "- Email entered already exists!");
			return true;
		}

		return false;
	}

	public void createAccount(int accountType) {
		String tabNewAccount = "new-account-tab";
		String buttonRegister = "#btn-register";
		String emailField = "input#registerLogin";
		String userEmailAddress = TestingConfig.getLogin(accountType).getUsername();

		Browser.scrollToElememnt(Browser.getWebElement(By.id(tabNewAccount)));
		Browser.getWebElement(By.id(tabNewAccount)).click();
		Logger.info("Log In Page - Selected tab new account.");
		Browser.waitForSomeTime();
		Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector(emailField)));
		Browser.scrollTo(400);

		WebElement registerWebElement = Browser.waitForElementToBeClickable(By.cssSelector(buttonRegister));

		Browser.getWebElement(By.cssSelector(emailField)).click();
		Browser.driver.findElement(By.cssSelector(emailField)).clear();
		Browser.driver.findElement(By.cssSelector(emailField)).sendKeys(userEmailAddress);
		Logger.info(
				pageNamePrefixForLogger + "Form Login - Selected Email Address field and entered: " + userEmailAddress);
		Browser.waitForSomeTime();

		Browser.click(registerWebElement);
		Logger.info(pageNamePrefixForLogger + "Form Login - Selected the register button.");
	}
}
