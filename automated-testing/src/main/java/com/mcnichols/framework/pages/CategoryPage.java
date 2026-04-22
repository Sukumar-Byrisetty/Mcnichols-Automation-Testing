package com.mcnichols.framework.pages;

import org.openqa.selenium.By;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CategoryPage extends ProductListingPage {
	public static final String PROPERTY_FILE = "product-page-config.xml";
	public static String pageNamePrefixForLogger = "Category Page - ";
	
	static String url = "";
	static String title = "";

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			Browser.waitForElementToBeVisible(By.cssSelector(".category-title"));
	
			if (Browser.isElementPresent(By.cssSelector(".category-title"))) {
				String title = Browser.driver.getTitle();
				Logger.info(pageNamePrefixForLogger + "Loaded and verified for: " + title);
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	@Override
	public void goTo() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
		
	}

	@Override
	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	@Override
	public String pageName() {
		String name = Browser.getWebElement(By.cssSelector("h1.category-title")).getText();
		if (StringUtil.isEmpty(name)) {
			name = "";
		}
		return name;
	}

	@Override
	public boolean isTabPresent(String tabName) {
		return false;
	}

	@Override
	public boolean isAtByPageHeading() {
		return false;
	}
}
