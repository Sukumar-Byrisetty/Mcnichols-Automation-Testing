package com.mcnichols.framework.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;

public class ProductPage {
	public static final String PROPERTY_FILE = "product-page-config.xml";
	public static String pageNamePrefixForLogger = "Product Page - ";

	static String url = "";
	static String title = "";

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			Browser.waitForElementToBeVisible(By.cssSelector("#pdpMainInfo"));

			if (Browser.isElementPresent(By.cssSelector("#pdpMainInfo"))) {
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

	public boolean isSkuPurchasableOnline() {
		boolean canBuyOnline = false;
		String logMessage = "Item not available for online purchase.";
		try {
			if (isAddToCartAvailable()
					|| Browser.isElementPresent(By.cssSelector("#product_sku_id .pdp-purchase-online"))
					|| Browser.isElementPresent(By.cssSelector("#ItemAvail .pdp-buy-online"))) {
				logMessage = "Item is available for online purchase.";
				canBuyOnline = true;
			}
		} catch (Exception e) {
			Logger.warning(
					pageNamePrefixForLogger + "Issue occured determining if item is avaiable for online purchase.");
		}

		Logger.info(pageNamePrefixForLogger + logMessage);
		return canBuyOnline;
	}

	public boolean selectPurchasableSku() {
		boolean canSelectPurchasableSku = false;

		if (isAddToCartAvailable()) {
			canSelectPurchasableSku = true;
		}

		if (!canSelectPurchasableSku) {
			Browser.scrollToElememnt(Browser.driver.findElement(By.cssSelector("#itemSkuId-button")));
			Browser.click(By.cssSelector("#itemSkuId-button"));
			Browser.waitForSomeTime();

			List<WebElement> dropdownSkus = Browser.driver
					.findElements(By.cssSelector("ul#itemSkuId-menu li.ui-menu-item"));
			int numberOfSkus = dropdownSkus.size();
			Logger.info(pageNamePrefixForLogger + "Count of SKUs available: " + numberOfSkus);

			for (int i = 1; i < numberOfSkus; i++) {
				boolean isItemActiveForPurchase = Browser.isElementPresent(By.cssSelector(
						"#itemSkuId-menu li:nth-child(" + i + ") div.ui-menu-item-wrapper span.pdp-sku-ui-dropdown"));

				if (isItemActiveForPurchase) {
					Browser.click(By.cssSelector("#itemSkuId-menu li:nth-child(" + i + ")"));
					Browser.waitForTheLoadingOverlayToDisappear("Product Page - SKU dropdown selected");
					Browser.waitForSomeTime();
				}

				if (Pages.productPage().isSkuPurchasableOnline()) {
					canSelectPurchasableSku = true;
					Logger.info(pageNamePrefixForLogger + "Selected Purchasable SKU from dropdown menu.");
					break;
				}
			}
			Browser.scrollTo(0);
		}

		return canSelectPurchasableSku;
	}

	private boolean isAddToCartAvailable() {
		try {
			By addToCartButton = By.cssSelector("button.addToCart-btn");
			if (!Browser.isElementPresent(addToCartButton)) {
				return false;
			}

			WebElement button = Browser.getWebElement(addToCartButton);
			return button.isDisplayed() && button.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	public String pageName() {
		return Browser.driver.getTitle();
	}

	public void selectTabSpecifications() {
		selectTab(".pdptab .pdpspecs");
	}

	public boolean isTabSpecificationsContentDisplayed() {
		return doesActiveTabMatchTabContent(".tab-pane.pdpSpecs-xs.active");
	}

	public void selectTabOverview() {
		selectTab(".pdptab .Overviewpdp");
	}

	public boolean isTabOverviewContentDisplayed() {
		return doesActiveTabMatchTabContent(".tab-pane.pdp-tabs.active");
	}

	public void selectTabOrdering() {
		selectTab(".pdptab .Orderingpdp");
	}

	public boolean isTabOrderingContentDisplayed() {
		return doesActiveTabMatchTabContent(".tab-pane.pdp-tabs.active");
	}

	public void selectTabCharts() {
		selectTab(".pdptab .Chartspdp");
	}

	public boolean isTabChartsContentDisplayed() {
		return doesActiveTabMatchTabContent(".tab-pane.pdp-tabs.active");
	}

	public void selectTabTables() {
		selectTab(".pdptab .Tablespdp");
	}

	public boolean isTabTablesContentDisplayed() {
		return doesActiveTabMatchTabContent(".tab-pane.pdp-tabs.active");
	}

	public void selectTab(String elementLocator) {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(elementLocator));
			element.click();
			Logger.info(pageNamePrefixForLogger + pageName() + " - Selecting tab by css selector: " + elementLocator);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + pageName() + " - Unable to select tab by css selector: "
					+ elementLocator);
		}
	}

	public boolean doesActiveTabMatchTabContent(String cssSelector) {
		boolean isVerified = false;
		if (Browser.isElementPresent(By.cssSelector(cssSelector))
				&& Browser.isElementVisible(By.cssSelector(cssSelector))) {
			Logger.info(pageNamePrefixForLogger + "The active tab matches the tab body content.");
			isVerified = true;
		} else {
			Logger.fail(pageNamePrefixForLogger + "The active tab does NOT match the tab body content!");
		}
		return isVerified;
	}

	public void addToCart() {
		try {
			Browser.waitForSomeTime();
			WebElement element = Browser.getWebElement(By.cssSelector("button.addToCart-btn"));
			Browser.scrollToElememnt(element);
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Selected the add to cart button.");
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + pageName() + " - Unable to select the add to cart button!");
		}
	}
}
