package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnRatingChangedCommand;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.utils.ToastUtil;

public class BookCommentViewModel1 extends PagingLoadViewModel {
	
	public final ArrayListObservable<CommentItem> bItems = new ArrayListObservable<CommentItem>(CommentItem.class);
	public final StringObservable bCommentEditText = new StringObservable();
	public final BooleanObservable bEditExtraVisibility = new BooleanObservable(false);
	public final BooleanObservable bNoCommentVisibility = new BooleanObservable(false);
	public final IntegerObservable bRatingValue = new IntegerObservable(10);
	
	private BookCommentListPagingloadModel mBookCommentListModel;
	private AddBookCommentModel mAddBookCommentModel;
	private String mBookId;
	private boolean isCommented;
	private UserActionListener mUserActionListener;

	public BookCommentViewModel1(Context context, INetLoadView loadView, String bookId) {
		super(context, loadView);
		mBookId = bookId;
		mBookCommentListModel = new BookCommentListPagingloadModel();
		mBookCommentListModel.setmBookId(mBookId);
		mBookCommentListModel.addCallBack(this);
		mAddBookCommentModel = new AddBookCommentModel();
		mAddBookCommentModel.addCallBack(this);
	}
	
	public OnClickCommand bEditTextClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
//			bEditExtraVisibility.set(true);
		}
	};
	
	public OnClickCommand bAddCommentClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(!TextUtils.isEmpty(bCommentEditText.get()) && !isCommented){
				
				boolean isUserLoginSuccess = PreferencesUtil.getInstance(getContext()).getIsLogin();
				if(!isUserLoginSuccess) {	//联网且游客身份，必须先登陆
					Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
					getContext().startActivity(intent);
					return;
				}
				
				if(mUserActionListener != null){
					mUserActionListener.hideSoftKeyboard();
				}
				
				mAddBookCommentModel.start(mBookId, bCommentEditText.get(), bRatingValue.get());
			}else if(isCommented){
				ToastUtil.showToast(getContext(), getResources().getString(R.string.result_code_had_commented));
			}else{
				ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_not_null));
			}
		}
	};
	
	public OnRatingChangedCommand bRatingBarChangeListener = new OnRatingChangedCommand() {
		
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			bRatingValue.set((int)(rating*2));
		}
	};

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(mAddBookCommentModel.getTag().equals(tag)){
			ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_fault));
			hideLoadView();
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if(mBookCommentListModel.getTag().equals(tag)){
				ArrayList<BookCommentInfo> list = (ArrayList<BookCommentInfo>)result;
				loadCommentItems(list);
				if(list.size() > 0){
					bNoCommentVisibility.set(false);
				}else{
					bNoCommentVisibility.set(true);
				}
			}
			if(mAddBookCommentModel.getTag().equals(tag)){
				boolean isSuccess = Boolean.parseBoolean(result.toString());
				if(isSuccess){
					bCommentEditText.set("");
					isCommented = true;
					ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_success));
				}else{
					ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_fault));
				}
			}
			hideLoadView();
		}
		return false;
	}

	@Override
	public void hideLoadView() {
		super.hideLoadView();
		if(mUserActionListener != null){
			mUserActionListener.loadCompleted();
		}
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void loadCommentItems(ArrayList<BookCommentInfo> list){
		for(int i = 0; i < list.size();i++){
			BookCommentInfo info = list.get(i);
			CommentItem item = new CommentItem();
			item.bUserNameText.set(info.getUsername());
			item.bTimeText.set(DateUtil.getFormateTimeString(info.getCreateTime()));
			item.bCommentText.set(info.getContent());
			item.bRatingItemValue.set(info.getStarsNum()/2f);
			bItems.add(item);
		}
		
//		for(int i = 0; i < 10;i++){
//			CommentItem item = new CommentItem();
//			item.bUserNameText.set("title"+i);
//			item.bTimeText.set("time");
//			item.bCommentText.set("content content content content content content content content");
//			bItems.add(item);
//		}
	}
	
	public void setEditExtraVisibility(boolean visibility){
		bEditExtraVisibility.set(visibility);
	}
	
	public static class CommentItem{
		public StringObservable bUserNameText = new StringObservable();
		public StringObservable bTimeText = new StringObservable();
		public StringObservable bCommentText = new StringObservable();
		public FloatObservable bRatingItemValue = new FloatObservable();
	}
	
	public static interface UserActionListener{
		public void loadCompleted();
		public void hideSoftKeyboard();
	}
	
	public void setUserActionListener(UserActionListener listener){
		mUserActionListener = listener;
	}

	@Override
	public void onStart() {
		mBookCommentListModel.loadPage();
	}

	@Override
	protected PagingLoadModel<?> getPagingLoadModel() {
		return mBookCommentListModel;
	}

}
