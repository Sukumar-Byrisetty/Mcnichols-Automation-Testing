package com.mcnichols.framework.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutOrderConfirmationPage {
	public static final String PROPERTY_FILE = "checkout-order-confirmation-page-config.xml";

	public static String url = "/checkout/confirmation.jsp";
	public static String title = "Order Confirmation";
	public static String pageHeader = "ITEMS ORDERED";
	public static String pageNamePrefixForLogger = "Checkout - Order Confirmation Page - ";
	private static final String CONFIRMATION_ROOT = "#confirmation-page";

	public boolean isAt() {
		return isAt(false);
	}

	public boolean isAt(boolean allowAdditionalAttempt) {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			isAt = isAtOrderConfirmationPageVerified();

			if (!isAt && allowAdditionalAttempt) {
				Logger.warning("Time exceeded to verify page.  Trying again...");
				isAt = isAtOrderConfirmationPageVerified();
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	private boolean isAtOrderConfirmationPageVerified() {
		boolean isAt = false;
		Browser.waitForJavaScriptDependencies();
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

		// Check 1: URL contains confirmation.jsp
		String currentUrl = Browser.getCurrentURL();
		if (StringUtil.isNotEmpty(currentUrl) && currentUrl.contains(url)) {
			Logger.info(pageNamePrefixForLogger + "URL verified: " + currentUrl);
			isAt = true;
		}

		// Check 2: Title contains "Order Confirmation"
		if (!isAt) {
			String currentTitle = Browser.title();
			if (StringUtil.isNotEmpty(currentTitle) && currentTitle.toLowerCase().contains(title.toLowerCase())) {
				Logger.info(pageNamePrefixForLogger + "title verified: " + currentTitle);
				isAt = true;
			}
		}

		// Check 3: Header element with "ITEMS ORDERED" text is present
		if (!isAt) {
			try {
				if (Browser.isElementPresent(By.cssSelector(".confirm-title-view .confirmation-items-ordered-label"))) {
					String expectedPageHeader = Browser.driver
							.findElement(By.cssSelector(".confirm-title-view .confirmation-items-ordered-label"))
							.getText().toLowerCase();
					if (StringUtil.isNotEmpty(expectedPageHeader)
							&& expectedPageHeader.contains(pageHeader.toLowerCase())) {
						Logger.info(pageNamePrefixForLogger + "page header verified.");
						isAt = true;
					}
				}
			} catch (Exception e) {
				Logger.warning(pageNamePrefixForLogger + "Header element check failed: " + e.getMessage());
			}
		}

		// Check 4: Order confirmation container is present
		if (!isAt) {
			if (Browser.isElementPresent(By.cssSelector(CONFIRMATION_ROOT))) {
				Logger.info(pageNamePrefixForLogger + "confirmation page container verified.");
				isAt = true;
			}
		}

		if (!isAt) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed. URL: " + currentUrl);
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	private String getFirstVisibleTrimmedText(By selector) {
		try {
			Browser.waitForPresenceOfElement(selector, 5);
		} catch (Exception e) {
			return "";
		}

		List<WebElement> candidates = Browser.driver.findElements(selector);
		for (WebElement candidate : candidates) {
			if (!candidate.isDisplayed()) {
				continue;
			}

			String candidateText = candidate.getText();
			if (candidateText != null) {
				candidateText = candidateText.trim();
			}

			if (StringUtil.isNotEmpty(candidateText)
					&& !"Web Reference #".equalsIgnoreCase(candidateText)
					&& !"Web Reference".equalsIgnoreCase(candidateText)) {
				return candidateText;
			}
		}

		return "";
	}

	private String validateWebReference(String text) {
		text = text.trim();

		if (!text.matches("(?i)^[A-Z]?\\d{5,}$")) {
			throw new RuntimeException(pageNamePrefixForLogger + "Invalid Web Reference format: " + text);
		}

		return text;
	}

	private String extractWebReference(String text) {
		text = text.trim();

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([A-Z]?\\d{5,})");
		java.util.regex.Matcher matcher = pattern.matcher(text);
		String lastMatch = null;

		while (matcher.find()) {
			lastMatch = matcher.group(1);
		}

		if (lastMatch != null) {
			return lastMatch.trim();
		}

		throw new RuntimeException(pageNamePrefixForLogger + "Unable to extract Web Reference from: " + text);
	}

	public String getValidatedOrderNumber() {
		Browser.waitForJavaScriptDependencies();
		Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
		Browser.waitForElementToBeVisible(By.cssSelector(CONFIRMATION_ROOT));

		By valueSelector = By.cssSelector(CONFIRMATION_ROOT + " .confirm-order-number .font-weight-normal");
		By containerSelector = By.cssSelector(CONFIRMATION_ROOT + " .confirm-order-number");

		String value = getFirstVisibleTrimmedText(valueSelector);
		if (StringUtil.isNotEmpty(value)) {
			String validatedValue = validateWebReference(value);
			return logAndReturn(validatedValue);
		}

		String containerText = getFirstVisibleTrimmedText(containerSelector);
		if (StringUtil.isNotEmpty(containerText)) {
			String extracted = extractWebReference(containerText);
			String validatedValue = validateWebReference(extracted);
			return logAndReturn(validatedValue);
		}

		By[] fallbackSelectors = new By[] {
				By.cssSelector(CONFIRMATION_ROOT + " .confirm-order-id"),
				By.cssSelector(CONFIRMATION_ROOT + " [data-testid='web-reference']"),
				By.cssSelector(CONFIRMATION_ROOT + " [data-test='web-reference']"),
				By.cssSelector(CONFIRMATION_ROOT + " #webReference") };

		for (By selector : fallbackSelectors) {
			String candidate = getFirstVisibleTrimmedText(selector);
			if (StringUtil.isNotEmpty(candidate)) {
				String validatedValue = validateWebReference(extractWebReference(candidate));
				return logAndReturn(validatedValue);
			}
		}

		String currentUrl = Browser.getCurrentURL();
		String errorMessage = pageNamePrefixForLogger + "Web Reference # is missing or empty.";
		Logger.warning(errorMessage);
		if (StringUtil.isNotEmpty(currentUrl)) {
			Logger.warning(pageNamePrefixForLogger + "Current URL at failure: " + currentUrl);
		}
		throw new RuntimeException(errorMessage);
	}

	private String logAndReturn(String value) {
		Logger.info(pageNamePrefixForLogger + "Confirmed Web Reference #: " + value);
		return value;
	}

	public boolean isOrderNumberPresent() {
		String validatedOrderNumber = getValidatedOrderNumber();
		return StringUtil.isNotEmpty(validatedOrderNumber);
	}
}
