package com.mcnichols.tests.vendor;

import org.testng.annotations.Test;

import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.vendor.VendorService;
import com.mcnichols.tests.util.TestClassBase;

public class VerifyCallRail extends TestClassBase {
	@Test(dependsOnMethods = { "startTest" })
	public void canGoToWordPressHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt(), "WordPress - Is at Home Page");
		
		boolean isHeaderSwapSuccessful = VendorService.callRail().isSwapSuccessfulInHeader();
		verifyTrue(isHeaderSwapSuccessful, "Header CallRail Swap");
		
		boolean isDynamicElementSwapSuccessful = VendorService.callRail().isSwapSuccessfulInDynamicElement();
		verifyTrue(isDynamicElementSwapSuccessful, "Dynamic Element CallRail Swap");
	}
	
	@Test(dependsOnMethods = { "canGoToWordPressHomePage" })
	public void canGoToATGLogInPage() throws Exception {
		Pages.headerPageInclude().goToLoginFromHeader();

		verifyTrue(Pages.loginPage().isAt(), "ATG - Is at Login Page");
		
		boolean isHeaderSwapSuccessful = VendorService.callRail().isSwapSuccessfulInHeader();
		verifyTrue(isHeaderSwapSuccessful, "Header CallRail Swap");
		
		boolean isDynamicElementSwapSuccessful = VendorService.callRail().isSwapSuccessfulInDynamicElement();
		verifyTrue(isDynamicElementSwapSuccessful, "Dynamic Element CallRail Swap");
	}
}
