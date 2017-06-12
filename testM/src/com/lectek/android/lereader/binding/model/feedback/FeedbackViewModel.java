package com.lectek.android.lereader.binding.model.feedback;

import gueei.binding.observables.StringObservable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.response.CommonResultInfo;
import com.lectek.android.lereader.net.response.FeedBackInfo;
import com.lectek.android.lereader.net.response.FeedbackSelectReplyInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.IBaseUserAction;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRequestResultViewNotify.tipImg;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.IMSIUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 *  意见反馈
 * @author donghz
 * @date 2014-3-21
 *
 */
public class FeedbackViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask{

	private FeedbackUserAction mUserAction;
	private Context mContext;
	private FeedBackGetListInfoModel mFeedBackGetListInfoModel;
	private FeedbackAddModel mFeedbackAddModel;
    private FeedbackReplyModel mFeedbackReplyModel;
    private FeedbackScoreModel mFeedbackScoreModel;
    private FeedbackSelectModel mFeedbackSelectModel;
    private String mTempImgUrl="";
    private String mFeedBackImgUrl="";
    public static final String URL_HEAD ="http://112.124.13.62:8088/"; 
    private String  mFeedbackId="";
    public  String  mLastUpdateTime ="";
    private boolean mReFresh = false;
    private boolean mIsColose = true;;
    /**文字为1**/
    private final static String SEND_TEXT="1";
    /**图片为2**/
    private final static String SEND_IMG="2";
    
    private  ArrayList<FeedBackInfo> mFeedbackInfoLists;
	public StringObservable bFeedBackEditContent = new StringObservable();
	
	private String mRcontentStr="";
	private String mRcreateTime="";
	private String mRcontentType="";
	
	private Integer mScorefeedbackId;
	private Integer mScoreScore;

	
	public FeedbackViewModel(Context context, INetLoadView loadView,FeedbackUserAction userAction) {
		super(context, loadView);
		mUserAction = userAction;
		mContext = context;
		mFeedBackGetListInfoModel = new FeedBackGetListInfoModel();
		mFeedBackGetListInfoModel.addCallBack(this);
		
		mFeedbackAddModel = new FeedbackAddModel();
		mFeedbackAddModel.addCallBack(this);
		
		mFeedbackReplyModel = new FeedbackReplyModel();
		mFeedbackReplyModel.addCallBack(this);
		
		mFeedbackScoreModel = new FeedbackScoreModel();
		mFeedbackScoreModel.addCallBack(this);
		
		mFeedbackSelectModel = new FeedbackSelectModel();
		mFeedbackSelectModel.addCallBack(this);
		
		mFeedbackInfoLists = new ArrayList<FeedBackInfo>();
	}

