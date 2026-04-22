package com.mcnichols.tests;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.pages.account.RegistrationPage;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.tests.util.TestClassBase;

public class CreateAccount extends TestClassBase {
	public int selectedAccountType = 0;
	public int standardAccountType = 0;
	public int expressCheckoutAccountType = 1;
	
	@Test(dependsOnMethods = { "startTest" }, description = "Go to the homepage.")
	@Parameters({ "accountType" })
	public void canGoToHomePage(int accountType) throws Exception {
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
		selectedAccountType = accountType;
	}
	
	@Test(dependsOnMethods = { "canGoToHomePage" })
	//public void canGoToLogInPage() throws Exception {
	public void canCreateAndConfigureAccount() throws Exception {
		Pages.headerPageInclude().goToLoginFromHeader();

		verifyTrue(Pages.loginPage().isAt(), "Is at Login Page");
		
		Pages.loginPage().createAccount(selectedAccountType);
		
		// Verify the account the account has not been created
		boolean hasError = Pages.loginPage().isErrorPresent();
		boolean doesEmailExistsError = Pages.loginPage().doesEmailExistsError();
		if (hasError && doesEmailExistsError) {
			// Call another method
			Pages.loginPage().logIn(selectedAccountType);
			
			canGoToAccountCreditCardPage();
			
		} else {
			canGoToRegistrationPage();
		}
		
		//verifyFalse(hasError, "Found submission error!");
	}
	
	//@Test(dependsOnMethods = { "canGoToLogInPage" })
	public void canGoToRegistrationPage() throws Exception {
		verifyTrue(Pages.accountRegistrationPage().isAt(), "Is at Registration Page");
		
		Pages.accountRegistrationPage().enterProfileInformation(selectedAccountType);
		
		Pages.accountRegistrationPage().enterOtherInformation(selectedAccountType);
		
		Pages.accountRegistrationPage().selectTermsAndConditions();
		
		Pages.accountRegistrationPage().selectSubmitButton();
		
		if (Pages.widgets().isAddressVerificationDisplay()) {
			Pages.widgets().selectAddressVerificationContinue();
			Browser.waitForTheLoadingOverlayToDisappear(RegistrationPage.pageNamePrefixForLogger + " AVS");
			
			boolean hasError = Pages.accountRegistrationPage().isErrorPresent();
			verifyFalse(hasError, "Found submission error!");
		}
		
		if (Pages.widgets().isAccountConfirmationModalPresent()) {
			Pages.widgets().selectAccountConfirmationButtonForModal(true);
			Browser.waitForTheLoadingOverlayToDisappear(RegistrationPage.pageNamePrefixForLogger + " Account Confirmation");
		}
		
		boolean hasError = Pages.accountRegistrationPage().isErrorPresent();
		verifyFalse(hasError, "Found submission error!");
		
		Pages.accountCreditCardsPage().goToByUrl();
		canGoToAccountCreditCardPage();
	}
	
	//@Test(dependsOnMethods = { "canGoToRegistrationPage" })
	public void canGoToAccountCreditCardPage() throws Exception {
		boolean isAt = Pages.accountCreditCardsPage().isAt();
		Logger.info("Is At Account Credit Card Page: " + isAt);
		
		if (!isAt) {
			Logger.info("Wait for some time to allow the page to load...");
			Browser.waitForSomeTime(8000);
		}
		
		verifyTrue(Pages.accountCreditCardsPage().isAt(), "Is at Account Credit Cards Page!");
		
		Pages.accountCreditCardsPage().clickAddNewCreditCardButton();
		
		verifyTrue(Pages.accountCreditCardsPage().isCreditCardPopupDisplayedWithCyberSourceContent(), "Is CyberSource Content Loaded!");
		
		Pages.accountCreditCardsPage().enterBillingPaymentDetails(selectedAccountType);
		Pages.accountCreditCardsPage().clickModalCyberSourceContinueButton();
		Pages.accountCreditCardsPage().selectDefaultCreditCard(1);
	}
}
