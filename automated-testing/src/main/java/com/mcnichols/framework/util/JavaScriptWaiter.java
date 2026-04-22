package com.mcnichols.framework.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;

public class JavaScriptWaiter {
	private static final long SECONDS_TO_WAIT = 40;

	// Wait Until JavaScript Ready
	public static void waitUntilJSReady() {
		WebDriverWait wait = new WebDriverWait(Browser.driver, SECONDS_TO_WAIT);
		JavascriptExecutor jsExec = (JavascriptExecutor) Browser.driver;

		// Wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) Browser.driver)
				.executeScript("return document.readyState").toString().equals("complete");

		// Get JS is Ready
		boolean jsReady = (Boolean) jsExec.executeScript("return document.readyState").toString().equals("complete");

		// Wait Javascript until it is Ready!
		if (!jsReady) {
			Logger.info("Javascript in NOT Ready!");
			// Wait for Javascript to load
			wait.until(jsLoad);
		}
	}

	// Wait for JQuery Load
	private static void waitForJQueryLoad() {
		WebDriverWait wait = new WebDriverWait(Browser.driver, SECONDS_TO_WAIT);
		JavascriptExecutor jsExec = (JavascriptExecutor) Browser.driver;

		// Wait for jQuery to load
		ExpectedCondition<Boolean> jQueryLoad = driver -> ((Long) ((JavascriptExecutor) Browser.driver)
				.executeScript("return jQuery.active") == 0);

		// Get JQuery is Ready
		boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");

		// Wait JQuery until it is Ready!
		if (!jqueryReady) {
			sleep(2);
			Logger.info("JQuery is NOT Ready!");
			// Wait for jQuery to load
			wait.until(jQueryLoad);
		}
	}

	// Wait Until JQuery and JavaScript Ready
	public static void waitUntilJQueryReady() {
		JavascriptExecutor jsExec = (JavascriptExecutor) Browser.driver;

		sleep(1);
		boolean jQueryDefined = false;
		try {
			jQueryDefined = (Boolean) jsExec.executeScript("try { return typeof jQuery != 'undefined' } catch(err) {}");	
		} catch (Exception e) {
			e.printStackTrace();
			sleep(20);
		}
		// First check that JQuery is defined on the page. If it is, then wait AJAX
		
		if (jQueryDefined == true) {
			// Pre Wait for stability (Optional)
			sleep(1);

			// Wait JQuery Load
			waitForJQueryLoad();

			// Wait JS Load
			waitUntilJSReady();

			// Post Wait for stability (Optional)
			sleep(1);
		} else {
			Logger.fail("jQuery is not defined on this site!");
		}
	}

	public static void sleep(Integer seconds) {
		long secondsLong = (long) seconds;
		try {
			Thread.sleep(secondsLong);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String getValueFromConsole(String inputValue) {
		String valueFromConsole = "";
		try {
			JavascriptExecutor jsExec = (JavascriptExecutor) Browser.driver;
			String statement = "if (" + inputValue + " != undefined) { " + "return " + inputValue + "; }";
	
			// Get JS value
			valueFromConsole = (String) jsExec.executeScript(statement).toString();
		} catch (Exception e) {
			// Do nothing
		}
		
		return valueFromConsole;
	}
}
