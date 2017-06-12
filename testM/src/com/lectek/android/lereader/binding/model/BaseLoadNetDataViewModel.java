package com.lectek.android.lereader.binding.model;

import android.content.Context;

import com.lectek.android.lereader.lib.utils.ApnUtil;
import com.lectek.android.lereader.ui.INetLoadView;
import com.lectek.android.lereader.ui.IRetryClickListener;
import com.lectek.android.lereader.ui.INetLoadView.NetworkChangeListener;
/**
 * 加载网络数据的ViewModel
 * @author linyiwei
 *
 */
public abstract class BaseLoadNetDataViewModel extends BaseLoadDataViewModel implements INetLoadView,NetworkChangeListener{
	private INetAsyncTask mLastTask;
	private IRetryClickListener mRetryClickListener;
	public BaseLoadNetDataViewModel(Context context,INetLoadView loadView) {
		super(context,loadView);
	}
	/**
	 * 尝试启动任务，此方法反正无网络提示，设置网络后自动重新请求流程
	 * @param lastTask 只保留最后一次任务
	 * @return 当前调用是否启动成功
	 */
	public boolean tryStartNetTack(INetAsyncTask lastTask) {
		if(lastTask == null){
			return false;
		}
		mLastTask = lastTask;
		if(ApnUtil.isNetAvailable(getContext())){
			if(mLastTask.isStop()){
				hideNetSettingView();
				hideRetryView();
				mLastTask.start();
				return true;
			}
		}else{
			showNetSettingView();
		}
		return false;
	}

	@Override
	public void registerNetworkChange(NetworkChangeListener l){
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.registerNetworkChange(l);
		}
	}
	
	@Override
	public void showNetSettingView() {
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.showNetSettingView();
		}
	}

	@Override
	public void showNetSettingDialog() {
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.showNetSettingDialog();
		}
	}
	
	@Override
	public void showRetryView(Runnable retryTask) {
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.showRetryView(retryTask);
		}
	}
	
	public void showRetryView() {
		showRetryView(new Runnable() {
			@Override
			public void run() {
				if(mLastTask != null){
					if (mRetryClickListener!=null) {//新增界面需要对重试时 对请求作变更。提供处理接口
						mRetryClickListener.onRetryClick();
					}
					tryStartNetTack(mLastTask);
				}
			}
		});
	}
	
	@Override
	public void hideNetSettingView() {
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.hideNetSettingView();
		}
	}
	
	@Override
	public void hideRetryView() {
		INetLoadView loadView = (INetLoadView) getCallBack();
		if(loadView != null){
			loadView.hideRetryView();
		}
	}
	
	@Override
	public void onChange(boolean isAvailable) {
		if(isAvailable){
			hideNetSettingView();
			if(mLastTask != null && mLastTask.isNeedReStart()){
				tryStartNetTack(mLastTask);
			}
		}
	}

	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		if(BaseLoadNetDataModel.START_RESULT_NOT_NET.equals(state)){
			onHandleNotNetStartFail();
		}
		return super.onStartFail(tag, state, params);
	}
	/**
	 * 处理因无网络而启动失败的情况
	 */
	protected void onHandleNotNetStartFail(){
		if(hasLoadedData()){
			showNetSettingDialog();
		}else{
			showNetSettingView();
		}
	}
	
	/**
	 * 界面数据是否已经加载过了
	 * @return
	 */
	protected abstract boolean hasLoadedData();
	
	@Override
	public void onStart() {
		super.onStart();
		registerNetworkChange(this);
	}
	
	public void setmRetryClickListener(IRetryClickListener mRetryClickListener) {
		this.mRetryClickListener = mRetryClickListener;
	}
}
