package com.lectek.android.lereader.binding.model.user;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.SpanObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.binding.command.OnItemLongClickedCommand;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.net.response.NotifyCustomInfo;
import com.lectek.android.lereader.storage.dbase.PushMessage;
import com.lectek.android.lereader.storage.dbase.util.NotifyCustomInfoDB;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRequestResultViewNotify;
import com.lectek.android.lereader.ui.IRequestResultViewNotify.tipImg;
import com.lectek.android.lereader.ui.specific.ActivityChannels;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;

/**
 * 我的消息视图。展现信鸽推送的通知信息
 * @author ljp
 * @since 2014年5月4日
 */
public class MyMessageViewModel extends BaseLoadNetDataViewModel {

	private MyMessageModel mMyMessageModel;

	public final ArrayListObservable<MsgInfo> bItems = new ArrayListObservable<MsgInfo>(MsgInfo.class);
	public final IntegerObservable bNoDateVisibility = new IntegerObservable(View.GONE);
	private UserActionListener mUserActionListener;
	
	private int hasreadedCount;
	
	private IRequestResultViewNotify viewNotify;
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MsgInfo info = bItems.get(position);
			if(info.msgStatus == NotifyCustomInfoDB.STATUS_UNREAD){
				if(hasreadedCount > 0)
					hasreadedCount--;
				
				NotifyCustomInfoDB.getInstance().updateMsgStatus(info.msgId, true);
				info.bDescriptionTextColor.set(getResources().getColor(R.color.common_10));
			}
			if(info.notifyCustomInfo.getMsgType() == NotifyCustomInfo.TYPE_MY_INFO)
				return;
				
			Intent intent = ActivityChannels.getNotifyInfoIntent(getContext(), info.notifyCustomInfo);
			intent.putExtra(ContentInfoActivityLeyue.GET_SUBJECT_FROM_MY_MESSAGE_TAG, true);
			getContext().startActivity(intent);
		}
	};

	public void deleteButtonOnClick(int id){
      //  NotifyCustomInfoDB.getInstance().delNotifyMsgByMsgId(info.msgId);
      //  bItems.remove(i);
	}
	
	public final OnItemLongClickedCommand bOnItemLongClickedCommand = new OnItemLongClickedCommand() {
		
		@Override
		public void onItemLongClick(AdapterView<?> parent, View view, int position,
				long id) {

		}
	};

	public MyMessageViewModel(Context context, INetLoadView loadView, IRequestResultViewNotify viewNotify) {
		super(context, loadView);
		this.viewNotify = viewNotify;
		mMyMessageModel = new MyMessageModel();
		mMyMessageModel.addCallBack(this);
        NotifyCustomInfoDB.getInstance().setOnMessageChangeListener(new NotifyCustomInfoDB.ChangeListener() {
            @Override
            public void onMessageReceive() {
                MyAndroidApplication.clearNotityContent(getContext());
                if (mMyMessageModel != null){
                    mMyMessageModel.start();
                }
            }
        });
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		bNoDateVisibility.set(View.GONE);
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(mMyMessageModel.getTag().equals(tag)){
			viewNotify.setTipView(tipImg.request_fail, true);
            showRetryView();
		}
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,boolean isCancel, Object... params) {
		if(!isCancel && result != null){
			if(mMyMessageModel.getTag().equals(tag)){
				ArrayList<PushMessage> list = (ArrayList<PushMessage>)result;
				loadUmengMsgInfoList(list);
                hideLoadView();
				if(bItems.size() <1){
					if(mUserActionListener != null){
						mUserActionListener.setNotDataView();
					}
					bNoDateVisibility.set(View.VISIBLE);
				}else{
	                bNoDateVisibility.set(View.GONE);					
				}
			}
		}else{
			hideLoadView();
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void loadUmengMsgInfoList(ArrayList<PushMessage> list){
        bItems.clear();
        hasreadedCount = 0;
		for(int i = 0; i < list.size(); i++){
			PushMessage uInfo = list.get(i);
			MsgInfo info = new MsgInfo();
			NotifyCustomInfo nInfo = getNotifyCustomInfo(uInfo.msgJsonStr);
			info.msgId = uInfo.msgId;
			info.msgStatus = uInfo.msgStatus;
			info.notifyCustomInfo = nInfo;
			info.bTitleText.set(nInfo.getMsgTitle());
			info.bDescriptionText.set(nInfo.getMsgDecription());
			info.bTimeText.set(DateUtil.getFormateTimeString(uInfo.msgCreateTime));
			if(uInfo.msgStatus == NotifyCustomInfoDB.STATUS_READ){
				info.bDescriptionTextColor.set(getResources().getColor(R.color.common_10));
			}else{
				hasreadedCount++;
			}
			bItems.add(info);
		}
	}
	
	private NotifyCustomInfo getNotifyCustomInfo(String jsonStr){
		NotifyCustomInfo info = new NotifyCustomInfo();
		try {
			info.fromJsonObject(new JSONObject(jsonStr));
		} catch (JSONException e) {
		}
		return info; 
	}
	
	@Override
	public void onStart() {
		mMyMessageModel.start();
	}

	public class MsgInfo{
		public String msgId;
		public int msgStatus;
		public NotifyCustomInfo notifyCustomInfo;
		public final BooleanObservable bItemCheck = new BooleanObservable(false);
		public final BooleanObservable bItemCheckBoxVisible = new BooleanObservable(false);
		public final StringObservable bTitleText = new StringObservable();
		public final IntegerObservable bDescriptionTextColor = new IntegerObservable(getResources().getColor(R.color.common_6));
		public final SpanObservable bTitleTextSpan = new SpanObservable(new UnderlineSpan());
		public final StringObservable bDescriptionText = new StringObservable();
		public final StringObservable bTimeText = new StringObservable();
	}
	
	public void setUserActionListener(UserActionListener listener){
		mUserActionListener = listener;
	}
	
	public static interface UserActionListener{
		public void setButtonRightEnable(boolean isEnable);
		public void setTitle(String string);
		public void setNotDataView();
		public void cancelEditModel();
	}

}
