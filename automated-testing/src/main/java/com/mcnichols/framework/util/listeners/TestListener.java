package com.mcnichols.framework.util.listeners;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.mcnichols.framework.config.TestingConfig;
import com.mcnichols.framework.util.TestUtil;
import com.mcnichols.framework.util.extentreports.ExtentManager;
import com.mcnichols.framework.util.extentreports.ExtentTestManager;

public class TestListener implements ITestListener, IExecutionListener {

    // Extent Report Declarations
	private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public synchronized void onStart(ITestContext context) {
        System.out.println("Test Suite started!");

        TestingConfig.setTestingEnvironment(context.getCurrentXmlTest().getParameter("base.url"), context.getCurrentXmlTest().getParameter("env"));
        extent = ExtentManager.createInstance(context.getName(), context.getSuite().getName());
    }

    @Override
    public synchronized void onFinish(ITestContext context) {
        System.out.println(("Test Suite is ending!"));
        extent.flush();

        // Copying directory to the default test-output directory for Jenkins to publish reporting  
        //TestUtil.transferReportingDirectory();
        
        // Zip directory for Email use or something...
        //TestUtil.compressTestReportDirectory();
    }

    @Override
    public synchronized void onTestStart(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " started!"));
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),result.getMethod().getDescription());
        test.set(extentTest);

        // Set ExtentTest to use in the framework
        ExtentTestManager.setTest(test);
    }

    @Override
    public synchronized void onTestSuccess(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " passed!"));
        test.get().pass("Test passed");
    }

    @Override
    public synchronized void onTestFailure(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " failed!"));
        test.get().fail(result.getThrowable());
    }

    @Override
    public synchronized void onTestSkipped(ITestResult result) {
        System.out.println((result.getMethod().getMethodName() + " skipped!"));
        test.get().skip(result.getThrowable());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
    }

	@Override
	public void onExecutionStart() {
		long startTime = System.currentTimeMillis();
		System.out.println("Inform all the suites have started execution at: " + startTime);
	}

	@Override
	public void onExecutionFinish() {
		long endTime = System.currentTimeMillis();
		System.out.println("Inform all the suites have finished execution at: " + endTime);
		
		// Copying directory to the default test-output directory for Jenkins to publish reporting  
        TestUtil.transferReportingDirectory();
        
        // Zip directory for Email use or something...
        TestUtil.compressTestReportDirectory();
	}
}
