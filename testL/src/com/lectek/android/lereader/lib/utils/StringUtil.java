package com.lectek.android.lereader.lib.utils;

import android.text.TextUtils;

/**
 * 字符串工具
 * @author chends@lectek.com
 * 2014-05-26
 */
public class StringUtil{

	/**
	 * 判断字符串是否为空
	 * @param value
	 * @return 如果是 空指针 || 长度为0 || 全部为空格 || "null"字符串 返回true,否则false
	 */
	public static boolean isEmpty(String value) {
		return value == null || TextUtils.isEmpty(value.trim()) || value.trim().compareToIgnoreCase("null") == 0;
	}

    /**
     * 根据规则获取文件名
     * @param src
     * @param startStr
     * @param endStr
     * @return
     */
    public static String subEndString(final String src, String startStr, String endStr) {
        String newStr = null;
        if (TextUtils.isEmpty(src)) {
            return null;
        }
        int start = src.lastIndexOf(startStr);
        if (start < 0) {
            return null;
        }
        int end = src.lastIndexOf(endStr);
        if (end < 0 || end < start) {
            return null;
        }
        if (start == 0 && end == 0) {
            newStr = src;
        } else {
            newStr = src.substring(start + 1, end);
        }

        return newStr;
    }

}
