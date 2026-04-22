package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;

public class SearchPage {
	public static String url = "/search";	
	public static final String PROPERTY_FILE = "search-page-config.xml";
	public static String pageNamePrefixForLogger = "Search Page - ";

	public boolean isAtByUrl() {
		if (Browser.driver.getCurrentUrl().contains(url)) {
			Logger.info(pageNamePrefixForLogger + "Search Page - is at verified by URL");
			return true;
		}
		Logger.info(pageNamePrefixForLogger + "is at failed!  Possible on-site search redirect occurred.");
		return false;
	}

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			Browser.waitForElementToBeVisible(By.cssSelector(".container"));
			
			if (Browser.isElementPresent(By.cssSelector("h2.sr-results"))) {
				String title = Browser.driver.getTitle();
				Logger.info(pageNamePrefixForLogger + "Loaded and verified for: " + title);
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public boolean hasSearchResults() {
		boolean hasSearchResults = false;
		String logMessage = "No search results for search term: ";
		try {
			WebElement element = Browser.waitForElementToBeVisible(By.cssSelector(".plp-listing-product:first-child img"));
			hasSearchResults = element.isDisplayed();
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "No search results present!  Search Term: " + getSearchTerm());
		}

		if (hasSearchResults) {
			logMessage = "Has search results for search term: ";
		}

		Logger.info(pageNamePrefixForLogger + logMessage + getSearchTerm());
		return false;
	}

	public String getSearchTerm() {
		String keyword = "";
		try {
			if (Browser.isElementPresent(By.cssSelector(".select-facets a"))) {
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector(".select-facets a"));
				keyword = element.getText();
			} else {
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector(".select-facets ul li span span"));
				keyword = element.getText();
			}
			
			Logger.info(pageNamePrefixForLogger + "Search Term: " + keyword);
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "No search results present!  Search Term: " + keyword);
		}
		return keyword;
	}

}