	/**
	 * 发送意见反馈事件
	 */
	public final OnClickCommand bFeedbackSendClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			sendFeedback();
		}
	};
	
	/**
	 * 发送意见反馈
	 */
	private void sendFeedback(){
		if(TextUtils.isEmpty(mFeedbackId)&&mIsColose){
			if(TextUtils.isEmpty(mFeedBackImgUrl)){
				sendText();
			}else{
				sendImage();
			}
		}else{
			feedbackSelect(mFeedbackId);
		}
	}
	
	/**
	 * 发送图片事件
	 */
	public final OnClickCommand bFeedbackSendImgClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			//判断网络是否可用
			if(!CommonUtil.isNetAvailable(mContext)){
				ToastUtil.showToast(mContext, R.string.notify_err_connection_tip);
				return;
			}
			mUserAction.sendImg();
		}
	};
	
	/**
	 * 发送文本
	 */
	private void sendText(){
		String contentStr = bFeedBackEditContent.get();
		if(TextUtils.isEmpty(bFeedBackEditContent.get())){
			ToastUtil.showToast(mContext, R.string.wo_feedback_is_empty);
		    return;
		}
		//判断网络是否可用
		if(!CommonUtil.isNetAvailable(mContext)){
			ToastUtil.showToast(mContext, R.string.notify_err_connection_tip);
			return;
		}
		
		if(mIsColose){//已关闭(重新创建话题)
			addFeedBackInfo(contentStr,SEND_TEXT);
		}else{//回复话题
			addReplyFeedbackInfo(mFeedbackId,contentStr,SEND_TEXT);
		}
		if(mUserAction!=null){//数据添加到适配器
			mUserAction.addMesToAdapter(mRcontentStr,mRcreateTime,mRcontentType);
		}
		bFeedBackEditContent.set("");
		mUserAction.hideKeyBoard();
	}
	/**
	 * 发送图片
	 */
	private void sendImage(){
		if(TextUtils.isEmpty(mFeedBackImgUrl)){
			ToastUtil.showToast(mContext, R.string.wo_feedback_img_is_empty);
		    return;
		}
		//判断网络是否可用
		if(!CommonUtil.isNetAvailable(mContext)){
			ToastUtil.showToast(mContext, R.string.notify_err_connection_tip);
			return;
		}
		if(mIsColose){//已关闭(重新创建话题)
			addFeedBackInfo(mFeedBackImgUrl,SEND_IMG);
		}else{//回复话题
			addReplyFeedbackInfo(mFeedbackId,mFeedBackImgUrl,SEND_IMG);
		}
		
		if(mUserAction!=null){//数据添加到适配器
			mUserAction.addMesToAdapter(mRcontentStr,mRcreateTime,mRcontentType);
		}
		
		bFeedBackEditContent.set("");
		mUserAction.hideKeyBoard();
	}
	
	/**
	 * 获取图片地址
	 */
	public void getImgUrl(final String imgUrl){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mTempImgUrl = imgUrl;
					FastDfsClient fc = new FastDfsClient();
					LogUtil.e("dhz", "imgUrl " + imgUrl);
					String result = fc.upload(imgUrl);
					if(!TextUtils.isEmpty(result)){//获取图片地址成功
						mFeedBackImgUrl = result;
						LogUtil.e("dhz", "发送图片: mFeedBackImgUrl:  " + mFeedBackImgUrl);
//						mFeedBackImgUrl = MD5.getMD5(mFeedBackImgUrl);
						//发送意见反馈/mnt/sdcard/leyuereader/temp/feedback/tem
						sendFeedback();
						//重命名文件
						File file = new File(mTempImgUrl);
						file.renameTo(new File(mFeedBackImgUrl));
					}else{//获取图片地址失败
						ToastUtil.showToast(mContext, R.string.wo_feedback_img_is_empty);
						clearUrl();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 获取意见反馈列表
	 */
	public void getFeedBackListInfo(String lastUpdateTime,boolean reFresh){
		mReFresh = reFresh;
		String userId = PreferencesUtil.getInstance(mContext).getUserId();
		String iMei = CommonUtil.getImei(mContext);
		mFeedBackGetListInfoModel.start(userId,iMei,lastUpdateTime);
		showLoadView();
		LogUtil.e("dhz", "Feedback  userId " + userId);
	}
	
	/**
	 * 提交新的意见反馈
	 */
	private void addFeedBackInfo(String content,String contentType ){
		String account=PreferencesUtil.getInstance(mContext).getUserName();
		String userId = PreferencesUtil.getInstance(mContext).getUserId();
		String iMei = CommonUtil.getImei(mContext);
		String simCode= IMSIUtil.getIMSI(mContext);
		String sourceType=LeyueConst.SOURCE_TYPE;
		String deviceModel=CommonUtil.getDeviceModel();
		String mdnCode="";
		String appVserion=Integer.toString(PkgManagerUtil.getApkVersionCode(mContext)) ;
		mFeedbackAddModel.start(userId,account,iMei,simCode,sourceType,
				deviceModel,mdnCode,appVserion,content,contentType);
		setReplyInfo(content,contentType);
	}
	
	/**
	 * 回复意见反馈
	 */
	private void addReplyFeedbackInfo(String feedbackId,String content,String contentType){
		String account =PreferencesUtil.getInstance(mContext).getUserName();
		String userId = PreferencesUtil.getInstance(mContext).getUserId();
		String iMei = CommonUtil.getImei(mContext);
		mFeedbackReplyModel.start(userId,account,iMei,feedbackId,content,contentType);
		setReplyInfo(content,contentType);
	}
	
	/**
	 * 意见反馈评分
	 * @param feedbackId
	 * @param score
	 */
	public void feedbackScore(Integer feedbackId,Integer score){
		if((feedbackId==null)||(score==null))
			return;
		mScorefeedbackId = feedbackId;
		mScoreScore = score;
		mFeedbackScoreModel.start(feedbackId,score);
	}
	
	/**
	 * 查询意见反馈是否是关闭状态
	 */
	private void feedbackSelect(String feedbackId){
		if(TextUtils.isEmpty(feedbackId))
			return;
		mFeedbackSelectModel.start(feedbackId);
	}
	
	/**
	 * 设置回复消息
	 */
	private void setReplyInfo(String contentStr,String contentType){
		mRcontentType = contentType;
		mRcontentStr = contentStr;
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mRcreateTime = sDateFormat.format(new java.util.Date());
	}
	
	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		LogUtil.e("dhz", "onFail" + e.toString());
		if(mFeedbackAddModel.getTag().equals(tag)){//添加失败
			clearUrl();
		}
		if(mFeedbackReplyModel.getTag().equals(tag)){//回复失败
			clearUrl();
		}
		return false;
	}
	
	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(result != null && !isCancel){
			if(mFeedBackGetListInfoModel.getTag().equals(tag)){//获取列表数据
				hideLoadView();
				mFeedbackInfoLists = (ArrayList<FeedBackInfo>)result;
				if(mFeedbackInfoLists.size()==0){
//					mUserAction.showNoDataView(tipImg.no_info,false);
				}
				mUserAction.getFeedBackListOk(mFeedbackInfoLists.get(0),mReFresh);
				if(TextUtils.isEmpty(mFeedbackId)){
					Integer feedbackId = mFeedbackInfoLists.get(0).getId();
					if(feedbackId!=null){
						mFeedbackId = String.valueOf(feedbackId);
					}
				}
				mLastUpdateTime = mFeedbackInfoLists.get(0).getCreateTime();
				LogUtil.e("dhz", "mFeedbackId " + mFeedbackId + "  UpdateTime  " + mLastUpdateTime);
			}
			
			if(mFeedbackAddModel.getTag().equals(tag)){//添加
                CommonResultInfo info = (CommonResultInfo)result;
				if(info!=null){
					mFeedbackId = info.getResult();
				}
				if(mUserAction!=null){
//					mUserAction.addMesToAdapter(mRcontentStr,mRcreateTime,mRcontentType);
				}
				clearUrl();
				LogUtil.v("dhz", "添加: " + info.getResult());
			}
			
			if(mFeedbackReplyModel.getTag().equals(tag)){// 回复
				if(mUserAction!=null){
//					mUserAction.addMesToAdapter(mRcontentStr,mRcreateTime,mRcontentType);
				}
				clearUrl();
			}
			
			if(mFeedbackScoreModel.getTag().equals(tag)){//评分
				Boolean replyInfo = (Boolean)result;
				boolean bresult	=replyInfo.booleanValue();
				if(bresult){
					mUserAction.scoreSuccessNotify(mScorefeedbackId,mScoreScore);
					ToastUtil.showToast(mContext,mContext.getString(R.string.feedback_commit_success));
				}else{
					ToastUtil.showToast(mContext,mContext.getString(R.string.feedback_commit_fail));
				}
				LogUtil.e("dhz", "评分  : " + bresult);
			}
			
			if(mFeedbackSelectModel.getTag().equals(tag)){//是否已关闭话题
				FeedbackSelectReplyInfo info = (FeedbackSelectReplyInfo)result;
				//未评分需要显示评分选项
				if(info.getIsClose().equals("1")&&info.getScore()==null){
					mUserAction.showScoreItem(Integer.valueOf(mFeedbackId),
							info.getUpdateTime(),info.getScore(),true);
				}
				
				if(info.getIsClose().equals("1")){
					mIsColose =true;
				}else{
					mIsColose = false;
				}
				if(TextUtils.isEmpty(mFeedBackImgUrl)){
					sendText();
				}else{
					sendImage();
				}
				Log.w("dhz", "是否已关闭话题: " + mIsColose);
			}
		}else{
			if(mFeedBackGetListInfoModel.getTag().equals(tag)){//获取列表数据为空
				hideLoadView();
				if(mReFresh){
					ToastUtil.showToast(mContext,mContext.getString(R.string.wo_feedback_no_data));
				}else{
//					mUserAction.showNoDataView(tipImg.no_info,false);
				}
			}
		}
		return false;
	}
	/**
	 * 清除URL地址
	 */
	private void clearUrl(){
		mFeedBackImgUrl="";
		mTempImgUrl="";
	}
	
	@Override
	public void onStart() {
		super.onStart();
		tryStartNetTack(this);
	}

	@Override
	protected boolean hasLoadedData() {
		return mFeedbackInfoLists !=null;
	}

	public static interface FeedbackUserAction extends IBaseUserAction{
		public void getFeedBackListOk(FeedBackInfo info, boolean isReFresh);
		public void getFeedBackListFail();
		public void hideKeyBoard();
		public void sendImg();
		/**
		 * 显示评分项
		 * @param feedbackId
		 * @param needNotify
		 */
		public void showScoreItem(Integer feedbackId, String updateTime, Integer score, boolean needNotify);
		public void addMesToAdapter(String mRcontentStr, String mRcreateTime, String contentType);
		public void showNoDataView(tipImg tip, boolean bShow);
		public void scoreSuccessNotify(Integer scorefeedbackId, Integer score);
	}

	@Override
	public boolean isNeedReStart() {
		return !hasLoadedData();
	}
	@Override
	public boolean isStop() {
		return !mFeedBackGetListInfoModel.isStart();
	}
	@Override
	public void start() {
		getFeedBackListInfo("",false);
	}
}
