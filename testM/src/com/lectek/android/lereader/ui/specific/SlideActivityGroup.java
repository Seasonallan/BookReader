/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lectek.android.lereader.ui.specific;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.UniqueIdUtils;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.presenter.SyncPresenter;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.widgets.SlidingView;
import com.lectek.android.lereader.widgets.SlidingView.ISlideGuestureHandler;
import com.lectek.android.lereader.widgets.drag.DragController;
import com.lectek.android.lereader.widgets.drag.OpenFolder;
import com.lectek.android.update.UpdateManager;
import com.umeng.analytics.MobclickAgent;


@SuppressWarnings("deprecation")
public class SlideActivityGroup extends ActivityGroup implements ISlideGuestureHandler{
	 
	public static final String Extra_Switch_UI = "Extra_Switch_UI";
	public static final String Extra_Switch_BookCity_UI = "Extra_Switch_BookCity_UI";
	
	public static final int BOOK_CITY = 0;
	public static final int BOOK_SHELF = BOOK_CITY + 1;
	public static final int PERSONAL_CENTER = BOOK_SHELF + 1;
	
	private static final String ACTIVITY_ID_USER_CENTER = "UserCenter";
	private static final String ACTIVITY_ID_BOOK_CITY = "BookCity";
	private static final String ACTIVITY_ID_BOOK_SHELF = "BookShelf";
	
	SlidingView mSlideView;
  
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		mSlideView = new SlidingView(this);
		mSlideView.setSlideGuestureListener(this);
		setContentView(mSlideView);
		init();
		
	}

	private void init() {
		registerReceiver(mNotify, new IntentFilter(AppBroadcast.ACTION_CLOSE_APP));
		
		mSlideView.setLeftView(getView(UserInfoActivity.class, ACTIVITY_ID_USER_CENTER));
		mSlideView.setRightView(getView(BookCityActivityGroup.class, ACTIVITY_ID_BOOK_CITY));
		mSlideView.setCenterView(getView(BookShelfActivity.class, ACTIVITY_ID_BOOK_SHELF));
		mSlideView.setCanSliding(true, true);
//		mSlideView.setCenterView(getView(BookCityActivityGroup.class, ACTIVITY_ID_BOOK_CITY));
//		mSlideView.setCanSliding(false, false);
		
		//检查更新
		final UpdateInfo updateInfo = UpdateManager.getUpdateInfo();
		if(updateInfo != null){
			PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(this);
			if(preferencesUtil.isShowUpdateAgain() || updateInfo.isMustUpdate()){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//UpdateManager.getInstance().startUpdateInfo(SlideActivityGroup.this, updateInfo, null);
					}
				});
			}
		}		
	}
	
	private void exitAppTask() {
		finish();
	}
	
	private BroadcastReceiver mNotify = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			//退出应用通知
			if(AppBroadcast.ACTION_CLOSE_APP.equals(intent.getAction())) {
				exitAppTask();
			}
		}
	};
	
	private View getView(Class<?> tClass, String id){
		Intent intent = new Intent(this, tClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Window subActivity = getLocalActivityManager().startActivity(id, intent);
		return subActivity.getDecorView();
	}
	 
	@Override
	public boolean consumeHorizontalSlide(float x, float y, float deltaX) {
		
		if(getLocalActivityManager().getActivity(getCurrentActivityID()) instanceof ISlideGuestureHandler) {
			return ((ISlideGuestureHandler)getLocalActivityManager().getActivity(getCurrentActivityID())).consumeHorizontalSlide(x, y, deltaX);
		}
		
		return false;
	}
	
	private String getCurrentActivityID() {
		String result = "";
		if(mSlideView.isLeftShow()) {
			result = ACTIVITY_ID_USER_CENTER;
		}else if(mSlideView.isRightShow()) {
			result = ACTIVITY_ID_BOOK_CITY;
		}else {
			result = ACTIVITY_ID_BOOK_SHELF;
		}
		
		return result;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		int switchTo = intent.getIntExtra(Extra_Switch_UI, -1);
		
		if(intent.getBooleanExtra(Extra_Switch_BookCity_UI, false)) {
			
			intent.setClass(this, BookCityActivityGroup.class);
			getLocalActivityManager().startActivity(ACTIVITY_ID_BOOK_CITY, intent);
			
			if(!mSlideView.isRightShow()) {
				mSlideView.showRightView();
			}
			return;
			
		}else if(switchTo >= BOOK_CITY) {
			
			if(switchTo == BOOK_CITY && !mSlideView.isRightShow()) {
				mSlideView.showRightView();
			}else if(switchTo == BOOK_SHELF ) {
				mSlideView.showCenterView();
			}else if(switchTo == PERSONAL_CENTER && !mSlideView.isLeftShow()) {
				mSlideView.showLeftView();
			}
			
			return;
		}
		
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainActivity");
		MobclickAgent.onResume(this);
		OpenBookAnimManagement.getInstance().starCloseBookAnim(null);
//		if(mSlideView.isLeftShow()) {
//			((UserInfoActivity)getLocalActivityManager().getActivity(ACTIVITY_ID_USER_CENTER)).onResume();
//		}else if(mSlideView.isRightShow()){
//			((BookCityActivityGroup)getLocalActivityManager().getActivity(ACTIVITY_ID_BOOK_CITY)).onResume();
//		}else {
//			((BookShelfActivity)getLocalActivityManager().getActivity(ACTIVITY_ID_BOOK_SHELF)).onResume();
//		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SyncPresenter.startSyncLocalSysBookMarkTask();
		MobclickAgent.onPageEnd("MainActivity");
		MobclickAgent.onPause(this);
	}

	private long lastExitTime = 0;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (DragController.getInstance().isDragWorking()){
                DragController.getInstance().notifyDragDisable();
                return false;
            }
            if (OpenFolder.sInstance != null && OpenFolder.sInstance.isOpened()){
                OpenFolder.sInstance.dismiss();
                return true;
            }
            
            //执行书城返回
            if(mSlideView.isRightShow()){
    			getLocalActivityManager().getActivity(ACTIVITY_ID_BOOK_CITY).onBackPressed();
    			return false;
    		}
            
			if(!mSlideView.showCenterView()){
				mSlideView.showCenterView();
				return false;
			}
			if(System.currentTimeMillis()-lastExitTime > 1500){
				lastExitTime = System.currentTimeMillis();
				Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT).show();
			}else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						//退出记录日志  20140507 -wuwq
						try {
							ApiProcess4Leyue.getInstance(MyAndroidApplication.getInstance()).exitLog(AccountManager.getInstance().getUserID(), 
													UniqueIdUtils.getDeviceId(MyAndroidApplication.getInstance()), UniqueIdUtils.getTerminalBrand(MyAndroidApplication.getInstance()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
				
				finish();
			}
			
			return true;
		}
		return super.dispatchKeyEvent(event);
	} 
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mNotify);
		ThreadPoolFactory.destroyAllThreadPools();
		sendBroadcast(new Intent(AppBroadcast.ACTION_CLOSE_APP));
		
		super.onDestroy();
	}

    public static interface IGotoFuliPage{
        public void onGoto(int page);
    }
    public static class PageController{
        private static PageController mInstance;
        private IGotoFuliPage mListener;
        public static PageController getmInstance(){
            if (mInstance == null){
                mInstance = new PageController();
            }
            return mInstance;
        }
        public void setmListener(IGotoFuliPage listener){
            this.mListener = listener;
        }
        public void notifyGo2Page(int page){
            if (mListener != null){
                mListener.onGoto(page);
            }
        }
    }

}
