package com.mcnichols.tests.sandbox;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.tests.util.TestClassBase;

public class ProductPageTest extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" }, description = "Loading homepage")
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());
		Browser.waitForSomeTime();
	}

	@Test(dependsOnMethods = { "canGoToHomePage" }, description = "Verify product page and active tabs match tab body content.")
	@Parameters({ "product.path" })
	public void canGoToProductPage(String productPath) throws Exception {
		Browser.goTo(productPath);

		verifyTrue(Pages.productPage().isAt());
		Browser.waitForSomeTime();
		//Browser.scrollTo(900);
		WebElement pdpTabElement = Browser.driver.findElement(By.cssSelector(".pdptab"));
		Browser.scrollToElememnt(pdpTabElement);
		Browser.waitForSomeTime(4000);
		
		verifyTrue(Pages.productPage().isTabSpecificationsContentDisplayed());
		
		Pages.productPage().selectTabOverview();
		Browser.waitForSomeTime(1000);
		verifyTrue(Pages.productPage().isTabOverviewContentDisplayed());
		TestUtil.takeScreenshot("ProductPageTest-TabOverview");
		
		Pages.productPage().selectTabOrdering();
		Browser.waitForSomeTime(1000);
		verifyTrue(Pages.productPage().isTabOrderingContentDisplayed());
		TestUtil.takeScreenshot("ProductPageTest-TabOrdering");
		
		Pages.productPage().selectTabCharts();
		Browser.waitForSomeTime(1000);
		verifyTrue(Pages.productPage().isTabChartsContentDisplayed());
		TestUtil.takeScreenshot("ProductPageTest-TabCharts");
		
		Pages.productPage().selectTabTables();
		Browser.waitForSomeTime(1000);
		verifyTrue(Pages.productPage().isTabTablesContentDisplayed());
		TestUtil.takeScreenshot("ProductPageTest-TabTables");
		
		Pages.productPage().selectTabSpecifications();
		Browser.waitForSomeTime(1000);
		verifyTrue(Pages.productPage().isTabSpecificationsContentDisplayed());
		
		if (Pages.widgets().isBackToTopVisibile()) {
			Pages.widgets().selectBackToTopElement();
		}
	}

}
