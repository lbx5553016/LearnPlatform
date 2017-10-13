package com.tencent.brendanlu.learnplatform.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author Brendanlu
 */

public class AppUtils {



	/**
	 * 快速点击识别
	 *
	 * @return boolean
	 */
	private static long lastClickTime; // vincentxluo 用于快速点击界定

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long interval = time - lastClickTime;
		if (0 < interval && interval < 300) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static boolean isEmpty(final String str) {
		return str == null || str.length() <= 0;
	}
	public static boolean isEmpty(final Collection<? extends Object> collection){
		return collection == null || collection.size() <= 0;
	}

	public static boolean isEmpty(final Map<? extends Object,? extends Object> list){
		return list == null || list.size() <= 0;
	}

	public static boolean isEmpty(final byte[] bytes) {
		return bytes == null || bytes.length <= 0;
	}

	public static boolean isEmpty(final String[] strArr){
		return strArr == null || strArr.length <= 0;
	}


}
