package com.mcnichols.tests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.tests.util.TestClassBase;

public class PlaceQuoteForGuestUser extends TestClassBase {

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
		Browser.scrollTo(700);
		Pages.cartPage().requestQuote();

		verifyTrue(Pages.logInPromptPage().isAt(), "Is at Login Prompt Page");
	}

	@Test(dependsOnMethods = { "canGoToLogInPromptPage" })
	public void canGoToContactInformationPage() throws Exception {
		Browser.scrollTo(200);
		Pages.logInPromptPage().continueAsGuestUser();

		verifyTrue(Pages.checkoutContactInformationPage().isAt(), "Is at Checkout Contact Information Page");
		
		Pages.checkoutContactInformationPage().scrollToPageHeader();

		Pages.checkoutContactInformationPage().enterContactInformationForm();
	}

	@Test(dependsOnMethods = { "canGoToLogInPromptPage" })
	public void canGoToRquestQuotePage() throws Exception {
		Pages.checkoutContactInformationPage().continueButton();

		if (Pages.widgets().isAddressVerificationDisplay()) {
			Pages.widgets().selectAddressVerificationContinue();
		}
		
		if (Pages.cartPage().isAt()) {
			Pages.cartPage().requestQuote();
		}

		verifyTrue(Pages.submitQuotePage().isAt(), "Is at Submit Quote Page");
		Browser.scrollTo(600);
		Pages.submitQuotePage().enterDateMaterialNeeded(Pages.submitQuotePage().getDateMaterialNeeded());
		verifyTrue(Pages.submitQuotePage().isSubmitButtonPresent(), "Is submit button present");
		Pages.submitQuotePage().selectSubmitButton();
	}
}
