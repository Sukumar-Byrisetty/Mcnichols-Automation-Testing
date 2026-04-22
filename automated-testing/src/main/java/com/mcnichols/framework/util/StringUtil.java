package com.mcnichols.framework.util;

public class StringUtil {
	
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.trim().equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
}
