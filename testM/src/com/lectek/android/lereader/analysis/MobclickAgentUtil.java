package com.lectek.android.lereader.analysis;

import android.content.Context;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟自定义事件汇聚[用于方便寻找使用点]
 * Created by ljp on 14-5-22.
 */
public class MobclickAgentUtil {

    /** 是否开启*/
    private static boolean ON = true;

    private static Context getContext(){
        return MyAndroidApplication.getInstance();
    }

    /**
     * 上传自定义统计事件
     * @param event
     * @param params
     */
    public static void uploadUmengMsg(String event, String... params){
        if(ON){
            HashMap<String, String > msgMaps = new HashMap<String, String>();
            if(params != null && params.length % 2 == 0){
                for (int i = 0; i< params.length/2; i++){
                    msgMaps.put(params[i * 2], params[i*2 +1]);
                }
            }
            MobclickAgent.onEvent(getContext(), event, msgMaps);
        }
    }
    /**
     * 上传自定义统计事件
     * @param event
     * @param msgMaps
     */
    public static void uploadUmengMsg(String event, HashMap<String, String> msgMaps){
        if(ON){
            MobclickAgent.onEvent(getContext(), event, msgMaps);
        }
    }

    /**
     * 获取书籍类型事件ID
     * @param type
     * @return
     */
    public static String findTypePosition(int id,String type){
        if(type != null){
            String[] types = getContext().getResources().getStringArray(id);
            for (int i = 0; i < types.length; i++){
                if (type.equals(types[i])){
                    return (i + 1)+"";
                }
            }
        }
        return "";
    }

}
