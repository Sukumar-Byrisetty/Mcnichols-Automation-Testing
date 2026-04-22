package com.mcnichols.framework.pages;

import org.openqa.selenium.support.PageFactory;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.account.CreditCardsPage;
import com.mcnichols.framework.pages.account.AccountLandingPage;
import com.mcnichols.framework.pages.account.RegistrationPage;
import com.mcnichols.framework.pages.plp.OnlineOrderingProductListingPage;
import com.mcnichols.framework.pages.plp.PerforatedMetalProductListingPage;

public class Pages {

	public static HomePage homePage() {
		HomePage homePage = new HomePage();
		return homePage;
	}

	public static HeaderPageInclude headerPageInclude() {
		return new HeaderPageInclude();
	}
	
	public static Widgets widgets() {
		Widgets widgets = new Widgets();
		return widgets;
	}

	public static ProductsPage productsPage() {
		ProductsPage productPages = new ProductsPage();
		return productPages;
	}
	
	public static CategoryPage categoryPage() {
		CategoryPage categoryPage = new CategoryPage();
		return categoryPage;
	}

	public static ProductListingPage perforatedMetalProductListingPage() {
		ProductListingPage perforatedMetalPLP = new PerforatedMetalProductListingPage();
		PageFactory.initElements(Browser.driver, perforatedMetalPLP);
		return perforatedMetalPLP;
	}

	public static ProductListingPage onlineOrderingProductListingPage() {
		ProductListingPage onlineOrderingPLP = new OnlineOrderingProductListingPage();
		PageFactory.initElements(Browser.driver, onlineOrderingPLP);
		return onlineOrderingPLP;
	}

	public static ProductPage productPage() {
		ProductPage productPage = new ProductPage();
		return productPage;
	}

	public static SearchPage searchPage() {
		SearchPage searchPage = new SearchPage();
		return searchPage;
	}
	
	public static LogInPage loginPage() {
		LogInPage loginPage = new LogInPage();
		return loginPage;
	}
	
	public static CartPage cartPage() {
		CartPage cartPage = new CartPage();
		return cartPage;
	}

	public static LogInPromptPage logInPromptPage() {
		LogInPromptPage logInPromptPage = new LogInPromptPage();
		return logInPromptPage;
	}

	public static SubmitQuotePage submitQuotePage() {
		SubmitQuotePage submitQuotePage = new SubmitQuotePage();
		return submitQuotePage; 
	}

	public static CheckoutContactInformationPage checkoutContactInformationPage() {
		CheckoutContactInformationPage checkoutContactInformationPage = new CheckoutContactInformationPage();
		return checkoutContactInformationPage;
	}

	public static CheckoutShippingPage checkoutShippingPage() {
		CheckoutShippingPage checkoutShippingPage = new CheckoutShippingPage();
		return checkoutShippingPage;
	}

	public static CheckoutPaymentPage checkoutPaymentPage() {
		CheckoutPaymentPage checkoutPaymentPage = new CheckoutPaymentPage();
		return checkoutPaymentPage;
	}
	
	public static CheckoutOrderReviewPage checkoutOrderReviewPage() {
		CheckoutOrderReviewPage checkoutOrderReviewPage = new CheckoutOrderReviewPage();
		return checkoutOrderReviewPage;
	}

	public static CheckoutOrderReviewExpressCheckoutPage checkoutOrderReviewExpressCheckoutPage() {
		CheckoutOrderReviewExpressCheckoutPage checkoutOrderReviewExpressCheckoutPage = new CheckoutOrderReviewExpressCheckoutPage();
		return checkoutOrderReviewExpressCheckoutPage;
	}
	
	public static CheckoutOrderConfirmationPage checkoutOrderConfirmationPage() {
		CheckoutOrderConfirmationPage checkoutOrderConfirmationPage = new CheckoutOrderConfirmationPage();
		return checkoutOrderConfirmationPage;
	}
	
	public static AccountLandingPage accountLandingPage() {
		AccountLandingPage mAccountPage = new AccountLandingPage();
		return mAccountPage;
	}
	
	public static CreditCardsPage accountCreditCardsPage() {
		CreditCardsPage creditCardsPage = new CreditCardsPage();
		return creditCardsPage;
	}
	
	public static RegistrationPage accountRegistrationPage() {
		RegistrationPage accountRegistrationPage = new RegistrationPage();
		return accountRegistrationPage;
	}
	
	public static Sitemap siteMapPage() {
		Sitemap mSitemap = new Sitemap();
		return mSitemap;
	}
}
