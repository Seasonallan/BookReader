/*
 * ========================================================
 * ClassName:MutilMediaWrapper.java* 
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
 * 2013-5-2     chendt          #00000       create
 */
package com.lectek.android.lereader.share.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.utils.CommonUtil;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXEmojiObject;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXVideoObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * @description
	分享到微信，发送多媒体 包装器。<p>
	使用微信开放平台发送的媒体对象中，<br>
	<b>网页，音频和视频</b>只能是网络URL，<b>图片</b>可发送本地路径，本地字节数据，网络URL，<br>
	<b>文件</b>可以发送本地字节数据和本地路径，附带在媒体对象中的字节数组，<br>
 * @author chendt
 * @date 2013-5-2
 * @Version 1.0
 * 
 * @add
 *  Q：为什么使用微信SDK分享信息给微信好友，有的消息发送成功，有的消息发送失败（打不开微信）？(New)
	A：这是因为SDK协议中对缩略图的大小作了限制，大小不能超过32K。另外限制的还有title、description等参数的大小。
 	Android开发者请见微信开放平台网站Android手册的“WXMediaMessage”页面。
 */
public class WXMutilMediaWrapper {
	
	private static final int THUMB_SIZE = 150;
	
	private final String TEXT = "text";
	private final String VIDEO = "video";
	private final String MUSIC = "music";
	private final String IMG = "img";
	private final String WEB = "web";
	private final String APPDATA = "appdata";
	private final String EMOJI = "emoji";
	
	private static WXMutilMediaWrapper mWrapper = new WXMutilMediaWrapper();
	
	private WXMutilMediaWrapper(){}
	
	public static WXMutilMediaWrapper getWrapperInstance(){
		return mWrapper;
	}
	
