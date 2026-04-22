package com.mcnichols.tests;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.tests.util.TestClassBase;

public class SitemapPage extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Go to the homepage.")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt(), "Is at Home Page");
	}

	@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Go To the Site map")
	@Parameters({ "sitemap" })
	public void canGoToSiteMap(String sitemap) {
		Browser.goToByURL(sitemap);
		verifyTrue(Pages.siteMapPage().isAt(), "Is at Site Map Page!");
		Logger.info("Go to the Sitemap Page");
	}

	@Test(dependsOnMethods = { "canGoToSiteMap" }, description = "Go To the About Us")
	public void verifyAboutUsLinks() {
		Pages.siteMapPage().verifyAboutUsPageUrl();
		Logger.info("Clicked About Us link from Sitemap");
	}

	@Test(dependsOnMethods = { "verifyAboutUsLinks" }, description = "Go To the Perforated Metals")
	public void verifyPerforatedMetalLinks() {
		Pages.siteMapPage().verifyPerforatedMetalPageUrl();
		Logger.info("Clicked Perforated Metal link from Sitemap");
	}

	@Test(dependsOnMethods = { "verifyPerforatedMetalLinks" }, description = "Go To the Contact Us")
	public void verifySupportLinks() {
		Pages.siteMapPage().verifySupportPageUrl();
		Logger.info("Clicked Contact Us link from Sitemap");
	}

	@Test(dependsOnMethods = { "verifySupportLinks" }, description = "Go To the Resources")
	public void verifyResourcesLinks() {
		Pages.siteMapPage().verifyResourcesPageUrl();
		Logger.info("Clicked Resource link from Sitemap");
	}
}
