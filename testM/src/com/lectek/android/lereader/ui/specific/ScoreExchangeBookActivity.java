package com.lectek.android.lereader.ui.specific;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreExchangeBookViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreExchangeBookViewModel.RechargeUserAction;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.ui.common.BaseActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.DialogUtil;

/**
 * @description
	积分兑书
 * @author chendt
 * @date 2013-12-27
 * @Version 1.0
 * @SEE ScoreExchangeBookViewModel
 */
public class ScoreExchangeBookActivity extends BaseActivity implements RechargeUserAction{
	
	private static final String EXTRA_BOOK_INFO = "extra_book_info";
	public static final int TAG_RELOAD = 0x10001;
	public static final int TAG_BUY_SUCCESS = 0x10002;
	
	private ScoreExchangeBookViewModel mScoreExchangeBookViewModel;
	private ContentInfoLeyue mContenInfo;
	
	public static void openActivity(Activity context, ContentInfoLeyue info){
		Intent intent = new Intent(context, ScoreExchangeBookActivity.class);
		intent.putExtra(EXTRA_BOOK_INFO, info);
		context.startActivityForResult(intent, ContentInfoActivityLeyue.REQUEST_CODE_EXCHANGE_BOOK);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		setTitleContent(getString(R.string.text_free_exchange_book));
		Intent intent = getIntent();
		mContenInfo = (ContentInfoLeyue) intent.getSerializableExtra(EXTRA_BOOK_INFO);
		mScoreExchangeBookViewModel = new ScoreExchangeBookViewModel(this, this,this,mContenInfo);
		mScoreExchangeBookViewModel.onStart();
		registerReceiver(mTipDialogBroadcastReceiver, 
				new IntentFilter(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE));
		registerReceiver(mTipDialogBroadcastReceiver, 
				new IntentFilter(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW));
		return bindView(R.layout.recharge_point_layout, mScoreExchangeBookViewModel);
	}

	@Override
	public void exchangeSuccess() {
		setResult(TAG_BUY_SUCCESS);
		sendBroadcast(new Intent(AppBroadcast.ACTION_BUY_SUCCEED));
		finish();
	}

	@Override
	public void reloadBookInfo() {
		setResult(TAG_RELOAD);
		finish();
	}

	@Override
	public void exceptionHandle(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showRetryImgView() {
		setTipView(tipImg.request_fail, true);
	}
	
	private BroadcastReceiver mTipDialogBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null){
				return;
			}
			final String soureName = intent.getStringExtra(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
			if(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE.equals(intent.getAction())
					&& CommonUtil.isOnCurrentActivityView(ScoreExchangeBookActivity.this)){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {	
						DialogUtil.commonConfirmDialog(ScoreExchangeBookActivity.this, 
								ScoreExchangeBookActivity.this.getString(R.string.share_tip), 
								ScoreExchangeBookActivity.this.getString(R.string.share_book_repeat_tip,soureName), 
								 R.string.share_enter_tip,R.string.share_exit_tip,new DialogUtil.ConfirmListener() {
							
									@Override
									public void onClick(View v) {
										ScoreRuleActivity.gotoScoreRuleActivity(ScoreExchangeBookActivity.this);
									}
								},null).show();
					}
				});
			}else if (ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW.equals(intent.getAction())) {
				if (mScoreExchangeBookViewModel!=null) {
					String totalScore = intent.getStringExtra(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW);
					mScoreExchangeBookViewModel.updateViewByScore(totalScore);
				}
				
			}
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mScoreExchangeBookViewModel!=null) {
			mScoreExchangeBookViewModel.activityResult(requestCode, resultCode, data);
		}
	};
	
	
	protected void onDestroy() {
		super.onDestroy();
		mScoreExchangeBookViewModel.finish();
		if (mTipDialogBroadcastReceiver!=null) {
			unregisterReceiver(mTipDialogBroadcastReceiver);
			mTipDialogBroadcastReceiver = null;
		}
	};
}
