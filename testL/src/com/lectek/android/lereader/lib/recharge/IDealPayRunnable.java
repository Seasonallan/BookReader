package com.lectek.android.lereader.lib.recharge;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.ServiceConnection;

/**
 * 支付流程反馈接口
 * 
 * @author yangwq
 * @date 2014年6月18日
 * @email 57890940@qq.com
 */
public interface IDealPayRunnable {
	
	public Object onGetOrder(int payType);
	
	public void onRegisterSmsReceiver(String action, BroadcastReceiver receiver);
	public void onUnregisterSmsReceiver(BroadcastReceiver receiver);
	
	public void bindService(Intent service, ServiceConnection conn, int flags);
	public void unbindService(ServiceConnection conn);
	
	public void startActivity(Intent intent);
	
	public void onPayComplete(boolean success, int resultCode, String resultMsg, Object resultData);
}
