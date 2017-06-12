package com.lectek.android.lereader.ui.specific;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.animation.OpenBookAnimManagement;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.DialogUtil;

public class BookShelfActivity extends BaseActivity {

	private BookShelfViewNotBinding mBookShelfView;
	private PopupWindow mImportPopupWin;	// 导入本地图书和wifi传书popupwindow
	
	public static final int REQUEST_CODE_SEARCH = 222;
	
	private BookShelfAction mBookShelfAction;
	
	/**
	 * 导入本地图书
	 */
	public final OnClickCommand bImportLocalBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mImportPopupWin != null) {
				mImportPopupWin.dismiss();
			}

            if(!FileUtil.isSDcardExist()) {
                DialogUtil.oneConfirmBtnDialog(this_, null, getString(R.string.import_local_no_sdcard), -1, null);
                return;
            }
			ActivityChannels.gotoImportLocalActivity(this_);
		}
	};

	/**
	 * wifi传书
	 */
	public final OnClickCommand bWifiTransferBtnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(mImportPopupWin != null) {
				mImportPopupWin.dismiss();
			}
            if(!FileUtil.isSDcardExist()) {
                DialogUtil.oneConfirmBtnDialog(this_, null, getString(R.string.import_wifi_no_sdcard), -1, null);
                return;
            }
			ActivityChannels.gotoWifiTransferActivity(this_);
		}
	};
	
	public final OnClickCommand bSortBookClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(mImportPopupWin != null) {
				mImportPopupWin.dismiss();
			}
			if(mBookShelfAction != null){
				mBookShelfAction.onSortBook();
			}
			
		}
	};
	
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mBookShelfView = new BookShelfViewNotBinding(this);
		return mBookShelfView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
		mBookShelfView.onCreate();
		
		mBookShelfView.findViewById(R.id.goto_book_city).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(this_, SlideActivityGroup.class);
				intent.putExtra(SlideActivityGroup.Extra_Switch_UI, SlideActivityGroup.BOOK_CITY);
				this_.startActivity(intent);
			}
		});
		
		mBookShelfView.findViewById(R.id.goto_personal_center).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(this_, SlideActivityGroup.class);
				intent.putExtra(SlideActivityGroup.Extra_Switch_UI, SlideActivityGroup.PERSONAL_CENTER);
				this_.startActivity(intent);
			}
		});
		
		mBookShelfView.findViewById(R.id.more_action).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showImportPopWin();
			}
		});
	}
	
	/**
	 * 显示本地导入和wifi传书的窗口
	 */
	private void showImportPopWin() {
		View view = bindView(R.layout.bookshelf_import_pop_lay, this);
		mImportPopupWin = new PopupWindow(view, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		mImportPopupWin.setFocusable(true);
		mImportPopupWin.setOutsideTouchable(true);
		mImportPopupWin.setBackgroundDrawable(new BitmapDrawable());
		mImportPopupWin.showAsDropDown(mBookShelfView.findViewById(R.id.more_action), 0, DimensionsUtil.dip2px(8, this_));	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_SEARCH){
			if(mBookShelfAction != null){
				mBookShelfAction.onSearchFinish();
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		OpenBookAnimManagement.getInstance().stopBookAnim();
		super.onDestroy();
	}


	public interface BookShelfAction{
		public void onSearchFinish();
		public void onSortBook();
	}

	public void setBookShelfAction(BookShelfAction mBookShelfAction) {
		this.mBookShelfAction = mBookShelfAction;
	}
	
	
}
