package com.mcnichols.framework.pages.account;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.config.UserCreditCard;
import com.mcnichols.framework.config.UserProfile;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CreditCardsPage {
	public static final String PROPERTY_FILE = "account-credit-card-page-config.xml";

	static String url = "/account/credit-cards.jsp";
	static String title = "Payment Methods";
	static String pageIDentifier="#credit-cards-list-container";
	public static String pageNamePrefixForLogger = "My Account - Credit Cards - ";
	public Select mDropDown;
	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

	public void goTo() {
		try {
			Browser.waitForSomeTime(3000);
			Browser.driver.findElement(By.linkText("CREDIT CARDS")).click();
			Logger.info(pageNamePrefixForLogger + "Click CreditCard menu");
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Creditcard button failed!");
			e.printStackTrace();
		}
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
		Browser.waitForSomeTime();
	}

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

			if (!isAt && Browser.isElementPresent(By.cssSelector(pageIDentifier))) {
				isAt = true;
			}
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "isAt failed by page title!");
		}

		return isAt;
	}
	
	public void onClickEvent(String className) {
		if (getListOfCards() > 2) {
			deleteCC();
		} else {
			Browser.waitForElementToBeClickable(By.cssSelector(className));
			Browser.click(By.cssSelector(className));
		}
	}
	
	public void clickAddNewCreditCardButton() {
		onClickEvent("button.add-new-card");
	}
	
	public void deleteCC() {
		List<WebElement> creditcardList = Browser.driver.findElements(By.cssSelector("button.delete-menu"));
		
		if (creditcardList.size() > 2) {
			creditcardList.get(creditcardList.size() - 1).click();
			Browser.waitForSomeTime();
			By submitButton = By.cssSelector("#commonModalContainer .delete-button");
			Browser.waitForElementToBeClickable(submitButton, 45);
			try {
				Browser.click(submitButton);
				Browser.waitForSomeTime();
				Logger.info(pageNamePrefixForLogger + "Delete credit card if exceed more than 2");
			} catch (Exception e) {
				Logger.warning(pageNamePrefixForLogger + "Issue in delete Credit Card!!!");
			}
		}

		Browser.waitForSomeTime(6000);
		onClickEvent("button.add-new-card");
		Browser.waitForSomeTime();
	}

	public Integer getListOfCards() {
		List<WebElement> creditcardList = Browser.driver.findElements(By.cssSelector("button.delete-menu"));
		return creditcardList.size();
	}
	
	public boolean isCreditCardPopupDisplayedWithCyberSourceContent() {
		boolean isCyberSourceContentVisible = true;
		Browser.waitForSomeTime();
		
		WebElement transactionUuid = Browser.driver.findElement(By.cssSelector("#payment_form > input[type=hidden]:nth-child(3)")); 
		Logger.info(pageNamePrefixForLogger + "CyberSource transaction_uuid: " + transactionUuid.getAttribute("value"));

		WebElement signedDateTime = Browser.driver.findElement(By.cssSelector("#payment_form > input[type=hidden]:nth-child(6)"));
		Logger.info(pageNamePrefixForLogger + "CyberSource signed_date_time: " + signedDateTime.getAttribute("value"));

		WebElement signature = Browser.driver.findElement(By.cssSelector("#payment_form > input[type=hidden]:nth-child(30)"));
		Logger.info(pageNamePrefixForLogger + "CyberSource signature: " + signature.getAttribute("value"));
		
		WebDriverWait wait = new WebDriverWait(Browser.driver, 200);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("cybersource-iframe"));
		
		boolean isBillingDetailsVisible = Browser.isElementPresent(By.cssSelector("#billing_details"));
		Logger.info(pageNamePrefixForLogger + "CyberSource billing details present: " + isBillingDetailsVisible);
		if (!isBillingDetailsVisible) {
			isCyberSourceContentVisible = isBillingDetailsVisible;
		}
		
		boolean isPaymentDetailsVisible = Browser.isElementPresent(By.cssSelector("#payment_details"));;
		Logger.info(pageNamePrefixForLogger + "CyberSource payment details present: " + isPaymentDetailsVisible);
		if (!isPaymentDetailsVisible) {
			isCyberSourceContentVisible = isPaymentDetailsVisible;
		}
		
		if (isCyberSourceContentVisible) {
			Logger.pass(pageNamePrefixForLogger + "CyberSource content is preent");	
		} else {
			String errorPageContent = Browser.driver.findElement(By.cssSelector("#content")).getText();
			Logger.info(pageNamePrefixForLogger + "CyberSource page content: " + errorPageContent);

			Logger.fail(pageNamePrefixForLogger + "CyberSource content is not preent!");
		}
		
		Browser.driver.switchTo().defaultContent();
				
		return isCyberSourceContentVisible;
	}

	public void overrideCCdetail(boolean overrideStatus) {
		Browser.waitForSomeTime();
		Browser.scrollTo(400);
		
		if (overrideStatus) {
			Browser.waitForTheLoadingOverlayToDisappear("Credit Card Page");
			
			WebDriverWait wait = new WebDriverWait(Browser.driver, 200);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("cybersource-iframe"));
			
			Browser.scrollTo(200);
			
			// Clear the form data
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_forename"))).clear();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_surname"))).clear();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_line1"))).clear();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_city"))).clear();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_postal_code"))).clear();

			// Now find the element
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_forename"))).sendKeys(properties.getProperty("billing.first.name"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_surname"))).sendKeys(properties.getProperty("billing.last.name"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_line1"))).sendKeys(properties.getProperty("billing.address1"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_city"))).sendKeys(properties.getProperty("billing.city"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_postal_code"))).sendKeys(properties.getProperty("billing.zip.code"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_number"))).sendKeys(properties.getProperty("billing.creditcard.number"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_cvn"))).sendKeys(properties.getProperty("billing.cvn"));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_type_001"))).click();

			mDropDown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("bill_to_address_country"))));
			mDropDown.selectByValue("US");

			mDropDown = new Select(Browser.driver.findElement(By.name("bill_to_address_state_us_ca")));
			mDropDown.selectByValue("FL");

			mDropDown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("card_expiry_month"))));
			mDropDown.selectByValue(properties.getProperty("billing.card_expiry_month"));

			mDropDown = new Select(Browser.driver.findElement(By.name("card_expiry_year")));
			mDropDown.selectByValue(properties.getProperty("billing.card_expiry_year"));
			
			Browser.waitForSomeTime();
			onClickEvent("#payment_details .pay_button");
			Browser.waitForSomeTime(8000);
			
			try {
				// ToDo: Chrome version 100 an exception started where webdriver target frame detached.  Research real fix for this... 
				Browser.driver.switchTo().defaultContent();	
			} catch (Exception e) {
				Logger.warning(e.getMessage());
				e.printStackTrace();
			}		
		}
	}
	
	public void enterBillingPaymentDetails(int accountType) {
		UserProfile userProfile = TestingConfig.getLogin(accountType).getUserProfile();
		UserCreditCard userCreditCard = TestingConfig.getLogin(accountType).getUserCreditCard();
		
		Browser.waitForSomeTime();
		Browser.scrollTo(400);
		
		Browser.waitForTheLoadingOverlayToDisappear("Credit Card Page");
		
		WebDriverWait wait = new WebDriverWait(Browser.driver, 200);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("cybersource-iframe"));
		
		Browser.scrollTo(200);
		
		// Clear billing information
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_forename"))).clear();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_surname"))).clear();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_line1"))).clear();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_city"))).clear();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_postal_code"))).clear();

		// Enter billing and payment details
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_forename"))).sendKeys(userProfile.getFistName());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_surname"))).sendKeys(userProfile.getLastName());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_line1"))).sendKeys(userProfile.getAddress());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_city"))).sendKeys(userProfile.getCity());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bill_to_address_postal_code"))).sendKeys(userProfile.getZip());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_number"))).sendKeys(userCreditCard.getNumber());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_cvn"))).sendKeys(userCreditCard.getCvn());
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_type_001"))).click();

		mDropDown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("bill_to_address_country"))));
		mDropDown.selectByValue("US");

		mDropDown = new Select(Browser.driver.findElement(By.name("bill_to_address_state_us_ca")));
		mDropDown.selectByValue(userProfile.getState());

		mDropDown = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("card_expiry_month"))));
		mDropDown.selectByValue(userCreditCard.getExpirationMonth());

		mDropDown = new Select(Browser.driver.findElement(By.name("card_expiry_year")));
		mDropDown.selectByValue(userCreditCard.getExpirationYear());
		
		Logger.info(pageNamePrefixForLogger + "Entered billing information and payment details.");
		
		Browser.waitForSomeTime();
	}
	
	public void clickModalCyberSourceContinueButton() {
		onClickEvent("#payment_details .pay_button");
		Logger.info(pageNamePrefixForLogger + "Clicked the CyberSource modal continue button.");
		Browser.waitForSomeTime(8000);
		
		try {
			// ToDo: Chrome version 100 an exception started where webdriver target frame detached.  Research real fix for this... 
			Browser.driver.switchTo().defaultContent();	
		} catch (Exception e) {
			Logger.warning(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void selectDefaultCreditCard(int position) {
		Logger.info(pageNamePrefixForLogger + "Setting the default credit card at postion: " + position);
		if (position == 1) {
			Browser.click(By.cssSelector("#collapse1 > div.menu-total-width > div:nth-child(1)"));	
		}
	}
	
	public boolean isCreditCardUpadteSuccessful() {
		boolean isSuccess = false;
		try {
			Browser.waitForSomeTime(3000);
			By creditCardSuccessSelector = By.cssSelector(".credit-cart-page #success.alert-success");
			
			if (Browser.isElementPresent(creditCardSuccessSelector)) {
				String message = Browser.driver.findElement(creditCardSuccessSelector).getText();
				String expectedMessage = "Credit card has been successfully created.";
				
				if (StringUtil.isNotEmpty(message) && message.toLowerCase().contains(expectedMessage.toLowerCase())) {
					isSuccess = true;
					Logger.pass(pageNamePrefixForLogger + "Credit Card update success!");
				}
			} else {
				Logger.fail(pageNamePrefixForLogger + "Credit Card update failed!");
			}
		} catch (Exception e) {
			Logger.fail(pageNamePrefixForLogger + "Credit Card update failed with exception: " + e.getMessage());
		}
		return isSuccess;
	}
}
