package com.lectek.android.lereader.presenter;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;

import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.application.MyAndroidApplication;

/**
 * @author linyiwei
 * @date 2012-02-29
 * @email 21551594@qq.com.com
 */
public class BasePresenter {
	public static Context getContext() {
		return MyAndroidApplication.getInstance();
	}

	public static ContentResolver getContentResolver() {
		return MyAndroidApplication.getInstance().getContentResolver();
	}

	public static Handler getHandler() {
		return BaseApplication.getHandler();
	}

	public static void runInUI(Runnable runnable) {
		BaseApplication.getHandler().post(runnable);
	}

	public interface LoadListRunUI<T> {
		public boolean onPreRun();

		public void onPostRun(ArrayList<T> newList);

		public void onFailUI(String msg);
	}
	
	public interface ILoadDataRunUI<T> {
		public boolean onPreRun();

		public void onPostRun(T object);

		public void onFailUI(String msg);
	}
	
	public interface ILoadRunnadle {
		public void onLoadData(Runnable runnable);
		public void onPostRunUI(Object object);
	}
	/**
	 * 建议替代以上接口
	 * @author Administrator
	 *
	 */
	public interface ILoadDataUIRunnadle {
		/**
		 * 返回true代表终止数据加载任务
		 * @return
		 */
		public boolean onPreRun();
		/**
		 * 数据加载结束时的回调
		 * @return
		 */
		public void onPostRun(Object... params);
		/**
		 * 数据加载失败时候的回调
		 * @return
		 */
		public void onFailUI(Object... params);
	}
	
	public static class LoadDataUIRunnadle implements ILoadDataUIRunnadle{
		/**
		 * 返回false代表终止数据加载任务
		 * @return
		 */
		@Override
		public boolean onPreRun() {
			return false;
		}
		/**
		 * 数据加载结束时的回调
		 * @return
		 */
		@Override
		public void onPostRun(Object... params) {
			
		}
		/**
		 * 数据加载失败时候的回调
		 * @return
		 */
		@Override
		public void onFailUI(Object... params) {
			
		}
	}
	
	public interface ILoadPostRunnable {
		public void run(boolean isSuccess);
	}
}
