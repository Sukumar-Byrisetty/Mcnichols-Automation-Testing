package com.mcnichols.tests.util;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.BrowserOperations;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.pages.Widgets;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.framework.util.extentreports.ExtentTestManager;
import com.mcnichols.framework.util.listeners.InvokedMethodListener;
import com.mcnichols.framework.util.listeners.TestListener;

@Listeners({TestListener.class, InvokedMethodListener.class})
public class TestBase extends TestListener {
	private int screenshotCounter = 1;
	public StringBuffer verificationErrors = new StringBuffer();

	@BeforeClass(alwaysRun = true)
	@Parameters({ "browser.name", "env"})
	public void setUp(@Optional("browser.name") String browserName, @Optional("env") String env) throws Exception {
		browserName = StringUtil.isEmpty(browserName) || browserName.equals("browser.name") ? BrowserOperations.CHROME_BROWSER : browserName;
		setupWorkingDirectories();

		BrowserOperations.launchBrowser(browserName, env);
	}

	@Test (priority = 0, description = "Setup test parameters.")
	@Parameters({ "base.url",  "browser.viewport",  "env"})
	public void startTest(String baseUrl, @Optional("browser.viewport") String browserViewport, @Optional("env") String env) throws Exception {
		browserViewport = StringUtil.isEmpty(browserViewport) || browserViewport.equals("browser.viewport") ? "" : browserViewport;
		if (StringUtil.isNotEmpty(env)) {
			Logger.info("Environment: " + env);	
		}
		Logger.info("Launching browser: " + BrowserOperations.testingBrowserName);

		BrowserOperations.setBrowserViewport(browserViewport);

		Browser.goToByURL(baseUrl);
		Logger.info("Loading URL: " + baseUrl);
		long startTime = System.currentTimeMillis();
		Browser.waitForJavaScriptDependencies();
		Browser.waitForTheLoadingOverlayToDisappear("");
		Logger.processTime(startTime, System.currentTimeMillis());

		if (Pages.widgets().isAnnouncementModalVisibile()) {
			Pages.widgets().closeAnnouncementModal();
		}
		
		if(Pages.widgets().isPromoBarVisibile()) {
			Pages.widgets().closePromoBar();
		}
		
		Widgets.isKlaviyoFlyoutFormPresent();
	}

	@BeforeMethod
	@AfterMethod
	public void takeScreenshot(ITestResult testResult, ITestContext testContext) throws IOException {
		try {
			String testName = testContext.getName();
			if(ITestResult.FAILURE == testResult.getStatus()){
				testName += "-FAIL";
			}

			takeScreenshot(testName);
		} catch (Exception e) {
			// Do nothing
			// Reporter.log("Exception while taking screenshot due to: " + e.getMessage(), true);
		}
	}

	@BeforeMethod
	public void setupJavaScriptErrorChecker() {
		Browser.setupJavaScriptErrorChecker();
	}

	@AfterMethod
	public void checkForJavaScriptErrors(ITestResult result) {
		Reporter.setCurrentTestResult(result);

		Object jsError = Browser.getJavaScriptErrors();
		if (jsError != null) {
			Logger.warning("JavaScript Error1: " + jsError.toString());
		}

		Browser.checkJavaScriptErrorLog();
	}
	
	public void takeScreenshot(String fileName) throws IOException {
		// Create reference of TakesScreenshot
		TakesScreenshot ts = (TakesScreenshot) Browser.driver;

		// Call method to capture screenshot
		File source = ts.getScreenshotAs(OutputType.FILE);

		FileUtils.copyFile(source, new File("." + TestUtil.getScreenshotsPath() + "/" + fileName + "-" + screenshotCounter + ".png"));
		ExtentTestManager.getTest().addScreenCaptureFromPath(TestUtil.testScreenshotsDirectoryName + "/" + fileName + "-" + screenshotCounter + ".png", fileName);
		screenshotCounter++;
	}

	private void setupWorkingDirectories() {
		TestUtil.createDirectory("./" + TestUtil.testReportDirectoryName);
		TestUtil.createDirectory("." + TestUtil.getTestIdReportingPath());
		TestUtil.createDirectory("." + TestUtil.getScreenshotsPath());
	}

	public void verifyTrue(boolean condition) {
		verifyTrue(condition, null);
	}

	public void verifyTrue(boolean condition, String message) {
		try {
			assertTrue(condition, message);
			Logger.info("Current URL: " + Browser.driver.getCurrentUrl());
		} catch (AssertionError e) {
			Logger.warning("Current URL: " + Browser.driver.getCurrentUrl());
			throw new AssertionError(e.getMessage());
		}
	}

	public void verifyFalse(boolean condition) {
		verifyFalse(condition, null);
	}

	public void verifyFalse(boolean condition, String message) {
		try {
			assertFalse(condition, message);
			Logger.info("Current URL: " + Browser.driver.getCurrentUrl());
		} catch (AssertionError e) {
			Logger.warning("Current URL: " + Browser.driver.getCurrentUrl());
			throw new AssertionError(e.getMessage());
		}
	}

	public void verifyEquals() {
		//ToDo
	}
	
	public void checkForCartedItemsAndRemove() {
		Logger.info("Check for carted items and remove any that are present to have a clean cart.");
		int countOfCartedItems = Browser.getCartedItemsCount();
		if (countOfCartedItems > 0) {
			// Go to the cart page to remove items
			Pages.cartPage().goToByUrl();
			Pages.cartPage().isAt();
			TestUtil.takeScreenshot("PlaceOrderForRegisteredUser-RemoveItemsFromCart");
			Pages.cartPage().getTotalNumberOfCartedItems();
			Pages.cartPage().removeCartedItems();
			
			// Return back to the home page and proceed with testing
			Pages.homePage().goToByUrl();
			verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
		}
	}
	
	@BeforeMethod
	@AfterMethod
	public static void getEnvironmentDetails() {
		Browser.getPageEnvironmentDetails();
	}
}
