package com.lectek.android.lereader.ui.specific;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.ReplyCommentViewModel;
import com.lectek.android.lereader.ui.common.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class ReplyCommentActivity extends BaseActivity {
	
	public static final String EXTRA_COMMENT_ID = "extra_comment_id";
	public static final String EXTRA_COMMENT_USERNAME = "extra_comment_username";
	
	private ReplyCommentViewModel mReplyCommentViewModel;
	
	public static void openActivity(Context context, int commentId,String CommentUserName){
		Intent intent = new Intent(context, ReplyCommentActivity.class);
		intent.putExtra(EXTRA_COMMENT_ID, commentId);
		intent.putExtra(EXTRA_COMMENT_USERNAME, CommentUserName);
		context.startActivity(intent);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		int commentId = getIntent().getIntExtra(EXTRA_COMMENT_ID, 0);
		String commentUserName=getIntent().getStringExtra(EXTRA_COMMENT_USERNAME);
		mReplyCommentViewModel = new ReplyCommentViewModel(this_, this,commentId,commentUserName);
		setTitleBarEnabled(false);
		return bindView(R.layout.yyl_reply_comment_layout, mReplyCommentViewModel);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//一进去就弹出键盘
	}
	
}
