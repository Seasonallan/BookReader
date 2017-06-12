package com.lectek.android.lereader.binding.model.contentinfo;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.binding.model.contentinfo.BookCommentLatestViewModel.CommentItem;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.IProguardFilter;
import com.lectek.android.lereader.net.response.BookCommentInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.BookCommentDetailActivity;
import com.lectek.android.lereader.utils.CommonUtil;


public class BookCommentHotViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask{
	
	public final ArrayListObservable<CommentItem> bItems = new ArrayListObservable<CommentItem>(CommentItem.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.GONE);
	private BookCommentHotModel mBookCommentHotModel;
	
	public final FooterViewModel bFooterViewModel = new FooterViewModel();
	private String mBookId;

	public BookCommentHotViewModel(Context context, INetLoadView loadView, String bookId) {
		super(context, loadView);
		mBookId = bookId;
		mBookCommentHotModel = new BookCommentHotModel();
		mBookCommentHotModel.addCallBack(this);
		bFooterViewModel.bFooterLoadingViewVisibility.set(false);
	}
	
	public final OnItemClickCommand bCommentItemClick = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CommentItem item = bItems.get(position);
			int commentId = item.commentId;
			String commentUserName=item.bUserNameText.get();
			BookCommentDetailActivity.openActivity(getContext(), commentId,commentUserName);
		}
	};

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		bNoDateVisibility.set(View.GONE);
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
		hideLoadView();
		if(mBookCommentHotModel.getTag().equals(tag)){
			if(!isCancel && result != null){
				ArrayList<BookCommentInfo> list = (ArrayList<BookCommentInfo>)result;
				if(list.size() > 0) {
					loadCommentItems(list);
					bNoDateVisibility.set(View.GONE);
					return true;
				}
			}
			
			bNoDateVisibility.set(View.VISIBLE);
		}		
		return false;
	}
	
	private void loadCommentItems(ArrayList<BookCommentInfo> list){
		bItems.clear();
		for(int i = 0; i < list.size() && i <3;i++){
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
			item.bSupportCountText.set(info.getSupportNum()+"");
			item.bReplyCountText.set(info.getCommentReplyNum()+"");
			item.bRatingItemValue.set(info.getStarsNum()/2f);
			item.bUserIconUrl.set(info.userIcon);
			if(Integer.parseInt(item.bSupportCountText.get())>0){
				item.bZanSrc.set(R.drawable.icon_zan_red);
			}else{
				item.bZanSrc.set(R.drawable.icon_zan_grey);
			}
			bItems.add(item);
		}
		
	}

	@Override
	protected boolean hasLoadedData() {
		return false;
	}
	
	@Override
	public void onStart() {
		tryStartNetTack(this);
	}
	
	private class FooterViewModel implements IProguardFilter{
		BooleanObservable bFootLoadingVisibility = new BooleanObservable(false);
		public final BooleanObservable bFooterLoadingViewVisibility = bFootLoadingVisibility;
	}

	@Override
	public boolean isNeedReStart() {
		return false;
	}

	@Override
	public boolean isStop() {
		return !mBookCommentHotModel.isStart();
	}

	@Override
	public void start() {
		mBookCommentHotModel.start(mBookId);
	}
}
