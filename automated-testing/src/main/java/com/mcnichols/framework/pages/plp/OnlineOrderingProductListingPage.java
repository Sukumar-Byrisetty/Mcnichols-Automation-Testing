package com.mcnichols.framework.pages.plp;

import org.openqa.selenium.By;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.ProductListingPage;
import com.mcnichols.framework.util.Logger;

public class OnlineOrderingProductListingPage extends ProductListingPage {

	public static final String NAME = "online-order-available";
	public static final String PROPERTY_FILE = "/productlistingpage/online-ordering-config.xml";
	static String url = "/online-order-available+yes";
	static String title = "McNICHOLS";
	static String pageHeading = "ONLINE ORDERING";
	static String pageHeadingUpdate = "BUY NOW - ALL PRODUCTS";

	public static String pageNamePrefixForLogger = "Product Listing Page - Online Ordering - ";

	@Override
	public boolean isAtByPageHeading() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			String heading = Browser.getWebElement(By.cssSelector("h1.category-title")).getText().toLowerCase();
			if (pageHeading.toLowerCase().contains(heading) || pageHeadingUpdate.toLowerCase().contains(heading)) {
				Logger.info(pageNamePrefixForLogger + "Page heading verified.");
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt faild by page heading!.");
		}
		return isAt;
	}

	@Override
	public void goTo() {
		goToByUrl();
	}

	@Override
	public void goToByUrl() {
		Browser.goToByURL(url);
	}

	@Override
	public String pageName() {
		// return "Perforated Metal | McNICHOLS�";
		return "Perforated Metal | McNICHOLS";

	}

	@Override
	public boolean isTabPresent(String tabName) {
		// TODO Auto-generated method stub
		return false;
	}

}
