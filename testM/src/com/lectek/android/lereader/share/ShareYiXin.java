package com.lectek.android.lereader.share;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import android.content.Context;
import android.graphics.Bitmap;

import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.share.entity.MutilMediaInfo;
import com.lectek.android.lereader.share.util.YXMutilMediaWrapper;

public class ShareYiXin extends BaseShare {
	private YXMutilMediaWrapper wrapper = null;
	// IYXAPI 是第三方app和y信通信的openapi接口
    private IYXAPI api;
	public ShareYiXin(Context context){
		wrapper = YXMutilMediaWrapper.getWrapperInstance();
		api = YXAPIFactory.createYXAPI(context, LeyueConst.YX_APP_ID);
		api.registerApp();
	}
	
	@Override
	public void sendText(MutilMediaInfo info) {
		// 通过YXAPIFactory工厂，获取IYXAPI的实例
    	SendMessageToYX.Req req = wrapper.getSendTextReq(info.getText(), info.getType());
    	api.sendRequest(req);
	}
	
	@Override
	public void sendTextWithPic(MutilMediaInfo info) {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		SendMessageToYX.Req req = null;
		req = wrapper.getSendWebReq(info.getLinkUrl(), info.getType(), info.getTitle(),
				info.getText(), info.getFilePath());
    	api.sendRequest(req);
	}
	
	public boolean isYxInstall(){
		return api.isYXAppInstalled();
	}

	@Override
	public void sendTextWithBtimap(MutilMediaInfo info,Bitmap bitmap) {
		SendMessageToYX.Req req = null;
		req = wrapper.getSendWebReq(info.getLinkUrl(), info.getType(), info.getTitle(),
				info.getText(), bitmap);
    	api.sendRequest(req);
	}
}
