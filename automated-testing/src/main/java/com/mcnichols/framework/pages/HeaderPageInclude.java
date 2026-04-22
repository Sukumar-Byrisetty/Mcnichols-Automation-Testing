package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.plp.OnlineOrderingProductListingPage;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.framework.util.TestUtil;

public class HeaderPageInclude {
	
	public static final String PROPERTY_FILE = "page-includes-config.xml";
	public static String pageNamePrefixForLogger = "Header - ";

	public boolean isGlobalBarPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("#announcementBar"))) {
				Logger.info(pageNamePrefixForLogger + "Global Annoucement Bar - verified.");
				return true;
			}
			
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Global Annoucement Bar - is not present!  Cannot location css selector: announcementBar");
		}
		return false;
	}

	public void goToBuyNowFromGlobalBar() {
		WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#announcementBar button"));
		if (StringUtil.isNotEmpty(element.getText()) && element.getText().contains("Buy Now")) {
			Logger.info(pageNamePrefixForLogger + "Global Annoucement Bar - Selected buy now");
			Browser.click(element);
		} else {
			Logger.info(pageNamePrefixForLogger + "Global Annoucement Bar - Selected buy now is not present.  Content was updated.");
			Logger.info(pageNamePrefixForLogger + "Going to the Buy Now category by URL.");
			ProductListingPage onlineOrderingPLP = Pages.productsPage().getProductListingPage(OnlineOrderingProductListingPage.NAME);
			onlineOrderingPLP.goToByUrl();
		}
	}

	public void closeGlobalBar() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#announcementBar .announcement-bar-close"));
			element.click();
			Logger.info(pageNamePrefixForLogger + "Global  Annoucement Bar - Selected close button.");
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Global Annoucement Bar - Selecting close button failed to close.");
		}
	}

	public boolean isPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("header"))) {
				Logger.info(pageNamePrefixForLogger + "container verified.");
				return true;
			}
			
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "container is not present!  Cannot location css selector: header");
		}
		return false;
	}

	public boolean isNavigationPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("nav.navbar"))) {
				Logger.info(pageNamePrefixForLogger + "Navigation bar verified.");
				return true;
			}
			
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Navigation  bar is not present!  Cannot location css selector: navbar");
		}
		return false;
	}

	public void goToLoginFromHeader() {
		try {
			String cssSelector = ".header-padding > .hidden-sm.hidden-xs > .header-block.user-section > ul > li.user > a > i";
			
			if (!Browser.isElementPresent(By.cssSelector(cssSelector))) {
				// This will become the primary after all environments are updated
				cssSelector = "#pageContent div > ul > li.nav-item.header-block-login > a > i";
			}
			
			if (!Browser.isElementPresent(By.cssSelector(cssSelector))) {
				// Branding link update
				cssSelector = "#menu-item-12906 > .ekit-menu-nav-link";
			}
			
			if (!Browser.isElementVisible(By.cssSelector(cssSelector))) {
				Browser.scrollTo(0);
			}
			
			Browser.click(By.cssSelector(cssSelector));

			Logger.info(pageNamePrefixForLogger + "Selected login button.");
		} catch (Exception e) {
			Logger.warning("Header - Unable to select log in button.");
		}
	}

	public boolean isUserLoggedIn() {
		try {
			if (Browser.isElementPresent(By.cssSelector("header #welcomeMsg .name"))) {
				WebElement element = Browser.getWebElement(By.cssSelector("header #welcomeMsg .name"));
				Logger.info(pageNamePrefixForLogger + "User is logged in: " + element.getText());
				
				return true;
			}
			
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "User is not logged in.");
		}
		return false;	
	}
	
	public void goToHomePageByLogo() {
		try {
			WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(TestingConfig.getProperties(HeaderPageInclude.PROPERTY_FILE).getProperty("header.1.logo")));
			Logger.info(pageNamePrefixForLogger + "Selecting Logo.");
			element.click();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select Logo!");
		}
	}

	public void performOnSiteSearch() {
		if (Browser.isElementPresent(By.id("StickyHeader"))) {
			performOnSiteSearchFromStickyHeader();
		} else {
			performOnSiteSearch("header.on-site.search.term");
		}
	}

	public void performOnSiteSearch(String searchTerm) {
		performOnSiteSearch(searchTerm, false);
	}

	public void performOnSiteSearch(String searchTerm, boolean useForwardLookup) {
		try {
			if (Browser.isElementPresent(By.cssSelector("#StickyHeader.banner--stick")) && Browser.isElementVisible(By.cssSelector("#StickyHeader.banner--stick"))) {
				// ToDo: Test and handle stickyHeader
				//performOnSiteSearchFromStickyHeader(searchTerm);
			} else {
				String searchBoxSelector = "#headerContainer #main-header-container .main-subheader #headerSearchQuestionFromHeader";
				if (!Browser.isElementPresent(By.cssSelector(searchBoxSelector))) {
					searchBoxSelector = "#headerSearchQuestion";
				}
				
				if (!Browser.isElementPresent(By.cssSelector(searchBoxSelector))) {
					searchBoxSelector = "#headerSearchQuestionFromHeader";
				}
				
				// WordPress Element Check
				if (!Browser.isElementVisible(By.cssSelector(searchBoxSelector))) {
					searchBoxSelector = ".elementor-element-82c56dc #headerSearchQuestionFromHeader";
				}
				
				Browser.driver.findElement(By.cssSelector(searchBoxSelector)).click();
				Browser.driver.findElement(By.cssSelector(searchBoxSelector)).clear();
				Browser.driver.findElement(By.cssSelector(searchBoxSelector)).sendKeys(searchTerm);
				
				// ToDo: Add logging for when things go side ways!
				if (useForwardLookup) {
					WebElement searchForwardLookupElement = Browser.waitForElementToBeClickable(By.cssSelector(".dropdown-search a"));
					
					if (searchForwardLookupElement.isDisplayed()) {
						Browser.waitForElementToBeVisible(By.cssSelector(".dropdown-search a"));
						TestUtil.takeScreenshot("search-dropdown");
						Browser.driver.findElement(By.cssSelector(searchBoxSelector)).click();
						searchForwardLookupElement.click();
						Logger.info(pageNamePrefixForLogger + "On-site Search - Selecting forward lookup for search term: " + searchTerm);
					} else {
						Logger.info(pageNamePrefixForLogger + "On-site Search - Forward lookup not present for search term: " + searchTerm);
						clickSearchButton();
						
					}
				} else {
					clickSearchButton();
				}
				
			}
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "Search box - Issue occurred interacting with elements.");
		}
	}
	
	public void clickSearchButton() {
		try {
			String searchIconCssSelector = ".search button .header-search-icon";
			if (!Browser.isElementPresent(By.cssSelector(searchIconCssSelector))) {
				searchIconCssSelector = "#globalSearchForm1 span i.header-search-icon";
			}
			
			if (!Browser.isElementPresent(By.cssSelector(searchIconCssSelector))) {
				// This will become the primary after all environments are updated
				searchIconCssSelector = "#searchIcon > i";
			}
			
			Browser.click(By.cssSelector(searchIconCssSelector));
			Logger.info(pageNamePrefixForLogger + "On-site Search - Clicked the search button");
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "Search box - Issue occurred clicking the search button!");
		}
	}

	public void performOnSiteSearchFromStickyHeader() {
		performOnSiteSearch("ladder");
	}

	public void performOnSiteSearchFromStickyHeader(String searchTerm) {
		WebElement element = Browser.waitForElementToBeClickable(By.cssSelector("#StickyHeader .header-search-icon"));
		Browser.driver.findElement(By.id("headerSearchQuestion1")).click();
		Browser.driver.findElement(By.id("headerSearchQuestion1")).clear();
		Browser.driver.findElement(By.id("headerSearchQuestion1")).sendKeys(searchTerm);
		element.click();
	}

	public void goToProductPages() {
		if (Browser.isElementPresent(By.cssSelector("sticky-header-bg")) && Browser.isElementVisible(By.cssSelector("sticky-header-bg"))) {
			Browser.scrollTo(0);
			Browser.waitForSomeTime();
			By allProductsXpath = By.xpath("//*[@id=\"bs-example-navbar-collapse-mainmenu\"]/div[3]/ul/li/a"); // XL and L view
			WebElement element = Browser.getWebElement(allProductsXpath);
			if (Browser.isElementPresent(allProductsXpath) && element.isDisplayed()) {
				Browser.click(allProductsXpath);
			} else {
				// ToDo: Make click when sticky header is visible
				String url = ProductsPage.url;
				Browser.goToByURL(url);
				Logger.info(pageNamePrefixForLogger + "Nav Bar - go to product page by URL: " + url);
			}
		} else {
			By productsLink = By.xpath("//*[@id=\"ProductMenu\"]/ul[1]/li[1]/div[3]/a");
			if (Browser.isElementPresent(productsLink) && Browser.isElementVisible(productsLink)) {
				Browser.click(productsLink);
				Logger.info(pageNamePrefixForLogger + "Nav Bar - Selecting PRODUCTS");
			} else {
				String url = ProductsPage.url;
				Browser.goToByURL(url);
				Logger.info(pageNamePrefixForLogger + "Nav Bar - go to product page by URL: " + url);
			}
		}
	}
}
