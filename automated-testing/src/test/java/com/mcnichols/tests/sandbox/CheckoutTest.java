package com.mcnichols.tests.sandbox;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.tests.util.TestClassBase;

public class CheckoutTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToBaseUrl() throws Exception {
		verifyTrue(Pages.checkoutContactInformationPage().isAt());
	}

	@Test(dependsOnMethods = { "canGoToBaseUrl" })
	public void canGoToContactInformationPage() throws Exception {
		verifyTrue(Pages.checkoutContactInformationPage().isAt());
		Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector(".contact-info-header")));

		Pages.checkoutContactInformationPage().enterContactInformationForm();

		//if (Pages.widgets().isAddressVerificationDisplay()) {
			//Pages.widgets().selectAddressVerificationContinue();
		//}

		//Pages.checkoutContactInformationPage().continueButton();
	}


}
