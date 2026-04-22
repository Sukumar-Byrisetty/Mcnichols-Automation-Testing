package com.mcnichols.tests.util;

import static org.testng.Assert.fail;

import org.testng.annotations.AfterSuite;

import com.mcnichols.framework.Browser;

public class TestSuiteBase extends TestBase {

	@AfterSuite(alwaysRun = true)
	public void tearDown() throws Exception {
		Browser.driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
