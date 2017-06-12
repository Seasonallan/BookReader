package com.lectek.android.LYReader.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel.ILoadDataCallBack;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.utils.CommonUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @description
	用于接收 微信 返回参数，该界面 不显示内容。
	只是 按微信返回接收要求定义的。 所以，获取的参数后，跳转到分享前的界面。
 * @author chendt
 * @date 2013-5-10
 * @Version 1.0
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler,ILoadDataCallBack{
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	public static final String RESPONSE_TIP = "response_tip";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, LeyueConst.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.e("onNewIntent");
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		LogUtil.e("onReq");
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		default:
			break;
		}
	}

	private ScoreUploadModel mUploadModel;
	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				mUploadModel = new ScoreUploadModel();
				mUploadModel.addCallBack(this);
				if (UserScoreInfo.WX_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE) || 
						UserScoreInfo.WX_ZONE.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,UmengShareUtils.LAST_SHARE_SOURCEID);
				}
				break;
			case BaseResp.ErrCode.ERR_COMM:
				Toast.makeText(WXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(WXEntryActivity.this, "分享取消", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(WXEntryActivity.this, "分享认证失败", Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(WXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
				break;
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		return false;
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		Toast.makeText(WXEntryActivity.this, R.string.share_record_fail_tip, Toast.LENGTH_LONG);
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if (!isCancel && result!=null) {
			if (tag.equals(mUploadModel.getTag())) {
				ScoreUploadResponseInfo info = (ScoreUploadResponseInfo) result;
				CommonUtil.handleForShareTip(WXEntryActivity.this, info);
				finish();
			}
		}
		return false;
	}

	
}