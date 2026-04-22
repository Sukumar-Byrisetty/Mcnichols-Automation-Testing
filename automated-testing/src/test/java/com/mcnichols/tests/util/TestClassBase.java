package com.mcnichols.tests.util;

import static org.testng.Assert.fail;

import org.testng.annotations.AfterClass;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;

public class TestClassBase extends TestBase {

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		try {
			Browser.driver.quit();
		} catch (Exception e) {
			Logger.fail("TestBase - Issue occured during tear down due to: " + e.getMessage());
		}

		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
