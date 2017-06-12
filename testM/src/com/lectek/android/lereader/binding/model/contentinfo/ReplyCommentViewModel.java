package com.lectek.android.lereader.binding.model.contentinfo;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.BookCommentDetailActivity;
import com.lectek.android.lereader.utils.ToastUtil;

import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ReplyCommentViewModel extends BaseLoadNetDataViewModel {
	
	public final StringObservable bReplyText = new StringObservable();
	public   StringObservable bTitle = new StringObservable();
	private int mCommentId;
	private String commentUserName;
	private ReplyCommentModel mReplyCommentModel;

	public ReplyCommentViewModel(Context context, INetLoadView loadView, int commentId,String commentUserName) {
		super(context, loadView);
		mCommentId = commentId;
		this.commentUserName=commentUserName;
		bTitle.set("回复"+commentUserName);
		mReplyCommentModel = new ReplyCommentModel();
		mReplyCommentModel.addCallBack(this);
	}
	
	public final OnClickCommand bBackClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			finish();
		}
	};
	
	public final OnClickCommand bPublishClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(!TextUtils.isEmpty(bReplyText.get())){
				String replyContent = bReplyText.get();
				replyContent = replyContent.replace("&", "");
				replyContent = replyContent.trim();
				if (!TextUtils.isEmpty(replyContent)){
					mReplyCommentModel.start(mCommentId, replyContent);
					return;
				}
			}
			ToastUtil.showToast(getContext(),
					getResources().getString(R.string.comment_not_null));
		}
	};

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(mReplyCommentModel.getTag().equals(tag)){
			ToastUtil.showToast(
					getContext(),
					getResources().getString(
							R.string.comment_reply_fail));
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if (mReplyCommentModel.getTag().equals(tag)) {
				boolean isSuccess = Boolean.parseBoolean(result.toString());
				if (isSuccess) {
					bReplyText.set("");
					ToastUtil.showToast(
							getContext(),
							getResources().getString(
									R.string.comment_reply_success));
					getContext().sendBroadcast(new Intent(BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
					finish();
				} else {
					ToastUtil.showToast(
							getContext(),
							getResources().getString(
									R.string.comment_reply_fail));
				}
			}
		}
		hideLoadView();
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}

}
