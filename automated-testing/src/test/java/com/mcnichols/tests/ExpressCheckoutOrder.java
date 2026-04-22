package com.mcnichols.tests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.pages.ProductListingPage;
import com.mcnichols.framework.pages.plp.OnlineOrderingProductListingPage;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.tests.util.TestClassBase;

public class ExpressCheckoutOrder extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToLogInPage() throws Exception {
		Pages.headerPageInclude().goToLoginFromHeader();

		verifyTrue(Pages.loginPage().isAt(), "Is at Login Page");
	}

	@Test(dependsOnMethods = { "canGoToLogInPage" })
	public void canGoToHomePage() throws Exception {
		Pages.loginPage().logInAsExpressCheckoutUser();

		if (!Pages.homePage().isAt()) {
			Logger.info("Not at Homepage.  Going to homepage now.");
			Pages.homePage().goToByUrl();	
		}
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
		
		checkForCartedItemsAndRemove();
	}

	@Test(dependsOnMethods = { "canGoToHomePage" })
	public void canGoToOnlineOrderingPage() throws Exception {
		boolean isGlobalBarPresent = Pages.headerPageInclude().isGlobalBarPresent();
		Logger.info("Is global header bar present: " + isGlobalBarPresent);
		
		ProductListingPage onlineOrderingPLP = Pages.productsPage().getProductListingPage(OnlineOrderingProductListingPage.NAME);
		onlineOrderingPLP.goToByUrl();
		
		verifyTrue(onlineOrderingPLP.isAtByPageHeading(), "Is at Online Ordering Page");
		Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector("#listing-top")));
		onlineOrderingPLP.isProductsFirstItemVisible();
	}

	@Test(dependsOnMethods = { "canGoToOnlineOrderingPage" })
	public void canGoToProductPage() throws Exception {
		ProductListingPage onlineOrderingPLP = Pages.productsPage().getProductListingPage(OnlineOrderingProductListingPage.NAME);
		//onlineOrderingPLP.goToProductsFirstItem();
		onlineOrderingPLP.goToProductsItem("8");

		verifyTrue(Pages.productPage().isAt(), "Is at Product Page");
		
		if (!Pages.productPage().isSkuPurchasableOnline()) {
			Pages.productPage().selectPurchasableSku();
		}
		
		verifyTrue(Pages.productPage().isSkuPurchasableOnline(), "Can purchase product online");
	}
	
	@Test(dependsOnMethods = { "canGoToProductPage" })
	public void canGoToCartPage() throws Exception {
		Pages.productPage().addToCart();

		verifyTrue(Pages.cartPage().isAt(), "Is at Cart Page");

		Pages.cartPage().getTotalNumberOfCartedItems();
		verifyFalse(Pages.cartPage().doesItemQuantitiesExceedLimitPerItem(), "Does Item Quantities Exceed Limit Per Order");
		verifyTrue(Pages.cartPage().isExpressCheckoutAvailable(), "Is Express Checkout Avaiable");
	}

	@Test(dependsOnMethods = { "canGoToCartPage" })
	public void canGoToOrderReviewPage() throws Exception {
		Pages.cartPage().expressCheckout();

		Browser.waitForSomeTime(10000);
		verifyTrue(Pages.checkoutOrderReviewExpressCheckoutPage().isAt(), "Is at Order Review Express Checkout Page");

		if (!Pages.checkoutOrderReviewExpressCheckoutPage().isCardVerificationNumberFieldPopulated()) {
			Pages.checkoutOrderReviewExpressCheckoutPage().populateCardVerificationNumberField();
			Browser.waitForTheLoadingOverlayToDisappear("Express Checkout Order Review Page");
		}

		Browser.waitForSomeTime(15000);
		Pages.checkoutOrderReviewPage().selectTermsAndConditions();
		Browser.waitForSomeTime();
	}

	@Test(dependsOnMethods = { "canGoToOrderReviewPage" })
	public void canGoToOrderConfirmationPage() throws Exception {
		Pages.checkoutOrderReviewExpressCheckoutPage().submitOrderBottomButton();
		verifyFalse(Pages.checkoutOrderReviewPage().isErrorPresent(), "Error occurred submitting the order!");

		Browser.waitForSomeTime(10000);
		verifyTrue(Pages.checkoutOrderConfirmationPage().isAt(true), "Is at Order Confirmation Page");
		Browser.waitForSomeTime();
		Pages.checkoutOrderConfirmationPage().isOrderNumberPresent();
	}
}
