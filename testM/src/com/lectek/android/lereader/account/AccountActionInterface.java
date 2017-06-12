package com.lectek.android.lereader.account;

import android.content.Context;

/**
 * 登录方式接口
 * @author chends@lectek.com
 * @date 2014-01-14
 */
public interface AccountActionInterface {

	/**
	 * 检查授权
	 * @return
	 */
	public boolean checkAuthorization(Context context);
	
	/**
	 * 登录
	 * @return
	 */
	public boolean login(Context context, String account, String password);
	
	/**
	 * 注销账号
	 */
	public void logout(Context context);
	
	/**
	 * 获取帐号信息
	 * @param context
	 */
	public boolean getAccountInfo(Context context); 
	
	/**
	 * 帐号激活
	 */
	public void onActivity(Context context);
}
