package com.mcnichols.tests.production;

import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.tests.util.TestClassBase;

public class PlaceQuoteForRegisteredUser extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
	}

	@Test(dependsOnMethods = { "canGoToHomePage" })
	public void canPerformSearch() throws Exception {
		Pages.headerPageInclude().performOnSiteSearch(TestingConfig.getItemNotForOnlinePurchase(), true);
	}

	@Test(dependsOnMethods = { "canPerformSearch" })
	public void canGoToProductPage() throws Exception {
		verifyTrue(Pages.productPage().isAt(), "Is at Product Page");
		verifyFalse(Pages.productPage().isSkuPurchasableOnline(), "Can purchase product online");
	}

	@Test(dependsOnMethods = { "canGoToProductPage" })
	public void canGoToCartPage() throws Exception {
		Pages.productPage().addToCart();

		verifyTrue(Pages.cartPage().isAt(), "Is at Cart Page");
	}

	@Test(dependsOnMethods = { "canGoToCartPage" })
	public void canGoToLogInPromptPage() throws Exception {
		Pages.cartPage().requestQuote();

		if (!Pages.headerPageInclude().isUserLoggedIn()) {
			verifyTrue(Pages.logInPromptPage().isAt(), "Is at Login Prompt Page");
			Browser.scrollTo(700);	
		} else {
			Logger.info("User is already logged in.");
		}
	}

	@Test(dependsOnMethods = { "canGoToLogInPromptPage" })
	public void canGoToLogInPage() throws Exception {
		if (!Pages.headerPageInclude().isUserLoggedIn()) {
			Pages.logInPromptPage().logInAsRegisteredUser();

			verifyTrue(Pages.loginPage().isAt(), "Is at Login Page");
			Browser.scrollTo(1000);
		} else {
			Logger.info("User is already logged in.");
		}
	}

	@Test(dependsOnMethods = { "canGoToLogInPromptPage" })
	public void canGoToRquestQuotePage() throws Exception {
		Pages.loginPage().logInAsRegisteredUser();
		verifyFalse(Pages.loginPage().isErrorPresent(), "Error occurred at login!");

		if (Pages.cartPage().isAt()) {
			verifyTrue(Pages.cartPage().isRequestQuoteButtonPresent(), "Is Request Quote Button Present");
			Pages.cartPage().requestQuote();
		}

		Browser.waitForSomeTime();
		verifyTrue(Pages.submitQuotePage().isAt(), "Is at Submit Quote Page");
		Browser.scrollTo(300);

		Pages.submitQuotePage().enterDateMaterialNeeded(Pages.submitQuotePage().getDateMaterialNeeded());
		verifyTrue(Pages.submitQuotePage().isSubmitButtonPresent(), "Is submit button present");
		// DO NOT PLACE QUOTE!!!
	}

	@Test(dependsOnMethods = { "canGoToRquestQuotePage" })
	public void canGoToCartPageToRemoveItems() throws Exception {
		Pages.cartPage().goToByUrl();
		Pages.cartPage().isAt();
		TestUtil.takeScreenshot("PlaceQuoteForRegisteredUser-RemoveItemsFromCart");
		Pages.cartPage().getTotalNumberOfCartedItems();
		Pages.cartPage().removeCartedItems();
	}
}
