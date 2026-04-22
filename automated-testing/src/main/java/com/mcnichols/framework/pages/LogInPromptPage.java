package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class LogInPromptPage {

	public static final String PROPERTY_FILE = "login-prompt-page-config.xml";

	public static String url = "/checkout/login-prompt.jsp";
	public static String title = "McNichols - Login";
	public static String titleUpdate = "Log In or Continue As Guest";
	public static String pageHeader = "LOGIN OR CONTINUE AS GUEST";
	public static String pageNamePrefixForLogger = "LogIn Prompt Page - ";

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			
			String browserPageTitle = Browser.title();
			if (browserPageTitle.contains(title) || browserPageTitle.contains(titleUpdate)) {
				Logger.info(pageNamePrefixForLogger + "Title verified.");
				isAt = true;
			}
			
			String expectedPageHeader = Browser.driver.findElement(By.cssSelector(".h1-login-prompt-header")).getText();
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
		try {
			By userLoginButton = By.cssSelector("button.login-prompt-login-btn");
			
			if (Browser.isElementPresent(userLoginButton)) {
				Browser.click(userLoginButton);
				Logger.info(pageNamePrefixForLogger + "Selected Login.");
			} else {
				Logger.fail(pageNamePrefixForLogger + "Login button is not present!");	
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Selecting Login failed!");
		}
	}

	public void continueAsGuestUser() {
		try {
			By guestButtonV3 = By.cssSelector("button.login-prompt-cont-as-guest");
			By guestButton = By.cssSelector("button.button-light");
			
			if (Browser.isElementPresent(guestButtonV3)) {
				Browser.click(guestButtonV3);
				Logger.info(pageNamePrefixForLogger + "Selected Guest.");
			} else if (Browser.isElementPresent(guestButton)) {
				Browser.click(guestButton);
				Logger.info(pageNamePrefixForLogger + "Selected Guest.");
			} else {
				Logger.fail(pageNamePrefixForLogger + "Guest button is not present!");	
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Selecting Guest failed!");
		}
	}

	public void createRegisteredUserAccount() {
		try {
			By userRegisterButton = By.cssSelector("button.login-prompt-register");
			
			if (Browser.isElementPresent(userRegisterButton)) {
				Browser.click(userRegisterButton);
				Logger.info(pageNamePrefixForLogger + "Selected Register.");
			} else {
				Logger.fail(pageNamePrefixForLogger + "Register button is not present!");	
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Selecting Register failed!");
		}
	}
}
