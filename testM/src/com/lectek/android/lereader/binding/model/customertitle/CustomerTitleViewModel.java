package com.lectek.android.lereader.binding.model.customertitle;

import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.view.View;

import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.ui.IView;

public class CustomerTitleViewModel extends BaseViewModel {
	
	public final StringObservable bRightBtnContent = new StringObservable();
	public final StringObservable bCustomerTitleContent = new StringObservable();
	public final IntegerObservable bLeftLineVisible = new IntegerObservable(View.VISIBLE);
	
	private OnClickCallback mLeftClickCallback;
	private OnClickCallback mRightClickCallback;
	
	public void setOnLeftClickCallback(OnClickCallback leftClickCallback) {
		mLeftClickCallback = leftClickCallback;
	}
	
	public void setOnRightClickCallback(OnClickCallback rightClickCallback) {
		mRightClickCallback = rightClickCallback;
	}

	public CustomerTitleViewModel(Context context, IView loadView) {
		super(context, loadView);
	}
	
	public OnClickCommand bLeftBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mLeftClickCallback != null) {
				mLeftClickCallback.onClick(v);
			}
		}
	};
	
	public OnClickCommand bRightBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mRightClickCallback != null) {
				mRightClickCallback.onClick(v);
			}
		}
	};
	
	/**
	 * 设置右边按钮的文字
	 * @param text
	 */
	public void setRightBtnText(String text) {
		bRightBtnContent.set(text);
	}
	/**
	 * 设置标题
	 * @param text
	 */
	public void setTitleContent(String text) {
		bCustomerTitleContent.set(text);
	}
	/**
	 * 设置左边竖线是否显示
	 * @param visibility
	 */
	public void setLeftLineVisibility(int visibility) {
		bLeftLineVisible.set(visibility);
	}
	
	public interface OnClickCallback {
		public void onClick(View v);
	}

}
