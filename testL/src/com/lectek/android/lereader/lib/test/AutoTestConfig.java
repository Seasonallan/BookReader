package com.lectek.android.lereader.lib.test;

import java.util.ArrayList;

public class AutoTestConfig {
	
	public static ArrayList<Class<? extends AbsTestUnit>> TEST_UNITS = new ArrayList<Class<? extends AbsTestUnit>>();
	
	static {
		TEST_UNITS.add(TestFileUtil.class);
	}
}
