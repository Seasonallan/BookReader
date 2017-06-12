package com.lectek.android.lereader.ui.basereader_leyue.digests;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.digest.BookDigestsDB;

public class TextSelectHandler extends AbsTextSelectHandler {
	private Bitmap mTopSelectCursorBitmap;
	private Bitmap mBottomSelectCursorBitmap;
	public TextSelectHandler(int width, int height) {
		super(width, height);
	}

	@Override
	protected Context getContext() {
		return BaseApplication.getInstance();
	}

	@Override
	protected Bitmap getTopSelectCursorBitmap() {
		if(mTopSelectCursorBitmap == null){
			Resources resources = getContext().getResources();
			mTopSelectCursorBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_xuanqu_shang);
		}
		return mTopSelectCursorBitmap;
	}

	@Override
	protected Bitmap getBottomSelectCursorBitmap() {
		if(mBottomSelectCursorBitmap == null){
			Resources resources = getContext().getResources();
			mBottomSelectCursorBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_xuanqu_xia);
		}
		return mBottomSelectCursorBitmap;
	}

	@Override
	protected void loadData(String contentId) {
		if(contentId == null){
			return;
		}
	
		ArrayList<BookDigests> bookDigestsList =  getBookDigestsDB().getListBookDigests(contentId);
		if(bookDigestsList == null){
			return;
		}
		ArrayList<BookDigests> chaptersBookDigestsList = null;
		
		for(BookDigests bookDigests : bookDigestsList){
			chaptersBookDigestsList = mSelectData.getBookDigestsList( bookDigests.getChaptersId() );
			
			if( chaptersBookDigestsList == null ){
				chaptersBookDigestsList = new ArrayList<BookDigests>();
				mSelectData.setBookDigestsList( bookDigests.getChaptersId(), chaptersBookDigestsList);
			}
			chaptersBookDigestsList.add(bookDigests);
		}
	}
	
	@Override
	public void createOrUpdateBookDigests(BookDigests bookDigests) {
		if (getBookDigestsDB().hasBookDigests(bookDigests)) {
			getBookDigestsDB().updateBookDigest(bookDigests);
			mSelectData.updateBookDigests(bookDigests);
			reLoadView();
		}else{
			long id = getBookDigestsDB().saveBookDigest(bookDigests);
			if(id != -1){ 
				mSelectData.addBookDigests(bookDigests);
				reLoadView();
			}
		}
	} 
	
	@Override
	public void deleteBookDigests(BookDigests bookDigests) {
		getBookDigestsDB().deleteBookDigest(bookDigests);
		mSelectData.removeBookDigests(bookDigests);
		reLoadView();
	}

	@Override
	public void deleteBookDigestsAll(ArrayList<BookDigests> bookDigestsList) {
		getBookDigestsDB().deleteBookDigestAll(bookDigestsList);
		mSelectData.removeBookDigestsAll(bookDigestsList);
		reLoadView();
	}
	
	private BookDigestsDB getBookDigestsDB(){
		return BookDigestsDB.getInstance();
	}

	@Override
	protected int bookDigestDefaultColor() {
		return BookDigestsRemarksDialog.BLUE;
	}
}

