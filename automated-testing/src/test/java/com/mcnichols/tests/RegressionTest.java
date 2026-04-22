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

public class RegressionTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());

		boolean isGlobalBarPresent = Pages.headerPageInclude().isGlobalBarPresent();
		if (isGlobalBarPresent) {
			Pages.headerPageInclude().closeGlobalBar();
		}

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

		By roundTile = By.cssSelector(".tile-name .subcategory img[title=\"Round\"]");
		perforatedMetalPLP.isTilePresent(roundTile);
		WebElement roundTileElement = Browser.getWebElement(roundTile);
		perforatedMetalPLP.selectTile(roundTileElement);

		perforatedMetalPLP.isProductsFirstItemVisible();
		perforatedMetalPLP.goToProductsFirstItem();
	}

	@Test(dependsOnMethods = { "canGoToPerforatedMetalProductListingPage" })
	public void canGoToProductPage() throws Exception {
		verifyTrue(Pages.productPage().isAt());
		
		//Browser.scrollTo(500);
		
		Pages.productPage().selectTabOverview();
		Pages.productPage().selectTabOrdering();
		Pages.productPage().selectTabCharts();
		Browser.waitForSomeTime(500);
		Pages.productPage().selectTabTables();
		Pages.productPage().selectTabSpecifications();
		
		if (Pages.widgets().isBackToTopVisibile()) {
			Pages.widgets().selectBackToTopElement();
		}
	}
	
	@Test(dependsOnMethods = { "canGoToProductPage" })
	public void canGoToLogInPage() throws Exception {
		if (!Pages.headerPageInclude().isUserLoggedIn()) {
			Pages.headerPageInclude().goToLoginFromHeader();
			
			verifyTrue(Pages.loginPage().isAt());
	
			Pages.loginPage().logInAsRegisteredUser();
			verifyFalse(Pages.loginPage().isErrorPresent(), "Error occurred at login!");
		}
	}

	@Test(dependsOnMethods = { "canGoToLogInPage" })
	public void canGoToProductPageLoggedIn() throws Exception {
		verifyTrue(Pages.productPage().isAt());
		verifyTrue(Pages.headerPageInclude().isUserLoggedIn());

		Pages.productPage().addToCart();
	}

	@Test(dependsOnMethods = { "canGoToProductPageLoggedIn" })
	public void canGoToCartPage() throws Exception {
		verifyTrue(Pages.cartPage().isAt());
	}
}
