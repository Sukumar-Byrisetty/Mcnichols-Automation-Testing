package com.mcnichols.framework.pages;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;

public class SubmitQuotePage {
	public static final String PROPERTY_FILE = "cart-page-config.xml";
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	public static String url = "/checkout/submit-quote.jsp";
	public static String title = "McNichols - Gimme5";
	public static String titleUpdate = "Submit a Quote";
	public static String titleLogin = "McNichols - Login";
	public static String titleLoginUpdate = "Log In or Continue As Guest";
	public static String pageHeader = "SUBMIT QUOTE";
	public static String pageNamePrefixForLogger = "Checkout - Submit Quote Page -";

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains((title)) || Browser.title().contains((titleUpdate)) 
					|| Browser.title().contains(titleLogin) || Browser.title().contains(titleLoginUpdate)) {
				Logger.info(pageNamePrefixForLogger + "Title verified.");
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public String getDateMaterialNeeded() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, 15); // Adds 15 days

		// convert calendar to date
        Date currentDatePlusFuture = c.getTime();
        return dateFormat.format(currentDatePlusFuture);
	}

	public void enterDateMaterialNeeded(String date) {
		try {
			Browser.driver.findElement(By.id("dateNeededCal")).click();
			Browser.driver.findElement(By.id("dateNeededCal")).sendKeys(date);
			Logger.info(pageNamePrefixForLogger + "Entering date for materal needed by: " + date);
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "Unable to enter date for materal needed by!");
		}
	}

	public boolean isSubmitButtonPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("button.contact-info-continueBtn"))) {
				Logger.info(pageNamePrefixForLogger + "Submit button is present.");
				return true;
			}
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "Submit button is not present!");
		}
		return false;
	}

	public void selectSubmitButton() {
		WebElement element = Browser.getWebElement(By.cssSelector("button.contact-info-continueBtn"));
		Browser.click(element);
		Logger.info(pageNamePrefixForLogger + "Submit button selected.");
		Browser.waitForSomeTime(8000);
	}
}
