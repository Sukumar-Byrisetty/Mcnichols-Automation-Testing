package com.mcnichols.framework.pages;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CartPage {
	private static final By[] EXPRESS_CHECKOUT_SELECTORS = new By[] {
			By.cssSelector("button.cart-express-checkout-btn"),
			By.cssSelector("a.cart-express-checkout-btn"),
			By.cssSelector(".cart-express-checkout-btn") };

	public static final String PROPERTY_FILE = "cart-page-config.xml";

	public static String url = "/checkout/cart.jsp";
	public static String title = "Shopping Cart";
	public static String pageHeader = "SHOPPING CART";
	public static String pageNamePrefixForLogger = "Cart Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains((title))) {
				Logger.info(pageNamePrefixForLogger + "title verified.");
				isAt = true;
			}

			if (!isAt) {
				String expectedPageHeader = Browser.driver.findElement(By.cssSelector(".cart-title")).getText();
				if (!isAt && StringUtil.isNotEmpty(expectedPageHeader) && expectedPageHeader.contains(pageHeader)) {
					Logger.info(pageNamePrefixForLogger + "page header verified.");
					isAt = true;
				}
			}

			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isContinueShoppingButtonPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("button.shopping-cont-btn"))) {
				Logger.info(pageNamePrefixForLogger + "Request Quote button verified.");
				return true;
			}

		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Request Quote button is not present!");
		}
		return false;
	}

	public boolean isRequestQuoteButtonPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("button.request-quote-btn"))) {
				Logger.info(pageNamePrefixForLogger + "Request Quote button verified.");
				return true;
			}

		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Request Quote button is not present!");
		}
		return false;
	}

	public boolean isCheckoutButtonPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector("button.cart-checkout-btn.xs-cart-checkout-visible"))) {
				Logger.info(pageNamePrefixForLogger + "Checkout button verified.");
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Checkout button is not present!");
		}
		return false;
	}

	public void continueShopping() {
		try {
			WebElement element = Browser.getWebElement(By.cssSelector("button.shopping-cont-btn"));
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Selected Continue Shopping button.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Selecting Request Quote button failed!");
			e.printStackTrace();
		}
	}

	public void requestQuote() {
		try {
			WebElement element = Browser.getWebElement(By.cssSelector("button.request-quote-btn"));
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Selected Request Quote button.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Selecting Request Quote button failed!");
			e.printStackTrace();
		}
	}

	public void checkout() {
		try {
			WebElement element = Browser
					.getWebElement(By.cssSelector("button.cart-checkout-btn.xs-cart-checkout-visible"));
			Browser.click(element);
			Logger.info(pageNamePrefixForLogger + "Selected Checkout button.");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger
					+ "Issue clicking Checkout button, attempting to use JavaScriptExecuter to send click to element.");
			e.printStackTrace();
		}
	}

	public boolean isExpressCheckoutAvailable() {
		try {
			for (By selector : EXPRESS_CHECKOUT_SELECTORS) {
				if (Browser.isElementPresent(selector)) {
					List<WebElement> expressButtons = Browser.driver.findElements(selector);
					for (WebElement expressButton : expressButtons) {
						if (expressButton.isDisplayed() && expressButton.isEnabled()) {
							Logger.info(pageNamePrefixForLogger + "Express Checkout button is present and clickable.");
							return true;
						}
					}
				}
			}

		} catch (Exception e) {
			Logger.info(pageNamePrefixForLogger + "Express Checkout button is not available for click.");
		}
		return false;
	}

	public void expressCheckout() {
		WebElement expressCheckoutElement = null;

		for (By selector : EXPRESS_CHECKOUT_SELECTORS) {
			if (Browser.isElementPresent(selector)) {
				List<WebElement> expressButtons = Browser.driver.findElements(selector);
				for (WebElement expressButton : expressButtons) {
					if (expressButton.isDisplayed() && expressButton.isEnabled()) {
						expressCheckoutElement = expressButton;
						break;
					}
				}
			}

			if (expressCheckoutElement != null) {
				break;
			}
		}

		if (expressCheckoutElement == null) {
			throw new RuntimeException(pageNamePrefixForLogger + "Express Checkout button is not visible/clickable.");
		}

		String startUrl = Browser.getCurrentURL();
		Browser.scrollToElememnt(expressCheckoutElement);
		Browser.waitForSomeTime();
		Browser.click(expressCheckoutElement);
		Browser.waitForSomeTime(4000);

		String currentUrl = Browser.getCurrentURL();
		Logger.info(
				pageNamePrefixForLogger + "Express Checkout start URL: " + startUrl + " current URL: " + currentUrl);

		if (currentUrl.contains("/checkout/review.jsp")) {
			throw new RuntimeException(
					pageNamePrefixForLogger + "Express Checkout click routed to standard review page.");
		}
	}

	public int getTotalNumberOfCartedItems() {
		int countOfItems = 0;
		String cssSelector = "#minicart .cart .count";

		if (Browser.isElementPresent(By.cssSelector(cssSelector))) {
			WebElement element = Browser.getWebElement(By.cssSelector(cssSelector));

			if (StringUtil.isNotEmpty(element.getText())) {
				countOfItems = Integer.valueOf(element.getText());
				Logger.info(pageNamePrefixForLogger + "Carted items: " + countOfItems);
			} else {
				countOfItems = -1;
			}
		} else {
			countOfItems = Browser.getCartedItemsCount();
		}

		return countOfItems;
	}

	public boolean doesItemQuantitiesExceedLimitPerItem() {
		boolean doesItemQuantitiesExceedLimitPerItem = false;

		List<WebElement> cartQtyValueElements = Browser.driver
				.findElements(By.cssSelector(".cart-QTY-value .visible-print.fake-print-qty"));
		for (WebElement cartQtyValueElement : cartQtyValueElements) {

			String cartQtyValue = cartQtyValueElement.getText();
			if (StringUtil.isNotEmpty(cartQtyValue) && Integer.valueOf(cartQtyValue) > 5) {
				Logger.info(pageNamePrefixForLogger + "An item exceed the quantities limit per item.  Quantity value: "
						+ cartQtyValue);
				doesItemQuantitiesExceedLimitPerItem = true;
				break;
			}
		}

		if (!doesItemQuantitiesExceedLimitPerItem) {
			int cartedItems = getTotalNumberOfCartedItems();
			if (cartedItems > 5) {
				Logger.info(pageNamePrefixForLogger + "An item exceed the quantities limit per item.  Quantity value: "
						+ cartedItems);
				doesItemQuantitiesExceedLimitPerItem = true;
			}
		}

		return doesItemQuantitiesExceedLimitPerItem;
	}

	public void removeCartedItems() {

		int numberOfCartedItems = getTotalNumberOfCartedItems();
		if (numberOfCartedItems >= 1) {
			for (int i = 0; i < numberOfCartedItems; i++) {
				List<WebElement> cartQtyValueElements = Browser.driver
						.findElements(By.cssSelector(".cart-delete-icon"));
				for (WebElement cartQtyValueElement : cartQtyValueElements) {
					Browser.click(cartQtyValueElement);
					Browser.waitForSomeTime();
					break;
				}
				goToByUrl();
				isAt();
			}

			Logger.info(pageNamePrefixForLogger + "Items were removed from cart.");
		} else {
			Logger.info(pageNamePrefixForLogger + "No items in cart to remove.");
		}
	}

	// ToDo:
	// Add function to determine how many items carted
	// Add function to remove item(s)
	// Add function to adjust item quantity

}
