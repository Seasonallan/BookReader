package com.lectek.android.lereader.push;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.lectek.android.lereader.net.response.NotifyCustomInfo;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.util.NotifyCustomInfoDB;
import com.lectek.android.lereader.ui.specific.WelcomeActivity;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 *  推送管理，
 *  信鸽推送，需要在根目录下创建receiver接收处理消息。
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class PushManager {

    /**
     * 在应用启动的时候调用，如MyAndroidApplication的onCreate中
     * @param context
     */
    public static void register(Context context){
        //umengMessageInit();
        XGPushManager.registerPush(context);
        // XGPushManager.setTag(this, "TAG_SIG");
        XGPushConfig.enableDebug(context, LeyueConst.IS_DEBUG);
    }

    /**
     * 接收到一条推送消息，保存到本地。 .CustomPushReceiver中的通知栏展示调用
     * @param context
     * @param id
     * @param title
     * @param content
     * @param params
     */
    public static void onReceiveAMessage(Context context, String id, String title, String content, String params){
        try {
            String msg_id = id;
            NotifyCustomInfo info = new NotifyCustomInfo();
            info.setMsgTitle(title);
            info.setMsgDecription(content);
            info.setMsgType(NotifyCustomInfo.TYPE_MY_INFO);
            String keyValue = params;
            if(!TextUtils.isEmpty(keyValue)){
                JSONObject json = new JSONObject(keyValue);
                Iterator<String> itr = json.keys();
                if(itr.hasNext()){
                    String key = itr.next();
                    String value = json.getString(key);
                    info.setMsgArgs(value);
                    info.setMsgType(key.equals(WelcomeActivity.GOTO_CONTENTINFO) ? NotifyCustomInfo.TYPE_BOOK:
                            key.equals(WelcomeActivity.GOTO_SUBJECTINFO) ? NotifyCustomInfo.TYPE_SUBJECT:
                                    NotifyCustomInfo.TYPE_MY_INFO
                    );
                }
            }
            String custom = info.toJsonObject().toString();
            NotifyCustomInfoDB.getInstance().setMessageInfo(msg_id, custom);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知栏的推送消息点击触发，更新数据库阅读状态。 .CustomPushReceiver中的通知栏点击调用
     * @param context
     * @param id
     */
    public static void onMessageClicked(Context context, String id){
        NotifyCustomInfoDB.getInstance().updateMsgStatus(id, true);
    }

}
