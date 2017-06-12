package com.lectek.android.lereader.ui.basereader_leyue.digests;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.ui.basereader_leyue.digests.AbsTextSelectHandler.ISelectorListener;

public class SelectorControlView implements ISelectorListener{
	
	private BookDigestsPopWin mBookDigestsPopWin;
		
	private View mReadView;
	
	private Activity mActivity;
	
	public SelectorControlView(View readView , Activity activity){
		mReadView = readView;
		mActivity = activity;
	}
	
	@Override
	public void onInit(float x, float y, Bitmap bitmap,
			AbsTextSelectHandler textSelectHandler) {
		
		if(mBookDigestsPopWin == null){
			View contentView = mActivity.getLayoutInflater().inflate(R.layout.book_digests_view_lay, null);
			mBookDigestsPopWin = new BookDigestsPopWin(contentView,mReadView
					, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,mActivity, textSelectHandler);
			
		}
		mBookDigestsPopWin.show(BookDigestsPopWin.VIEW_TYPE_MAGNIFIER, x, y, bitmap);
	}
		
	@Override
	public void onChange(float x, float y, Bitmap bitmap,
			AbsTextSelectHandler textSelectHandler) {
		if(mBookDigestsPopWin == null){
			return;
		}
		mBookDigestsPopWin.show(BookDigestsPopWin.VIEW_TYPE_MAGNIFIER, x, y, bitmap);
		mBookDigestsPopWin.setmTextSelectHandler(textSelectHandler);
		
	}

	@Override
	public void onPause(float x, float y,AbsTextSelectHandler textSelectHandler) {
		if(mBookDigestsPopWin == null){
			return;
		}
		mBookDigestsPopWin.show(BookDigestsPopWin.VIEW_TYPE_MENU_1, x, y);
		mBookDigestsPopWin.setmTextSelectHandler(textSelectHandler);
		
	}

	@Override
	public void onStop(AbsTextSelectHandler textSelectHandler) {
		if(mBookDigestsPopWin == null){
			return;
		}
		if(mBookDigestsPopWin != null){
			mBookDigestsPopWin.dismiss();
		}
//		textSelectHandler.noticeDataChanges();
	}

	@Override
	public void onOpenEditView(float x, float y,BookDigests bookDigests,
			AbsTextSelectHandler textSelectHandler) {
		
		View contentView = mActivity.getLayoutInflater().inflate(R.layout.book_digests_view_lay, null);
		mBookDigestsPopWin = new BookDigestsPopWin(contentView,mReadView
				, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,mActivity, textSelectHandler);
		mBookDigestsPopWin.setmBookDigests(bookDigests);
		mBookDigestsPopWin.setTouchable(true);
		mBookDigestsPopWin.show(BookDigestsPopWin.VIEW_TYPE_MENU_2, x, y);
		
	}

	@Override
	public void onCloseEditView(AbsTextSelectHandler textSelectHandler) {
		if(mBookDigestsPopWin == null){
			return;
		}
		mBookDigestsPopWin.dismiss();
	}
	
}
