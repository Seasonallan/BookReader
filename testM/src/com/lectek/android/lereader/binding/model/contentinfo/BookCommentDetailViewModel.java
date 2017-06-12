package com.lectek.android.lereader.binding.model.contentinfo;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.net.response.BookCommentDetail;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.net.response.BookCommentReplyInfo;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.SupportCommentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.login_leyue.UserLoginLeYueNewActivity;
import com.lectek.android.lereader.ui.specific.ReplyCommentActivity;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.FloatObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

public class BookCommentDetailViewModel extends BaseLoadNetDataViewModel {
	
	
	public final StringObservable bUserNameText = new StringObservable();
	public final StringObservable bTimeText = new StringObservable();
	public final StringObservable bCommentText = new StringObservable();
	public final StringObservable bSupportCountText = new StringObservable();
	public final StringObservable bReplyCountText = new StringObservable();
	public final BooleanObservable bReplyTitleVisibility = new BooleanObservable(false);
	public final FloatObservable bRatingItemValue = new FloatObservable();
	public final IntegerObservable bZanSrcIV = new IntegerObservable();
	public final StringObservable bUserIconUrl = new StringObservable();
	
	public final ArrayListObservable<ReplyItem> bItems = new ArrayListObservable<ReplyItem>(ReplyItem.class);
	
	private BookCommentDetailModel mBookCommentDetailModel;
	private BookCommentSupportModel mBookCommentSupportModel;
	private int mCommentId;
	private String mCommentUserName;
	private boolean isCommented;
	private BookCommentDetailAciton mUserAction;
	
	public final OnClickCommand bSupportClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			if(!isCommented){
				mBookCommentSupportModel.start(mCommentId);
				mUserAction.startZanAnimation();
			}else{
				ToastUtil.showToast(
						getContext(),
						getResources().getString(
								R.string.result_code_had_supported));
			}
		}
	};
	
	public final OnClickCommand bReplyClick = new OnClickCommand() {
		
		@Override
		public void onClick(View v) {
			
			boolean isUserLoginSuccess = PreferencesUtil.getInstance(getContext()).getIsLogin();
			if (!isUserLoginSuccess) { // 联网且游客身份，必须先登陆
//				Intent intent = new Intent(getContext(), UserLoginActivityLeYue.class);
				Intent intent = new Intent(getContext(), UserLoginLeYueNewActivity.class);
				getContext().startActivity(intent);
				return;
			}
			
			ReplyCommentActivity.openActivity(getContext(), mCommentId,mCommentUserName);
		}
	};
	private int mSupportCount;
	private boolean isNeedUpateCommentInfo;

	public BookCommentDetailViewModel(Context context, INetLoadView loadView, int commentId,String commentUserName) {
		super(context, loadView);
		mCommentId = commentId;
		mCommentUserName=commentUserName;
		mBookCommentDetailModel = new BookCommentDetailModel();
		mBookCommentDetailModel.addCallBack(this);
		mBookCommentSupportModel = new BookCommentSupportModel();
		mBookCommentSupportModel.addCallBack(this);
	}
	public BookCommentDetailViewModel(Context context, INetLoadView loadView, int commentId,String commentUserName,BookCommentDetailAciton mUserAction) {
		this(context,loadView,commentId,commentUserName);
		this.mUserAction=mUserAction;
	}
	public boolean checkNeedUpdateCommentInfo(){
		return isNeedUpateCommentInfo;
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		if(mBookCommentDetailModel.getTag().equals(tag)){
			showLoadView();
		}
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(mBookCommentDetailModel.getTag().equals(tag)){
			finish();
		}
		if(mBookCommentSupportModel.getTag().equals(tag)){
			ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_support_fail));
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if(mBookCommentDetailModel.getTag().equals(tag)){
				BookCommentDetail info = (BookCommentDetail)result;
				loadData(info);
			}
			if(mBookCommentSupportModel.getTag().equals(tag)){
				SupportCommentInfo info = (SupportCommentInfo)result;
				if(info.getSupportNum() > 0){
					ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_support_success));
					mSupportCount++;
					bSupportCountText.set(mSupportCount+"");
					bZanSrcIV.set(R.drawable.icon_zan_big_red);
					isNeedUpateCommentInfo = true;
					isCommented = true;
				}else{
					ToastUtil.showToast(getContext(), getResources().getString(R.string.comment_support_fail));
				}
			}
		}
		hideLoadView();
		return false;
	}
	
	private void loadData(BookCommentDetail detail){
		BookCommentInfo info = detail.getBookCommentInfo();
		
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
				bUserNameText.set(userNickName);
			}else{
				if(!TextUtils.isEmpty(info.getUsername())){
					String name = info.getUsername();
					if(CommonUtil.isMobileNO(name)){
						name = CommonUtil.getEllipsisPhone(name);
					}
					bUserNameText.set(name);
				}
			}
		}else{
			if(TextUtils.isEmpty(info.getUsername())){
				String name = info.getUsername();
				if(CommonUtil.isMobileNO(name)){
					name = CommonUtil.getEllipsisPhone(name);
				}
				//不是当前用户，或者是游客，则直接显示服务器默认游客名字
				bUserNameText.set(name);
			}
			bUserNameText.set(info.getUsername());
		}
		
		
		