	/**
	 * 获取发送 指定文本的 请求对象 req
	 * @param text
	 * @param scene 场景 （分享到好友圈还是好友）SendMessageToWX.Req.WXSceneSession ；SendMessageToWX.Req.WXSceneTimeline
	 * @return
	 */
	public SendMessageToWX.Req getSendTextReq(String text,int scene){
		// 1
		WXTextObject wxTextObject = new WXTextObject();
		wxTextObject.text = text;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxTextObject;
		//发送文本消息时 title不起作用。
		wxMediaMessage.description = text;
		
		// 3 
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(TEXT);// transaction字段用于唯一标识一个请求
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * 获取发送指定 drawable 的 req对象
	 * @param drawable
	 * @param scene 场景 （分享到好友圈还是好友）SendMessageToWX.Req.WXSceneSession ；SendMessageToWX.Req.WXSceneTimeline
	 * @return
	 */
	public SendMessageToWX.Req getSendImgReq(Bitmap bitmap, int scene){
		Bitmap bm = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
		// 1
		WXImageObject wxImageObject = new WXImageObject(bitmap);
		bitmap.recycle();//是否有效？
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxImageObject;
		wxMediaMessage.thumbData = getImageDataByLimitSize(bitmap, 100, 32000); //设置缩略图
		
		// 3 
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(IMG);// transaction字段用于唯一标识一个请求
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * 获取发送本地路径图片 的 req对象 
	 * @param path 
	 * @param scene 好友圈，好友
	 * @param isPath
	 * @return
	 * @throws FileNotFoundException 当无法获取指定图片时，抛该异常，上层界面做toast 提示。
	 */
	public SendMessageToWX.Req getSendImgReq(String path, int scene,boolean isPath) throws FileNotFoundException{
		if (!isPath) {
			return null;
		}
		File file = new File(path);
		if (!file.exists()) {
//			String tip = getString(R.string.send_img_file_not_exist);
//			Toast.makeText(SendToWXActivity.this, tip + " path = " + path, Toast.LENGTH_LONG).show();
			throw new FileNotFoundException("send_img_file_not_exist!");
		}
		
		// 1
		WXImageObject wxImageObject = new WXImageObject();
		wxImageObject.imagePath = path;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxImageObject;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		
		wxMediaMessage.thumbData = getImageDataByLimitSize(bitmap, 100, 32000); //设置缩略图
		LogUtil.e("---wxMediaMessage.thumbData---"+wxMediaMessage.thumbData.length);
//		Bitmap thumbBmp = Bitmap.createScaledBitmap(Util.comp(bitmap), THUMB_SIZE, THUMB_SIZE, true);
		bitmap.recycle();
//		wxMediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true);
		
		// 3 
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(IMG);// transaction字段用于唯一标识一个请求
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * (带描述)获取发送本地路径图片 的 req对象 
	 * @param path 
	 * @param description 
	 * @param scene 好友圈，好友
	 * @param isPath
	 * @return
	 * @throws FileNotFoundException 当无法获取指定图片时，抛该异常，上层界面做toast 提示。
	 */
	public SendMessageToWX.Req getSendImgWithTextReq(String path,String description, int scene,boolean isPath) throws FileNotFoundException{
		if (!isPath) {
			return null;
		}
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException("send_img_file_not_exist!");
		}
		
		// 1
		WXImageObject wxImageObject = new WXImageObject();
		wxImageObject.imagePath = path;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxImageObject;
		wxMediaMessage.description = "This is description";
		wxMediaMessage.title = "This is title";
		
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		
		wxMediaMessage.thumbData = getImageDataByLimitSize(bitmap, 100, 32000); //设置缩略图
		LogUtil.e("---wxMediaMessage.thumbData---"+wxMediaMessage.thumbData.length);
//		Bitmap thumbBmp = Bitmap.createScaledBitmap(Util.comp(bitmap), THUMB_SIZE, THUMB_SIZE, true);
		bitmap.recycle();
//		wxMediaMessage.thumbData = Util.bmpToByteArray(thumbBmp, true);
		
		// 3 
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(IMG);// transaction字段用于唯一标识一个请求
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * 获取发送指定url 图片 的 req对象 
	 * @param url
	 * @param scene 好友圈，好友
	 * @return
	 * @throws MalformedURLException 界面toast提示。
	 * @throws IOException
	 */
	public SendMessageToWX.Req getSendImgReq(String url, int scene) throws MalformedURLException, IOException{
		// 1
		WXImageObject wxImageObject = new WXImageObject();
		wxImageObject.imageUrl = url;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxImageObject;
		
		Bitmap bitmap = BitmapFactory.decodeStream( new URL(url).openStream());
		
		wxMediaMessage.thumbData = getImageDataByLimitSize(bitmap, 100, 32000); //设置缩略图
		bitmap.recycle();
		
		// 3 
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(IMG);// transaction字段用于唯一标识一个请求
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * @param url
	 * @param scene 好友圈，好友
	 * @param title 音乐标题
	 * @param description 关于音乐的具体描述
	 * @param bitmap 用于显示音乐配图
	 * @param isBandUrl 是否是低带宽URL
	 * @return
	 */
	public SendMessageToWX.Req getSendMusicReq(String url,int scene,String title,String description,
			Bitmap bitmap,boolean isBandUrl){
		// 1
		WXMusicObject wxMusicObject = new WXMusicObject();
		if (isBandUrl) {
			wxMusicObject.musicLowBandUrl = url;
		}else {
			wxMusicObject.musicUrl = url;
		}
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxMusicObject;
		wxMediaMessage.title = title;
		wxMediaMessage.description = description;
		wxMediaMessage.thumbData = Util.bmpToByteArray(bitmap, true);
		
		// 3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(MUSIC);
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * @param url
	 * @param scene 好友圈，好友
	 * @param title 视频标题
	 * @param description 视频描述
	 * @param bitmap 视频配图
	 * @param isBandUrl 是否是低带宽url
	 * @return
	 */
	public SendMessageToWX.Req getSendVideoReq(String url,int scene,String title,String description,
			Bitmap bitmap,boolean isBandUrl){
		// 1
		WXVideoObject wxVideoObject = new WXVideoObject();
		if (isBandUrl) {
			wxVideoObject.videoLowBandUrl = url;
		}else {
			wxVideoObject.videoUrl = url;
		}
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxVideoObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		wxMediaMessage.thumbData = Util.bmpToByteArray(bitmap, true);
		
		// 3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.transaction = buildTransaction(VIDEO);
		req.scene = handleScene(scene);
		return req;
	}
	
	/**
	 * 获取 发送指定网页 的req对象 
	 * @param url
	 * @param scene 好友圈，好友
	 * @param title 网页标题
	 * @param description 网页具体描述
	 * @param bitmap 网页配图
	 * @return
	 */
	public SendMessageToWX.Req getSendWebReq(String url,int scene,String title,
					String description,Bitmap bitmap){
		// 1
		WXWebpageObject wxWebpageObject = new WXWebpageObject();
		wxWebpageObject.webpageUrl = url;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxWebpageObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		wxMediaMessage.thumbData = Util.bmpToByteArray(bitmap, true);
		
		// 3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(WEB);
		return req;
		
	}
	
	/**
	 * 获取 发送指定网页 的req对象 
	 * @param url
	 * @param scene 好友圈，好友
	 * @param title 网页标题
	 * @param description 网页具体描述
	 * @param path 网页配图地址
	 * @return
	 */
	public SendMessageToWX.Req getSendWebReq(String url,int scene,String title,
			String description,String path){
		// 1
		WXWebpageObject wxWebpageObject = new WXWebpageObject();
		wxWebpageObject.webpageUrl = url;
		
		// 2
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxWebpageObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		
//		Bitmap bitmap = BitmapFactory.decodeFile(path);
		Bitmap bitmap = null;
		if (!TextUtils.isEmpty(path)) {
			bitmap = CommonUtil.getImageInSdcard(path);
		}
		wxMediaMessage.thumbData = getImageDataByLimitSize(bitmap, 100, 32000); //设置缩略图
		if (bitmap!=null) {
			bitmap.recycle();
		}
		
		// 3
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(WEB);
		return req;
		
	}
	
	/**
	 *  微信默认会是点击则给出下载界面，<br>
	          下载后存储的路径是在/sdcard/Tecent/MicroMsg/本机微信帐号（一般是一串字母数字的组合体）/attachment文件夹下，<br>
		这个路径不需要自己去记录，微信会在媒体对象的filepath字段中记录，此时一定注意的是当你响应微信的show请求时，<br>
		媒体对象中的filedata字段是为空的，即使你在发送的时候附带在了这个字段中，如前面所说，只能用filepath这个字段。
	*/
	
//	 appdata类型 可以回调自己的APP
//	 *  就只是告诉微信这个消息点击时需要调用第三方应用，
//	 *  而其他类型的媒体对象被点击时所显示的是微信的默认页面
	
	/**
	 * （应该是拍照后，直接调用）获取 拍照的图片 的req对象。
	 * @param path 照片存储的本地路径
	 * @param scene 
	 * @param title 照片名称
	 * @param description 照片描述
	 * @param extInfo 照片扩展信息
	 * @return
	 * @throws NullPointerException 无法获取到拍照的照片时，界面提示。
	 */
	public SendMessageToWX.Req getSendAppDataByCameraReq(String path,int scene,String title,String description,
			String extinfo) throws NullPointerException{
		WXAppExtendObject wxAppExtendObject = new WXAppExtendObject();
		wxAppExtendObject.filePath = path;
		wxAppExtendObject.extInfo = extinfo;
		
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxAppExtendObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		wxMediaMessage.setThumbImage(Util.extractThumbNail(path, THUMB_SIZE, THUMB_SIZE, true));
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(APPDATA); 
		return req;
	}
	
	/**
	 * @param path
	 * @param scene
	 * @param title
	 * @param description
	 * @param extinfo
	 * @return
	 * @throws NullPointerException 无法获取到本地照片或者照片太大，界面提示。
	 */
	public SendMessageToWX.Req getSendAppDataByLocalReq(String path,int scene,String title,String description,
			String extinfo)throws NullPointerException{
		WXAppExtendObject wxAppExtendObject = new WXAppExtendObject();
		wxAppExtendObject.fileData = Util.readFromFile(path, 0, -1);;
		wxAppExtendObject.extInfo = extinfo;
		
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxAppExtendObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		wxMediaMessage.setThumbImage(Util.extractThumbNail(path, THUMB_SIZE, THUMB_SIZE, true));
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(APPDATA); 
		return req;
	}
	
	/**
	 * 获取 不带附件的 req对象
	 * @param scene
	 * @param title
	 * @param description
	 * @param extinfo
	 * @return
	 */
	public SendMessageToWX.Req getSendAppDataNoAtmReq(int scene,String title,String description,String extinfo){
		WXAppExtendObject wxAppExtendObject = new WXAppExtendObject();
		wxAppExtendObject.extInfo = extinfo;
		
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject = wxAppExtendObject;
		wxMediaMessage.description = description;
		wxMediaMessage.title = title;
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(APPDATA); 
		return req;
	}
	
	/**
	 * @deprecated 有问题<br>
	 * 获取发送 emoji表情 的req 对象
	 * @param gifPath
	 * @param jpgPath
	 * @param scene
	 * @param decription
	 * @param title
	 * @param isLocalPath 本地路径 、本地表情二进制数据
	 * @return
	 */
	private SendMessageToWX.Req getEmojiReq(String gifPath,String jpgPath,int scene,String decription,String title,
			boolean isLocalPath){
		WXEmojiObject wxEmojiObject = new WXEmojiObject();
		if (isLocalPath) {
			wxEmojiObject.emojiData = Util.readFromFile(gifPath, 0, (int) new File(gifPath).length());
		}else {
			wxEmojiObject.emojiPath = gifPath;
		}
		
		WXMediaMessage wxMediaMessage = new WXMediaMessage();
		wxMediaMessage.mediaObject =wxEmojiObject;
		wxMediaMessage.description = decription;
		wxMediaMessage.title = title;
		wxMediaMessage.thumbData = Util.readFromFile(jpgPath, 0, (int) new File(jpgPath).length());
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = wxMediaMessage;
		req.scene = handleScene(scene);
		req.transaction = buildTransaction(EMOJI);
		return req;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * 处理分享到哪里 -- 好友圈，好友
	 * @param scene
	 * @return
	 */
	private int handleScene(int scene){
		if (scene!=1) {
			return SendMessageToWX.Req.WXSceneSession;
		}else {
			return SendMessageToWX.Req.WXSceneTimeline;
		}
	}
	
	/**
	 * bitmap转成byte[] 获得限制大小的图片JPEG数据 (用于头像)
	 * 
	 * @param bitmap
	 *            图片
	 * @param quality
	 *            起始jpeg图片质量
	 * @param limitSize
	 *            限制的图片大小
	 */
	public static byte[] getImageDataByLimitSize(Bitmap bitmap, int quality,
			int limitSize) {
		if (bitmap == null || limitSize <= 0) {
			LogUtil.e("photo err bitmap=null;limitSize=" + limitSize);
			return null;
		}
		if (quality <= 0) {
			LogUtil.e("photo err limitSize=" + limitSize + ";quality="
					+ quality + ";width=" + bitmap.getWidth() + ";height="
					+ bitmap.getHeight());
			return null;
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		byte[] bytes = baos.toByteArray();

		LogUtil.e("photo limitSize=" + limitSize + ";length="
				+ bytes.length + ";quality=" + quality + ";width="
				+ bitmap.getWidth() + ";height=" + bitmap.getHeight());
		if (bytes.length > limitSize) {
//			bytes = null;
			bytes = getImageDataByLimitSize(bitmap, quality - 10, limitSize);
		}
		return bytes;
	}
}