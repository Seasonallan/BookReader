package com.lectek.android.lereader.share;

import android.content.Context;
import android.graphics.Bitmap;

import com.lectek.android.lereader.lib.share.ShareListener;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.WXMutilMediaWrapper;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareWeiXin extends BaseShare {
	private WXMutilMediaWrapper wrapper = null;
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	public ShareWeiXin(Context context){
		wrapper = WXMutilMediaWrapper.getWrapperInstance();
		api = WXAPIFactory.createWXAPI(context, LeyueConst.WX_APP_ID, false);
		api.registerApp(LeyueConst.WX_APP_ID);
	}
	
	@Override
	public void sendText(MutilMediaInfo info) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
    	SendMessageToWX.Req req = wrapper.getSendTextReq(info.getText(), info.getType());
    	api.sendReq(req);
	}
	
	@Override
	public void sendTextWithPic(MutilMediaInfo info) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
    	SendMessageToWX.Req req = null;
		req = wrapper.getSendWebReq(info.getLinkUrl(), info.getType(), info.getTitle(),
				info.getText(), info.getFilePath());
    	api.sendReq(req);
	}

	/**
	 * 注册到微信
	 * （只需传APPID）
	 * @param context
	 * @param info
	 * @param listener
	 */
	public void registerApp(MutilMediaInfo info,
			ShareListener listener){
		api.registerApp(LeyueConst.WX_APP_ID);
	}
	
	public boolean isWxInstall(){
		return api.isWXAppInstalled();
	}
	
	public boolean isSupportVersion(){
		return api.isWXAppSupportAPI();
	}

	@Override
	public void sendTextWithBtimap(MutilMediaInfo info, Bitmap bitmap) {
		SendMessageToWX.Req req = null;
		req = wrapper.getSendWebReq(info.getLinkUrl(), info.getType(), info.getTitle(),
				info.getText(), bitmap);
    	api.sendReq(req);
	}
}
