package com.mcnichols.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.pages.ProductListingPage;
import com.mcnichols.framework.pages.plp.PerforatedMetalProductListingPage;
import com.mcnichols.tests.util.TestClassBase;

public class BrowseAndSearchTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());

		boolean isGlobalBarPresent = Pages.headerPageInclude().isGlobalBarPresent();
		if (isGlobalBarPresent) {
			Pages.headerPageInclude().closeGlobalBar();
		}

		verifyTrue(Pages.homePage().isHeroSectionPresent());
		verifyTrue(Pages.homePage().isFeaturedSectionPresent());
		verifyTrue(Pages.homePage().isPromotionInformationSectionPresent());
	}
	
	@Test(dependsOnMethods = { "canGoToHomePage" })
	public void canGoToSearchPage() throws Exception {
		Pages.headerPageInclude().performOnSiteSearch(TestingConfig.getRandomSearchTerm());
		Browser.waitForSomeTime(2000);
		if (Pages.searchPage().isAtByUrl()) {
			Pages.searchPage().hasSearchResults();
		}
	}

	@Test(dependsOnMethods = { "canGoToSearchPage" })
	public void canGoToProductsPage() throws Exception {
		Pages.headerPageInclude().goToProductPages();

		verifyTrue(Pages.productsPage().isAt());
		verifyTrue(Pages.productsPage().isProductsContainerPresent());
		verifyTrue(Pages.productsPage().isPerforatedMetalProduct());
	}
	
	@Test(dependsOnMethods = { "canGoToProductsPage" })
	public void canGoToPerforatedMetalProductListingPage() throws Exception {
		ProductListingPage perforatedMetalPLP = Pages.productsPage().getProductListingPage(PerforatedMetalProductListingPage.NAME);
		perforatedMetalPLP.goTo();
		verifyTrue(perforatedMetalPLP.isAtPage());

		Browser.scrollTo(500);
		perforatedMetalPLP.selectTabOverview();
		Browser.waitForSomeTime(500);
		perforatedMetalPLP.selectTabOrdering();
		perforatedMetalPLP.selectTabCharts();
		perforatedMetalPLP.selectTabTables();
		perforatedMetalPLP.selectTabProducts();

		String roundTileContainer = ".tile-name .subcategory img[title=\"Round\"]";
		if (!Browser.isElementPresent(By.cssSelector(roundTileContainer))) {
			roundTileContainer = ".tile-name .subcategory img[title=\"Round Perforated Metal\"]";	
		}
		
		
		By roundTile = By.cssSelector(roundTileContainer);
		perforatedMetalPLP.isTilePresent(roundTile);
		WebElement roundTileElement = Browser.getWebElement(roundTile);
		perforatedMetalPLP.selectTile(roundTileElement);
	}
	
	
	@Test(dependsOnMethods = { "canGoToPerforatedMetalProductListingPage" })
	public void canGoToPerforatedMetalProductRoundHoleListingPage() throws Exception {
		verifyTrue(Pages.categoryPage().isAt());

		Browser.waitForSomeTime();
		ProductListingPage perforatedMetalPLP = Pages.productsPage().getProductListingPage(PerforatedMetalProductListingPage.NAME);
		perforatedMetalPLP.isProductsFirstItemVisible();
		perforatedMetalPLP.goToProductsFirstItem();
	}

	@Test(dependsOnMethods = { "canGoToPerforatedMetalProductListingPage" })
	public void canGoToProductPage() throws Exception {
		verifyTrue(Pages.productPage().isAt());
		
		//Browser.scrollTo(500);

		Pages.productPage().selectTabOverview();
		Pages.productPage().isTabOverviewContentDisplayed();

		Pages.productPage().selectTabOrdering();
		Pages.productPage().selectTabCharts();
		Browser.waitForSomeTime(500);
		Pages.productPage().selectTabTables();
		Pages.productPage().selectTabSpecifications();
		
		if (Pages.widgets().isBackToTopVisibile()) {
			Pages.widgets().selectBackToTopElement();
		}
	}
	
}
