package com.lectek.android.LYReader.yxapi;

import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.BaseYXEntryActivity;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;
import android.util.Log;
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
/**
 * @description
	易信回到乐阅的接收回调类
 * @author chendt
 * @date 2013-11-28
 * @Version 1.0
 */
public class YXEntryActivity extends BaseYXEntryActivity implements ILoadDataCallBack{

	/*******************
	 * 返回第三方app根据app id创建的IYXAPI，
	 * 
	 * @return
	 */
	protected IYXAPI getIYXAPI() {
		return YXAPIFactory.createYXAPI(this, LeyueConst.YX_APP_ID);
	}
	private ScoreUploadModel mUploadModel;
	/**
	 * 易信调用调用时的触发函数
	 */
	@Override
	public void onResp(BaseResp resp) {
		LogUtil.e("Yixin.SDK.YXEntryActivity", "onResp called: errCode=" + resp.errCode + ",errStr=" + resp.errStr
				+ ",transaction=" + resp.transaction+";---LAST_SHARE_TYPE = "+UmengShareUtils.LAST_SHARE_TYPE);
		switch (resp.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Resp resp1 = (SendMessageToYX.Resp) resp;
			switch (resp1.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					mUploadModel = new ScoreUploadModel();
					mUploadModel.addCallBack(this);
					if (UserScoreInfo.YX_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE) || 
							UserScoreInfo.YX_ZONE.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
						mUploadModel.start(UmengShareUtils.LAST_SHARE_TYPE,UmengShareUtils.LAST_SHARE_SOURCEID);
					}
					break;
				case BaseResp.ErrCode.ERR_COMM:
					Toast.makeText(YXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					Toast.makeText(YXEntryActivity.this, "分享取消", Toast.LENGTH_LONG).show();
					break;
				case BaseResp.ErrCode.ERR_SENT_FAILED:
					Toast.makeText(YXEntryActivity.this, "发送失败", Toast.LENGTH_LONG).show();
					break;
				default:
					Toast.makeText(YXEntryActivity.this, "分享失败", Toast.LENGTH_LONG).show();
					break;
			}
		}
		finish();
	}

	/**
	 * 易信调用调用时的触发函数
	 */
	@Override
	public void onReq(BaseReq req) {
		Log.i("YX-SDK-Client", "onReq called: transaction=" + req.transaction);
		switch (req.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Req req1 = (SendMessageToYX.Req) req;
			Toast.makeText(YXEntryActivity.this, req1.message.title, Toast.LENGTH_LONG).show();
		}
		finish();
	}

	@Override
	public boolean onStartFail(String tag, String state, Object... params) {
		LogUtil.e("tag = "+tag+",state ="+state);
		return false;
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		Toast.makeText(YXEntryActivity.this, R.string.share_record_fail_tip, Toast.LENGTH_LONG);
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if (!isCancel && result!=null) {
			if (tag.equals(mUploadModel.getTag())) {
				ScoreUploadResponseInfo info = (ScoreUploadResponseInfo) result;
				CommonUtil.handleForShareTip(YXEntryActivity.this, info);
				finish();
			}
		}
		return false;
	}
}
