package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.common.PagingLoadModel;
import com.lectek.android.lereader.binding.model.common.PagingLoadViewModel;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.BookCommentDetailActivity;
import com.lectek.android.lereader.utils.CommonUtil;

public class BookCommentLatestViewModel extends PagingLoadViewModel {
	
	public final ArrayListObservable<CommentItem> bItems = new ArrayListObservable<CommentItem>(CommentItem.class);
	public final StringObservable bCommentEditText = new StringObservable();
	public final BooleanObservable bEditExtraVisibility = new BooleanObservable(false);
	public final BooleanObservable bNoCommentVisibility = new BooleanObservable(false);
	public final IntegerObservable bRatingValue = new IntegerObservable(10);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.GONE);
	
	private BookCommentListPagingloadModel mBookCommentListModel;
//	private AddBookCommentModel mAddBookCommentModel;
	private String mBookId;
	private boolean isCommented;
	private UserActionListener mUserActionListener;
	
	public final FooterViewModel bFooterViewModel = new FooterViewModel();

	public BookCommentLatestViewModel(Context context, INetLoadView loadView, String bookId) {
		super(context, loadView);
		mBookId = bookId;
		mBookCommentListModel = new BookCommentListPagingloadModel();
		mBookCommentListModel.setmBookId(mBookId);
		mBookCommentListModel.addCallBack(this);
//		mAddBookCommentModel = new AddBookCommentModel();
//		mAddBookCommentModel.addCallBack(this);
	}
	
//	public OnClickCommand bEditTextClick = new OnClickCommand() {
//		
//		@Override
//		public void onClick(View v) {
////			bEditExtraVisibility.set(true);
//		}
//	};
//	
//	public OnClickCommand bAddCommentClick = new OnClickCommand() {
//		
//		@Override
//		public void onClick(View v) {
//			if(!TextUtils.isEmpty(bCommentEditText.get()) && !isCommented){
//				
//				boolean isUserLoginSuccess = PreferencesUtil.getInstance(getContext()).getIsLogin();
//				if(!isUserLoginSuccess) {	//联网且游客身份，必须先登陆
//					Intent intent = new Intent(getContext(), UserLoginActivityLeYue.class);
//					getContext().startActivity(intent);
//					return;
//				}
//				
//				if(mUserActionListener != null){
//					mUserActionListener.hideSoftKeyboard();
//				}
//				
//				mAddBookCommentModel.start(mBookId, bCommentEditText.get(), bRatingValue.get());
//			}else if(isCommented){
//				ToastUtil.showToast(getContext(), getResources().getString(R.string.result_code_had_commented));
//			}else{
//				ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_not_null));
//			}
//		}
//	};
//	
//	public OnRatingChangedCommand bRatingBarChangeListener = new OnRatingChangedCommand() {
//		
//		@Override
//		public void onRatingChanged(RatingBar ratingBar, float rating,
//				boolean fromUser) {
//			bRatingValue.set((int)(rating*2));
//		}
//	};
	
	public final OnItemClickCommand bCommentItemClick = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(bItems.size() > 0 && position <= bItems.size()-1){
				CommentItem item = bItems.get(position);
				int commentId = item.commentId;
				String commentUserName=item.bUserNameText.get();
				BookCommentDetailActivity.openActivity(getContext(), commentId,commentUserName);
			}
		}
	};

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if(mBookCommentListModel.getTag().equals(tag)){
				ArrayList<BookCommentInfo> list = (ArrayList<BookCommentInfo>)result;
				if(list.size() > 0){
					loadCommentItems(list);
				}else{
					setLoadPageCompleted(true);
				}
			}
			if(bItems.size()<1){
				bNoDateVisibility.set(View.VISIBLE);
			}else{
				bNoDateVisibility.set(View.GONE);
			}
		}
		return super.onPostLoad(result, tag, isSucceed, isCancel, params);
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
		//bItems.clear();
		for(int i = 0; i < list.size();i++){
			BookCommentInfo info = list.get(i);
			CommentItem item = new CommentItem();
			item.commentId = info.getId();
			//判断当前评论是否是本机的用户所评论的
			String currentUserId = PreferencesUtil.getInstance(getContext()).getUserId();
			String userNickName = PreferencesUtil.getInstance(getContext()).getUserNickName();
			userNickName = userNickName != null ? userNickName.trim() : null;
			//如果是本机用户的评论，并且当前账号不是有课，则同步以前的本用户评论的用户名为当前的用户
			if(currentUserId.equals(info.getUserId()+"") && !AccountManager.getInstance().isVisitor()){
				if(!TextUtils.isEmpty(userNickName)){
					if(CommonUtil.isMobileNO(userNickName)){
						userNickName = CommonUtil.getEllipsisPhone(userNickName);
					}
					item.bUserNameText.set(userNickName);
				}else{
					if(!TextUtils.isEmpty(info.getUsername())){
						String name = info.getUsername();
						if(CommonUtil.isMobileNO(name)){
							name = CommonUtil.getEllipsisPhone(name);
						}
						item.bUserNameText.set(name);
					}
				}
			}else{
				if(!TextUtils.isEmpty(info.getUsername())){
					String name = info.getUsername();
					if(CommonUtil.isMobileNO(name)){
						name = CommonUtil.getEllipsisPhone(name);
					}
					//不是当前用户，或者是游客，则直接显示服务器默认游客名字
					item.bUserNameText.set(name);
				}
			}
			item.bTimeText.set(DateUtil.getFormateTimeString(info.getCreateTime()));
			item.bCommentText.set(info.getContent());
			item.bSupportCountText.set(""+info.getSupportNum());
			item.bReplyCountText.set(""+info.getCommentReplyNum());
			item.bRatingItemValue.set(info.getStarsNum()/2f);
			item.bUserIconUrl.set(info.getUserIcon());
			if(Integer.parseInt(item.bSupportCountText.get())>0){
				item.bZanSrc.set(R.drawable.icon_zan_red);
			}else{
				item.bZanSrc.set(R.drawable.icon_zan_grey);
			}
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
		public int commentId;
		public StringObservable bUserNameText = new StringObservable();
		public StringObservable bTimeText = new StringObservable();
		public StringObservable bCommentText = new StringObservable();
		public StringObservable bSupportCountText = new StringObservable();
		public StringObservable bReplyCountText = new StringObservable();
		public FloatObservable bRatingItemValue = new FloatObservable();
		public IntegerObservable bZanSrc = new IntegerObservable(R.drawable.icon_zan_red);
		public StringObservable bUserIconUrl = new StringObservable();
	}
	
	public static interface UserActionListener{
		public void loadCompleted();
		public void hideSoftKeyboard();
	}
	
	public void setUserActionListener(UserActionListener listener){
		mUserActionListener = listener;
	}

    public void clear(){
        bItems.clear();
    }

	@Override
	public void onStart() {
		mBookCommentListModel.loadPage();
	}

	@Override
	protected PagingLoadModel<?> getPagingLoadModel() {
		return mBookCommentListModel;
	}
	
	private class FooterViewModel implements IProguardFilter{
		public final BooleanObservable bFooterLoadingViewVisibility = BookCommentLatestViewModel.this.bFootLoadingVisibility;
		public final BooleanObservable bFooterLoadingCompletedVisibility = BookCommentLatestViewModel.this.bFootLoadCompletedVisibility;
	}

}
