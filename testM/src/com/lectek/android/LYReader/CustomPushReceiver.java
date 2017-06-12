package com.lectek.android.LYReader;

import android.content.Context;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.push.PushManager;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * 信鸽毁掉接收器
 * @author ljp
 */
public class CustomPushReceiver extends XGPushBaseReceiver {
	/**
	 * 注册结果
	 */
	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult registerMessage) {
        LogUtil.i(errorCode+ ">> ICD>> onRegisterResult "+registerMessage);
	}

	/**
	 * 反注册结果
	 */
	@Override
	public void onUnregisterResult(Context context, int errorCode) {

	}

	/**
	 * 设置标签操作结果
	 */
	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
        LogUtil.i(errorCode+ ">> ICD>> onSetTagResult "+tagName);
	}

	/**
	 * 删除标签操作结果
	 */
	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {

	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {

	}

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
        if (message != null){
            PushManager.onMessageClicked(context, message.getMsgId()+"");
        }
	}

    /**
     * 接受到通知并展示
     * @param context
     * @param notifiShowedRlt
     */
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
        if (notifiShowedRlt != null){
            PushManager.onReceiveAMessage(context, notifiShowedRlt.getMsgId()+"",
                    notifiShowedRlt.getTitle(), notifiShowedRlt.getContent(), notifiShowedRlt.getCustomContent());
        }
	}
}
