package com.lectek.android.lereader.binding.model.bookCity;

import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.content.Intent;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.utils.ToastUtil;

public class BookCityCommonBookItem {

	public String bookId;
	public String outbookId;
	public String filePath;
	public boolean embedToBookCity = true;
	public StringObservable bRecommendedCoverUrl = new StringObservable();
	public StringObservable bBookName = new StringObservable();
	public StringObservable bAuthorName = new StringObservable();
	public StringObservable bReadState = new StringObservable();
	public BooleanObservable bReadStateVisibility = new BooleanObservable(false);
	public StringObservable bDecContent = new StringObservable();
	public BooleanObservable bDecContentVisibility = new BooleanObservable(false);
	public StringObservable bAddBookBtn = new StringObservable();
	public IntegerObservable bAddBookTextColor = new IntegerObservable();
	public BooleanObservable bAddBookVisibility = new BooleanObservable(false);
	public IntegerObservable bAddBtnBackGround = new IntegerObservable();
	public BooleanObservable bRedotVisibility = new BooleanObservable(false);
	public BooleanObservable bReadedVisibility = new BooleanObservable(false);
	public StringObservable bReadedText= new StringObservable();
	public IntegerObservable bTopThreeIV = new IntegerObservable();
	public BooleanObservable bTopThreeIVVisibility = new BooleanObservable(false);
	
	public final OnClickCommand bAddBookClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			DownloadInfo downloadInfo = new DownloadInfo();
			downloadInfo.authorName = bAuthorName.get();
	        downloadInfo.contentID = bookId;
	        downloadInfo.contentName = bBookName.get();
	        downloadInfo.logoUrl =bRecommendedCoverUrl.get();
	        downloadInfo.timestamp = System.currentTimeMillis();
	        downloadInfo.url = filePath;
	        downloadInfo.state = DownloadAPI.STATE_ONLINE;
	        
	        DownloadPresenterLeyue.addBookDownload(downloadInfo);
	        bAddBookBtn.set(v.getContext().getString(R.string.book_in_bookshelf));
	        //getResources().getColor(R.color.common_17)
			bAddBookTextColor.set(R.color.common_17);
			bAddBtnBackGround.set(R.color.white);
	        v.getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
		}
	};
}
