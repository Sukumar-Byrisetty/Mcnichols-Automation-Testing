package com.mcnichols.framework.pages.plp;

import java.util.Properties;

import org.openqa.selenium.By;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.pages.ProductListingPage;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class PerforatedMetalProductListingPage extends ProductListingPage {

	public static final String NAME = "perforated-metals";
	public static final String PROPERTY_FILE = "/productlistingpage/perforated-metal-config.xml";
	static String url = "/perforated-metal";
	static String title = "Perforated Metal";
	static String pageHeading = "PERFORATED METAL";
	public static String pageNamePrefixForLogger = "Product Listing Page - Perforated Metal - ";
	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	@Override
	public boolean isAtByPageHeading() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			String heading = Browser.getWebElement(By.cssSelector("h1.category-title")).getText();
			if (pageHeading.contains(heading)) {
				Logger.info(pageNamePrefixForLogger + "Page title verified.");
				isAt = true;
			} else {
				Logger.info(pageNamePrefixForLogger + "Page title not verified.");
			}
			
			String expectedPageHeader = Browser.driver.findElement(By.cssSelector("h1.category-title")).getText();
			if (!isAt && StringUtil.isNotEmpty(expectedPageHeader) && pageHeading.contains(expectedPageHeader)) {
				Logger.info(pageNamePrefixForLogger + "Category title verified.");
				isAt = true;
			} else {
				Logger.info(pageNamePrefixForLogger + "Category title not verified.");
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
		return properties.getProperty("page.title");
	}

	@Override
	public boolean isTabPresent(String tabName) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
