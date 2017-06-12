package com.lectek.android.lereader.binding.model.imports;

import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.data.FileData;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.transfer.WebServiceModule;
import com.lectek.android.transfer.util.Constants;

public class WifiTransferViewModel extends BaseViewModel{
	
	private static final int INVALID = 1;
	private static final int CONNECTED = 2;
	private static final int UPLOADING = 3;
	private static final String UPLOAD_DIR = com.lectek.android.lereader.utils.Constants.WIFI_STORED_DIRECTORY;
	
	private int mSuccessCount = 0,mFailCount=0;
	private boolean isTransfering = false;
	private WebServiceModule mWebServiceModule;
	
	public final IntegerObservable bTransferBottomConnectV = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bTransferBottomReadyV = new IntegerObservable(View.VISIBLE);
	public final IntegerObservable bTransferBottomReadyPreV = new IntegerObservable(View.GONE);
	public final IntegerObservable bTransferBottomReadyRunV = new IntegerObservable(View.GONE);
	public final IntegerObservable bTransferProgress = new IntegerObservable(0);
	
	public final StringObservable bTransferIpportT = new StringObservable(getString(R.string.import_connecting));
	public final StringObservable bTransferDescT = new StringObservable();
	public final StringObservable bTransferResT = new StringObservable();

	private ImportBookModel mImportBookModel;
	private boolean isNeedNotice;

	public WifiTransferViewModel(Context context, IView loadView) {
		super(context, loadView);
		Constants.UPLOAD_DIR = UPLOAD_DIR;
		mImportBookModel = new ImportBookModel();
	}
	
	private void onHttpStatusChange(int status) {
		switch (status) {
		case INVALID:
			mSuccessCount = 0;
			mFailCount=0;
			bTransferBottomConnectV.set(View.VISIBLE);
			bTransferBottomReadyV.set(View.GONE);
			bTransferBottomReadyPreV.set(View.GONE);
			bTransferBottomReadyRunV.set(View.GONE);
			break;
		case CONNECTED:
			bTransferBottomConnectV.set(View.GONE);
			bTransferBottomReadyV.set(View.VISIBLE);
			bTransferBottomReadyPreV.set(View.VISIBLE);
			bTransferBottomReadyRunV.set(View.GONE);
			break;
		case UPLOADING:
			bTransferBottomConnectV.set(View.GONE);
			bTransferBottomReadyV.set(View.VISIBLE);
			bTransferBottomReadyPreV.set(View.GONE);
			bTransferBottomReadyRunV.set(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onStart(Bundle savedInstanceState) {
		super.onStart(savedInstanceState);
		onHttpStatusChange(INVALID);
		mWebServiceModule = new WebServiceModule((Activity)getContext()) {
			@Override
			protected void onServiceStopped() {
				onHttpStatusChange(INVALID);
				String coning = getResources().getString(R.string.import_connecting); 
				bTransferIpportT.set(coning);
				isTransfering = false;
			}
			@Override
			protected void onServiceStarted(String ip) {
				onHttpStatusChange(INVALID);
				bTransferIpportT.set(ip);
			}
			@Override
			protected void onFileUploadError(String fileName, String error) {
				mFailCount++;
				bTransferProgress.set(0);
				bTransferDescT.set(getResources().getString(R.string.transfer_error, fileName));
				bTransferResT.set(getResources().getString(R.string.transfer_res, mSuccessCount+mFailCount, mFailCount));
			}
			@Override
			protected void onFileUpload(String fileName, int percent) {
				isTransfering = true;
				onHttpStatusChange(UPLOADING);
				if (!TextUtils.isEmpty(fileName) && percent > 0) {
					bTransferDescT.set(getResources().getString(R.string.transfer_ing, fileName));
					bTransferProgress.set(percent);
				}
			}
			
			@Override
			protected void onFileDeleted(String fileName) {
				ToastUtil.showToast(getContext(), getResources().getString(R.string.transfer_delete_file, fileName));
				mImportBookModel.deleteBook(new FileData(UPLOAD_DIR+ fileName, fileName, ""));
				isNeedNotice = true;
			}
			
			@Override
			protected void onFileAdded(String fileName) {
				mSuccessCount++;
				bTransferProgress.set(100);
				bTransferDescT.set(getResources().getString(R.string.transfer_end, fileName));
				bTransferResT.set(getResources().getString(R.string.transfer_res, mSuccessCount + mFailCount, mFailCount));
				mImportBookModel.addBook(new FileData(UPLOAD_DIR+ fileName, fileName, ""));
				isNeedNotice = true;
			}
			
			@Override
			protected void onComputerConnected() {
				if(!isTransfering)
					onHttpStatusChange(CONNECTED);
			}
		};
		mWebServiceModule.onCreate(savedInstanceState);
	}
	
	@Override
	public void onRelease() {
		super.onRelease();
		mWebServiceModule.onDestroy();
		if(isNeedNotice){
			getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
		}
	}
}
