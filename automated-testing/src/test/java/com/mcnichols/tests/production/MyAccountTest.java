/**
 * 
 */
package com.mcnichols.tests.production;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.tests.util.TestClassBase;

/**
 * @author Vadivel
 *
 */
public class MyAccountTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Go to the homepage.")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
	}

	@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Login Page")
	public void canGoToLogInPage() throws Exception {
		Pages.headerPageInclude().goToLoginFromHeader();
		verifyTrue(Pages.loginPage().isAt(), "Is at Login Page");
	}

	@Test(dependsOnMethods = { "canGoToLogInPage" }, description = "Authendicate login detail")
	public void canGoToHomePage2() throws Exception {
		Pages.loginPage().logInAsRegisteredUser();
		verifyFalse(Pages.loginPage().isErrorPresent(), "Error occurred at login!");
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
	}

	@Test(dependsOnMethods = { "canGoToHomePage2" }, description = "Go to the CreditCard Page")
	@Parameters({ "accountCC" })
	public void canGotoMyAccount(String accountCC) throws Exception {
		Browser.goToByURL(accountCC);
		verifyTrue(Pages.accountCreditCardsPage().isAt(), "Is at Home Page!");
	}

	@Test(dependsOnMethods = { "canGotoMyAccount" }, description = "Click Add New Card button - CC")
	public void openCCpopup() throws Exception {
		Pages.accountCreditCardsPage().onClickEvent("button.add-new-card");
		verifyTrue(Pages.accountCreditCardsPage().isAt(), "Is at CreditCard Page!");

	}

	@Test(dependsOnMethods = { "openCCpopup" }, description = "CC Popup - Enter Billing and Payment detail")
	public void overrideCCDetail() throws Exception {
		verifyTrue(Pages.accountCreditCardsPage().isCreditCardPopupDisplayedWithCyberSourceContent(), "Is CyberSource Content Loaded!");
	}
}
