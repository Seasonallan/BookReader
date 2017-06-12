package com.lectek.android.lereader.binding.model.user;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.SpanObservable;
import gueei.binding.observables.StringObservable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.account.AccountManager.ILoginInterface;
import com.lectek.android.lereader.account.AccountType;
import com.lectek.android.lereader.account.LoginType;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.openapi.ResponseResultCodeTY;
import com.lectek.android.lereader.net.response.tianyi.OrderedResult;
import com.lectek.android.lereader.net.response.tianyi.PointRule;
import com.lectek.android.lereader.storage.dbase.TianYiUserInfo;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRequestResultViewNotify;
import com.lectek.android.lereader.ui.IRequestResultViewNotify.tipImg;
import com.lectek.android.lereader.ui.pay.AlipayRechargeActivity;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-9-9
 */
public class PointManageViewModel extends BaseLoadNetDataViewModel {
	
	public final StringObservable bPointTimeText = new StringObservable();
	public final StringObservable bScoreText = new StringObservable();
	public final StringObservable bPointText = new StringObservable();
	public final SpanObservable bUserPointSpan = new SpanObservable();
	public final StringObservable bUserPointRecharge = new StringObservable();
	public final SpanObservable bUserPointRechargeSpan = new SpanObservable();
	
	//积分兑换阅点
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ItemViewModel>(ItemViewModel.class);
	
	private String mUserScore;
	
	private PointManageModel mPointManageModel;//阅点充值管理
	private PointRechargeModel mPointRechargeModel;//获取积分兑换阅点列表
	private PointConvertModel mPointConvertModel;//积分兑换阅点接口
	private TianYiUserInfo mDataSource;
	
	private ArrayList<PointRule> mPointRules;//积分兑换阅点列表
	
	private int mLoadDataCount = 0;//用户统计当前数据是否全部加载完毕，目前需要加载两次数据
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	
	private boolean mIsLoginTianYi = false;
	
	public final OnClickCommand bAlipayClick = new OnClickCommand(){
		@Override
		public void onClick(View v) {
			
			if(mIsLoginTianYi){
				((Activity)getContext()).startActivityForResult(new Intent(getContext(), AlipayRechargeActivity.class), AlipayRechargeActivity.REQUEST_CODE);
			}else{
				AccountManager.getInstance().login(AccountType.TIANYI, LoginType.BASE_LOGIN, new ILoginInterface() {
					
					@Override
					public void showLoading() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void loginSuccess() {
						((Activity)getContext()).startActivityForResult(new Intent(getContext(), AlipayRechargeActivity.class), AlipayRechargeActivity.REQUEST_CODE);
						
					}
					
					@Override
					public void loginFail() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void hideLoading() {
						// TODO Auto-generated method stub
						
					}
				}, getContext());
			}
			
		}
	};
	
