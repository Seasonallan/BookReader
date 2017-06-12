/*
 * ========================================================
 * ClassName:UmengShareUtils.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-11-27     chendt          #00000       create
 */
package com.lectek.android.lereader.share.util;

import im.yixin.sdk.api.SendMessageToYX;

import java.util.ArrayList;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.ShareBoradInfo;
import com.lectek.android.lereader.lib.share.entity.UmengShareInfo;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.permanent.ShareConfig;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.ToastUtil;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class UmengShareUtils {
	// 首先在您的Activity中添加如下成员变量
	public final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",RequestType.SOCIAL);
	// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
//	private	String appID = LeyueConst.WX_APP_ID;
	
	/**点击好友分享的内容框时，跳转的链接地址*/
	public static String contentUrl = "http://www.leread.com/";
	
	/**记录当前分享类型*/
	public static String LAST_SHARE_TYPE = "";
	
	/**记录当前分享资源id*/
	public static String LAST_SHARE_SOURCEID = "";
	
	public static Context shareContext; 
	
	/**使用单例导致会出现 context 相应的bug*/
//	private UmengShareUtils(){}
//	private static UmengShareUtils shareUtils = new UmengShareUtils();
//	public static UmengShareUtils getInstance(){
//		return shareUtils;
//	}
	
	/**
	 * 友盟社会化分享 默认初始化所有
	 * @param activity
	 */
	public void baseInit(Activity activity) {
	/**SSO_新浪分享*/
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	/**微信分享*/
		// 添加微信平台，参数1为当前Activity, 参数2为用户申请的AppID, 参数3为点击分享内容跳转到的目标url
//		mController.getConfig().supportWXPlatform(activity,appID, contentUrl);     
		// 支持微信朋友圈
//		mController.getConfig().supportWXCirclePlatform(activity,appID, contentUrl) ;
	/**QQ好友分享*/
		//  参数1为当前Activity， 参数2为QQ的APP ID，参数3为用户点击分享内容时跳转到的目标地址
		mController.getConfig().supportQQPlatform(activity, ShareConfig.QQ_APP_ID, contentUrl);
	/**SSO_QQ空间分享*/
		mController.getConfig().setSsoHandler(new QZoneSsoHandler(activity));
	/**SSO_腾讯微博分享*/
		//设置腾讯微博SSO handler
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		mController.getConfig().closeToast();
	}
	
	public void setShareInfo(Activity activity,UmengShareInfo info){
		// 设置分享内容
		mController.setShareContent(info.getShareText());
		// 设置分享图片, 参数2为图片的url地址
		if (!TextUtils.isEmpty(info.getSharePicUrl())) {
			mController.setShareMedia(new UMImage(activity, info.getSharePicUrl()));
		}
		//设置分享图片，参数2为本地图片的资源引用
		mController.setShareMedia(new UMImage(activity,CommonUtil.drawableToBitmap(activity.getResources().getDrawable(R.drawable.share_icon))));
		//设置分享图片，参数2为本地图片的路径(绝对路径)
//		mController.setShareMedia(new UMImage(mActivity, BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));
	}
	
	/**
	 * 设置邮件分享 标题内容。
	 * @param title
	 */
	public void setMailSubjectTitle(String title){
		mController.getConfig().setMailSubject(title);
	}
	
	/**
	 * 使用本地图片，不处理，可能导致发送失败
	 * @param activity
	 * @param info
	 * @param sourceId
	 */
	public void setShareInfo(Activity activity,UmengShareInfo info,Bitmap bitmap){
		// 设置分享内容
		mController.setShareContent(info.getShareText());
		if(bitmap != null){
			//设置分享图片，参数2为本地图片的资源引用
			mController.setShareMedia(new UMImage(activity, bitmap));
		}else if(!TextUtils.isEmpty(info.getSharePicUrl())){
			// 设置分享图片, 参数2为图片的url地址
			mController.setShareMedia(new UMImage(activity, info.getSharePicUrl()));
		}
		//设置分享图片，参数2为本地图片的路径(绝对路径)
//		mController.setShareMedia(new UMImage(mActivity, BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));
	}
	
	
	public void shareForQQ(QQShareContent qqShareContent){
		mController.setShareContent(null);
		mController.setShareMedia(qqShareContent);
	}
	
	/**获取onActivityResult 回调的 SsoHander*/
	public UMSsoHandler getSsoHandler(int requestCode){
		 return mController.getConfig().getSsoHandler(requestCode);
	}
	
	/**
	 * 调用分享
	 * @param activity
	 */
	public void share(Activity activity){
		// 打开平台选择面板，参数2为打开分享面板时是否强制登录,false为不强制登录
		mController.openShare(activity, false);
	}
	
	/**
	 * 使用自定义分享面板，编辑界面和接口使用友盟的
	 * @param activity
	 * @param type SHARE_MEDIA 枚举类型
	 */
	public void userStep2Share(final Activity activity,SHARE_MEDIA type,SnsPostListener listener){
		// 参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
		try {
			mController.postShare(activity,type,listener); 
		} catch (java.lang.reflect.UndeclaredThrowableException e) {
			ToastUtil.showToast(activity, "分享失败 ，异常：UndeclaredThrowableException");
			dismissPop();
		} catch (Exception e) {
			ToastUtil.showToast(activity, "分享失败 ，异常！");
			dismissPop();
		}
	}
	
	public void shareToQzone(final Activity activity){
		mController.postShare(activity, SHARE_MEDIA.QZONE,
				new SnsPostListener() {
					@Override
					public void onComplete(SHARE_MEDIA arg0, int arg1,
							SocializeEntity arg2) {
						Toast.makeText(activity, "分享完成", Toast.LENGTH_SHORT)
								.show();
					}

					@Override
					public void onStart() {
//						Toast.makeText(activity, "开始分享", Toast.LENGTH_SHORT)
//								.show();
					}
				});
	}
	
	PopupWindow popupWindow;
	
	/**
	 * 自定义分享面板
	 * @param activity
	 * @param parent 显示在该控件下方
	 * @param onItemClickListener 点击项处理。
	 * @return
	 */
	public PopupWindow showPopupWindow(final Activity activity,final View viewParent,final YXHanlder hanlder,final SnsPostListener listener) {  
		if (popupWindow == null) {
			View view = LayoutInflater.from(activity).inflate(R.layout.pop_share_view, null);
			GridView gridView = (GridView) view.findViewById(R.id.gridview);
			view.findViewById(R.id.exit_block).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissPop();
				}
			});
			ShareBoardAdapter adapter = new ShareBoardAdapter(activity);
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					// 处理分享 调用对应的平台。
					if(view.getTag(R.id.share_board_item_key) instanceof ShareBoradInfo){
						ShareBoradInfo info = (ShareBoradInfo) view.getTag(R.id.share_board_item_key);
						SHARE_MEDIA type = null;
						int xType = -1;
						switch (info.img) {
						case R.drawable.umeng_socialize_yixin:
							LAST_SHARE_TYPE = UserScoreInfo.YX_FRIEND;
							type = SHARE_MEDIA.GENERIC;
							xType = SendMessageToYX.Req.YXSceneSession;
							MobclickAgent.onEvent(activity, ShareConfig.EVENT_SOCIALIZE_YIXIN);
							break;
						case R.drawable.umeng_socialize_yxcircle:
							LAST_SHARE_TYPE = UserScoreInfo.YX_ZONE;
							type = SHARE_MEDIA.GENERIC;
							xType = SendMessageToYX.Req.YXSceneTimeline;
							MobclickAgent.onEvent(activity, ShareConfig.EVENT_SOCIALIZE_YXCIRCLE);
							break;
						case R.drawable.umeng_socialize_wechat:
							LAST_SHARE_TYPE = UserScoreInfo.WX_FRIEND;
							type = SHARE_MEDIA.WEIXIN;
							xType = SendMessageToWX.Req.WXSceneSession;
							break;
						case R.drawable.umeng_socialize_wxcircle:
							LAST_SHARE_TYPE = UserScoreInfo.WX_ZONE;
							type = SHARE_MEDIA.WEIXIN_CIRCLE;
							xType = SendMessageToWX.Req.WXSceneTimeline;
							break;
						case R.drawable.umeng_socialize_sina_on:
							LAST_SHARE_TYPE = UserScoreInfo.SINA;
							type = SHARE_MEDIA.SINA;
							break;
						case R.drawable.umeng_socialize_qq_on:
							LAST_SHARE_TYPE = UserScoreInfo.QQ_FRIEND;
							type = SHARE_MEDIA.QQ;
							if (hanlder!=null) {
								hanlder.handleForQQ();
							}
							break;
						case R.drawable.umeng_socialize_qzone_on:
							//FIXME:扩展这些类型后，记得添加 LAST_SHARE_TYPE的状态
							type = SHARE_MEDIA.QZONE;
							break;
						case R.drawable.umeng_socialize_tx_on:
							type = SHARE_MEDIA.TENCENT;
							break;
						case R.drawable.umeng_socialize_sms:
							type = SHARE_MEDIA.SMS;
							if (hanlder!=null) {
								hanlder.handleForSMS();
							}
							break;
						case R.drawable.umeng_socialize_gmail:
							type = SHARE_MEDIA.EMAIL;
							break;
						case R.drawable.umeng_socialize_douban_on:
							type = SHARE_MEDIA.DOUBAN;
							break;
						case R.drawable.umeng_socialize_renren_on:
							type = SHARE_MEDIA.RENREN;
							break;
							
						default:
							type = SHARE_MEDIA.GENERIC;
							break;
						}
						LogUtil.e("LAST_SHARE_TYPE---"+LAST_SHARE_TYPE);
						switch (type) {
						case GENERIC:
							if (xType == SendMessageToYX.Req.YXSceneSession || xType == SendMessageToYX.Req.YXSceneTimeline) {
								if (hanlder!=null) {
									hanlder.handleForYiXin(xType);
								}else {
									
								}
							}
							break;
						case WEIXIN_CIRCLE:	
						case WEIXIN:
							if (hanlder!=null) {
								hanlder.handleForWeiXin(xType);
							}
							break;
						case QZONE:
							if (hanlder!=null) {
								hanlder.handleForQQZONE();
							}
							break;
						default:
							if (activity!=null) {
								userStep2Share(activity, type,listener);
							}else {
								if (view.getParent()!=null ) {
									if(view.getParent() instanceof Activity){
										userStep2Share((Activity)view.getParent(), type,listener);
									}
								}else {
									dismissPop();
								}
							}
							break;
						}
					}
					dismissPop();
					if (hanlder!=null) {
						hanlder.saveSourceId();
					}
				}
				
			});
			popupWindow = new PopupWindow(activity);  
			popupWindow.setTouchable(true); // 设置PopupWindow可触摸  
			popupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸  
			popupWindow.setContentView(view);
