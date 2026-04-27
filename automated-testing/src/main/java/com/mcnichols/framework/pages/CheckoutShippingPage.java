package com.mcnichols.framework.pages;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.config.LoginCredentials;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.Logger;
import com.mcnichols.framework.util.StringUtil;

public class CheckoutShippingPage {
	public static final String PROPERTY_FILE = "checkout-shipping-page-config.xml";

	public static String url = "/checkout/shipping.jsp";
	public static String title = "Shipping";
	public static String pageHeader = "SHIPPING";
	public static String pageNamePrefixForLogger = "Checkout - Shipping Page - ";

	Properties properties = TestingConfig.getProperties(PROPERTY_FILE);

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
			Logger.processTime(startTime, System.currentTimeMillis(), pageNamePrefixForLogger);
			Browser.getPageEnvironmentDetails();

			Widgets.isKlaviyoFlyoutFormPresent();
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "isAt failed by page title!");
		}
		return isAt;
	}

	public void goToByUrl() {
		String baseUrl = Browser.getBaseUrl(Browser.driver.getCurrentUrl());
		Browser.driver.get(baseUrl + url);
	}

	public void populateShippingAddressForm() {
		populateShippingAddressForm(false, null);
	}

	public void populateShippingAddressForm(LoginCredentials profile) {
		populateShippingAddressForm(false, profile);
	}

	public void populateShippingAddressForm(boolean overwriteFields) {
		populateShippingAddressForm(overwriteFields, null);
	}

	public void populateShippingAddressForm(boolean overwriteFields, LoginCredentials profile) {
		String firstNameValue = properties.getProperty("guest.first.name");
		String lastNameValue = properties.getProperty("guest.last.name");
		String address1Value = properties.getProperty("guest.address1");
		String cityValue = properties.getProperty("guest.city");
		String expectedState = properties.getProperty("guest.state");
		String postalCodeValue = properties.getProperty("guest.zip.code");
		String phoneValue = properties.getProperty("guest.phone");

		if (profile != null) {
			if (StringUtil.isNotEmpty(profile.getFistName())) {
				firstNameValue = profile.getFistName();
			}
			if (StringUtil.isNotEmpty(profile.getLastName())) {
				lastNameValue = profile.getLastName();
			}

			if (profile.getUserProfile() != null) {
				if (StringUtil.isNotEmpty(profile.getUserProfile().getFistName())) {
					firstNameValue = profile.getUserProfile().getFistName();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getLastName())) {
					lastNameValue = profile.getUserProfile().getLastName();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getAddress())) {
					address1Value = profile.getUserProfile().getAddress();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getCity())) {
					cityValue = profile.getUserProfile().getCity();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getState())) {
					expectedState = profile.getUserProfile().getState();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getZip())) {
					postalCodeValue = profile.getUserProfile().getZip();
				}
				if (StringUtil.isNotEmpty(profile.getUserProfile().getPhone1())) {
					phoneValue = profile.getUserProfile().getPhone1();
				}
			}
		}

		WebDriverWait wait = new WebDriverWait(Browser.driver, 30);

		Browser.scrollTo(300);
		Browser.waitForSomeTime();

		if (overwriteFields) {
			// Clear fields
			Browser.driver.findElement(By.id("firstName")).clear();
			Browser.driver.findElement(By.id("lastName")).clear();
			Browser.driver.findElement(By.id("address1")).clear();
			Browser.driver.findElement(By.id("city")).clear();
			Browser.driver.findElement(By.cssSelector("#state-button > .ui-selectmenu-text")).clear();
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).clear();
			Browser.driver.findElement(By.id("phoneNumber")).clear();

			// Populate
			Browser.driver.findElement(By.id("firstName")).sendKeys(firstNameValue);
			Browser.driver.findElement(By.id("lastName")).sendKeys(lastNameValue);
			Browser.driver.findElement(By.id("address1")).sendKeys(address1Value);
			Browser.driver.findElement(By.id("city")).sendKeys(cityValue);

			selectState(expectedState);

			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip")).clear();
			Browser.click(By.cssSelector("#postalCodeContainer .contact-info-zip"));
			Browser.driver.findElement(By.cssSelector("#postalCodeContainer .contact-info-zip"))
					.sendKeys(postalCodeValue);

			Browser.driver.findElement(By.id("phoneNumber")).clear();
			Browser.click(By.id("phoneNumber"));
			Browser.driver.findElement(By.id("phoneNumber")).sendKeys(phoneValue);
		} else {
			// Confirm we are on the shipping page before interacting with the form
			wait.until(ExpectedConditions.urlContains("shipping"));
			Browser.waitForSomeTime();

			// Fall back to Add New Address when no saved address can be selected.
			if (!isShippingFormVisible()) {
				boolean noAddressBookEntries = !hasSelectableAddressBookEntries();
				if (noAddressBookEntries) {
					Logger.info(pageNamePrefixForLogger
							+ "No selectable address-book entries found. Falling back to Add New Address.");
				}

				boolean selected = selectAddNewShippingAddressOption();
				if (!selected) {
					Logger.warning(pageNamePrefixForLogger + "Could not select 'Add new Shipping Address' option!");
				}
			}

			WebElement firstNameField = waitForAnyVisibleElement(wait, new By[] {
					By.id("firstNameAddNew"),
					By.id("firstName"),
					By.id("fname"),
					By.name("firstName"),
					By.name("fname"),
					By.name("fname"),
					By.cssSelector("input[placeholder*='First Name']")
			}, "first name");

			if (StringUtil.isEmpty(firstNameField.getAttribute("value"))) {
				firstNameField.clear();
				firstNameField.sendKeys(firstNameValue);
			}

			WebElement lastNameField = waitForAnyVisibleElement(wait, new By[] {
					By.id("lastNameAddNew"),
					By.id("lastName"),
					By.id("lname"),
					By.name("lastName"),
					By.name("lname"),
					By.cssSelector("input[placeholder*='Last Name']")
			}, "last name");

			if (StringUtil.isEmpty(lastNameField.getAttribute("value"))) {
				lastNameField.clear();
				lastNameField.sendKeys(lastNameValue);
			}

			WebElement address1Field = waitForAnyVisibleElement(wait, new By[] {
					By.id("address1AddNew"),
					By.id("address1"),
					By.name("address1AddNew"),
					By.name("address1"),
					By.cssSelector("input[placeholder*='Address']")
			}, "address line 1");

			if (StringUtil.isEmpty(address1Field.getAttribute("value"))) {
				address1Field.clear();
				address1Field.sendKeys(address1Value);
			}

			WebElement cityField = waitForAnyVisibleElement(wait, new By[] {
					By.id("cityAddNew"),
					By.id("city"),
					By.name("cityAddNew"),
					By.name("city"),
					By.cssSelector("input[placeholder*='City']")
			}, "city");

			if (StringUtil.isEmpty(cityField.getAttribute("value"))) {
				cityField.clear();
				cityField.sendKeys(cityValue);
			}

			selectState(expectedState);

			WebElement postalCodeField = waitForAnyVisibleElement(wait, new By[] {
					By.cssSelector("#postalCodeContainer #postalCode"),
					By.cssSelector("#postalCodeContainer .contact-info-zip"),
					By.id("postalCode"),
					By.id("postalCodeAddNew"),
					By.name("zip"),
					By.name("postalCode"),
					By.cssSelector("input[placeholder*='Zip']")
			}, "postal code");

			if (StringUtil.isEmpty(postalCodeField.getAttribute("value"))) {
				postalCodeField.clear();
				Browser.click(postalCodeField);
				postalCodeField.sendKeys(postalCodeValue);
			}

			WebElement phoneField = waitForAnyVisibleElement(wait, new By[] {
					By.id("phoneNumberAddNew"),
					By.id("phoneNumber"),
					By.id("phone"),
					By.name("phoneNumberAddNew"),
					By.name("phoneNumber"),
					By.name("phone"),
					By.cssSelector("input[type='tel']")
			}, "phone number");

			if (StringUtil.isEmpty(phoneField.getAttribute("value"))) {
				Browser.scrollTo(400);
				Browser.waitForSomeTime();

				phoneField.clear();
				Browser.click(phoneField);
				phoneField.sendKeys(phoneValue);
			}
		}

		submitAddNewShippingAddressIfPresent();

		Logger.info(pageNamePrefixForLogger + "Entered values into address information form.");
	}

	private void submitAddNewShippingAddressIfPresent() {
		try {
			By submitAddressButton = By.id("addNewShippingAddressButton");
			if (!Browser.isElementPresent(submitAddressButton)) {
				return;
			}

			WebElement button = Browser.getWebElement(submitAddressButton);
			if (!button.isDisplayed()) {
				return;
			}

			Browser.scrollToElememnt(button);
			Browser.click(button);
			Browser.waitForSomeTime(1000);
			Logger.info(pageNamePrefixForLogger + "Selected add new shipping address button.");

			Widgets widgets = new Widgets();
			if (widgets.isAddressVerificationDisplay()) {
				widgets.selectAddressVerificationContinue();
				Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to submit add new shipping address.");
		}
	}

	private boolean selectAddNewShippingAddressOption() {
		By addNewLabel = By.id("newShipTo");
		By addNewMethodInput = By.cssSelector(
				"#newShipTo > input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedDeliverymethod']");

		List<WebElement> addNewLabels = Browser.driver.findElements(addNewLabel);
		for (WebElement label : addNewLabels) {
			if (!label.isDisplayed()) {
				continue;
			}

			try {
				Browser.scrollToElememnt(label);
				Browser.click(label);
				Browser.waitForSomeTime(300);

				List<WebElement> methodInputs = Browser.driver.findElements(addNewMethodInput);
				if (!methodInputs.isEmpty()) {
					WebElement methodInput = methodInputs.get(0);
					Browser.click(methodInput);
					dispatchChangeEvent(methodInput);
				}

				Browser.waitForSomeTime(600);
				if (isShippingFormVisible()) {
					Logger.info(pageNamePrefixForLogger + "Selected 'Add new Shipping Address' option via #newShipTo.");
					return true;
				}
			} catch (Exception ignored) {
			}
		}

		By[] addNewInputCandidates = new By[] {
				By.cssSelector("#newShipTo input[type='radio']"),
				By.id("addNewShippingAddressRadio"),
				By.cssSelector(
						"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedDeliverymethod'][value*='new']"),
				By.xpath("//label[contains(normalize-space(.),'Add new Shipping Address')]//input[@type='radio']"),
				By.xpath(
						"//label[contains(normalize-space(.),'Add new Shipping Address')]/following-sibling::input[@type='radio']"),
				By.xpath(
						"//label[contains(normalize-space(.),'Add new Shipping Address')]/preceding-sibling::input[@type='radio']"),
				By.xpath(
						"//input[@type='radio' and (contains(@id,'new') or contains(@name,'new') or contains(@value,'new'))]")
		};

		for (By candidate : addNewInputCandidates) {
			List<WebElement> options = Browser.driver.findElements(candidate);
			for (WebElement option : options) {
				if (!option.isDisplayed()) {
					continue;
				}
				try {
					Browser.scrollToElememnt(option);
					Browser.click(option);
					dispatchChangeEvent(option);
					Browser.waitForSomeTime(500);
					if (option.isSelected() || isShippingFormVisible()) {
						Logger.info(pageNamePrefixForLogger + "Selected 'Add new Shipping Address' option.");
						return true;
					}
				} catch (Exception ignored) {
				}
			}
		}

		By[] addNewLabelCandidates = new By[] {
				By.id("newShipTo"),
				By.xpath("//label[contains(normalize-space(.),'Add new Shipping Address') and @for]"),
				By.xpath("//label[contains(normalize-space(.),'Add new Shipping Address')]")
		};

		for (By candidate : addNewLabelCandidates) {
			List<WebElement> labels = Browser.driver.findElements(candidate);
			for (WebElement label : labels) {
				if (!label.isDisplayed()) {
					continue;
				}
				try {
					Browser.scrollToElememnt(label);
					Browser.click(label);
					Browser.waitForSomeTime(500);
					List<WebElement> methodInputs = Browser.driver.findElements(addNewMethodInput);
					if (!methodInputs.isEmpty()) {
						dispatchChangeEvent(methodInputs.get(0));
					}
					if (isShippingFormVisible()) {
						Logger.info(pageNamePrefixForLogger
								+ "Selected 'Add new Shipping Address' option via label click.");
						return true;
					}
				} catch (Exception ignored) {
				}
			}
		}

		return false;
	}

	private void dispatchChangeEvent(WebElement element) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) Browser.driver;
			js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
		} catch (Exception ignored) {
		}
	}

	private boolean hasSelectableAddressBookEntries() {
		By[] selectors = new By[] {
				By.cssSelector("#addressBook option[value]:not([value=''])"),
				By.cssSelector("select[id*='address'] option[value]:not([value=''])"),
				By.cssSelector("input[type='radio'][name*='address'][value]:not([value='new'])"),
				By.cssSelector(".saved-address input[type='radio'], .address-book-item input[type='radio']")
		};

		for (By selector : selectors) {
			List<WebElement> entries = Browser.driver.findElements(selector);
			for (WebElement entry : entries) {
				if (entry.isDisplayed()) {
					return true;
				}
			}
		}

		return false;
	}

	private WebElement waitForAnyVisibleElement(WebDriverWait wait, By[] candidates, String fieldName) {
		WebElement element = wait.until(driver -> {
			for (By candidate : candidates) {
				List<WebElement> elements = driver.findElements(candidate);
				for (WebElement candidateElement : elements) {
					if (candidateElement.isDisplayed()) {
						return candidateElement;
					}
				}
			}
			return null;
		});

		if (element == null) {
			throw new RuntimeException(pageNamePrefixForLogger + "Unable to locate visible field: " + fieldName);
		}

		return element;
	}

	private boolean isShippingFormVisible() {
		List<WebElement> addAddressSections = Browser.driver.findElements(By.id("addAddressSection"));
		for (WebElement section : addAddressSections) {
			if (section.isDisplayed()) {
				return true;
			}
		}

		By[] firstNameCandidates = new By[] {
				By.id("firstNameAddNew"),
				By.id("firstName"),
				By.id("fname"),
				By.name("firstNameAddNew"),
				By.name("firstName"),
				By.name("fname"),
				By.cssSelector("input[placeholder*='First Name']")
		};

		for (By candidate : firstNameCandidates) {
			List<WebElement> firstNameElements = Browser.driver.findElements(candidate);
			for (WebElement element : firstNameElements) {
				if (element.isDisplayed()) {
					return true;
				}
			}
		}
		return false;
	}

	public void selectSpecialHandlingRequired(boolean isRequired) {
		try {
			String[] candidateSelectors = isRequired ? new String[] {
					"#isSpecialHandleTrue",
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.limitedAccess'][value='true']"
			}
					: new String[] {
							"#isSpecialHandleFalse",
							"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.limitedAccess'][value='false']",
							"#specialHandlingSection div:nth-of-type(2) div:nth-of-type(3) label"
					};

			if (clickFirstAvailableSelector(candidateSelectors)) {
				Logger.info(pageNamePrefixForLogger + "Selecting special handling.");
			} else {
				Logger.warning(pageNamePrefixForLogger + "Unable to locate special handling selector.");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select special handling!");
		}
	}

	public void selectDeliveryAppoinmentRequired(boolean isRequired) {
		try {
			String[] candidateSelectors = isRequired ? new String[] {
					"#preNotificationTrue",
					"input[name='preNotification'][value='true']",
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.preNotification'][value='true']",
					"input[name='deliveryAppointment'][value='true']",
					"label[for='preNotificationTrue']"
			}
					: new String[] {
							"#preNotificationFalse",
							"input[name='preNotification'][value='false']",
							"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.preNotification'][value='false']",
							"input[name='deliveryAppointment'][value='false']",
							"label[for='preNotificationFalse']",
							"#specialHandlingSection div:nth-of-type(4) div:nth-of-type(3) label"
					};

			if (clickFirstAvailableSelector(candidateSelectors)) {
				Logger.info(pageNamePrefixForLogger + "Selecting delivery appointment.");
			} else {
				Logger.warning(pageNamePrefixForLogger + "Unable to locate delivery appointment selector.");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select delivery appointment!");
		}
	}

	private void selectState(String expectedState) {
		try {
			if (selectStateFromNativeSelect(expectedState)) {
				return;
			}

			boolean didSelectState = false;

			By[] stateDropdownCandidates = new By[] {
					By.cssSelector("#addAddressSection #state-button > .ui-selectmenu-text"),
					By.cssSelector("#addAddressSection #state-button"),
					By.cssSelector("#addAddressSection span[id*='state'][id$='-button']"),
					By.cssSelector("#state-button > .ui-selectmenu-text"),
					By.cssSelector("#state-button")
			};

			for (By dropdown : stateDropdownCandidates) {
				List<WebElement> dropdowns = Browser.driver.findElements(dropdown);
				for (WebElement stateDropdown : dropdowns) {
					if (!stateDropdown.isDisplayed()) {
						continue;
					}

					Browser.scrollToElememnt(stateDropdown);
					Browser.click(stateDropdown);
					Browser.waitForSomeTime(500);

					By[] stateOptionCandidates = new By[] {
							By.cssSelector(
									"ul#state-menu li div, ul#state-menu li span, ul#state-menu li .ui-menu-item-wrapper"),
							By.cssSelector(
									"ul[id*='state'][id$='-menu'] li div, ul[id*='state'][id$='-menu'] li span, ul[id*='state'][id$='-menu'] li .ui-menu-item-wrapper"),
							By.cssSelector(
									"ul[id*='state'] li div, ul[id*='state'] li span, ul[id*='state'] li .ui-menu-item-wrapper"),
							By.cssSelector(
									"ul[role='listbox'] li div, ul[role='listbox'] li span, ul[role='listbox'] li")
					};

					for (By optionsLocator : stateOptionCandidates) {
						List<WebElement> stateElements = Browser.driver.findElements(optionsLocator);
						for (WebElement stateElement : stateElements) {
							if (!stateElement.isDisplayed()) {
								continue;
							}

							String state = stateElement.getText();
							if (stateMatches(state, expectedState)) {
								Browser.waitForSomeTime();
								Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + state);
								Browser.scrollToElememnt(stateElement);
								Browser.click(stateElement);
								didSelectState = true;
								break;
							}
						}

						if (didSelectState) {
							break;
						}
					}

					if (didSelectState) {
						break;
					}
				}

				if (didSelectState) {
					break;
				}
			}

			if (!didSelectState) {
				didSelectState = selectStateFromNativeSelect(expectedState);
			}

			if (!didSelectState) {
				Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select state from dropdown form!");
		}
	}

	private boolean selectStateFromNativeSelect(String expectedState) {
		try {
			By[] nativeSelectCandidates = new By[] {
					By.cssSelector("#addAddressSection select#state"),
					By.cssSelector("#addAddressSection select[name='state']"),
					By.cssSelector("#addAddressSection select[id*='state' i]"),
					By.cssSelector("#addAddressSection select[name*='state' i]"),
					By.cssSelector("select#state"),
					By.cssSelector("select[name='state']"),
					By.cssSelector("select[id*='state' i]"),
					By.cssSelector("select[name*='state' i]")
			};

			for (By selectLocator : nativeSelectCandidates) {
				List<WebElement> selects = Browser.driver.findElements(selectLocator);
				for (WebElement selectElement : selects) {
					Select stateDropdown = new Select(selectElement);
					for (WebElement option : stateDropdown.getOptions()) {
						String state = option.getText();
						if (stateMatches(state, expectedState)) {
							if (!selectStateOptionViaScript(selectElement, state)) {
								stateDropdown.selectByVisibleText(state);
								dispatchChangeEvent(selectElement);
							}
							Browser.waitForSomeTime(300);

							String selected = stateDropdown.getFirstSelectedOption().getText();
							if (stateMatches(selected, expectedState) || isStateButtonSelected(expectedState)) {
								Logger.info(pageNamePrefixForLogger + "Selecing state dropdown value: " + selected);
								return true;
							}
						}
					}
				}
			}
		} catch (Exception ignored) {
		}

		return false;
	}

	private boolean selectStateOptionViaScript(WebElement selectElement, String targetText) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) Browser.driver;
			Object result = js.executeScript(
					"var sel = arguments[0];"
							+ "var target = (arguments[1] || '').toLowerCase().trim();"
							+ "if (!sel || !sel.options) return false;"
							+ "var idx = -1;"
							+ "for (var i = 0; i < sel.options.length; i++) {"
							+ "  var txt = (sel.options[i].text || '').toLowerCase().trim();"
							+ "  if (txt === target || txt.indexOf(target) !== -1 || target.indexOf(txt) !== -1) { idx = i; break; }"
							+ "}"
							+ "if (idx < 0) return false;"
							+ "sel.selectedIndex = idx;"
							+ "sel.value = sel.options[idx].value;"
							+ "sel.dispatchEvent(new Event('input', { bubbles: true }));"
							+ "sel.dispatchEvent(new Event('change', { bubbles: true }));"
							+ "if (window.jQuery) {"
							+ "  try { window.jQuery(sel).trigger('change'); } catch (e) {}"
							+ "  try { if (window.jQuery.fn.selectmenu) { window.jQuery(sel).selectmenu('refresh'); } } catch (e) {}"
							+ "}"
							+ "return true;",
					selectElement,
					targetText);

			return result instanceof Boolean && (Boolean) result;
		} catch (Exception ignored) {
			return false;
		}
	}

	private boolean isStateButtonSelected(String expectedState) {
		try {
			By[] buttonTextCandidates = new By[] {
					By.cssSelector("#addAddressSection #state-button .ui-selectmenu-text"),
					By.cssSelector("#state-button .ui-selectmenu-text")
			};

			for (By candidate : buttonTextCandidates) {
				List<WebElement> buttons = Browser.driver.findElements(candidate);
				for (WebElement button : buttons) {
					if (!button.isDisplayed()) {
						continue;
					}

					String selectedText = button.getText();
					if (StringUtil.isNotEmpty(selectedText)
							&& !selectedText.trim().equalsIgnoreCase("State*")
							&& !selectedText.trim().equalsIgnoreCase("State")
							&& stateMatches(selectedText, expectedState)) {
						return true;
					}
				}
			}
		} catch (Exception ignored) {
		}

		return false;
	}

	private boolean stateMatches(String actualStateText, String expectedStateText) {
		if (StringUtil.isEmpty(actualStateText) || StringUtil.isEmpty(expectedStateText)) {
			return false;
		}

		String actual = actualStateText.trim();
		String expected = expectedStateText.trim();
		String expectedAbbreviation = expected.contains(" - ") ? expected.split(" - ")[0].trim() : expected;
		String expectedName = expected.contains(" - ") ? expected.split(" - ")[1].trim() : expected;

		return actual.contains(expected)
				|| actual.equalsIgnoreCase(expectedAbbreviation)
				|| actual.startsWith(expectedAbbreviation + " -")
				|| actual.toLowerCase().contains(expectedName.toLowerCase());
	}

	private boolean clickFirstAvailableSelector(String[] cssSelectors) {
		for (String selector : cssSelectors) {
			By locator = By.cssSelector(selector);
			List<WebElement> elements = Browser.driver.findElements(locator);
			for (WebElement element : elements) {
				if (!element.isDisplayed() || !element.isEnabled()) {
					continue;
				}

				Browser.scrollToElememnt(element);
				Browser.click(element);
				dispatchChangeEventForFormControl(element);
				Browser.waitForSomeTime();
				return true;
			}
		}
		return false;
	}

	private void dispatchChangeEventForFormControl(WebElement element) {
		try {
			String tagName = element.getTagName();
			if (StringUtil.isNotEmpty(tagName)
					&& ("input".equalsIgnoreCase(tagName) || "select".equalsIgnoreCase(tagName)
							|| "textarea".equalsIgnoreCase(tagName))) {
				dispatchChangeEvent(element);
				return;
			}

			List<WebElement> controls = element.findElements(By.cssSelector("input, select, textarea"));
			if (!controls.isEmpty()) {
				dispatchChangeEvent(controls.get(0));
			}
		} catch (Exception ignored) {
		}
	}

	public void selectInstructionsRequired(boolean isRequired) {
		try {
			String[] candidateSelectors = isRequired ? new String[] {
					"#addInstructionsTrue",
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.additionalInstructions'][value='true']"
			}
					: new String[] {
							"#addInstructionsFalse",
							"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.additionalInstructions'][value='false']",
							"#specialHandlingSection div:nth-of-type(5) div:nth-of-type(3) label"
					};

			if (clickFirstAvailableSelector(candidateSelectors)) {
				Logger.info(pageNamePrefixForLogger + "Selecting special instructions.");
			} else {
				Logger.warning(pageNamePrefixForLogger + "Unable to locate special instructions selector.");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select special instructions!");
		}
	}

	public void continueButton() {
		try {
			By[] continueCandidates = new By[] {
					By.id("continueButton"),
					By.id("bottomContinueButtonShippingDelivery"),
					By.cssSelector("#shippingMethodContent #continueButton"),
					By.cssSelector("#shippingMethodContent button[id*='continue']"),
					By.cssSelector("button[type='submit'][id*='continue']"),
					By.cssSelector("input[type='submit'][id*='continue']")
			};

			waitForAnyContinueButtonEnabled(continueCandidates, 12);

			boolean progressed = false;
			String startUrl = Browser.getCurrentURL();

			for (By candidate : continueCandidates) {
				List<WebElement> elements = Browser.driver.findElements(candidate);
				for (WebElement element : elements) {
					if (!element.isDisplayed() || !element.isEnabled()) {
						continue;
					}

					String urlBeforeClick = Browser.getCurrentURL();
					Browser.scrollToElememnt(element);
					Browser.waitForSomeTime();
					Browser.click(element);
					Browser.waitForTheLoadingOverlayToDisappear(pageNamePrefixForLogger);

					if (waitForShippingSubmissionOutcome(urlBeforeClick)) {
						progressed = true;
						break;
					}
				}
				if (progressed) {
					break;
				}
			}

			if (progressed) {
				Logger.info(pageNamePrefixForLogger + "Continue button selected.");
			} else {
				Logger.warning(pageNamePrefixForLogger
						+ "Continue button click did not trigger any submission outcome. Attempting shipping form submit fallback...");

				if (submitShippingFormFallback() && waitForShippingSubmissionOutcome(Browser.getCurrentURL())) {
					Logger.info(
							pageNamePrefixForLogger + "Shipping form submit fallback triggered a submission outcome.");
				} else {
					Logger.warning(pageNamePrefixForLogger
							+ "Unable to trigger submission outcome from shipping continue action. Start URL: "
							+ startUrl
							+ " Current URL: " + Browser.getCurrentURL());
				}
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select continue button!");
		}
	}

	private boolean waitForAnyContinueButtonEnabled(By[] continueCandidates, int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(Browser.driver, timeoutSeconds);
			wait.until(driver -> {
				for (By candidate : continueCandidates) {
					List<WebElement> elements = driver.findElements(candidate);
					for (WebElement element : elements) {
						if (element.isDisplayed() && element.isEnabled()) {
							return true;
						}
					}
				}
				return false;
			});
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	private boolean submitShippingFormFallback() {
		try {
			JavascriptExecutor js = (JavascriptExecutor) Browser.driver;
			Object submitted = js.executeScript(
					"var selectors = ["
							+ "'#continueButton',"
							+ "'#bottomContinueButtonShippingDelivery',"
							+ "'#shippingMethodContent button[id*=\"continue\"]',"
							+ "'#shippingMethodContent input[type=\"submit\"][id*=\"continue\"]'"
							+ "];"
							+ "for (var i = 0; i < selectors.length; i++) {"
							+ "  var btn = document.querySelector(selectors[i]);"
							+ "  if (!btn || btn.disabled || btn.offsetParent === null) continue;"
							+ "  btn.dispatchEvent(new MouseEvent('mousedown', { bubbles: true, cancelable: true, view: window }));"
							+ "  btn.dispatchEvent(new MouseEvent('mouseup', { bubbles: true, cancelable: true, view: window }));"
							+ "  btn.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));"
							+ "  if (btn.form && typeof btn.form.requestSubmit === 'function') { btn.form.requestSubmit(btn); }"
							+ "  return true;"
							+ "}"
							+ "return false;");

			return submitted instanceof Boolean && (Boolean) submitted;
		} catch (Exception ignored) {
			return false;
		}
	}

	private boolean waitForShippingSubmissionOutcome(String urlBeforeClick) {
		try {
			WebDriverWait wait = new WebDriverWait(Browser.driver, 12);
			wait.until(driver -> {
				String currentUrl = driver.getCurrentUrl().toLowerCase();
				String beforeUrl = StringUtil.isNotEmpty(urlBeforeClick) ? urlBeforeClick.toLowerCase() : "";

				if (currentUrl.contains(CheckoutPaymentPage.url.toLowerCase())) {
					return true;
				}

				if (currentUrl.contains(url.toLowerCase()) && !currentUrl.equals(beforeUrl)) {
					return true;
				}

				List<WebElement> modalElements = driver.findElements(By.cssSelector("#avsModalContainer"));
				for (WebElement modal : modalElements) {
					if (modal.isDisplayed()) {
						return true;
					}
				}

				List<WebElement> errorElements = driver.findElements(By.cssSelector("#error"));
				for (WebElement error : errorElements) {
					if (error.isDisplayed() && StringUtil.isNotEmpty(error.getText())) {
						return true;
					}
				}

				return false;
			});

			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	public void selectCommonCarrier() {
		try {
			waitForCarrierOptionsToRender();

			boolean selectedUps = selectUpsCarrierIfAvailable();

			String[] candidateSelectors = new String[] {
					"#commonCarrierCheckbox",
					"#commonCarrierCheckbox input[value='commonCarrier']",
					"#commonCarrierCheckbox input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']",
					"#selectCarrierSection input[value='commonCarrier']",
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']",
					"#selectCarrierSection label",
					"#shippingMethodContent input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']",
					"#shippingMethodContent label[for*='carrier' i]"
			};

			boolean selectedCommonCarrier = selectedUps || clickFirstAvailableSelector(candidateSelectors);

			if (selectedCommonCarrier) {
				if (selectedUps) {
					Logger.info(pageNamePrefixForLogger + "Selected UPS carrier option.");
				} else {
					Logger.info(pageNamePrefixForLogger + "Selected common carrier.");
				}

				By[] continueCandidates = new By[] {
						By.id("continueButton"),
						By.id("bottomContinueButtonShippingDelivery"),
						By.cssSelector("#shippingMethodContent #continueButton"),
						By.cssSelector("#shippingMethodContent button[id*='continue']"),
						By.cssSelector("button[type='submit'][id*='continue']"),
						By.cssSelector("input[type='submit'][id*='continue']")
				};

				if (!waitForAnyContinueButtonEnabled(continueCandidates, 10)) {
					Logger.warning(pageNamePrefixForLogger
							+ "Carrier was selected, but continue button is still disabled after waiting.");
				}
			} else {
				Logger.warning(pageNamePrefixForLogger + "Unable to locate common carrier selector.");
			}
		} catch (Exception e) {
			Logger.warning(pageNamePrefixForLogger + "Unable to select common carrier!");
		}
	}

	private boolean selectUpsCarrierIfAvailable() {
		By[] upsCarrierCandidates = new By[] {
				By.cssSelector("#commonCarrierCheckbox"),
				By.cssSelector("#commonCarrierCheckbox input[value='commonCarrier']"),
				By.cssSelector("#selectCarrierSection input[value='commonCarrier']"),
				By.cssSelector("#selectCarrierSection input[value*='UPS' i]"),
				By.cssSelector("#selectCarrierSection input[id*='ups' i]"),
				By.cssSelector(
						"#selectCarrierSection input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier'][value*='UPS' i]"),
				By.cssSelector(
						"#shippingMethodContent input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier'][value*='UPS' i]"),
				By.cssSelector("#shippingMethodContent input[id*='ups' i]"),
				By.xpath(
						"//div[@id='selectCarrierSection']//label[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'UPS')]"),
				By.xpath(
						"//label[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'UPS')]")
		};

		for (By candidate : upsCarrierCandidates) {
			List<WebElement> elements = Browser.driver.findElements(candidate);
			for (WebElement element : elements) {
				if (!element.isDisplayed() && !isHiddenCarrierInput(element)) {
					continue;
				}

				try {
					if (selectCarrierElement(element)) {
						return true;
					}
				} catch (Exception ignored) {
				}
			}
		}

		return false;
	}

	private boolean selectCarrierElement(WebElement element) {
		WebElement carrierInput = resolveCarrierInput(element);

		if (element.isDisplayed()) {
			Browser.scrollToElememnt(element);
			Browser.click(element);
		}

		if (carrierInput != null) {
			setCarrierInputSelected(carrierInput);
		}

		Browser.waitForSomeTime(400);
		return isCarrierSelectionApplied(element, carrierInput);
	}

	private WebElement resolveCarrierInput(WebElement element) {
		try {
			String tagName = element.getTagName();
			if (StringUtil.isNotEmpty(tagName) && "input".equalsIgnoreCase(tagName)) {
				return element;
			}

			List<WebElement> nestedInputs = element.findElements(By.cssSelector(
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']"));
			if (!nestedInputs.isEmpty()) {
				return nestedInputs.get(0);
			}

			String forAttribute = element.getAttribute("for");
			if (StringUtil.isNotEmpty(forAttribute)) {
				List<WebElement> mappedInputs = Browser.driver.findElements(By.id(forAttribute));
				if (!mappedInputs.isEmpty()) {
					return mappedInputs.get(0);
				}
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	private void setCarrierInputSelected(WebElement input) {
		try {
			JavascriptExecutor js = (JavascriptExecutor) Browser.driver;
			js.executeScript(
					"arguments[0].checked = true;"
							+ "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
							+ "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));"
							+ "arguments[0].dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }));"
							+ "if (typeof carrierSelectedForShipping === 'function') { carrierSelectedForShipping(); }",
					input);
		} catch (Exception ignored) {
		}
	}

	private boolean isCarrierSelectionApplied(WebElement element, WebElement carrierInput) {
		try {
			if (carrierInput != null && carrierInput.isSelected()) {
				return true;
			}
		} catch (Exception ignored) {
		}

		try {
			String className = element.getAttribute("class");
			if (StringUtil.isNotEmpty(className) && className.toLowerCase().contains("checked")) {
				return true;
			}
		} catch (Exception ignored) {
		}

		try {
			List<WebElement> selectedCarrierInputs = Browser.driver.findElements(By.cssSelector(
					"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']:checked"));
			return !selectedCarrierInputs.isEmpty();
		} catch (Exception ignored) {
			return false;
		}
	}

	private boolean isHiddenCarrierInput(WebElement element) {
		try {
			String tagName = element.getTagName();
			String type = element.getAttribute("type");
			String name = element.getAttribute("name");
			return "input".equalsIgnoreCase(tagName)
					&& StringUtil.isNotEmpty(type)
					&& "radio".equalsIgnoreCase(type)
					&& StringUtil.isNotEmpty(name)
					&& name.contains("DeliveryShippingFormHandler.editValue.selectedCarrier");
		} catch (Exception ignored) {
			return false;
		}
	}

	private void waitForCarrierOptionsToRender() {
		try {
			WebDriverWait wait = new WebDriverWait(Browser.driver, 20);
			wait.until(driver -> {
				List<WebElement> selectedCarrierInputs = driver.findElements(By.cssSelector(
						"input[name='/vsg/commerce/order/purchase/DeliveryShippingFormHandler.editValue.selectedCarrier']"));
				for (WebElement input : selectedCarrierInputs) {
					if (input.isDisplayed()) {
						return true;
					}
				}

				List<WebElement> upsLabels = driver.findElements(By.xpath(
						"//label[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'UPS')]"));
				for (WebElement label : upsLabels) {
					if (label.isDisplayed()) {
						return true;
					}
				}

				List<WebElement> commonCarrierLabels = driver.findElements(By.xpath(
						"//label[contains(translate(normalize-space(.), 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'), 'COMMON CARRIER')]"));
				for (WebElement label : commonCarrierLabels) {
					if (label.isDisplayed()) {
						return true;
					}
				}

				List<WebElement> calculatingMessages = driver
						.findElements(By.xpath("//*[contains(normalize-space(.), 'Calculating Shipping Options')]"));
				for (WebElement message : calculatingMessages) {
					if (message.isDisplayed()) {
						return false;
					}
				}

				return false;
			});
		} catch (Exception ignored) {
			Logger.warning(pageNamePrefixForLogger
					+ "Carrier options were not fully visible before selection attempt. Continuing with best-effort locators.");
		}
	}

	public boolean isErrorPresent() {
		boolean isErrorPresent = false;

		try {
			Browser.waitForSomeTime();
			if (Browser.isElementPresent(By.cssSelector("#error"))
					&& Browser.isElementVisible(By.cssSelector("#error"))) {
				String errorsText = Browser.driver.findElement(By.cssSelector("#error")).getText();
				if (StringUtil.isNotEmpty(errorsText)) {
					isErrorPresent = true;
					Logger.warning(
							"Checkout Shipping Page - Found the following form validation errors: " + errorsText);
				}
			}
			if (!isErrorPresent) {
				Logger.info(pageNamePrefixForLogger + "No form validatoin errors occured.");
			}
		} catch (Exception e) {
			// Since there is a delay for the form error, check if the element is stale
			// (StaleElementReferenceException) and allow test to move forward.
			Logger.info(pageNamePrefixForLogger + "isErrorPresent failed due to: " + e.getMessage());
		}

		return isErrorPresent;
	}
}
