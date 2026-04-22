package com.mcnichols.tests;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.pages.Pages;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.tests.util.TestClassBase;

public class VerifySiteLinks extends TestClassBase {

	@Test(dependsOnMethods = { "startTest" })
	public void canGoToHomePage() throws Exception {
		verifyTrue(Pages.homePage().isAt());

		Browser.checkForBrokenLinks();
	}
	
	@Test(dependsOnMethods = { "canGoToHomePage" })
	public static void verifyPageByLinks() {
	     List<WebElement> linkElements = Browser.driver.findElements(By.tagName("a"));
	     String[] linkTexts = new String[linkElements.size()];
	     int i = 0;

	     //extract the link texts of each link element
	     for (WebElement e : linkElements) {
	    	 if (!StringUtil.isEmpty(e.getText()) && !e.getText().contains("tel:")) {
	    		 linkTexts[i] = e.getText();
		    	 i++;	 
	    	 }
	     }

	     // test each link
	    for (String t : linkTexts) {
	    	try {
	    		Browser.driver.findElement(By.linkText(t)).click();
	    	} catch (Exception e) {
				// Element is present but having issue with overlays.
				// Use of JavaScriptExecuter to send click directly on the element.
	    		JavascriptExecutor executor = (JavascriptExecutor) Browser.driver;
				executor.executeScript("arguments[0].click();", Browser.driver.findElement(By.linkText(t)));	
	    	}

	    	if (Browser.driver.getTitle().equals(Pages.homePage().getPageTitle())) {
	    		System.out.println("\"" + t + "\""
	    				+ " potential issue with link!");
    		} else {
    			System.out.println("\"" + t + "\""
						+ " is working.");			
    		}
	    	Browser.driver.navigate().back();			
	    }
	}
}
