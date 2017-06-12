package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 * 第三方用户在乐阅的信息.
 *
 * @author wuwq
 */
public class UserThirdLeyueInfo extends BaseDao{
	
	/**  后台第三方关系表id. */
	@Json(name = "id")
	public String id;
	
	/** 用户id. */
	@Json(name = "userId")
	public String userId;
	
	/** 第三方id. */
	@Json(name = "thirdId")
	public String thirdId;
	
	/** 用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）. */
	@Json(name = "source")
	public String source;
	
	/** 访问令牌. */
	@Json(name = "accessToken")
	public String accessToken;
	
	/** 刷新令牌. */
	@Json(name = "refreshToken")
	public String refreshToken;
	
	/**
	 * Gets the 后台第三方关系表id.
	 *
	 * @return the 后台第三方关系表id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the 后台第三方关系表id.
	 *
	 * @param id the new 后台第三方关系表id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the 用户id.
	 *
	 * @return the 用户id
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Sets the 用户id.
	 *
	 * @param userId the new 用户id
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Gets the 第三方id.
	 *
	 * @return the 第三方id
	 */
	public String getThirdId() {
		return thirdId;
	}
	
	/**
	 * Sets the 第三方id.
	 *
	 * @param thirdId the new 第三方id
	 */
	public void setThirdId(String thirdId) {
		this.thirdId = thirdId;
	}
	
	/**
	 * Gets the 用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）.
	 *
	 * @return the 用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * Sets the 用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）.
	 *
	 * @param source the new 用户来源（20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * Gets the 访问令牌.
	 *
	 * @return the 访问令牌
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Sets the 访问令牌.
	 *
	 * @param accessToken the new 访问令牌
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * Gets the 刷新令牌.
	 *
	 * @return the 刷新令牌
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	/**
	 * Sets the 刷新令牌.
	 *
	 * @param refreshToken the new 刷新令牌
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	
}
