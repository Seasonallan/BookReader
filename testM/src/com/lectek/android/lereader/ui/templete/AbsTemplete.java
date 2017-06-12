package com.lectek.android.lereader.ui.templete;

import java.lang.ref.SoftReference;

import com.lectek.android.lereader.ui.model.dataProvider.AbsUiDataProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 界面组件模板
 * @author chends@lectek.com
 * @date 2014/07/08
 */
public abstract class AbsTemplete<T> {
	
	private SoftReference<View> mBindView;
	
	private AbsUiDataProvider<T> mDataProvider;
	
	public AbsTemplete(View bindView) {
		mBindView = new SoftReference<View>(bindView);
		
		init();
	}

	public AbsTemplete(Context context, int bindLayout) {
		mBindView = new SoftReference<View>(LayoutInflater.from(context).inflate(bindLayout, null));
		
		init();
	}
	
	protected void init() {
		mDataProvider = onGetDataProvider();
	}
	
	/**
	 * 获取绑定的View
	 * @return
	 */
	public View getBindView() {
		return mBindView.get();
	}
	
	/**
	 * 刷新UI
	 */
	protected void refreshUI() {
		
	}
	
	protected abstract AbsUiDataProvider<T> onGetDataProvider();
	
	public void onResume() {
		if(mDataProvider.readCachData()) {
			refreshUI();
		}else {
			startLoadData();
		}
	}
	
	private void startLoadData() {
		beforeLoadData();
		
	}
	
	protected void beforeLoadData() {}
	
	protected void finishLoadData() {}
	
	/**
	 * 销毁模板
	 */
	public void onDestory() {
		if(mDataProvider != null) {
			mDataProvider.release();
			mDataProvider = null;
		}
	}
}
