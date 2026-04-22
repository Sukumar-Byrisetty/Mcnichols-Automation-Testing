package com.mcnichols.framework.util.extentreports;

import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

	private static ThreadLocal<ExtentTest> test;

	public static void setTest(ThreadLocal<ExtentTest> test) {
		ExtentTestManager.test = test;
	}

	public static synchronized ExtentTest getTest() {
        return (ExtentTest) test.get();
    }
}
