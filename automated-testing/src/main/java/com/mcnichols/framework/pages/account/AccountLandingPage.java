package com.mcnichols.framework.pages.account;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class AccountLandingPage {
	public static final String PROPERTY_FILE = "account-page-properties-config.xml";

	static String url = "/account/account-landing.jsp";
	static String title = "My Account";
	public static String pageHeader = "My Account";
	
	public static String pageNamePrefixForLogger = "My Account - Credit Cards - ";
	public Select mDropDown;
	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public void goTo() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

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
			
			String expectedPageHeader = Browser.driver.findElement(By.cssSelector("h1")).getText();
			if (!isAt && StringUtil.isNotEmpty(expectedPageHeader) && expectedPageHeader.contains(pageHeader)) {
				Logger.info(pageNamePrefixForLogger + "page header verified.");
				isAt = true;
			}
			
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "is At failed by page title!");
		}

		return isAt;
	}

	public void goToCreditCardPage() {
		try {
			Browser.waitForSomeTime(3000);
			Browser.driver.findElement(By.linkText("CREDIT CARDS")).click();
			Logger.info(pageNamePrefixForLogger + "Click CreditCard menu");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Creditcard button failed!");
			e.printStackTrace();
		}
	}
}
