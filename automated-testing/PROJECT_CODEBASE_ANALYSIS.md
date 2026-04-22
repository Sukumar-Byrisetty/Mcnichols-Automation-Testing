# Project Overview

- This repository is a Java-based UI test automation project that executes browser-driven tests against McNICHOLS web endpoints. Evidence:
  - Selenium WebDriver dependency in `pom.xml`.
  - TestNG dependency and TestNG suite XML files in `src/test/resources/*`.
  - Test classes such as `PlaceOrderForRegisteredUser`, `PlaceQuoteForGuestUser`, `MyAccountTest`, `VerifySiteLinks`, `VerifyCallRail`.
  - Browser bootstrap and interaction helpers in `com.mcnichols.framework.Browser` and `com.mcnichols.framework.BrowserOperations`.
- Project type: **test automation framework** (not a backend API or standalone app). Justification:
  - No `public static void main(...)` entrypoint found in `src/**/*.java`.
  - Execution is wired through Maven Surefire + TestNG suite XMLs (`pom.xml`, `src/test/resources/**/*.xml`).

# Tech Stack

- Java version:
  - `1.8` source/target configured via `maven-compiler-plugin` in `pom.xml`.
- Build tool:
  - Maven (`pom.xml`).
- Testing framework:
  - TestNG (`org.testng:testng:6.14.3` in `pom.xml`).
  - Test lifecycle annotations in code (`@Test`, `@BeforeClass`, `@BeforeMethod`, `@AfterMethod`, `@AfterClass`, `@AfterSuite`).
- Browser automation:
  - Selenium Java (`org.seleniumhq.selenium:selenium-java:3.11.0`).
- Reporting:
  - ExtentReports (`com.aventstack:extentreports:3.1.5`) via `TestListener` + `ExtentManager`.
- Other libraries from `pom.xml`:
  - `org.json:json:20190722`
  - `org.apache.commons:commons-io:1.3.2`

# Project Structure

- Root-level build and docs:
  - `pom.xml`
  - `README.md`
- Source code:
  - `src/main/java/com/mcnichols/framework`
    - `Browser`, `BrowserOperations`: WebDriver setup and browser operations.
    - `config`: JSON/XML-backed test data models and environment flags (`TestingConfig`, `TestParameters`, `LoginCredentials`, `UserProfile`, `UserCreditCard`).
    - `pages`: page-object classes for site areas (home, login, cart, checkout, product/category/search, account pages, widgets).
    - `util`: helpers for logging, waits, JSON parsing, report/screenshot/zip operations.
    - `util.listeners`: TestNG listeners (`TestListener`, `InvokedMethodListener`).
    - `vendor`: vendor-specific checks (`CallRail`, `VendorService`).
  - `src/test/java/com/mcnichols/tests`
    - Core test classes for browse/search, order/quote, account, sitemap/link checks.
    - `production` and `sandbox` package variants of tests.
    - `util` base classes (`TestBase`, `TestClassBase`, `TestSuiteBase`).
    - `vendor` tests (`VerifyCallRail`).
- Runtime resources:
  - `src/main/resources/framework/pages/*.xml`: page configuration XML files.
  - `src/main/resources/framework/util/*.xml|*.json`: framework config (`test-base-config.xml`, `extent-report-config.xml`, `test-parameters-config.json`).
  - `src/main/resources/webdrivers/*.exe`: bundled driver binaries (Chrome and Gecko executables).
- Test suite files:
  - `src/test/resources/{development,production,sandbox,staging,staging-qa2,test-suite}/*.xml`.
- `bin/` directory:
  - Contains a duplicate `pom.xml` and compiled `.class` artifacts under `bin/src/main/java/...` (observed as `.class` files, not `.java`).

# Execution Flow

- Entry points:
  - Maven Surefire suite execution configured in `pom.xml`.
  - TestNG suite XML files under `src/test/resources/**`.
  - No Java `main` entrypoint found.

- How execution starts:
  1. Maven runs tests (`mvn test`) and Surefire loads configured suite XML(s).
  2. TestNG creates test contexts with suite/test parameters (`base.url`, `env`, `browser.name`, `browser.viewport`, plus test-specific params).
  3. `TestBase` listeners are active via `@Listeners({TestListener.class, InvokedMethodListener.class})`.

- Sequence inside test lifecycle (from code):
  1. `TestListener.onStart(...)`:
     - Calls `TestingConfig.setTestingEnvironment(base.url, env)`.
     - Creates Extent report instance via `ExtentManager.createInstance(...)`.
  2. `TestBase.setUp(...)` (`@BeforeClass`):
     - Creates report/screenshot directories.
     - Launches browser through `BrowserOperations.launchBrowser(browserName, env)`.
  3. `TestBase.startTest(...)` (`@Test(priority=0)`):
     - Sets viewport.
     - Navigates to `base.url`.
     - Waits for JS/jQuery/loading overlay.
     - Closes announcement/promo overlays when present.
  4. Concrete test methods run (most depend on `startTest` using `dependsOnMethods`).
  5. Method-level hooks (`@BeforeMethod`/`@AfterMethod`) in `TestBase`:
     - JavaScript error checker setup.
     - Screenshot capture.
     - JavaScript console/log checks.
     - Environment detail collection.
  6. `TestClassBase.tearDown()` (`@AfterClass`) quits browser.
  7. `TestListener.onFinish(...)` flushes Extent report.
  8. `TestListener.onExecutionFinish(...)` copies report artifacts to `target/test-report` and zips report output.

