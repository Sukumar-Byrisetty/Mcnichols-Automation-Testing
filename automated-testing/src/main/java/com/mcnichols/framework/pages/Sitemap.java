package com.mcnichols.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.TestUtil;

public class Sitemap {

	static String url = "";
	static String title = "Site Map";
	static String titleWordPress = "Sitemap";
	static String pageIDentifier = ".mkt-line-bottom";
	public static String pageNamePrefixForLogger = "Sitemap page - ";
	public String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
	public static Integer timeout = 4000;

	public void goTo() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			
			if (Browser.title().contains((title)) || Browser.title().contains(titleWordPress)) {
				Logger.info(pageNamePrefixForLogger + "title verified.");
				isAt = true;
			}

			if (!isAt && Browser.isElementPresent(By.cssSelector(pageIDentifier))) {
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "is At failed by page title!");
		}
		return isAt;
	}

	public String getPageTitle() {
		return title;
	}

	public void verifyAboutUsPageUrl() {

		Browser.waitForSomeTime(timeout);
		Browser.driver.findElement(By.linkText("About Us")).sendKeys(selectLinkOpeninNewTab);
		Browser.waitForSomeTime(timeout);
		
		mSwitchToCurrentTab("about-us");
		
		if (Browser.driver.getTitle().contains("Your Specialty Metals Supplier - About Us")) {
			Logger.pass(pageNamePrefixForLogger + "From Site map - About Us page Url verified!");
			Browser.waitForSomeTime(5000);
			closeCurrentTab();
		} else {
			Logger.fail(pageNamePrefixForLogger + "From Site map - Failed to verify About Us page Url");
		}
	}

	public void verifyPerforatedMetalPageUrl() {
		pageUrlVerification("Perforated Metal");
		
		Browser.waitForSomeTime(timeout);
		
		mSwitchToCurrentTab("perforated-metal");
		
		if (Browser.driver.getTitle().contains("Perforated Metal")) {
			Logger.pass(pageNamePrefixForLogger + "From Site map - Perforated Metal Url verified!");
			Browser.waitForSomeTime(timeout);
			closeCurrentTab();
		} else {
			Logger.fail(pageNamePrefixForLogger + "From Site map - Failed to verify Perforated Metal page Url");
		}
	}

	public void verifySupportPageUrl() {
		pageUrlVerification("SUPPORT");
		Browser.waitForSomeTime(timeout);
		mSwitchToCurrentTab("contact-us");
		
		if (Browser.driver.getTitle().contains("Contact Us - Customer Support")) {
			Logger.pass(pageNamePrefixForLogger + "From Site map - Contact Us page Url verified!");
			Browser.waitForSomeTime(timeout);
			closeCurrentTab();

		} else {
			Logger.fail(pageNamePrefixForLogger + "From Site map - Failed to verify Contact Us page Url");
			Browser.waitForSomeTime();

		}
	}

	public void verifyResourcesPageUrl() {
		pageUrlVerification("RESOURCES");
		Browser.waitForSomeTime(timeout);
		mSwitchToCurrentTab("resources");
		
		if (Browser.driver.getTitle().contains("Case Studies, Glossary")) {
			Logger.pass(pageNamePrefixForLogger + "From Sitemap - Resources page Url verified!");
			Browser.waitForSomeTime(timeout);
			closeCurrentTab();

		} else {
			Logger.fail(pageNamePrefixForLogger + "From Sitemap - Failed to verify Resources page Url");
		}
	}

	public void pageUrlVerification(String linkName) {
		Browser.driver.findElement(By.linkText(linkName)).sendKeys(selectLinkOpeninNewTab);
	}

	public void mSwitchToCurrentTab(String tabName) {
		String originalHandle = Browser.driver.getWindowHandle();
		for (String handle : Browser.driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				Browser.driver.switchTo().window(handle);
				Browser.waitForJavaScriptDependencies();
				Browser.waitForTheLoadingOverlayToDisappear("");
			}
		}
		TestUtil.takeScreenshot(tabName);
	}

	public void closeCurrentTab() {
		String originalHandle = Browser.driver.getWindowHandle();		
		
		for (String handle : Browser.driver.getWindowHandles()) {
			if (!handle.equals(originalHandle)) {
				Browser.driver.switchTo().window(handle);
				break;
			}
		}
	}

	public boolean isArticleContentPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector(pageIDentifier))) {
				Logger.info(pageNamePrefixForLogger + "Article content verified.");
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger
					+ "Article content section is not present!  Cannot location css selector: .home-article");
		}
		return false;
	}

}