	//积分兑换阅点列表子项点击事件
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			int pos = position;
			final PointRule pointRule = mPointRules.get(pos);
			DialogUtil.commonConfirmDialog((Activity)getContext(), getContext().getString(R.string.point_to_readpoint),
					getContext().getString(R.string.convert_tip,pointRule.getRuleName(),pointRule.getConverPoint()), 
					R.string.convert, R.string.btn_text_cancel, new DialogUtil.ConfirmListener() {
				
				@Override
				public void onClick(View v) {
					mPointConvertModel.start(pointRule.getRuleId());
				}
			}, null);
		}
	};
	private IRequestResultViewNotify viewNotify;
	public PointManageViewModel(Context context,INetLoadView loadView,IRequestResultViewNotify viewNotify) {
		super(context,loadView);
		this.viewNotify = viewNotify;
		mPointManageModel = new PointManageModel();
		mPointManageModel.addCallBack(this);
		//积分兑换阅点
		mPointRechargeModel = new PointRechargeModel();
		mPointRechargeModel.addCallBack(this);
		mPointConvertModel = new PointConvertModel();
		mPointConvertModel.addCallBack(this);
		mIsLoginTianYi = AccountManager.getInstance().isLoginTianYiAccount();
		String score = AccountManager.getInstance().getUserInfo().getScore();
		bPointTimeText.set(getString(R.string.user_info_read_point_manager_time, sdf.format(new Date())));
		bPointText.set("0");
		bScoreText.set(score);
	}

	@Override
	public void onStart() {
		super.onStart();
		//获取用户积分阅点信息
		if(mIsLoginTianYi){
			mPointManageModel.start();
		}
	}

	@Override
	public void onRelease() {
		super.onRelease();
		mPointManageModel.cancel();
		mPointRechargeModel.cancel();
		mPointConvertModel.cancel();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		if (mPointManageModel.getTag().equals(tag)) {
			mLoadDataCount = 0;//重置
		}
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if (mPointManageModel.getTag().equals(tag)||mPointConvertModel.getTag().equals(tag)) {
			mLoadDataCount++;
			if (mLoadDataCount>=2) {//TODO:一个挂了，应该两个都取消掉。cancel。但不知道，cancel后是否会走onFail
				if(mPointConvertModel.getTag().equals(tag)){
					if(e instanceof GsonResultException){
						String msg = ((GsonResultException) e).getResponseInfo().getSurfingMsg();
						int code = ((GsonResultException) e).getResponseInfo().getSurfingCode();
						if (code == ResponseResultCodeTY.POINT_NOT_ENOUGH) {
							ToastUtil.showToast(getContext(), msg);
						}else {
							ToastUtil.showToast(getContext(), "兑换失败！");
						}
					}
				}else {
					viewNotify.setTipView(tipImg.request_fail, true);
					showRetryView();
				}
			}
		}
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed, boolean isCancel, Object... params) {
		if (result != null && !isCancel) {
			//获取阅点充值管理数据
			if(mPointManageModel.getTag().equals(tag)){
				mDataSource = (TianYiUserInfo) result;
				String point = mDataSource.getReadPoint();
				if(!TextUtils.isEmpty(point)){
					bPointText.set(point);
				}
				
				mLoadDataCount++;
				//获取积分兑换阅点列表
//				mPointRechargeModel.start();
			}else if(mPointRechargeModel.getTag().equals(tag)){
				bItems.clear();
				//获取积分兑换阅点列表数据  需要mPointManageModel接口的数据
				mPointRules = (ArrayList<PointRule>) result;
				for (PointRule pointRule : mPointRules) {
					bItems.add(newItemViewModel(pointRule));
				}
				mLoadDataCount++;
			}else if(mPointConvertModel.getTag().equals(tag)){
				//积分兑换阅点接口
				OrderedResult rs = (OrderedResult)result;
				if(rs.getCode() == ResponseResultCodeTY.CONVERT_POINT_SUCCESS){
					ToastUtil.showToast(getContext(),"兑换成功!");
					onStart();
				}else if(rs.getCode() == ResponseResultCodeTY.POINT_NOT_ENOUGH){
					ToastUtil.showToast(getContext(), rs.getSurfingMsg());
				}else {
					ToastUtil.showToast(getContext(), "兑换失败！");
				}
				mLoadDataCount = 2;
			}
		}
		LogUtil.e("---mLoadDataCount--"+mLoadDataCount);
		//加载两次证明数据全部获取完毕，隐藏加载视图
//		if(mLoadDataCount >=2){
			hideLoadView();
//		}
		return false;
	}
	
	//---积分兑换阅点实体--- begin
	
	private ItemViewModel newItemViewModel(PointRule pointRule) {
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.pointRule = pointRule;
		String readPointSrt = pointRule.getRuleName();
		if(readPointSrt.contains("阅点")){
			int lastPos = readPointSrt.indexOf("阅点");
			readPointSrt = readPointSrt.substring(0, lastPos);
		}
		itemViewModel.bReadPoint.set(readPointSrt);
		itemViewModel.bConvertPoint.set(getContext().getString(R.string.account_points2readpoint_point, pointRule.getConverPoint()));
		int userScore = 0;
		if(!TextUtils.isEmpty(mUserScore)){
			userScore = Integer.parseInt(mUserScore);
		}
		int convertPoint = Integer.parseInt(pointRule.getConverPoint());
		Log.e("pinotao", "pointRule = "+pointRule.toString());
		Log.e("pinotao", "userScore = "+userScore+";convertPoint = "+convertPoint);
		if(userScore >= convertPoint){
			Log.e("pinotao", "read_point_default_bg");
			itemViewModel.bReadTicketBg.set(R.drawable.read_point_default_bg);
			itemViewModel.bReadPointColor.set(getContext().getResources().getColor(R.color.white));
		}else{
			Log.e("pinotao", "read_point_unenable_bg");
			itemViewModel.bReadTicketBg.set(R.drawable.read_point_unenable_bg);
			itemViewModel.bReadPointColor.set(getContext().getResources().getColor(R.color.common_6));
		}
		return itemViewModel;
	}
	
	public class ItemViewModel {
		public PointRule pointRule;
		public final StringObservable bReadPoint = new StringObservable();
		public final IntegerObservable bReadPointColor = new IntegerObservable(getContext().getResources().getColor(R.color.white));
		public final StringObservable bConvertPoint = new StringObservable();
		public final IntegerObservable bReadTicketBg = new IntegerObservable(R.drawable.read_point_default_bg);
	}
	
	//---积分兑换阅点实体--- end

	@Override
	protected boolean hasLoadedData() {
		return mDataSource != null;
	}
}
