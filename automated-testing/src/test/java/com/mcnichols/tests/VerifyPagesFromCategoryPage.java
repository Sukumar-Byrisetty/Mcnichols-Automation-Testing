package com.mcnichols.tests;

import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.tests.util.TestClassBase;

public class VerifyPagesFromCategoryPage extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Loading homepage")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());
		Browser.waitForSomeTime();
	}

	@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Verify category page was loaded.")
	@Parameters({ "category.path" })
	public void canGoToCategoryPage(String categoryPath) throws Exception {
		Browser.goTo(categoryPath);

		verifyTrue(Pages.categoryPage().isAt());
		Browser.waitForSomeTime();
		
		Browser.scrollToElememnt(Browser.getWebElement(By.cssSelector(".tab-content")));
		Pages.categoryPage().isTilePresent(By.cssSelector(".tile-name .subcategory img[title=\"Stair Tread Plank (PG)\"]"));
		Pages.categoryPage().isProductsFirstItemVisible();
	}
	
	@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Verify product page is loading from category page.")
	@Parameters({ "category.path" })
	public void canGoToProductPageFromCategory(String categoryPath) throws Exception {
		Pages.categoryPage().isProductsFirstItemVisible();

		List<String> productLinks = Pages.categoryPage().getCategoryPageProductLinks();
		boolean doesProductPageHaveError = false;
		for (int i = 0; i < productLinks.size(); i++) {
			if (i == 5) {
				if (!doesProductPageHaveError) {
					Logger.info("Things look good!  Ending the product page testing early.");
				}
				break;
			}
			String url = productLinks.get(i);

			Logger.info("Verifying Product Page URL: " + url);
			Browser.goToByURL(url);
			if (!Pages.productPage().isAt()) {
				Logger.fail("Page failed to load for: " + url);
				TestUtil.takeScreenshot("ProductPage-" + i);
				doesProductPageHaveError = true;
			}

			Browser.getJavaScriptErrors();
		}

		verifyFalse(doesProductPageHaveError);
	}
}
