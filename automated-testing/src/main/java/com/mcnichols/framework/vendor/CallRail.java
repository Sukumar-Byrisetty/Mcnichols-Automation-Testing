package com.mcnichols.framework.vendor;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.Logger;

public class CallRail {
	public static String pageNamePrefixForLogger = "CallRail - ";
	
	public static final String STATIC_PHONE_NUMBER_AREA_CODE = "813";
	public static final String STATIC_PHONE_NUMBER_PREFIX = "466";
	public static final String STATIC_PHONE_NUMBER_LINE = "6979";
	public static final String STATIC_PHONE_NUMBER = STATIC_PHONE_NUMBER_AREA_CODE 
			+ STATIC_PHONE_NUMBER_PREFIX + STATIC_PHONE_NUMBER_LINE;
	
	public boolean isSwapSuccessfulInHeader() {
		boolean isSuccesful = false;
		WebElement element = Browser.driver.findElement(By.cssSelector(".wp-phone-number"));
		String phoneNumber = element.getAttribute("textContent").replaceAll("[^0-9]", "");
		String phoneNumberlinkValue = element.getAttribute("href").replaceAll("[^0-9]", "");
		
		Logger.info("Swap - Header - Phone number on page: " + phoneNumber);
		Logger.info("Swap - Header - Phone number link: " + phoneNumberlinkValue);
		
		if (STATIC_PHONE_NUMBER.contains(phoneNumber) || STATIC_PHONE_NUMBER.contains(phoneNumberlinkValue)) {
			Logger.fail(pageNamePrefixForLogger + "Swap - Header - Failed!");
		} else {
			isSuccesful = true;
			Logger.pass(pageNamePrefixForLogger + "Swap - Header - Successful");
		}
		
		return isSuccesful;
	}
	
	public boolean isSwapSuccessfulInDynamicElement() {
		boolean isSuccesful = false;
		WebElement element = Browser.driver.findElement(By.cssSelector("#dynamic-phone-number-link"));
		String dynamicPhomeNumber = element.getAttribute("textContent").replaceAll("[^0-9]", "");
		String dynamicLinkValue = element.getAttribute("href").replaceAll("[^0-9]", "");
		
		if (dynamicPhomeNumber.isEmpty()) {
			List<WebElement> elements = Browser.driver.findElements(By.cssSelector("#dynamic-phone-number-link"));
			dynamicPhomeNumber = getPhoneNumerTextFromElement(elements).replaceAll("[^0-9]", "");
		}
		
		Logger.info("Swap - DynamicElement - Phone number on page: " + dynamicPhomeNumber);
		Logger.info("Swap - DynamicElement - Phone number link: " + dynamicLinkValue);
		
		if (STATIC_PHONE_NUMBER.contains(dynamicPhomeNumber) || STATIC_PHONE_NUMBER.contains(dynamicLinkValue)) {
			Logger.fail(pageNamePrefixForLogger + "Swap - DynamicElement - Failed!");
		} else {
			isSuccesful = true;
			Logger.pass(pageNamePrefixForLogger + "Swap - DynamicElement - Successful");
		}
		
		return isSuccesful;
	}
	
	public String getPhoneNumerTextFromElement(List<WebElement> elements) {
		String phoneNumber = "";
		for (WebElement element : elements) {
			if (!element.getText().isEmpty()) {
				phoneNumber = element.getText();
				break;
			}
		}
		return phoneNumber;
	}
}