//		bUserNameText.set(CommonUtil.getEllipsisPhone(info.getUsername()));
		bTimeText.set(DateUtil.getFormateTimeString(info.getCreateTime()));
		bCommentText.set(info.getContent());
		mSupportCount = info.getSupportNum();
		bSupportCountText.set(mSupportCount+"");
		bReplyCountText.set(info.getCommentReplyNum()+"");
		bRatingItemValue.set(info.getStarsNum()/2f);
		bUserIconUrl.set(info.getUserIcon());
		if(Integer.parseInt(bSupportCountText.get())>0){
			bZanSrcIV.set(R.drawable.icon_zan_big_red);
		}else{
			bZanSrcIV.set(R.drawable.icon_zan_big_grey);
		}
		if(detail.getCommentReplyInfos() != null && detail.getCommentReplyInfos().size() > 0){
			bReplyTitleVisibility.set(false);
			ArrayList<BookCommentReplyInfo> cInfos = detail.getCommentReplyInfos();
			bItems.clear();
			for(int i = 0; i < cInfos.size(); i++){
				BookCommentReplyInfo cInfo = cInfos.get(i);
				ReplyItem item = new ReplyItem();
				
				//如果是本机用户的评论，并且当前账号不是有课，则同步以前的本用户评论的用户名为当前的用户
				if(currentUserId.equals(cInfo.getReplyUserId()+"") && !AccountManager.getInstance().isVisitor()){
					if(!TextUtils.isEmpty(userNickName)){
						if(CommonUtil.isMobileNO(userNickName)){
							userNickName = CommonUtil.getEllipsisPhone(userNickName);
						}
						item.bUserNameText.set(userNickName);
					}else{
						if(!TextUtils.isEmpty(cInfo.getAccount())){
							String name = cInfo.getAccount();
							if(CommonUtil.isMobileNO(name)){
								name = CommonUtil.getEllipsisPhone(name);
							}
							item.bUserNameText.set(name);
						}
					}
				}else{
					//不是当前用户，或者是游客，则直接显示服务器默认游客名字
					if(!TextUtils.isEmpty(info.getUsername())){
						String name = info.getUsername();
						if(CommonUtil.isMobileNO(name)){
							name = CommonUtil.getEllipsisPhone(name);
						}
						item.bUserNameText.set(name);
					}
				}
				
//				item.bUserNameText.set(CommonUtil.getEllipsisPhone(cInfo.getAccount()));
				item.bTimeText.set(DateUtil.getFormateTimeString(cInfo.getCreateTime()));
				item.bCommentText.set(cInfo.getReplyContent());
//				item.bSrcIV.set(cInfo.);
				bItems.add(item);
			}
			
		}else{
			bReplyTitleVisibility.set(false);
		}
	}
	
	public static class ReplyItem{
		public StringObservable bUserNameText = new StringObservable();
		public StringObservable bTimeText = new StringObservable();
		public StringObservable bCommentText = new StringObservable();
		public StringObservable bSrcIV = new StringObservable();
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onStart() {
		mBookCommentDetailModel.start(mCommentId);
	}
	
	public interface BookCommentDetailAciton extends IBaseUserAction{  
		
		public void startZanAnimation();
	}
}
