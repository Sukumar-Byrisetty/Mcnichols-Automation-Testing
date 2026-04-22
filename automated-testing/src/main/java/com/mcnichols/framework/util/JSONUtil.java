package com.mcnichols.framework.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	public static JSONObject parseJSONFile(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			return new JSONObject(content);
		} catch (JSONException jsonE) {
			jsonE.printStackTrace();
		} catch (IOException ioE) {
			ioE.printStackTrace();
		}
		return new JSONObject();
	}
}
