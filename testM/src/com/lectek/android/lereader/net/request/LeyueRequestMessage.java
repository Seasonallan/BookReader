package com.lectek.android.lereader.net.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.text.TextUtils;

import com.lectek.android.lereader.lib.api.request.GenerateOrderInfo;
import com.lectek.android.lereader.lib.net.http.ApiHttpConnect;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.utils.EncryptUtils;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.StringUtil;
import com.lectek.android.lereader.net.response.BookMarkResponse;
import com.lectek.android.lereader.net.response.DigestResponse;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.storage.dbase.mark.BookMark;
import com.lectek.android.lereader.utils.MD5;

/**
 * @description
	乐阅 服务器 请求包信息封装
 * @author chendt
 * @date 2013-9-26
 * @Version 1.0
 */
public class LeyueRequestMessage {
	
	/**
	 * 乐阅用户登录
	 * */
	public static String userLoginURL(String account, String password){
		final StringBuilder sb = new StringBuilder();
		sb.append("&account=");
		sb.append(account);
		sb.append("&password=");
		sb.append(MD5.getMD5(password.getBytes()));
		return sb.toString();
	}
	
	/**
	 * 用户注册
	 * @param source 注册来源
	 * */
	public static String userRegistURL(String account, String password, String nickname, String source
			,String version,String releaseChannel,String salesChannel,int sourceType){
		final StringBuilder sb = new StringBuilder();
		sb.append("&account=");
		sb.append(account);
		sb.append("&password=");
		sb.append(MD5.getMD5(password.getBytes()));
		if(!StringUtil.isEmpty(nickname)){//昵称可省去
			sb.append("&nickname=");
			sb.append(nickname);
		}
		sb.append("&source=");
		sb.append(source);
		sb.append("&version=");
		sb.append(version);
		if(!StringUtil.isEmpty(releaseChannel)){
			sb.append("&releaseChannel=");
			sb.append(releaseChannel);
		}
		if(!StringUtil.isEmpty(salesChannel)){
			sb.append("&salesChannel=");
			sb.append(salesChannel);
		}
		sb.append("&sourceType=");
		sb.append(sourceType);
		return sb.toString();
	}
	
	/**
	 * 书籍详情
	 * @param userId
	 * @return
	 */
	public static String packageParamGetContentInfo(String userId){
		StringBuilder sb = new StringBuilder();//这里加的话，外面组建时会在开头附加一个问号
		sb.append("userId=");
		sb.append(userId);
		return sb.toString();
	}
	
