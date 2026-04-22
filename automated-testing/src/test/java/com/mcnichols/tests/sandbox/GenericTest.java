package com.mcnichols.tests.sandbox;

import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.extentreports.ExtentTestManager;
import com.mcnichols.tests.util.TestClassBase;

public class GenericTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Loading homepage")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());
		ExtentTestManager.getTest().info("We are at the homepage.");
		Browser.waitForSomeTime();
	}

	/*@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Verify product page")
	@Parameters({ "product.path" })
	public void canGoToProductPage(String productPath) throws Exception {
		Browser.goTo(productPath);

		verifyTrue(Pages.productPage().isAt());
		Browser.waitForSomeTime();
		Browser.scrollTo(500);
		
	}*/

}
