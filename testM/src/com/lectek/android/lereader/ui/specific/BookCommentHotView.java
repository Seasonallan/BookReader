package com.lectek.android.lereader.ui.specific;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentHotViewModel;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BookCommentHotView extends BaseNetPanelView {
	
	private BookCommentHotViewModel mBookCommentHotViewModel;
	private String mBookId;

	public BookCommentHotView(Context context, String bookId) {
		super(context);
		mBookId = bookId;
	}

	@Override
	public void onCreate() {
		mBookCommentHotViewModel = new BookCommentHotViewModel(getContext(), this, mBookId);
		bindView(R.layout.book_comment_list_layout, this, mBookCommentHotViewModel);
		mBookCommentHotViewModel.onStart();
		getContext().registerReceiver(mBroadcastReceiver, new IntentFilter(BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
	}

	@Override
	public void onDestroy() {
		if(mBroadcastReceiver != null){
			getContext().unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
	}
	
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			mBookCommentHotViewModel.onStart();
		}
	};

}
