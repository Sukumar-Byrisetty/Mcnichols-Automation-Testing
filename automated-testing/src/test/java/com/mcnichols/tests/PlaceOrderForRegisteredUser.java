package com.mcnichols.tests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.LoginCredentials;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.pages.ProductListingPage;
import com.mcnichols.framework.pages.plp.OnlineOrderingProductListingPage;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.tests.util.TestClassBase;

public class PlaceOrderForRegisteredUser extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Go to the homepage.")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
	}
	
	@Test(dependsOnMethods = { "canGoToHomePage" })
	public void canGoToLogInPage() throws Exception {
		Pages.headerPageInclude().goToLoginFromHeader();

		verifyTrue(Pages.loginPage().isAt(), "Is at Login Page");
	}

	@Test(dependsOnMethods = { "canGoToLogInPage" })
	public void canGoToHomePage2() throws Exception {
		Pages.loginPage().logInAsRegisteredUser();
		verifyFalse(Pages.loginPage().isErrorPresent(), "Error occurred at login!");

		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
		
		checkForCartedItemsAndRemove();
	}

	@Test(dependsOnMethods = { "canGoToHomePage2" })
	public void canGoToOnlineOrderingPage() throws Exception {
		boolean isGlobalBarPresent = Pages.headerPageInclude().isGlobalBarPresent();
		Logger.info("Is global header bar present: " + isGlobalBarPresent);
		
		ProductListingPage onlineOrderingPLP = Pages.productsPage().getProductListingPage(OnlineOrderingProductListingPage.NAME);
		onlineOrderingPLP.goToByUrl();
		
		verifyTrue(onlineOrderingPLP.isAtByPageHeading());
		Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector("#listing-top")));
		onlineOrderingPLP.isProductsFirstItemVisible();
	}

	@Test(dependsOnMethods = { "canGoToOnlineOrderingPage" })
	public void canGoToProductPage() throws Exception {
		ProductListingPage onlineOrderingPLP = Pages.productsPage().getProductListingPage(OnlineOrderingProductListingPage.NAME);
		//onlineOrderingPLP.goToProductsFirstItem();
		onlineOrderingPLP.goToProductsItem("3");
		
		verifyTrue(Pages.productPage().isAt(), "Is at Product Page");
		
		if (!Pages.productPage().isSkuPurchasableOnline()) {
			Pages.productPage().selectPurchasableSku();
		}
		
		if (!Pages.productPage().isSkuPurchasableOnline()) {
			// Make another attempt with different item
			Logger.warning("The product page does not have any items available for online purchase!");
			Logger.warning("Making another Attempt by returning to the PLP and selecting a different item...");
			onlineOrderingPLP.goToByUrl();
			
			verifyTrue(onlineOrderingPLP.isAtByPageHeading());
			Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector("#listing-top")));
			onlineOrderingPLP.isProductsFirstItemVisible();
			onlineOrderingPLP.goToProductsItem("1");
			
			verifyTrue(Pages.productPage().isAt(), "Is at Product Page");
			
			if (!Pages.productPage().isSkuPurchasableOnline()) {
				Pages.productPage().selectPurchasableSku();
			}
		}
		
		verifyTrue(Pages.productPage().isSkuPurchasableOnline(), "Can purchase product online");
	}
	
	@Test(dependsOnMethods = { "canGoToProductPage" })
	public void canGoToCartPage() throws Exception {
		Pages.productPage().addToCart();

		verifyTrue(Pages.cartPage().isAt(), "Is at Cart Page");

		Pages.cartPage().getTotalNumberOfCartedItems();
		verifyFalse(Pages.cartPage().doesItemQuantitiesExceedLimitPerItem());
	}
	
	@Test(dependsOnMethods = { "canGoToCartPage" })
	public void canGoToShippingPage() throws Exception {
		Pages.cartPage().checkout();

		verifyTrue(Pages.checkoutShippingPage().isAt(), "Is at Checkout Shipping Page");
		Pages.checkoutShippingPage().populateShippingAddressForm(getUserProfile());
		Pages.checkoutShippingPage().selectDeliveryAppoinmentRequired(false);
		Pages.checkoutShippingPage().selectSpecialHandlingRequired(false);
		Pages.checkoutShippingPage().selectInstructionsRequired(false);
	}

	@Test(dependsOnMethods = { "canGoToShippingPage" })
	public void canGoToPaymentPage() throws Exception {
		Pages.checkoutShippingPage().continueButton();

		verifyFalse(Pages.checkoutShippingPage().isErrorPresent(), "Error occurred submitting the shipping page!");

		if (Pages.widgets().isAddressVerificationDisplay()) {
			Pages.widgets().selectAddressVerificationContinue();
			Browser.waitForTheLoadingOverlayToDisappear("");
			verifyFalse(Pages.checkoutShippingPage().isErrorPresent(), "Error occurred submitting the shipping page!");
		}

		boolean didCheckoutPaymentPageLoad = Pages.checkoutPaymentPage().isAt();
		
		// Attempt to remove carted items when a shipping error occurs 
		if (!didCheckoutPaymentPageLoad) {
			TestUtil.takeScreenshot("PlaceOrderForRegisteredUser-FAIL");
			Logger.fail("Failed to load the payment page from the shipping!");
			Logger.info("Attempting to remove items from the cart...");
			
			Pages.cartPage().goToByUrl();
			Pages.cartPage().isAt();
			TestUtil.takeScreenshot("PlaceOrderForRegisteredUser-RemoveItemsFromCart");
			Pages.cartPage().getTotalNumberOfCartedItems();
			Pages.cartPage().removeCartedItems();
			TestUtil.takeScreenshot("PlaceOrderForRegisteredUser-RemoveItemsFromCart");
		}
		verifyTrue(didCheckoutPaymentPageLoad, "Is at Checkout Payment Page");		
		
		// Proceed if there are no shipping errors
		if (Pages.checkoutPaymentPage().isDefaultCreditCardSelected() && !Pages.checkoutPaymentPage().isCardVerificationNumberFieldPopulated()) {
			Pages.checkoutPaymentPage().populateCardVerificationNumberField();
		} else {
			verifyTrue(false, "Expected credit card to be default for the user testing account!");
			// ToDo: Add conditions to handle different scenarios
		}
	}

	@Test(dependsOnMethods = { "canGoToPaymentPage" })
	public void canGoToOrderReviewPage() throws Exception {
		Pages.checkoutPaymentPage().continueButton();

		verifyTrue(Pages.checkoutOrderReviewPage().isAt(), "Is at Checkout Order Review Page");

		Pages.checkoutOrderReviewPage().selectTermsAndConditions();
	}

	@Test(dependsOnMethods = { "canGoToOrderReviewPage" })
	public void canGoToOrderConfirmationPage() throws Exception {
		Pages.checkoutOrderReviewPage().submitOrderButton();
		Browser.waitForSomeTime();
		verifyFalse(Pages.checkoutOrderReviewPage().isErrorPresent(), "Error occurred submitting the order!");

		verifyTrue(Pages.checkoutOrderConfirmationPage().isAt(true), "Is at Order Confirmation Page");
		Browser.waitForSomeTime();
		Pages.checkoutOrderConfirmationPage().isOrderNumberPresent();
	}
	
	public LoginCredentials getUserProfile() {
		return TestingConfig.getLogin(0);
	}
}
