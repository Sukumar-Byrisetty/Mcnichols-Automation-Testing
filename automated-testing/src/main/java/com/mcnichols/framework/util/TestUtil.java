package com.mcnichols.framework.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.mcnichols.framework.Browser;
import com.mcnichols.framework.util.extentreports.ExtentTestManager;

public class TestUtil {
	public static String testId = "";
	public static String testReportDirectoryName = "test-report";
	public static String testScreenshotsDirectoryName = "screenshots";

	public static void createDirectory(String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
        	testDirectory.mkdir();
        	// System.out.println("Directory created: " + path);
        }
    }

	public static boolean doesDirectoryExists(String path) {
        File testDirectory = new File(path);
        return testDirectory.exists();
    }

	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		System.out.println(formatter.format(date));
		return formatter.format(date);
	}

	public static String getUUID() {
		// Creating a random UUID (Universally unique identifier).
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        System.out.println("Test UUID: " + randomUUIDString);
        return randomUUIDString;
	}

	public static String getRunningTestId() {
		String testUUID = "";
		if (StringUtil.isEmpty(testId)) {
			testUUID = getCurrentDate() + "_" + getUUID();
			testId = testUUID; 
		} else {
			testUUID = testId;
		}
		return testUUID;
	}

	public static String getTestIdReportingPath() {
		return "/" + TestUtil.testReportDirectoryName + "/" + getRunningTestId();
	}

	public static String getScreenshotsPath() {
		return getTestIdReportingPath() + "/" + testScreenshotsDirectoryName;
	}

	public static void takeScreenshot(String fileName) {
		try {
			long fileNameSuffix = System.currentTimeMillis();

			// Create refernce of TakesScreenshot
			TakesScreenshot ts = (TakesScreenshot) Browser.driver;

			// Call method to capture screenshot
			File source = ts.getScreenshotAs(OutputType.FILE);

			FileUtils.copyFile(source, new File("." + TestUtil.getScreenshotsPath() + "/" + fileName + "-" + fileNameSuffix + ".png"));
			ExtentTestManager.getTest().addScreenCaptureFromPath(TestUtil.testScreenshotsDirectoryName + "/" + fileName + "-" + fileNameSuffix + ".png", fileName);
		} catch (Exception e) {
			Logger.warning("Unable to take screenshot! Due to: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// This might be temporary.  Trying to figure out the best way to present the reporting in Jenkins.
	// For now, copy files to the target directory to be used in Jenkins.
	public static void transferReportingDirectory() {
		String sourcePath = "." + getTestIdReportingPath() + "/";
        File srcDir = new File(sourcePath);

        String destinationPath = "./target/" + TestUtil.testReportDirectoryName;
        File destDir = new File(destinationPath);
        
        // Make a copy emailable-report.html to reporting directory
 		try {
         	File emailableReportSource = new File("./target/surefire-reports/emailable-report.html");
         	
         	if (emailableReportSource.exists()) {
         		File emailableReportDest = new File(srcDir + "/emailable-report.html");
             	FileUtils.copyFile(emailableReportSource, emailableReportDest);	
         	}
 		} catch (IOException e) {
 			e.printStackTrace();
 		}

 		// Copy current reporting folder to test-report to expose artifacts to Jenkins 
        try {
        	copyFolder(srcDir, destDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// A utility method to copy a folder with its contents
	public static void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        // Check if sourceFolder is a directory or file
        // If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            // Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
                //System.out.println("Directory created :" + destinationFolder);
            }

            // Get all files from source directory
            String files[] = sourceFolder.list();

            // Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                // Recursive function call
                copyFolder(srcFile, destFile);
            }
        } else {
        	// Copy the file content from one place to another 
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //System.out.println("File copied :" + destinationFolder);
        }
    }

	public static void compressTestReportDirectory() {
		String testReportSrc = "." + getTestIdReportingPath();
		String testReportDest = "./target";

		File dir = new File(testReportSrc);
        String zipDirName = testReportDest + "/test-report-" + getRunningTestId() + ".zip";

        ZipUtil zipUtil = new ZipUtil();
        zipUtil.zipDirectory(dir, zipDirName);
	}

}
