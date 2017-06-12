package com.lectek.android.lereader.binding.model.feedback;

import gueei.binding.observables.StringObservable;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadNetDataViewModel;
import com.lectek.android.lereader.binding.model.INetAsyncTask;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.PkgManagerUtil;
import com.lectek.android.lereader.net.response.CommonResultInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.specific.FeedBackNewActivity;
import com.lectek.android.lereader.ui.specific.FeedBackNewActivity.IFeedBackAction;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.IMSIUtil;
import com.lectek.android.lereader.utils.KeyBoardUtil;
import com.lectek.android.lereader.utils.ToastUtil;

/**
 * 意见反馈ViewModel
 * 
 * @author yangwq
 * @date 2014年7月8日
 * @email 57890940@qq.com
 */
public class FeedBackNewViewModel extends BaseLoadNetDataViewModel implements INetAsyncTask{
	
	private StringObservable bCommunicationText = new StringObservable();
	public StringObservable bFeedBackText = new StringObservable("");
	/**文字为1**/
    private final static String SEND_TEXT="1";
	private FeedBackNewActivity mActivity;
	private FeedbackAddModel mFeedbackAddModel;

	public FeedBackNewViewModel(FeedBackNewActivity context, INetLoadView loadView) {
		super(context, loadView);
		mActivity = context;
		mActivity.setFeedBackAction(mFeedBackAction);
		mFeedbackAddModel = new FeedbackAddModel();
		mFeedbackAddModel.addCallBack(this);
	}
	
	
	/**
	 * 提交新的意见反馈
	 */
	private void addFeedBackInfo(String content){
		String account=PreferencesUtil.getInstance(getContext()).getUserName();
		String userId = PreferencesUtil.getInstance(getContext()).getUserId();
		String iMei = CommonUtil.getImei(getContext());
		String simCode= IMSIUtil.getIMSI(getContext());
		String sourceType=LeyueConst.SOURCE_TYPE;
		String deviceModel=CommonUtil.getDeviceModel();
		String mdnCode="";
		String appVserion=Integer.toString(PkgManagerUtil.getApkVersionCode(getContext())) ;
		mFeedbackAddModel.start(userId,account,iMei,simCode,sourceType,
				deviceModel,mdnCode,appVserion,content,SEND_TEXT);
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		showLoadView();
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		hideLoadView();
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		hideLoadView();
		if(mFeedbackAddModel.getTag().equals(tag)){//添加
            CommonResultInfo info = (CommonResultInfo)result;
			if(info!=null){
				Integer id = Integer.parseInt(info.getResult());
				if(id > 0){
					ToastUtil.showToast(getContext(), R.string.feedback_success);
					this.finish();
				}else{
					ToastUtil.showToast(getContext(), R.string.feedback_fail);
				}
			}else{
				ToastUtil.showToast(getContext(), R.string.feedback_fail);
			}
			
		}
		return false;
	}

	@Override
	protected boolean hasLoadedData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNeedReStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}
	
	private IFeedBackAction mFeedBackAction = new IFeedBackAction() {
		
		@Override
		public void onSendFeedBackClick() {
			String feedbackText = bFeedBackText.get();
			if(TextUtils.isEmpty(feedbackText.trim())){
				ToastUtil.showToast(getContext(), R.string.feedback_empty_tip);
			}else{
				addFeedBackInfo(feedbackText);
			}
			
			
		}
	};
	

}
