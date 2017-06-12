package com.lectek.android.lereader.ui.specific;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseViewModel;
import com.lectek.android.lereader.binding.model.bookDetail.BookDetailPublishCommentViewModelLeyue;
import com.lectek.android.lereader.binding.model.bookDetail.BookDetailPublishCommentViewModelLeyue.PublishCommentAction;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.ui.common.BaseActivity;

/**
 * 发表评论界面
 * 
 * @author yyl
 *
 */
public class PublishCommentActivity extends BaseActivity {

	public static final String EXTRA_BOOK_ID = "extra_book_id";
	public static final String EXTRA_LEBOOKID = "extra_leBookId";
	public static final String EXTRA_ISSURFINGREADER = "extra_isSurfingReader";
	private String mBookId;
	private String leBookId;
	BookDetailPublishCommentViewModelLeyue mBookDetailPublishCommentViewModelLeyue;

	public static void openActivity(Context context, String mBookId,
			String leBookId, boolean mIsSurfingReader) {
		Intent intent = new Intent(context, PublishCommentActivity.class);
		intent.putExtra(EXTRA_BOOK_ID, mBookId);
		intent.putExtra(EXTRA_LEBOOKID, leBookId);
		intent.putExtra(EXTRA_ISSURFINGREADER, mIsSurfingReader);
		context.startActivity(intent);
	}

	@Override
	protected View newContentView(Bundle savedInstanceState) {
		mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
		try {
			leBookId = getIntent().getStringExtra(EXTRA_LEBOOKID);
			LogUtil.i("yyl",leBookId);
		} catch (Exception e) {
			LogUtil.i("yyl", "iebookid错了");
			e.printStackTrace();
		}
		boolean mIsSurfingReader = getIntent().getBooleanExtra(
				EXTRA_ISSURFINGREADER, false);
		mBookDetailPublishCommentViewModelLeyue = new BookDetailPublishCommentViewModelLeyue(
				this, new MyPublishCommentAciton(), this, mBookId, leBookId,
				mIsSurfingReader);
		mBookDetailPublishCommentViewModelLeyue.setLeBookId(leBookId);
		return bindView(R.layout.publish_comment_layout, this,
				mBookDetailPublishCommentViewModelLeyue);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleBarEnabled(false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);//一进去就弹出键盘
		
	}

	@Override
	protected boolean onClickBackBtn() {
		LogUtil.i("yyl", "button");
		System.out.println("button");
		// TODO Auto-generated method stub
		return super.onClickBackBtn();
	}

	class MyPublishCommentAciton implements PublishCommentAction {

		@Override
		public void finish() {
			System.out.println("finish");
			this_.finish();
		}

		@Override
		public boolean bindDialogViewModel(Context context,
				BaseViewModel baseViewModel) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getRes(String type) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
