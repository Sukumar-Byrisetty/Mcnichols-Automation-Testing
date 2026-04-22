package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.plp.OnlineOrderingProductListingPage;
import com.mcnichols.framework.pages.plp.PerforatedMetalProductListingPage;
import com.mcnichols.framework.util.Logger;

public class ProductsPage {
	public static String url = "/all-products";
	public static String title = "Perforated Metal, Wire Mesh, Expanded Metal & Grating Products";
	public static String titleUpdate = "Specialty Metals and Gratings | All Products";
	
	public static String pageNamePrefixForLogger = "Category - Products Page - ";

	public void goTo() {
		if (Browser.isElementPresent(By.id("StickyHeader"))) {
			// ToDo: Make click when sticky header is visible
			goToByUrl();
			Logger.info(pageNamePrefixForLogger + "The sticky header is present.  Going to Category Products Page by URL.");
		} else {
			//Browser.goToByLinkText("PRODUCTS");
			WebElement productsLinkTextElement = Browser.getWebElement(By.linkText("PRODUCTS"));
			Browser.click(productsLinkTextElement);
			Logger.info(pageNamePrefixForLogger + "Selecting the Category Products by the header navigation bar.");
		}
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public ProductListingPage getProductListingPage(String page) {
		switch (page) {
		case PerforatedMetalProductListingPage.NAME:
			return Pages.perforatedMetalProductListingPage();
			
		case OnlineOrderingProductListingPage.NAME:
			return Pages.onlineOrderingProductListingPage();
		}

		return null;
	}

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains((title)) || Browser.title().contains((titleUpdate))) {
				Logger.info(pageNamePrefixForLogger + "Title verified.");
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToPerforatedMetalProduct() {
		if (Browser.isElementPresent(By.cssSelector(".mkt-products img[title=\"Perforated Metal\"]"))) {
			WebElement element = Browser.getWebElement(By.cssSelector(".mkt-products img[title=\"Perforated Metal\"]"));
			goToProduct(element);
			Logger.info(pageNamePrefixForLogger + "Selected Perforated Metal.");
		} else if (Browser.isElementPresent(By.xpath("//*[@id=\"pageContent\"]/div/div/section[2]/div/div[1]/div/div[1]/div/div/a"))) {
			// WordPress Page
			WebElement element = Browser.getWebElement(By.xpath("//*[@id=\"pageContent\"]/div/div/section[2]/div/div[1]/div/div[1]/div/div/a"));
			goToProduct(element);
			Logger.info(pageNamePrefixForLogger + "Selected Perforated Metal from WordPress page.");
		}
	}

	public void goToProduct(WebElement element) {
		//Browser.goToByCssSelector(element);
		Browser.click(element);
		Logger.info(pageNamePrefixForLogger + "Selected product.");
	}

	public boolean isProductsContainerPresent() {
		if (Browser.isElementPresent(By.cssSelector(".mkt-allproducts"))) {
			Logger.info(pageNamePrefixForLogger + "Products container verified.");
			return true;
		} else if (Browser.isElementPresent(By.xpath("//*[@id=\"pageContent\"]/div/div/section[2]/div/div[1]/div/div[1]/div/figure/a"))) {
			// WordPress Page
			Logger.info(pageNamePrefixForLogger + "WordPress Products container verified.");
			return true;
		}
		
		Logger.fail(pageNamePrefixForLogger + "Products container is not present!");
		return false;
	}

	public boolean isPerforatedMetalProduct() {
		if (Browser.isElementPresent(By.cssSelector(".mkt-products img[title=\"Perforated Metal\"]"))) {
			Logger.info(pageNamePrefixForLogger + "Perforated Metal verified.");
			return true;
		} else {
			// WordPress Page
			WebElement element = Browser.getWebElement(By.xpath("//*[@id=\"pageContent\"]/div/div/section[2]/div/div[1]/div/div[1]/div/figure/figcaption"));
			String text = element.getText();
			
			if (text.equalsIgnoreCase("Perforated Metal")) {
				Logger.info(pageNamePrefixForLogger + "WordPress Perforated Metal verified.");
				return true;
			}
		}
		
		Logger.fail(pageNamePrefixForLogger + "Perforated Metal is not present!");
		return false;
	}
}
