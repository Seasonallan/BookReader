package com.lectek.android.lereader.binding.model.common;

import gueei.binding.IObservable;
import gueei.binding.Observer;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.ObjectObservable;
import gueei.binding.observables.StringObservable;

import java.util.Collection;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseDialogViewModel;
import com.lectek.android.lereader.binding.model.BaseViewModel;

public class CommonDialogViewModel extends BaseDialogViewModel {
	public final IntegerObservable bBtnLayoutVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bLeftBtnVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bRightBtnVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bTitleVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bStrContentVisibility = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bContentViewRes = new IntegerObservable(0);
	public final StringObservable bLeftBtnStr = new StringObservable();
	public final StringObservable bRightBtnStr = new StringObservable();
	public final StringObservable bContentStr = new StringObservable();
	public final StringObservable bTitleStr = new StringObservable();
	public final ObjectObservable bContentViewModel = new ObjectObservable();
	
	public final OnClickCommand bLeftBtnClickCommand = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mLeftBtnClickCallBack != null){
				mLeftBtnClickCallBack.onClick(v,CommonDialogViewModel.this);
			}
		}
	};
	
	public final OnClickCommand bRightBtnClickCommand = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mRightBtnClickCallBack != null){
				mRightBtnClickCallBack.onClick(v,CommonDialogViewModel.this);
			}
		}
	};
	
	private OnBtnClickCallBack mLeftBtnClickCallBack;
	private OnBtnClickCallBack mRightBtnClickCallBack;

	public CommonDialogViewModel(Context context,String title,String content
			,String letfBtnStr,String rightBtnStr,OnBtnClickCallBack leftBtnClick,OnBtnClickCallBack rightBtnClick) {
		super(context);
		onInit();
		if(TextUtils.isEmpty(title)){
			bTitleVisibility.set(View.GONE);
		}
		setBtn(letfBtnStr, rightBtnStr, leftBtnClick, rightBtnClick);
		bTitleStr.set(title);
		bContentStr.set(content);
	}
	
	public CommonDialogViewModel(Context context,String title,int contentRes,BaseViewModel contentViewModel
			,String letfBtnStr,String rightBtnStr,OnBtnClickCallBack leftBtnClick,OnBtnClickCallBack rightBtnClick) {
		super(context);
		onInit();
		if(TextUtils.isEmpty(title)){
			bTitleVisibility.set(View.GONE);
		}
		setBtn(letfBtnStr, rightBtnStr, leftBtnClick, rightBtnClick);
		bTitleStr.set(title);
		bContentViewRes.set(contentRes);
		bContentViewModel.set(contentViewModel);
	}
	
	private void setBtn(String letfBtnStr,String rightBtnStr,OnBtnClickCallBack leftBtnClick,OnBtnClickCallBack rightBtnClick){
		if(leftBtnClick == null){
			bLeftBtnVisibility.set(View.GONE);
		}
		if(rightBtnClick == null){
			bRightBtnVisibility.set(View.GONE);
			if(leftBtnClick == null){
				bBtnLayoutVisibility.set(View.GONE);
			}
		}
		mLeftBtnClickCallBack = leftBtnClick;
		mRightBtnClickCallBack = rightBtnClick;
		bLeftBtnStr.set(letfBtnStr);
		bRightBtnStr.set(rightBtnStr);
	}
	
	private void onInit(){
		bContentViewRes.subscribe(new Observer() {
			@Override
			public void onPropertyChanged(IObservable<?> prop,
					Collection<Object> initiators) {
				Integer integer = ((IntegerObservable)prop).get();
				if(integer == null || integer == 0){
					bStrContentVisibility.set(View.VISIBLE);
				}else{
					bStrContentVisibility.set(View.GONE);
				}
			}
		});
	}

	public OnBtnClickCallBack getLeftBtnClickCallBack() {
		return mLeftBtnClickCallBack;
	}

	public OnBtnClickCallBack getRightBtnClickCallBack() {
		return mRightBtnClickCallBack;
	}
}
