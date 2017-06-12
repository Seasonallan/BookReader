package com.lectek.android.lereader.ui.specific;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentHotViewModel;
import com.lectek.android.lereader.binding.model.user.MyMessageViewModel;
import com.lectek.android.lereader.ui.IRequestResultViewNotify;
import com.lectek.android.lereader.ui.common.BaseNetPanelView;

public class MyMessageView extends BaseNetPanelView implements IRequestResultViewNotify {

	private BookCommentHotViewModel mBookCommentHotViewModel;

	public MyMessageView(Context context) {
		super(context);
	}
    MyMessageViewModel mMyMessageViewModel;
	@Override
	public void onCreate() {
        mMyMessageViewModel = new MyMessageViewModel(getContext(), this,this);
        mMyMessageViewModel.setUserActionListener(new MyMessageViewModel.UserActionListener() {

            @Override
            public void setButtonRightEnable(boolean isEnable) {

            }

            @Override
            public void setTitle(String string) {

            }

            @Override
            public void setNotDataView() {
                //showTipView(IRequestResultViewNotify.tipImg.no_info, false, "", null);
            }

            @Override
            public void cancelEditModel() {
            }
        });
        bindView(R.layout.my_message_layout, this, mMyMessageViewModel);
        mMyMessageViewModel.onStart();
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

    @Override
    public void setTipView(tipImg tipImg, Boolean isNeedOprBtn) {

    }
}
