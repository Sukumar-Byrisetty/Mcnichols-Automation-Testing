package com.mcnichols.framework.pages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class HomePage {

	public static final String PROPERTY_FILE = "home-page-config.xml";
	public static final String PAGE_NAME = "homepage";
	
	static String url = "";
	static String title = "Specialty Metals and Fiberglass Products In Stock | McNICHOLS";  
	static String titleUpdate = "Perforated Metal, Wire Mesh, Expanded Metal & Gratings | McNICHOLS";
	static String titleWordPress = "Specialty Metals and Fiberglass Products | McNICHOLS";
	public static String pageNamePrefixForLogger = "Homepage - ";
	
	public void goTo() {
		WebElement element = Browser.waitForElementToBeClickable(By.cssSelector(TestingConfig.getProperties(HeaderPageInclude.PROPERTY_FILE).getProperty("header.1.logo")));
		element.click();
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public boolean isAt() {
		boolean isAt = false;
		try {
			long startTime = System.currentTimeMillis();
			Browser.waitForJavaScriptDependencies();
			Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

			if (Browser.title().contains(title) || Browser.title().contains(titleUpdate) || Browser.title().contains(titleWordPress)) {
				Logger.info(pageNamePrefixForLogger + "title verified.");
				isAt = true;
			}
			
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "isAt failed by page title!");
		}

		return isAt;
	}

	public String getPageTitle() {
		return title;
	}

	public boolean isHeroSectionPresent() {
		boolean hasHeroSection = false;
		if (Browser.isElementPresent(By.cssSelector(".hero-slider"))) {
			hasHeroSection = true;
		} else if (Browser.isElementPresent(By.cssSelector("#hero"))) {
			Logger.info(pageNamePrefixForLogger + "Hero section verified.");
			hasHeroSection = true;
		}
		
		// WordPress Page Check
		if (!hasHeroSection && isHomePage()) {
			if (Browser.isElementPresent(By.cssSelector(".elementor-element-20f2cb42"))) {
				Logger.info(pageNamePrefixForLogger + "WordPress Hero section verified.");
				hasHeroSection = true;	
			} else if(Browser.isElementPresent(By.cssSelector(".elementor-element-9d67132"))) {
				Logger.info(pageNamePrefixForLogger + "WordPress Hero section verified.");
				hasHeroSection = true;	
			}
		}
		
		if (!hasHeroSection ) {
			Logger.fail(pageNamePrefixForLogger + "Hero section is not present!");
		}
		return hasHeroSection;
	}
	
	public boolean isFeaturedSectionPresent() {
		boolean hasFeaturedSection = false;
		if (Browser.isElementPresent(By.cssSelector("section.section-featured"))) {
			Logger.info(pageNamePrefixForLogger + "Section featured with products verified.");
			hasFeaturedSection = true;
		}
		
		// WordPress Page Check
		if (!hasFeaturedSection && isHomePage()) {
			// Product lines text
			if (Browser.isElementPresent(By.cssSelector(".elementor-element-fa64af4"))) {
				Logger.info(pageNamePrefixForLogger + "WordPress featured product line text verified.");
				hasFeaturedSection = true;	
			}
			
			// Product tiles
			if (Browser.isElementPresent(By.cssSelector(".elementor-element-0b68fa5"))) {
				Logger.info(pageNamePrefixForLogger + "WordPress featured product tiles verified.");
				hasFeaturedSection = true;	
			}
		}
		
		if (!hasFeaturedSection) {
			Logger.warning(pageNamePrefixForLogger + "Section featured with product lines are not present!");	
		}
		
		return hasFeaturedSection;
	}

	public boolean isInformationSectionPresent() {
		boolean hasInformationSection = false;
		try {
			if (Browser.isElementPresent(By.cssSelector("section.homepage-info"))) {
				Logger.info(pageNamePrefixForLogger + "Information section verified.");
				hasInformationSection = true;
			}
			
			// WordPress Page Check
			if (!hasInformationSection && isHomePage()) {
				if (Browser.isElementPresent(By.cssSelector(".elementor-element-56abb46"))) {
					Logger.info(pageNamePrefixForLogger + "WordPress information section (Hole Product Catalogs, The Hole Story, Hole Product Ideas) verified.");
					hasInformationSection = true;	
				}
			}
			
			if (!hasInformationSection) {
				Logger.warning(pageNamePrefixForLogger + "Information featured section is not present!");	
			}
			
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Information section is not present!  Cannot location css selector: homepage-info");
		}
		return hasInformationSection;
	}
	
	public boolean isPromotionInformationSectionPresent() {
		boolean hasPromotionInformationSection = false;
		try {
			if (Browser.isElementPresent(By.cssSelector("section.homepage-promo-info"))) {
				Logger.info(pageNamePrefixForLogger + "Promotion section verified.");
				
				//WebElement element = Browser.waitForElementToBeVisibleByCssSelector("section.homepage-promo-info");
				//Browser.scrollToElememnt(element);
				hasPromotionInformationSection = true;
			}
			
			// WordPress Page Check
			if (!hasPromotionInformationSection && isHomePage()) {
				if (Browser.isElementPresent(By.cssSelector(".elementor-element-5153d76"))) {
					Logger.info(pageNamePrefixForLogger + "WordPress Promotion information section (Locations, Designer Metals, Serving Here) verified.");
					hasPromotionInformationSection = true;	
				}
			}
			
			if (!hasPromotionInformationSection) {
				Logger.warning(pageNamePrefixForLogger + "Promotion Information section is not present!");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Promotion Information section is not present!  Cannot location css selector: homepage-promo-info");
		}
		return hasPromotionInformationSection;
	}
	
	public boolean isArticleContentPresent() {
		boolean hasArticleContent = false;
		try {
			if (Browser.isElementPresent(By.cssSelector("section.home-article"))) {
				Logger.info(pageNamePrefixForLogger + "Article content verified.");
				
				//WebElement element = Browser.waitForElementToBeVisibleByCssSelector("section.homepage-promo-info");
				//Browser.scrollToElememnt(element);
				hasArticleContent = true;
			}
			
			// WordPress Page Check
			if (!hasArticleContent && isHomePage()) {
				if (Browser.isElementPresent(By.cssSelector(".elementor-element-0172fa4"))) {
					Logger.info(pageNamePrefixForLogger + "WordPress Article section (Scott's letter) verified.");
					hasArticleContent = true;	
				}
			}
			
			if (!hasArticleContent) {
				Logger.warning(pageNamePrefixForLogger + "Article content section is not present!");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Article content section is not present!  Cannot location css selector: .home-article");
		}
		return hasArticleContent;
	}

	public boolean isBestBrowserLabelPresent() {
		try {
			if (Browser.isElementPresent(By.cssSelector(".best-browser-label"))) {
				Logger.info(pageNamePrefixForLogger + "This website is best viewed label verified.");
				
				WebElement element = Browser.waitForElementToBeVisible(By.cssSelector("section.homepage-promo-info"));
				Browser.scrollToElememnt(element);
				return true;
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "This website is best viewed label is not present!  Cannot location css selector: .best-browser-label");
		}
		return false;
	}

	// ToDo: Move the following to a util class
	public void checkForBrokenLinks() {
		List<WebElement> links = Browser.driver.findElements(By.tagName("a"));
		System.out.println("Total links are "+links.size());
		
		Set<String> uniqueLinks = new HashSet<String>();

		for (int i = 0; i < links.size(); i++) {
			WebElement element = links.get(i);
			//By using "href" attribute, we could get the url of the requried link
			String url = element.getAttribute("href");
			if (!StringUtil.isEmpty(url) && !url.contains("tel:")) {
				uniqueLinks.add(url);
				//calling verifyLink() method here. Passing the parameter as url which we collected in the above link
				//See the detailed functionality of the verifyLink(url) method below
				Browser.verifyLink(url);
			}
		}

		for (String urlLink : uniqueLinks) {
			Browser.verifyLink(urlLink);
		}
	}
	
	public boolean isHomePage() {
		boolean isHomePage = false;
		String pageName = Browser.getPageNameFromMCNObject();
		
		if (StringUtil.isEmpty(pageName) || PAGE_NAME.equals(pageName)) {
			isHomePage = true;
		}
		
		return isHomePage;
	}
}