# Key Components

- Config classes:
  - `TestingConfig`: loads XML properties and exposes test-data selection helpers (`getLogin`, random term/item getters).
  - `TestParameters`: parses `test-parameters-config.json`, maps environment-specific login lists (`Dev-Logins`, `Prod-Logins`, default `Logins`).
  - `LoginCredentials`, `UserProfile`, `UserCreditCard`: data holders for account/profile/payment data.

- Test base classes:
  - `TestBase`: listener wiring + browser setup + `startTest` + cross-cutting hooks (screenshots, JS checks, helpers).
  - `TestClassBase`: class-level teardown (`driver.quit`).
  - `TestSuiteBase`: suite-level teardown (`driver.quit`) but no class in `src/test/java` currently extends it.

- Utility classes:
  - `Browser`: WebDriver wrapper methods (navigation, waits, element interactions, link verification, environment detail extraction).
  - `BrowserOperations`: driver initialization (Chrome/Firefox), options, viewport sizing.
  - `JavaScriptWaiter`: waits for `document.readyState` and jQuery activity.
  - `Logger`: sends logs to both TestNG reporter and Extent test.
  - `TestUtil`: directories, screenshots, report copy/zip.
  - `JSONUtil`, `StringUtil`, `ZipUtil`: generic support utilities.

- Listeners:
  - `TestListener`:
    - Implements `ITestListener` and `IExecutionListener`.
    - Creates/flushed Extent report and final artifact transfer/zip.
  - `InvokedMethodListener`:
    - Logs failure messages after failed test method invocations.

- Reporting:
  - Extent HTML report via `ExtentManager` and config `src/main/resources/framework/util/extent-report-config.xml`.
  - Screenshot attachments through `ExtentTestManager` + `TestUtil.takeScreenshot(...)`.
  - TestNG report artifacts are additionally copied into `target/test-report` at execution finish.

- Vendor integration:
  - `VendorService.callRail()` returns `CallRail` helper.
  - `CallRail` verifies dynamic phone number replacement behavior.
  - Test coverage in `com.mcnichols.tests.vendor.VerifyCallRail`.

# Configuration Details

- Environment config inputs:
  - Passed from suite XML params: `env`, `base.url`, `browser.name`, `browser.viewport`, and other test-specific values (e.g., `accountCC`, `category.path`, `product.path`, `sitemap`).
- Browser/driver config:
  - `src/main/resources/framework/util/test-base-config.xml` controls:
    - headless mode
    - Chrome/Firefox driver executable paths
    - optional Chrome profile dir
    - Chrome flags (best practices/incognito)
- Test data config:
  - `src/main/resources/framework/util/test-parameters-config.json` includes:
    - login datasets
    - search terms
    - item IDs for purchase/quote flows
- Page config files:
  - Multiple XML files under `src/main/resources/framework/pages/` (selectors/config values referenced by page classes).
- External dependencies required at runtime (from code):
  - Java 8-compatible runtime.
  - Maven.
  - Browser binaries compatible with provided driver executables.

# How to Run the Project

- From project root:

```bash
mvn clean
mvn compile
mvn test
```

- Run with `run` profile and explicit suite path (`pom.xml` profile `run`):

```bash
mvn test -Prun -DrunSuite=src/test/resources/production/regression-test.xml
```

- Run by profile (`pom.xml`):

```bash
mvn test -U -Pproduction-regression-tests
mvn test -U -Pdevelopment-regression-tests
mvn test -U -Pdevelopment-upgrade-regression-tests
```

- Run a suite directly in IDE (from `README.md`):
  - Right-click a suite XML under `src/test/resources/**` and run as TestNG Suite.

# Observations / Gaps

- Suite filename mismatch in `pom.xml`:
  - Default and `production-regression-tests` profile reference `src/test/resources/production/regression-test.xml`.
  - Available file present is `src/test/resources/production/regression-test-suite.xml`.
- Missing resource path for a configured profile:
  - `development-upgrade-regression-tests` references `src/test/resources/development-upgrade/...`.
  - `src/test/resources/development-upgrade/` directory is not present.
- No top-level `testng.xml` found:
  - Execution relies on environment-specific suite XML files and Maven Surefire configuration.
- `TestSuiteBase` exists but is not used by any discovered test class (`extends TestSuiteBase` not found).
- Credentials and card-like values appear in `test-parameters-config.json` in plaintext.
- `bin/` contains a second `pom.xml` and compiled class artifacts; relationship to active build lifecycle is **Not स्पष्ट / Not explicitly defined in codebase**.
- CI/CD pipeline definition (e.g., Jenkinsfile/GitHub Actions) is **Not स्पष्ट / Not explicitly defined in codebase**.
- Intended complete environment matrix selection strategy across `development`, `production`, `sandbox`, `staging`, `staging-qa2` is partly inferable from XML files, but central orchestration policy is **Not स्पष्ट / Not explicitly defined in codebase**.
