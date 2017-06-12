package com.lectek.android.lereader.lib.test;

import java.io.IOException;

import android.os.Environment;

import com.lectek.android.lereader.lib.utils.FileUtil;

public class TestFileUtil extends AbsTestUnit {

	@Override
	public String getTag() {
		return "FileUtil";
	}
	
	public void testCheckPath() {
		try {
			logout(String.format("checkPath(%s , %s)", "_", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath(null, false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "E:/abc", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath("E:/abc", false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "E:/abc/", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath("E:/abc/", false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath("/", false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath(), false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard/abc/cde/efg", "fasle"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath() + "/abc/cde/efg", false) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard/abc/cde/efg", "true"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath() + "/abc/cde/efg", true) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard/abc/cdf/efg/", "true"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath() + "/abc/cdf/efg/", true) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard/abd/", "true"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath() + "/abd/", true) ? "true" : "false"));
			logout("-------------------------------------");
			
			logout(String.format("checkPath(%s , %s)", "/sdcard/abe", "true"));
			logout(String.format("result: %s", FileUtil.checkPath(Environment.getExternalStorageDirectory().getPath() + "/abe", true) ? "true" : "false"));
			logout("-------------------------------------");
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
