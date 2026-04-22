package com.mcnichols.framework.util.extentreports;

import org.openqa.selenium.Platform;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.mcnichols.framework.BrowserOperations;
import com.mcnichols.framework.config.TestParameters;
import com.mcnichols.framework.util.StringUtil;
import com.mcnichols.framework.util.TestUtil;

public class ExtentManager {

	private static ExtentReports extent;
    private static Platform platform;
    private static String reportFileName = "Test-Automaton-Report.html";
    private static String path = TestUtil.getTestIdReportingPath();
    private static String reportFileLoc = path + "\\" + reportFileName;

    public static ExtentReports getInstance() {
        if (extent == null)
            createInstance();
        return extent;
    }
 
    // Create an extent report instance
    public static ExtentReports createInstance() {
    	return createInstance("", "");
    }
    public static ExtentReports createInstance(String testName, String suiteName) {

    	if (StringUtil.isEmpty(testName)) {
    		reportFileLoc = "." + path + "/" + reportFileName;
    	} else {
    		reportFileLoc = "." + path + "/" + testName + ".html";
    	}

        platform = getCurrentPlatform();
        String fileName = getReportFileLocation(platform);

        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
        String xmlConfig = "." + TestParameters.UTIL_PROPERTY_DIRECTORY_LOCATION + "extent-report-config.xml";
        htmlReporter.loadXMLConfig(xmlConfig);

        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
        htmlReporter.config().setDocumentTitle(testName);
        htmlReporter.config().setReportName(testName);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        extent.setSystemInfo("Hostname", "localhost");
        extent.setSystemInfo("OS", "Windows 10");
        extent.setSystemInfo("Browser", BrowserOperations.testingBrowserName);
        extent.setSystemInfo("Browser Viewport", BrowserOperations.testingBrowserViewport);
 
        return extent;
    }

    // Select the extent report file location based on platform
    private static String getReportFileLocation(Platform platform) {
        String reportFileLocation = null;

        TestUtil.createDirectory(path);
        reportFileLocation = reportFileLoc;

        System.out.println("ExtentReport Path: " + path + "\n");
        
        return reportFileLocation;
    }

    // Get current platform
    private static Platform getCurrentPlatform () {
        if (platform == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                platform = Platform.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                platform = Platform.LINUX;
            } else if (operSys.contains("mac")) {
                platform = Platform.MAC;
            }
        }
        return platform;
    }
}
