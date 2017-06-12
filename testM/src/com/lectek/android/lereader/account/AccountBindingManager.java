package com.lectek.android.lereader.account;

import java.util.ArrayList;

import com.lectek.android.lereader.net.response.AccountBindingInfo;
import com.lectek.android.lereader.permanent.ApiConfig;

/**
 * 账户绑定管理类
 * 
 * @author yangwq
 * @date 2014年7月11日
 * @email 57890940@qq.com
 */
public class AccountBindingManager {
	
	private static AccountBindingManager mInstance;
	
	/** 所有绑定账户信息列表*/
	private ArrayList<AccountBindingInfo> bindingList = new ArrayList<AccountBindingInfo>();
	
	/** 腾讯绑定信息*/
	private AccountBindingInfo mTencentAccountInfo = null;
	
	/** 新浪绑定信息*/
	private AccountBindingInfo mSinaAccountInfo = null;
	
	/** 天翼阅读绑定信息*/
	private AccountBindingInfo mTianYiAccountInfo = null;
	
	private boolean mHasStartBindingListModel = false;
	
	
	private AccountBindingManager(){
		
	}
	
	private synchronized static void syncInit(){
		if(mInstance == null){
			mInstance = new AccountBindingManager();
		}
	}
	
	public static AccountBindingManager getInstance(){
		if(mInstance == null){
			syncInit();
		}
		return mInstance;
	}
	
	
	/**
	 * 获取是否已经取得服务器账户绑定信息
	 * @return
	 */
	public boolean getHasStartBindingListModel(){
		return mHasStartBindingListModel;
	}
	
	/**
	 * 设置是否已经取得服务器账户绑定信息
	 * @param hasStart
	 */
	public void setHasStartBindingListModel(boolean hasStart){
		mHasStartBindingListModel = hasStart;
	}
	
	/**
	 * 获取是否已绑定腾讯账号
	 * @return
	 */
	public boolean getHasBindingTencent(){
		if(mTencentAccountInfo == null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 获取是否已绑定新浪账号
	 * @return
	 */
	public boolean getHasBindingSina(){
		if(mSinaAccountInfo == null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 获取是否已绑定天翼账号
	 * @return
	 */
	public boolean getHasBindingTianYi(){
		if(mTianYiAccountInfo == null){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 更新所有绑定账户情况
	 */
	private void refreshBindingAccounts(){
		try {
			if(bindingList != null){
				int size = bindingList.size();
				for (int i = 0; i < size; i++) {
					AccountBindingInfo accountBindingInfo = bindingList.get(i);
					String source = accountBindingInfo.getSource();
					if(source.equals(ApiConfig.QQ_REGISTER)){
						setmTencentAccountInfo(accountBindingInfo);
					}else if(source.equals(ApiConfig.SINA_REGISTER)){
						setmSinaAccountInfo(accountBindingInfo);
					}else if(source.equals(ApiConfig.TIAN_YI_REGISTER)){
						setmTianYiAccountInfo(accountBindingInfo);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	/**
	 * 更新某个渠道的账户绑定信息
	 * @param aAccountBindingInfo
	 */
	public void updateBindingAccount(AccountBindingInfo aAccountBindingInfo){
		String source = aAccountBindingInfo.getSource();
		if(source.equals(ApiConfig.QQ_REGISTER)){
			setmTencentAccountInfo(aAccountBindingInfo);
		}else if(source.equals(ApiConfig.SINA_REGISTER)){
			setmSinaAccountInfo(aAccountBindingInfo);
		}else if(source.equals(ApiConfig.TIAN_YI_REGISTER)){
			setmTianYiAccountInfo(aAccountBindingInfo);
		}
		
	}
	
	/**
	 * 解绑某个渠道的账户信息
	 * @param source
	 */
	public void onUnbindAccountBySource(String source){
		if(source.equals(ApiConfig.QQ_REGISTER)){
			setmTencentAccountInfo(null);
		}else if(source.equals(ApiConfig.SINA_REGISTER)){
			setmSinaAccountInfo(null);
		}else if(source.equals(ApiConfig.TIAN_YI_REGISTER)){
			setmTianYiAccountInfo(null);
		}
	}
	
	

	public ArrayList<AccountBindingInfo> getBindingList() {
		return bindingList;
	}

	public void setBindingList(ArrayList<AccountBindingInfo> bindingList) {
		this.bindingList = bindingList;
		refreshBindingAccounts();
	}

	public AccountBindingInfo getmTencentAccountInfo() {
		return mTencentAccountInfo;
	}

	public void setmTencentAccountInfo(AccountBindingInfo mTencentAccountInfo) {
		this.mTencentAccountInfo = mTencentAccountInfo;
	}

	public AccountBindingInfo getmSinaAccountInfo() {
		return mSinaAccountInfo;
	}

	public void setmSinaAccountInfo(AccountBindingInfo mSinaAccountInfo) {
		this.mSinaAccountInfo = mSinaAccountInfo;
	}

	public AccountBindingInfo getmTianYiAccountInfo() {
		return mTianYiAccountInfo;
	}

	public void setmTianYiAccountInfo(AccountBindingInfo mTianYiAccountInfo) {
		this.mTianYiAccountInfo = mTianYiAccountInfo;
	}	
	
	
	
	
	
	
	
	
	

}
