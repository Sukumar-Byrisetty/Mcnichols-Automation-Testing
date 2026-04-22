# Automated Testing Framework
An automation framework architecture with Selenium. The absolute requirements is to pass or fail and allow the tests drive the direction of the framework.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You will need to have Maven installed and TestNG added to the eclipse IDE. The maven project will download the other dependencies.

### Project Setup
1. Under your workspace create a folder name “automated-testing”
2. Right click on the newly created folder and select “SVN Checkout…”
	a. Set the URL of repository to the following and verify the checkout directory.
	[https://hqsource01.mcnichols.com:4444/svn/atg/dev/misc/automated-testing](https://hqsource01.mcnichols.com:4444/svn/atg/dev/misc/automated-testing) - Project artifacts
3. Add a new project to Eclipse
	a. In Eclipse go to: File > Import
	b. Select Maven > Existing Maven Projects
	c. Click Browse and select the automated-testing folder
	d. Click the pom.xml box under projects and click next then finish
	e. Open the automated-testing folder > right click on the pom.xml > Run As > click Maven build
4. From the eclipse market place install TestNG for Eclipse


## Running the tests
There are a few ways to execute a test.
1.	Run as TestNG Suite

``` 
	Navigate to a TestNG suite file > right click the XML > Run As > TestNG Suite
		Example: AutomatedTesting > src > test > resources > development > place-quote-test.xml
```

2.	Run Maven commands to execute the test. Open a command prompt and navigate to the AutomatedTesting folder and execute a Maven test command.


Cleaning and compiling the code

```
mvn clean
mvn compile
```

Running the tests

```
Run options
Run all tests specified in pom file
	mvn test
Run test by passing in the suite file path
	mvn test -Prun -DrunSuite=src/test/resources/production/regression-test.xml
Run test by profile name
	mvn test -U -Pproduction-regression-tests

```


## Built With

* [Selenium WebDriver](https://www.seleniumhq.org) - Tool used to automate web application testing to verify that it works as expected. 
* [TestNG](http://testng.org/doc/) - TestNG is a testing framework used
* [Maven](https://maven.apache.org/) - Dependency Management

