package com.lectek.android.lereader.ui.specific;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.ITitleBar;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentDetailViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentDetailViewModel.BookCommentDetailAciton;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;

/**
 * 评论详情
 * @author Shizq
 * @date 2014-4-24
 *
 */
public class BookCommentDetailActivity extends BaseActivity implements BookCommentDetailAciton{
	
	public static final String ACTION_REFREASH_DATA_BROADCAST = "action_refreash_data_broadcast";
	
	public static final String EXTRA_COMMENT_ID = "extra_comment_id";
	public static final String EXTRA_COMMENT_USERNAME = "extra_comment_username";
	
	private BookCommentDetailViewModel mBookCommentDetailViewModel;
	private TextView zanPlusOne;
	
	public static void openActivity(Context context, int commentId,String commentName){
		Intent intent = new Intent(context, BookCommentDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_COMMENT_ID, commentId);
		intent.putExtra(EXTRA_COMMENT_USERNAME, commentName);
		context.startActivity(intent);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		int commentId = getIntent().getIntExtra(EXTRA_COMMENT_ID, 0);
		String commentUserName=getIntent().getStringExtra(EXTRA_COMMENT_USERNAME);
		
		mBookCommentDetailViewModel = new BookCommentDetailViewModel(this_, this, commentId,commentUserName,this);
		View view= bindView(R.layout.book_comment_detail_layout, mBookCommentDetailViewModel);
		zanPlusOne=(TextView) view.findViewById(R.id.zanPlusOne);
		return view;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBookCommentDetailViewModel.onStart();
		setTitleContent(getResources().getString(R.string.title_comment_detail));
		registerReceiver(mBroadcastReceiver, new IntentFilter(ACTION_REFREASH_DATA_BROADCAST));
	}
	
	@Override
	protected void onDestroy() {
		if(mBroadcastReceiver != null){
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
		if(mBookCommentDetailViewModel.checkNeedUpdateCommentInfo()){
			sendBroadcast(new Intent(BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
		}
		super.onDestroy();
	}

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			mBookCommentDetailViewModel.onStart();
		}
	};

	@Override
	public void exceptionHandle(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optToast(String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startZanAnimation() {
		Animation animation=AnimationUtils.loadAnimation(this_, R.anim.zan_anim);
		zanPlusOne.startAnimation(animation);
	}

}
