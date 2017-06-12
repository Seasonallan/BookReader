package com.lectek.android.lereader.binding.model.bookDetail;

import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnRatingChangedCommand;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.AddBookCommentModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.ui.IView;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.specific.BookCommentDetailActivity;
import com.lectek.android.lereader.utils.ToastUtil;

public class BookDetailPublishCommentViewModelLeyue extends
		BaseLoadDataViewModel {
	private Boolean isCommented;
	private Boolean mIsSurfingReader;
	private PublishCommentAction mPublishCommentAction;
	public StringObservable bETContent = new StringObservable();
	public StringObservable bText = new StringObservable();
	public FloatObservable bRating = new FloatObservable((float) 5);
	public OnClickCommand bBackClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			mPublishCommentAction.finish();
		}

	};
	public OnRatingChangedCommand bRatingBarChangeClick = new OnRatingChangedCommand() {
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			ratingBar.setRating(rating);
		}
	};
	public OnClickCommand bPublishClick = new OnClickCommand() {

		@Override
		public void onClick(View v) {
			String commentContent = bETContent.get();
			if (!TextUtils.isEmpty(commentContent) && !isCommented) {
				boolean isUserLoginSuccess = PreferencesUtil.getInstance(
						getContext()).getIsLogin();
				if (!isUserLoginSuccess) { // 联网且游客身份，必须先登陆
					Intent intent = new Intent(getContext(),
							UserLoginLeYueNewActivity.class);
					getContext().startActivity(intent);
					return;
				}
				if (mIsSurfingReader) {
					mAddBookCommentModel.start(leBookId, commentContent,
							(int) (bRating.get() * 2));
				} else {
					mAddBookCommentModel.start(mBookId, commentContent,
							(int) (bRating.get() * 2));
				}
			} else if (isCommented) {
				ToastUtil.showToast(
						getContext(),
						getResources().getString(
								R.string.result_code_had_commented));
			} else {
				ToastUtil.showToast(getContext(),
						getResources().getString(R.string.comment_not_null));
			}
			
		}

	};

	private AddBookCommentModel mAddBookCommentModel;// 添加评论

	private String leBookId;
	private String mBookId;

	/**
	 * 设置天翼阅读书籍对应的乐阅ID，书签笔记同步使用该ID
	 * 
	 * @param leBookId
	 */
	public void setLeBookId(String leBookId) {
		this.leBookId = leBookId;
	}

	public BookDetailPublishCommentViewModelLeyue(Context context,
			PublishCommentAction mPublishCommentAction, ILoadView loadView,
			String mBookId, String leBookId, boolean mIsSurfingReader) {
		super(context, loadView);
		this.mPublishCommentAction = mPublishCommentAction;
		this.mBookId = mBookId;
		this.leBookId = leBookId;
		this.mIsSurfingReader = mIsSurfingReader;
		isCommented=false;//暂时不知道怎么弄这个参数
		bRating.set((float) 0);
		mAddBookCommentModel = new AddBookCommentModel();
		mAddBookCommentModel.addCallBack(this);
	}

	public interface PublishCommentAction extends IView {
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		boolean isSuccess = Boolean.parseBoolean(result.toString());
		if (isSuccess) {
			LogUtil.i("yyl", "success");
//			isCommented = true;
			ToastUtil.showToast(getContext(),
					getResources().getString(R.string.comment_success));
			getContext().sendBroadcast(new Intent(BookCommentDetailActivity.ACTION_REFREASH_DATA_BROADCAST));
			finish();
		} else {
			LogUtil.i("yyl", "fail");
			ToastUtil.showToast(getContext(),
					getResources().getString(R.string.comment_fault));
		}
		return false;
	}

}
