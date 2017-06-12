package com.lectek.android.lereader.lib.test;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 测试基类
 * @author chends@lectek.com
 *
 */
public abstract class AbsTestUnit {
	
	private OutputStream mLogStream;
	private static final String TEST_METHOD_PREFIX = "test";	//自动测试子类需要加入测试的方法必须包含前缀
	
	public void setLogOutputStream(OutputStream logOutputStream) {
		mLogStream = logOutputStream;
	}
	
	/**
	 * log输出
	 * @param str
	 * @throws IOException
	 */
	protected void logout(String str) throws IOException{
		if(mLogStream != null) {
			mLogStream.write(str.getBytes());
			mLogStream.write("\n".getBytes());
			mLogStream.flush();
		}
	}
	
	/**
	 * 开始测试
	 */
	public final void runTest() throws InvocationTargetException, IllegalAccessException{
		
		Method[] methods = this.getClass().getDeclaredMethods();
		final int count = methods.length;
		for(int i = 0; i < count; i++) {
			Method method= methods[i];
			if(method.getName().toLowerCase().contains(TEST_METHOD_PREFIX)) {
				method.setAccessible(true);
				methods[i].invoke(this);
			}
		}
	}
	
	/**
	 * 获取测试模块名称
	 * @return
	 */
	public abstract String getTag();
}
