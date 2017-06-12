/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.lectek.android.lereader.lib.recharge.alipay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;
import com.lectek.android.lereader.lib.recharge.IDealPayRunnable;
import com.lectek.android.lereader.lib.utils.LogUtil;

public class MobileSecurePayer {
	static String TAG = "MobileSecurePayer";

	Integer lock = 0;
	IAlixPay mAlixPay = null;
	boolean mbPaying = false;

	private Handler mHandler;
	private IDealPayRunnable mDealCallback;
//	Context mContext = null;
	
	private ServiceConnection mAlixPayConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
		    synchronized( lock ) {	
		    	mAlixPay 	= IAlixPay.Stub.asInterface(service);
		    	lock.notify();
		    }
		}
	
		public void onServiceDisconnected(ComponentName className) {
			mAlixPay	= null;
		}
	};
	
	public void abort() {
		if(mDealCallback != null) {
			mDealCallback.unbindService(mAlixPayConnection);
		}
	}
	
	public boolean pay(final String strOrderInfo, Handler callback, final int myWhat, IDealPayRunnable callBack) {
		mHandler = callback;
		if( mbPaying ){
			return false;
		}
		mbPaying = true;
		mDealCallback = callBack;
//		mContext = activity;
		
		// bind the service.
		if (mAlixPay == null) {
			String className = IAlixPay.class.getName();
			mDealCallback.bindService(new Intent(className), mAlixPayConnection, Context.BIND_AUTO_CREATE);
		}
		//else ok.
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					// wait for the service bind operation to completely finished.
					// Note: this is important,otherwise the next mAlixPay.Pay() will fail.
					synchronized (lock) {
						if (mAlixPay == null) {
							lock.wait();
						}
					}

					// register a Callback for the service.
					mAlixPay.registerCallback(mCallback);
					
					// call the MobileSecurePay service.
					String strRet = mAlixPay.Pay(strOrderInfo);
					LogUtil.i(TAG, "After Pay: " + strRet);

					// set the flag to indicate that we have finished.
					// unregister the Callback, and unbind the service.
					mbPaying = false;
					mAlixPay.unregisterCallback(mCallback);
					mDealCallback.unbindService(mAlixPayConnection);
					
					// send the result back to caller.
					Message msg = mHandler.obtainMessage(myWhat);
					msg.obj = strRet;
					mHandler.sendMessage(msg);
					
				} catch (Exception e) {
					e.printStackTrace();
					
					// send the result back to caller.
					Message msg = mHandler.obtainMessage(myWhat);
					msg.obj = e.toString();
					mHandler.sendMessage(msg);
				}
				mbPaying = false;
			}
		}).start();
		
		return true;
	}
	
	 /**
	 * This implementation is used to receive callbacks from the remote
	 * service.
	 */
	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
		/**
		 * This is called by the remote service regularly to tell us
		 * about new values. Note that IPC calls are dispatched through
		 * a thread pool running in each process, so the code executing
		 * here will NOT be running in our main thread like most other
		 * things -- so, to update the UI, we need to use a Handler to
		 * hop over there.
		 */
		public void startActivity(String packageName, String className, int iCallingPid, Bundle bundle)
				throws RemoteException {
			Intent intent	= new Intent(Intent.ACTION_MAIN, null);
			
			if( bundle == null ) {
				bundle = new Bundle();
			}
			// else ok.

			try {
				bundle.putInt("CallingPid", iCallingPid);
				intent.putExtras(bundle);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			intent.setClassName(packageName, className);
			mDealCallback.startActivity(intent);
		}
	};
}