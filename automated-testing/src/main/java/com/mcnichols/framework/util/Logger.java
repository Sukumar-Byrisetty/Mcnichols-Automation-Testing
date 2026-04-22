package com.mcnichols.framework.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.testng.Reporter;

import com.mcnichols.framework.util.extentreports.ExtentTestManager;

public class Logger {
		
	public static void info(String message) {
		ExtentTestManager.getTest().info(message);
		Reporter.log(message, true);
	}
	
	public static void warning(String message) {
		ExtentTestManager.getTest().warning(message);
		Reporter.log(message, true);
	}

	public static void pass(String message) {
		ExtentTestManager.getTest().pass(message);
		Reporter.log(message, true);
	}

	public static void fail(String message) {
		ExtentTestManager.getTest().fail(message);
		Reporter.log(message, true);
	}

	public static void processTime(long startTime, long endTime) {
		processTime(startTime, endTime, "");
	}
	
	public static void processTime(long startTime, long endTime, String pageNamePrefixForLogger) {
		NumberFormat formatter = new DecimalFormat("#0.00000");
		String prefix = StringUtil.isNotEmpty(pageNamePrefixForLogger) ? pageNamePrefixForLogger + "" : "";
		String message = prefix + "Processing time is " + formatter.format((endTime - startTime) / 1000d) + " seconds";

		Reporter.log(message, true);
		ExtentTestManager.getTest().info(message);
	}
}