//			popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.share_item_bg));
			popupWindow.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.transparent_background));
			popupWindow.setAnimationStyle(R.style.umeng_socialize_shareboard_animation);   //设置 popupWindow 动画样式
			popupWindow.setWidth(LayoutParams.MATCH_PARENT);  
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
        popupWindow.showAtLocation(viewParent, Gravity.FILL, 0, 0);  
        popupWindow.update();  
        return popupWindow;
    }
	
	private void dismissPop(){
		if (popupWindow!=null) {
			popupWindow.dismiss();
		}
	}
	
	public class ShareBoardAdapter extends BaseAdapter{
		private ArrayList<ShareBoradInfo> infos;
		private Activity mActivity;
		public ShareBoardAdapter(Activity activity){
			mActivity = activity;
			infos = getShareBoradInfos(
							CommonUtil.getIntArray(mActivity, R.array.share_board_item_img),
							activity.getResources().getStringArray(R.array.share_board_item_text));
			if (!LeyueConst.isLeyueVersion) {
				//非乐阅版本，需要过滤微信，易信数据
				for (ListIterator<ShareBoradInfo> iterator = infos.listIterator();iterator.hasNext();) {
					ShareBoradInfo info = iterator.next();
					if (info!=null) {
						switch (info.img) {
						case R.drawable.umeng_socialize_yixin:
						case R.drawable.umeng_socialize_yxcircle:
						case R.drawable.umeng_socialize_wechat:
						case R.drawable.umeng_socialize_wxcircle:
							iterator.remove();
							break;

						default:
							break;
						}
					}
					
				}
			}
		}
		
		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ShareBoradInfo info = infos.get(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mActivity).inflate(R.layout.umeng_socialize_shareboard_item, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) convertView.findViewById(R.id.umeng_socialize_shareboard_image);
				viewHolder.text = (TextView) convertView.findViewById(R.id.umeng_socialize_shareboard_pltform_name);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.img.setImageResource(info.img);
			viewHolder.text.setText(info.text);
			convertView.setTag(R.id.share_board_item_key, info);
			return convertView;
		}
	}
	
	/**
	 * 根据本地资源 获取图文信息列表
	 * @param imgArray
	 * @param textArray
	 * @return
	 */
	private ArrayList<ShareBoradInfo> getShareBoradInfos(int[] imgArray,String[] textArray){
		ArrayList<ShareBoradInfo> infos = null;
		if (imgArray.length == textArray.length && imgArray.length > 0) {
			infos = new ArrayList<ShareBoradInfo>();
			for (int i = 0; i < textArray.length; i++) {
				ShareBoradInfo info = new ShareBoradInfo();
				info.img = imgArray[i];
				info.text = textArray[i];
				infos.add(info);
			}
		}
		return infos;
	}
	
	public class ViewHolder{
		public ImageView img;
		public TextView text;
	}
	
	public static interface YXHanlder{
		public void handleForYiXin(int type);
		public void handleForWeiXin(int type);
		public void handleForQQ();
		public void handleForQQZONE();
		public void handleForSMS();
		/**存储当前分享的资源id*/
		public void saveSourceId();
	}
	
	public static void popWindowShow(View parent,PopupWindow popupWindow){
		popupWindow.showAtLocation(parent, Gravity.FILL, 0, 0);
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
	}
	
	/**
	 * 对指定平台删除授权。
	 * @param activity
	 * @param type 取消授权平台
	 * @param listener 返回结果监听器。status == 200 为成功，其余为失败情况。
	 * @note 取消授权后，请更新本地存储当前绑定账号的相应的信息。
	 */
	public void deleteOauth(Activity activity,SHARE_MEDIA type,SocializeClientListener listener){
		mController.deleteOauth(activity, type,listener);
	}
	
	/**
	 * 进行指定平台授权。
	 * @param activity
	 * @param type 授权平台
	 * @param listener 返回结果监听器。onComplete 中。
	 * <br/>
	 * if (value != null && !TextUtils.isEmpty(uid)) {成功}else{失败}
	 * <br/>
	 * String uid = value.getString("uid");
	 * 
	 * @note onError;onCancel状态也要处理
	 */
	public void getOauth(Activity activity,SHARE_MEDIA type,UMAuthListener listener){
		mController.doOauthVerify(activity, type,listener);
	}
}
