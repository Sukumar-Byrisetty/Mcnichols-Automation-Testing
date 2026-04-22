package com.mcnichols.framework.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public abstract class ProductListingPage {
	public static String pageNamePrefixForLogger = "Product Listing Page - ";

	public abstract void goTo();

	public abstract void goToByUrl();

	public abstract String pageName();

	public abstract boolean isTabPresent(String tabName);

	public abstract boolean isAtByPageHeading();

	public boolean isTilePresent(By attribute) {
		if (Browser.isElementPresent(attribute)) {
			Logger.info(pageNamePrefixForLogger + "Tile verified.");
			return true;
		}
		Logger.warning(pageNamePrefixForLogger + "Tile is not present!");
		return false;
	}

	public boolean isAtPage() {
		boolean isAt = false;
		
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains(pageName())) {
				Logger.info(pageNamePrefixForLogger + "Title verified for: " + pageName());
				isAt = true;
			} else {
				Logger.warning(pageNamePrefixForLogger + "Title not verified for: " + pageName());
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void selectTile(WebElement element) {
		Browser.click(element);
	}
	
	public boolean isTabProductsPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#productResourceId"))) {
				Logger.info(pageNamePrefixForLogger + pageName() + " - Product tab verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#productResourceId"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Product tab is not present!");
		}
		return false;
	}
	
	public boolean isTabOverviewPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#Overviewid"))) {
				Logger.info(pageNamePrefixForLogger + pageName() + " - Overview tab verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#Overviewid"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Overview tab is not present!");
		}
		return false;
	}

	public boolean isTabOrderingPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#Orderingid"))) {
				Logger.info(pageNamePrefixForLogger + pageName() + " - Ordering tab verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#Orderingid"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Ordering tab is not present!");
		}
		return false;
	}

	public boolean isTabChartsPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#Chartsid"))) {
				Logger.info(pageNamePrefixForLogger + pageName() + " - Product tab verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#Chartsid"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Product tab is not present!");
		}
		return false;
	}
	
	public boolean isTabTablesPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#Tablesid"))) {
				Logger.info(pageNamePrefixForLogger + pageName() + " - Tables tab verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("#Tablesid"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Tables tab is not present!");
		}
		return false;
	}
	
	
	public void selectTabProducts() {
		selectTab("#productResourceId");
	}
	
	public void selectTabOverview() {
		selectTab("#Overviewid");
	}
	
	public void selectTabOrdering() {
		selectTab("#Orderingid");
	}
	
	public void selectTabCharts() {
		selectTab("#Chartsid");
	}
	
	public void selectTabTables() {
		selectTab("#Tablesid");
	}

	public void selectTab(String elementLocator) {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(elementLocator));
			element.click();
			Logger.info(pageNamePrefixForLogger + pageName() + " - Selecting tab by css selector: " + elementLocator);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Unable to select tab by css selector: " + elementLocator);
		}
	}

	public boolean isProductsFirstItemVisible() {
		boolean isProductsFirstItemVisible = false;
		try {
			WebElement element = Browser.waitForElementToBeVisible(By.cssSelector(".plp-listing-product:first-child img"));
			Browser.scrollToElememnt(element);
			isProductsFirstItemVisible = element.isDisplayed();
			Logger.info(pageNamePrefixForLogger + pageName() + " - Is first item present: " + isProductsFirstItemVisible);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Unable to location items!");
			return false;
		}
		return isProductsFirstItemVisible;
	}

	public void goToProductsFirstItem() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(".plp-listing-product:first-child img"));
			Logger.info(pageNamePrefixForLogger + pageName() + " - Selecting first item in results.");
			Browser.click(element);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Unable to Select the first item in results.");
		}
	}
	
	public void goToProductsItem(String nthChild) {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(".plp-listing-product:nth-child(" + nthChild + ") img"));
			Logger.info(pageNamePrefixForLogger + pageName() + " - Selecting item " + nthChild + " in results.");
			Browser.click(element);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Unable to Select the item " + nthChild + " in results.");
		}
	}

	public List<WebElement> getCategoryPageProductElements() {
		List<WebElement> links = Browser.driver.findElements(By.cssSelector("#productsplp .plp-listing-product .product-size-plp a"));

		Logger.info("Category Page Product Element(s) Total: " + links.size());

		return links;
	}

	public List<String> getCategoryPageProductLinks() {
		Set<String> uniqueLinks = new HashSet<String>();
		List<WebElement> links = Browser.driver.findElements(By.cssSelector("#productsplp .plp-listing-product .product-size-plp a"));

		for (int i = 0; i < links.size(); i++) {
			WebElement element = links.get(i);
			// By using "href" attribute, we could get the url of the requried link
			String url = element.getAttribute("href");
			if (!StringUtil.isEmpty(url)) {
				uniqueLinks.add(url);
			}
		}

		Logger.info("Category Page Product Link(s) Total: " + uniqueLinks.size());

		return new ArrayList<String>(uniqueLinks);
	}
}