	/**
	 *  积分兑书
	 * @param userId
	 * @param bookId
	 * @param volumnId
	 * @param chapterId
	 * @param channelId 
	 * @param calType 计费类型（1：按书，2：按卷，3：按章，4：包月）
	 * @param calObj 计费对象（按书时，bookId，按卷，volumnId，按章，chapterId，按频道，channelId）
	 * @param charge
	 * @param purchaseType 支付类型（8积分订购）
	 * @param sourceType 平台（{@link #SOURCE_LEYUE}：乐阅平台、{@link #SOURCE_SURFING}：天翼阅读、
	 * 		{@link #SOURCE_ZHONGXING}：中信出版社、{@link #SOURCE_DBS}：单本书）
	 * @param version
	 * @param account 账号（客户端用户账号）= username
	 * @param calObjName 计费对象名称
	 * @param releaseChannel 发布渠道
	 * @param salesChannel 销售渠道
	 * @return
	 */
	public static String scoreExchangeBookURL(String userId, String bookId, String volumnId, String chapterId, 
			String channelId, int calType, String calObj, int charge,String purchaseType,
			int sourceType,String version,String account,String calObjName,String releaseChannel,
			String salesChannel){
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&bookId=");
		sb.append(bookId);
		sb.append("&volumnId=");
		sb.append(volumnId);
		sb.append("&chapterId=");
		sb.append(chapterId);
		sb.append("&channelId=");
		sb.append(channelId);
		sb.append("&calType=");
		sb.append(calType);
		sb.append("&calObj=");
		sb.append(calObj);
		sb.append("&charge=");
		sb.append(charge);
		sb.append("&purchaser=");
		sb.append(userId);
		sb.append("&purchaseType=");
		sb.append(purchaseType);
		sb.append("&sourceType=");
		sb.append(sourceType);
		sb.append("&version=");
		sb.append(version);
		sb.append("&account=");
		sb.append(account);
		sb.append("&calObjName=");
		sb.append(calObjName);
		sb.append("&releaseChannel=");
		sb.append(releaseChannel);
		sb.append("&salesChannel=");
		sb.append(salesChannel);
		sb.append("&authorcator=");
		String temp3DES = EncryptUtils.encryptBase643DES(
				getScoreExchangeBookSource(userId, bookId,purchaseType, 
						calType, calObj,charge, userId,sourceType,version,account,calObjName), LeyueConst.KEY);
		try {
			String tempURLENC = URLEncoder.encode(temp3DES,"UTF-8");
			LogUtil.v("tempURLENC", tempURLENC);
			sb.append(tempURLENC);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LogUtil.v("scoreExchangeBookURL", sb.toString());
		return sb.toString();
	}
	
	/**
	 * 订购信息(新接口规则-单本书)
	 * @author chendt
	 * */
	public static String bookOrderURL(String userId, String bookId, String volumnId, String chapterId, 
			String channelId, int calType, String calObj, String charge,int sourceType,String version,
			String calObjName,String releaseChannel,String salesChannel,int purchaseType){
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&bookId=");
		sb.append(bookId);
		sb.append("&volumnId=");
		sb.append(volumnId);
		sb.append("&chapterId=");
		sb.append(chapterId);
		sb.append("&channelId=");
		sb.append(channelId);
		sb.append("&calType=");
		sb.append(calType);
		sb.append("&calObj=");
		sb.append(calObj);
		sb.append("&charge=");
		sb.append(charge);
		sb.append("&purchaser=");
		sb.append(userId);
		sb.append("&sourceType=");
		sb.append(sourceType);
		sb.append("&version=");
		sb.append(version);
		if (!StringUtil.isEmpty(calObjName)) {
			sb.append("&calObjName=");
			sb.append(calObjName);
		}
		sb.append("&releaseChannel=");
		sb.append(releaseChannel);
		sb.append("&salesChannel=");
		sb.append(salesChannel);
		sb.append("&purchaseType=");
		sb.append(purchaseType);
		sb.append("&authorcator=");
		String temp3DES = EncryptUtils.encryptBase643DES(getSource(userId, bookId, volumnId, chapterId, channelId, calType, calObj, userId, charge,sourceType,version), LeyueConst.KEY);
		try {
			String tempURLENC = URLEncoder.encode(temp3DES,"UTF-8");
			LogUtil.v("tempURLENC", tempURLENC);
			sb.append(tempURLENC);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		LogUtil.v("bookOrderURL", sb.toString());
		return sb.toString();
	}
	
	private static String getScoreExchangeBookSource(String userId,String bookId, String purchaseType, 
							int calType, String calObj,int charge,String purchaser,int sourceType,
							String version,String account,String calObjName){
		StringBuffer sb = new StringBuffer();
		sb.append(userId);
		sb.append(bookId);
		sb.append(calType);
		sb.append(calObj);
		sb.append(charge);
		sb.append(purchaser);
		sb.append(purchaseType);
		sb.append(sourceType);
		sb.append(version);
		sb.append(account);
		sb.append(calObjName);
		sb.append(LeyueConst.KEY);
		LogUtil.v("getSource", sb.toString());
		return sb.toString();
	}
	
	private static String getSource(String userId, String bookId, String volumnId, String chapterId, String channelId, 
			int calType, String calObj, String purchaser, String charge,int sourceType,String version){
		StringBuffer sb = new StringBuffer();
		sb.append(userId);
		sb.append(bookId);
		if(!StringUtil.isEmpty(volumnId)){
			sb.append(volumnId);
		}
		if(!StringUtil.isEmpty(chapterId)){
			sb.append(chapterId);
		}
		if(!StringUtil.isEmpty(channelId)){
			sb.append(channelId);
		}
		sb.append(calType);
		sb.append(calObj);
		sb.append(purchaser);
		sb.append(charge);
		sb.append(sourceType);
		if (!StringUtil.isEmpty(version)) {
			sb.append(version);
		}
		sb.append(LeyueConst.KEY);
		LogUtil.v("getSource", sb.toString());
		return sb.toString();
	}
	
	/**
	 * 获取最新版本接口
	 * @param clientName
	 * @param versionCode
	 * @param versionNum
	 * @return
	 */
	public static String checkUpdateURL(String clientName,String versionCode,int versionNum){
		final StringBuilder sb = new StringBuilder();
		sb.append("clientName=");
		sb.append(clientName);
		sb.append("&versionCode=");
		sb.append(versionCode);
		sb.append("&versionNum=");
		sb.append(versionNum);
		return sb.toString();
	}
	
	/**
	 * 客户端支付完回调服务端
	 * @param tradeNo
	 * @param payPlatform
	 * @param payType
	 * @param payTradeType
	 * @param payGoodName
	 * @param payGoodDesc
	 * @param paymentAmount
	 * @return
	 */
	public static String responseClientPlayStatusURL(String tradeNo,String payPlatform,String payType
			,String payTradeType,String payGoodName,String payGoodDesc,Double paymentAmount){
		final StringBuilder sb = new StringBuilder();
		sb.append("&tradeNo=");
		sb.append(tradeNo);
		sb.append("&payPlatform=");
		sb.append(payPlatform);
		sb.append("&payType=");
		sb.append(payType);
		sb.append("&payTradeType=");
		sb.append(payTradeType);
		sb.append("&payGoodName=");
		sb.append(payGoodName);
		sb.append("&payGoodDesc=");
		sb.append(payGoodDesc);
		sb.append("&paymentAmount=");
		sb.append(paymentAmount);
		return sb.toString();
	}
	
	/**
	 * 获取书籍解密秘钥接口
	 * @param versionId
	 * @param userId
	 * @param bookId
	 * @return
	 */
	public static String responseDecodeKeyURL(int versionId,String userId,String bookId){
		final StringBuilder sb = new StringBuilder();
		sb.append("versionId=");
		sb.append(versionId);
		sb.append("&userId=");
		sb.append(userId);
		sb.append("&digest=");
		//加入urlEncode 防止 + 被解析成空格
		try {
			sb.append(URLEncoder.encode(EncryptUtils.encryptBase643DES(getSourceForDecodekey(versionId,userId,bookId), LeyueConst.KEY),LeyueConst.UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 积分上传统计接口
	 * @param userId
	 * @param jsonScore
	 * @return
	 */
	public static String updateUserScoreListURL(String userId,String jsonScore){
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&scorelist=");
		sb.append(jsonScore);
		sb.append("&authorcator=");
		try {
			sb.append(URLEncoder.encode(EncryptUtils.encryptBase643DES(userId+LeyueConst.KEY, LeyueConst.KEY),LeyueConst.UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static String getSourceForDecodekey(int versionId,String userId, String bookId){
		StringBuffer sb = new StringBuffer();
		sb.append(versionId);
		sb.append(userId);
		sb.append(bookId);
		LogUtil.v("getSourceForDecodekey", sb.toString());
		return sb.toString();
	}
	
	/**
	 * 修改用户信息
	 * */
	public static String userInfoModifyURL(String nickname, String newPassword, String mobile, String email, String sex, String birthday, String account, String signature){
		final StringBuilder sb = new StringBuilder();
		if(!StringUtil.isEmpty(nickname)){
			sb.append("map:nickname=");
			sb.append(nickname);
		}
		if(!StringUtil.isEmpty(mobile)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:mobile=");
			sb.append(mobile);
		}
		if(!StringUtil.isEmpty(email)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:email=");
			sb.append(email);
		}
		if(!StringUtil.isEmpty(sex)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:sex=");
			sb.append(sex);
		}
		if(!StringUtil.isEmpty(birthday)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:birthday=");
			sb.append(birthday);
		}
		if(!StringUtil.isEmpty(signature)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:signature=");
			sb.append(signature);
		}
		if(!StringUtil.isEmpty(account)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:account=");
			sb.append(account);
		}
		if(!StringUtil.isEmpty(newPassword)){
			if(sb.length() != 0){
				sb.append("&");
			}
			sb.append("map:password=");
			sb.append(MD5.getMD5(newPassword.getBytes()));
		}
		return sb.toString();
	}
	
	/**
	 * 客户端第三方账号记录URL
	 * @param thirdId
	 * @param source
	 * @param accessToken
	 * @param refreshToken
	 * @param sourceType 平台（{@link #SOURCE_LEYUE}：乐阅平台、{@link #SOURCE_SURFING}：天翼阅读、
	 * 		{@link #SOURCE_ZHONGXING}：中信出版社、{@link #SOURCE_DBS}：单本书）
	 * @return
	 */
	public static String getThirdUrl(String thirdId, String source, String accessToken, String refreshToken
			,String version,String releaseChannel,String salesChannel,int sourceType,String userId){
		final StringBuilder sb = new StringBuilder();
		sb.append("&thirdId=");
		sb.append(thirdId);
		sb.append("&source=");
		sb.append(source);
		if(!StringUtil.isEmpty(accessToken)){
			sb.append("&accessToken=");
			sb.append(accessToken);
		}
		if(!StringUtil.isEmpty(refreshToken)){
			sb.append("&refreshToken=");
			sb.append(refreshToken);
		}
		if(!StringUtil.isEmpty(version)){
			sb.append("&version=");
			sb.append(version);
		}
		if(!StringUtil.isEmpty(releaseChannel)){
			sb.append("&releaseChannel=");
			sb.append(releaseChannel);
		}
		if(!StringUtil.isEmpty(salesChannel)){
			sb.append("&salesChannel=");
			sb.append(salesChannel);
		}
		sb.append("&sourceType=");
		sb.append(sourceType);
		if(!StringUtil.isEmpty(userId)){
			sb.append("&userId=");
			sb.append(userId);
		}
		return sb.toString();
	}
	
	public static String getAddBookCommentParams(String bookId, String title, String content, String userId, String username, String commentSource, String starsNum){
		StringBuilder sb = new StringBuilder();
		sb.append("bookId=");
		sb.append(bookId);
		if(!StringUtil.isEmpty(title)){
			sb.append("&title=");
			sb.append(title);
		}
		sb.append("&content=");
		sb.append(content);
		sb.append("&userId=");
		sb.append(userId);
		sb.append("&username=");
		sb.append(username);
		sb.append("&commentSource=");
		sb.append(commentSource);
		sb.append("&starsNum=");
		sb.append(starsNum);
		return sb.toString();
	}
	
	public static String getBookCommentListParams(String bookId,int start, int count){
		StringBuilder sb = new StringBuilder();
		sb.append("bookId=");
		sb.append(bookId);
		sb.append("&start=");
		sb.append(start);
		sb.append("&count=");
		sb.append(count);
		return sb.toString();
	}
	
    public static String getAddBookDigestParams(String userId, BookDigests digest, String noteId){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        sb.append("&bookId=");
        sb.append(digest.getContentId());
        sb.append("&chapterId=");
        sb.append(digest.getChaptersId());
        sb.append("&sequence=");
        sb.append(1112);
        sb.append("&start=");
        sb.append(digest.getPosition());
        sb.append("&end=");
        sb.append(digest.getPosition() + digest.getCount());
        sb.append("&sourceContent=");
        sb.append(digest.getContent());
        sb.append("&noteContent=");
        sb.append(digest.getMsg());
        sb.append("&clientNoteId=");
        sb.append(digest.hashCode());
        sb.append("&updateDate=");
        sb.append(digest.getDate());
        sb.append("&backColor=");
        sb.append(digest.getBGColor());
        if(!StringUtil.isEmpty(noteId)){
            sb.append("&noteId=");
            sb.append(noteId);
        }
        return sb.toString();
    }

    public static String getDeleteBookDigestParams(String userBookTagId){
        StringBuilder sb = new StringBuilder();
        sb.append("noteId=");
        sb.append(userBookTagId);
        return sb.toString();
    }

    public static String getBookDigestListParams(String userId, String bookId, String chapterId){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        if(!StringUtil.isEmpty(chapterId)){
            sb.append("&noteId=");
            sb.append(chapterId);
        }
        if(!StringUtil.isEmpty(bookId)){
            sb.append("&bookId=");
            sb.append(bookId);
        }
        return sb.toString();
    }

    public static String getSyncBookDigestListParams(JsonArrayList<DigestResponse> list){
        StringBuilder sb = new StringBuilder();
        String listStr = list.toJsonArray().toString();
        sb.append("list=");
        sb.append(listStr);
        return sb.toString();
    }
    
    public static String getReplyBookCommentParams(int commentId, String userId, String username, String content, String commentSource){
		StringBuilder sb = new StringBuilder();
		sb.append("commentId=");
		sb.append(commentId);
		sb.append("&replyUserId=");
		sb.append(userId);
		sb.append("&account=");
		sb.append(username);
		sb.append("&replyContent=");
		sb.append(content);
		sb.append("&replySource=");
		sb.append(commentSource);
		return sb.toString();
	}
    
    public static String getCollectQueryParames(String userId,Integer resourceId){
		StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&resourceId=");
		sb.append(resourceId);
		return sb.toString();
	}
    
    public static String getCollectAddParames(String userId,int resourceId,int resourceType,Integer groupId){
		StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&resourceId=");
		sb.append(resourceId);
		sb.append("&resourceType=");
		sb.append(resourceType);
		sb.append("&groupId=");
		sb.append(groupId);
		return sb.toString();
	}
	
	public static String getCollectDelParames(Integer collectionId,String userId){
		StringBuilder sb = new StringBuilder();
		sb.append("collectionId=");
		sb.append(collectionId);
		sb.append("&userId=");
		sb.append(userId);
		return sb.toString();
	}


    public static String getBookLabelListParams(String userId, String bookId, String start, String count){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        if(!StringUtil.isEmpty(start)){
            sb.append("&start=");
            sb.append(start);
        }
        if(!StringUtil.isEmpty(count)){
            sb.append("&count=");
            sb.append(count);
        }
        if(!StringUtil.isEmpty(bookId)){
            sb.append("&bookId=");
            sb.append(bookId);
        }
        return sb.toString();
    }


    public static String getAddBookLabelParams(String userId, BookMark bookMark){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        sb.append("&bookId=");
        sb.append(bookMark.getContentID());
        sb.append("&tagName=");
        sb.append(bookMark.getBookmarkName());
        sb.append("&chapterId=");
        sb.append(bookMark.getChapterID());
        sb.append("&sourceType=");
        sb.append(LeyueConst.SOURCE_TYPE);
        sb.append("&position=");
        sb.append(bookMark.getPosition());
        return sb.toString();
    }

    public static String getDeleteBookLabelParams(String userBookTagId){
        StringBuilder sb = new StringBuilder();
        sb.append("userBookTagId=");
        sb.append(userBookTagId);
        return sb.toString();
    }

    public static String getSyncBookLabelListParams(JsonArrayList<BookMarkResponse> list){
        StringBuilder sb = new StringBuilder();
        String listStr = list.toJsonArray().toString();
        sb.append("list=");
        sb.append(listStr);
        return sb.toString();
    }

    public static String getAddUpdateSysMarkParams(String userId, BookMark bookMark, int groupId, String source){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        sb.append("&bookId=");
        sb.append(bookMark.getContentID());
        sb.append("&sequence=");
        sb.append(bookMark.getChapterID());
        sb.append("&chapterId=");
        sb.append(bookMark.getChapterID());
        sb.append("&source=");
        sb.append(source);
        sb.append("&position=");
        sb.append(bookMark.getPosition());
        sb.append("&markSequence=");
        sb.append(1);
        if(groupId > 0){
            sb.append("&groupId=");
            sb.append(groupId);
        }
        return sb.toString();
    }

    public static String getDelSysMarkParams(String userId, String[] ids){
        StringBuilder sb = new StringBuilder();
        sb.append("systemIds=");
        StringBuffer idsBuffer = new StringBuffer();
        if(ids.length > 0){
            idsBuffer.append(ids[0]);
        }
        for (int i = 1;i<ids.length;i++){
            idsBuffer.append(","+ids[i]);
        }
        sb.append(idsBuffer.toString());
        sb.append("&userId=");
        sb.append(userId);
        return sb.toString();
    }

    public static String getGetUpdateSysMarkParams(String userId, String source){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        sb.append("&source=");
        sb.append(source);
        sb.append("&start=");
        sb.append(0);
        sb.append("&count=");
        sb.append(Integer.MAX_VALUE);
        return sb.toString();
    }

    public static String getGetSysMarkGroupParams(String userId, String source){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        if (!TextUtils.isEmpty(source)){
            sb.append("&source=");
            sb.append(source);
        }
        return sb.toString();
    }

    public static String getCollectDelListParames(String collectionIds,Integer userId){
        StringBuilder sb = new StringBuilder();
        sb.append("collectionIds=");
        sb.append(collectionIds);
        sb.append("&userId=");
        sb.append(userId);
        return sb.toString();
    }

    public static String getCollectGetListParames(Integer userId,Integer start,Integer count){
        StringBuilder sb = new StringBuilder();
        sb.append("userId=");
        sb.append(userId);
        sb.append("&start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        return sb.toString();
    }

    public static String getFeedBackListParames(Integer userId, String imei,Integer start,Integer count,String lastUpdateTime){
        StringBuilder sb = new StringBuilder();
        if(userId==null){
            sb.append("imei=");
            sb.append(imei);
        }else{
            sb.append("userId=");
            sb.append(userId);
        }
        sb.append("&start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        sb.append("&lastUpdateTime=");
        sb.append(lastUpdateTime);
        return sb.toString();
    }


    public static String addFeedbackParames(String userId,String account,String imei,String simCode,
                                            String sourceType,String deviceModel,String mdnCode,String appVserion,String content,String contentType){
        StringBuilder sb = new StringBuilder();
        if(userId==null){
            sb.append("imei=");
            sb.append(imei);
        }else{
            sb.append("userId=");
            sb.append(userId);
        }
        sb.append("&account =");
        sb.append(account);
        sb.append("&simCode=");
        sb.append(simCode);
        sb.append("&sourceType=");
        sb.append(sourceType);
        sb.append("&deviceModel=");
        sb.append(deviceModel);
        sb.append("&mdnCode=");
        sb.append(mdnCode);
        sb.append("&appVserion=");
        sb.append(appVserion);
        sb.append("&content=");
        sb.append(content);
        sb.append("&contentType=");
        sb.append(contentType);
        return sb.toString();
    }

    public static String addReplyFeedbackParames(String userId,String account,String imei,
                                                 String feedbackId,String content,String contentType){
        StringBuilder sb = new StringBuilder();
        if(userId==null){
            sb.append("imei=");
            sb.append(imei);
        }else{
            sb.append("userId=");
            sb.append(userId);
        }
        sb.append("&account=");
        sb.append(account);
        sb.append("&feedbackId=");
        sb.append(feedbackId);
        sb.append("&content=");
        sb.append(content);
        sb.append("&contentType=");
        sb.append(contentType);
        return sb.toString();
    }

    public static String getFeedbackScoreParames(Integer score){
        StringBuilder sb = new StringBuilder();
        sb.append("score=");
        sb.append(score);
        return sb.toString();
    }
    public static String getFeedbackSelectParames(String feedbackId){
        StringBuilder sb = new StringBuilder();
        sb.append("feedbackId=");
        sb.append(feedbackId);
        return sb.toString();
    }
    
    /**
	 * 电信下单记录URL
	 * @param userId
	 * @param calType
	 * @param calObj（不传了）
	 * @param charge
	 * @param purchaser
	 * @param sourceType
	 * @param version
	 * @return
	 */
	public static String getAddOrderDetailURL(String userId,String bookId, String bookName,int calType,String calObj, String charge,String purchaser, String purchaseType,int sourceType,String version){
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&bookId=");
		sb.append(bookId);
		sb.append("&calObjName=");
		sb.append(bookName);
		sb.append("&calType=");
		sb.append(calType);
		sb.append("&calObj=");
		sb.append(calObj);
		sb.append("&charge=");
		sb.append(charge);
		sb.append("&purchaser=");
		sb.append(purchaser);
		sb.append("&purchaseType=");
		sb.append(purchaseType);
		sb.append("&sourceType=");
		sb.append(sourceType);
		sb.append("&version=");
		sb.append(version);
		sb.append("&authorcator=");
		LogUtil.i("getAddOrderDetailURL", sb.toString());
		sb.append(URLEncoder.encode(ApiHttpConnect.genAuthorizationByBase643DES(userId, purchaseType, bookId, calType, calObj, charge, purchaser, sourceType, version, LeyueConst.KEY)));
		return sb.toString();
	}

	/**
	 * 根据后台第三方关系ID更新第三方关系记录
	 * @param accessToken
	 * @param refreshToken
	 * @return
	 */
	public static String updateThirdAccessTokenURL(String accessToken,
			String refreshToken) {
		final StringBuilder sb = new StringBuilder();
		sb.append("accessToken=");
		sb.append(accessToken);
		sb.append("&refreshToken=");
		sb.append(refreshToken);
		return sb.toString();
	}
	
	/**
	 * 按两次退出按钮退出时记录登出日志
	 * @return
	 */
	public static String exitPostURL(String userId,String deviceId,String terminalBrand) {
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		sb.append("&deviceId=");
		sb.append(deviceId);
		sb.append("&terminalBrand=");
		sb.append(terminalBrand);
		return sb.toString();
	}
	
	public static String generateOrderURL(GenerateOrderInfo goi){
		final StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(goi.getUserId());
		sb.append("&bookId=");
		sb.append(goi.getBookId());
		sb.append("&volumnId=");
		sb.append(goi.getVolumnId());
		sb.append("&chapterId=");
		sb.append(goi.getChapterId());
		sb.append("&channelId=");
		sb.append(goi.getChannelId());
		sb.append("&calType=");
		sb.append(goi.getCalType());
		sb.append("&calObj=");
		sb.append(goi.getCalObj());
		sb.append("&charge=");
		sb.append(goi.getCharge());
		sb.append("&purchaser=");
		sb.append(goi.getPurchaser());
		sb.append("&sourceType=");
		sb.append(goi.getSourceType());
		sb.append("&version=");
		sb.append(goi.getVersion());
		LogUtil.i("generateOrderURL", sb.toString());
		sb.append("&authorcator=");
		sb.append(URLEncoder.encode(ApiHttpConnect.genAuthorizationByBase643DES(
				goi.getUserId(), 
				goi.getBookId(),
				goi.getCalType(),
				goi.getCalObj(), 
				goi.getCharge().toString(),
				goi.getPurchaser(), 
				goi.getSourceType(), 
				goi.getVersion(), 
				LeyueConst.KEY)));
		
		sb.append("&account=");
		sb.append(goi.getAccount());
		sb.append("&cpid=");
		sb.append(goi.getCpid());
		sb.append("&calObjName=");
		sb.append(goi.getCalObjName());
		sb.append("&releaseChannel=");
		sb.append(goi.getReleaseChannel());
		sb.append("&salesChannel=");
		sb.append(goi.getSalesChannel());
		sb.append("&purchaseType=");
		sb.append(goi.getPurchaseType());
		return sb.toString();
	}
	
	public static String updateOrderURL(Integer orderId, String orderNo, String token){
		final StringBuilder sb = new StringBuilder();
		
		sb.append("orderId=");
		sb.append(orderId);
		
		sb.append("&orderNo=");
		sb.append(orderNo);
		
		sb.append("&token=");
		sb.append(token);
		
		
		return sb.toString();
	}
	
	public static String generateOrderToTYReadURL(String clientAppKey, String productId, String callBackUrl,  String token, long timestamp){
		final StringBuilder sb = new StringBuilder();
		sb.append("&client_app_key=");
		sb.append(clientAppKey);
		
		sb.append("&token=");
		sb.append(token);
		
		sb.append("&timestamp=");
		sb.append(timestamp);
		
		sb.append("&product_id=");
		sb.append(productId);
		
		sb.append("&call_back_url=");
		sb.append(callBackUrl);
		
		return sb.toString();
	}
	
	public static String getOrderInfoParames(String orderId){
        StringBuilder sb = new StringBuilder();
        sb.append("orderId=");
        sb.append(orderId);
        return sb.toString();
    }

	public static String getBookCityClassifyDetailParams(int start, int count,
			String isFee) {
		StringBuilder sb = new StringBuilder();
        sb.append("start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        if(!TextUtils.isEmpty(isFee)){
	        sb.append("&isFee=");
	        sb.append(isFee);
        }
        return sb.toString();
	}

	public static String getBookCitySubjectRecommendParams(int start, int count) {
		StringBuilder sb = new StringBuilder();
        sb.append("start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        return sb.toString();
	}

	public static String getBookCitySubjectHeavyRecommendParams(int start,
			int count) {
		StringBuilder sb = new StringBuilder();
        sb.append("start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        return sb.toString();
	}

	public static String getBookCityOnlyStartAndCount(int start, int count) {
		StringBuilder sb = new StringBuilder();
		sb.append("module=8");
        sb.append("&start=");
        sb.append(start);
        sb.append("&count=");
        sb.append(count);
        return sb.toString();
	}
	
	public static String getBookCitySearchResult(String word,int start,int count){
		StringBuilder sb = new StringBuilder();
		sb.append("field=all");
		sb.append("&word=");
		sb.append(word);
		sb.append("&start=");
		sb.append(start);
		sb.append("&count=");
		sb.append(count);
		return sb.toString();
	}
	
	public static String getAccountBindingListParam(String userId){
		StringBuilder sb = new StringBuilder();
		sb.append("userId=");
		sb.append(userId);
		return sb.toString();
	}
	
	public static String getSaveAccountBindingParam(String uid, String source, String account, String password){
		StringBuilder sb = new StringBuilder();
		sb.append("uid=");
		sb.append(uid);
		sb.append("&sourceId=");
		sb.append(source);
		sb.append("&account=");
		sb.append(account);
		sb.append("&password=");
		sb.append(password);
		return sb.toString();
	}
	
	public static String getUnbindAccountParam(String id){
		StringBuilder sb = new StringBuilder();
		sb.append("id=");
		sb.append(id);
		return sb.toString();
	}
	
	public static HashMap<String, String> getUploadHeadParams(String userId, String files){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("file1", files);
		return params;
	}
    
}
