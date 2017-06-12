package com.lectek.android.lereader.account;
/**
 * 账号封装基本接口
 * @author wuwq
 *
 */
public interface AccountInfoInterface<T> {
	/**
	 * 获取账号信息
	 * @return
	 */
	public T getAccountInfo(String userId,int type);
	/**
	 * 插入账号信息
	 * @param t
	 * @return
	 */
	public int setAccountInfo(T t);
	/**
	 * 更新用户信息
	 * @param t
	 * @return
	 */
	public T updateAccountInfo(T t);
	/**
	 * 删除用户
	 * @param t
	 * @return
	 */
	public int deleteAccountInfo(T t);
	
	/**
	 * 解密
	 * @param featureName
	 * @return
	 */
	public String decode(String value);
	
	/**
	 * 加密
	 * @param value
	 * @return
	 */
	public String encode(String value);
	
	/**
	 * 以字符串形式输出
	 * @return
	 */
	public String toString();
	
	/**
	 * 账号是否有效
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * 销毁实例时
	 */
	public void onDestory();
	
}
