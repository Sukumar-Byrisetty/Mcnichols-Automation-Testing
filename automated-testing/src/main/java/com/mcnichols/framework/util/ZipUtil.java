package com.mcnichols.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	List<String> filesListInDir = new ArrayList<String>();

	/**
     * This method zips the directory
     * @param dir
     * @param zipDirName
     */
    public void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir);

            // Now zip files one by one create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (String filePath : filesListInDir) {
                //System.out.println("Zipping: "+ filePath);

                // For ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length() + 1, filePath.length()));
                zos.putNextEntry(ze);

                // Read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
        	// For now exclude images
        	if (file.toString().contains(".png")) {
        		continue;
        	}
        	
            if (file.isFile()) {
            	if (file.toString().contains(".zip")) {
            		continue;
            	}
            	filesListInDir.add(file.getAbsolutePath());
            } else {
            	populateFilesList(file);
            }
        }
    }

}
