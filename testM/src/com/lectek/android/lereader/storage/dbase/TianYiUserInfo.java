package com.lectek.android.lereader.storage.dbase;

import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;
 
/**
 * 天翼阅读个人信息.
 *
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-8-21
 */
@Table(name = "user_tianyi_info_record")
public class TianYiUserInfo extends BaseDao{

	/**  用户昵称. */
	@Json(name ="nick_name")
	@Column(name = "feature_nick_name")
	public String nickName;
	
	/**  用户邮箱. */
	@Column(name = "feature_email")
	public String email;
	
	/**  用户手机. */
	@Json(name ="nick_phone")
	@Column(name = "feature_nick_phone")
	public String userPhone;
	
	/** 性别：1代表男性 2代表女性 -1代表保密. */
	@Json(name ="user_sex")
	@Column(name = "feature_user_sex")
	public String userSex;
	
	/**  用户年龄. */
	@Json(name ="user_age")
	@Column(name = "feature_user_age")
	public String userAge;
	
	/**  兴趣爱好. */
	@Column(name = "feature_interest")
	public String interest;
	
	/**  用户积分数. */
	@Json(name ="user_score")
	@Column(name = "feature_score")
	public String userScore;
	
	/**  用户阅点数. */
	@Json(name ="read_point")
	@Column(name = "feature_read_point")
	public String readPoint;
	
	/**  用户经验值. */
	@Column(name = "feature_experience")
	public String experience;
	
	/** 用户等级 格式：levelname_lowexperience_topexperience. */
	@Json(name ="user_level")
	@Column(name = "feature_user_level")
	public String userLevel;
	
	/** 用户下一等级 格式：levelname_lowexperience_topexperience. */
	@Json(name ="next_level")
	@Column(name = "feature_next_level")
	public String nextLevel;
	
	/**  返回状态码. */
	public String status;
	
	/** 天翼用户开放平台id. */
	@Column(name = "feature_user_id", isPrimaryKey = true)
	public String userId;
	
	/** 登录后获得的acctoken. */
	@Column(name = "feature_access_token")
	public String accessToken;
	
	/** 刷新acctoken. */
	@Column(name = "feature_refresh_token")
	public String refreshToken;
	
	/** 后台用户对应. */
	@Column(name = "feature_leyue_userid")
	public String leyueUserId;

	
	@Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		if (oldVersion < 11) {  
			dropTable(db);
			createTable(db);
		}
	}
	
	

	/**
	 * Gets the 用户昵称.
	 *
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	
	/**
	 * Sets the 用户昵称.
	 *
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	/**
	 * Gets the 用户邮箱.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Sets the 用户邮箱.
	 *
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * Gets the 用户手机.
	 *
	 * @return the userPhone
	 */
	public String getUserPhone() {
		return userPhone;
	}
	
	/**
	 * Sets the 用户手机.
	 *
	 * @param userPhone the userPhone to set
	 */
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	
	/**
	 * Gets the 性别：1代表男性 2代表女性 -1代表保密.
	 *
	 * @return the userSex
	 */
	public String getUserSex() {
		return userSex;
	}
	
	/**
	 * Sets the 性别：1代表男性 2代表女性 -1代表保密.
	 *
	 * @param userSex the userSex to set
	 */
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	
	/**
	 * Gets the 用户年龄.
	 *
	 * @return the userAge
	 */
	public String getUserAge() {
		return userAge;
	}
	
	/**
	 * Sets the 用户年龄.
	 *
	 * @param userAge the userAge to set
	 */
	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}
	
	/**
	 * Gets the 兴趣爱好.
	 *
	 * @return the interest
	 */
	public String getInterest() {
		return interest;
	}
	
	/**
	 * Sets the 兴趣爱好.
	 *
	 * @param interest the interest to set
	 */
	public void setInterest(String interest) {
		this.interest = interest;
	}
	
	/**
	 * Gets the 用户积分数.
	 *
	 * @return the userScore
	 */
	public String getUserScore() {
		return userScore;
	}
	
	/**
	 * Sets the 用户积分数.
	 *
	 * @param userScore the userScore to set
	 */
	public void setUserScore(String userScore) {
		this.userScore = userScore;
	}
	
	/**
	 * Gets the 用户阅点数.
	 *
	 * @return the readPoint
	 */
	public String getReadPoint() {
		return readPoint;
	}
	
	/**
	 * Sets the 用户阅点数.
	 *
	 * @param readPoint the readPoint to set
	 */
	public void setReadPoint(String readPoint) {
		this.readPoint = readPoint;
	}
	
	/**
	 * Gets the 用户经验值.
	 *
	 * @return the experience
	 */
	public String getExperience() {
		return experience;
	}
	
	/**
	 * Sets the 用户经验值.
	 *
	 * @param experience the experience to set
	 */
	public void setExperience(String experience) {
		this.experience = experience;
	}
	
	/**
	 * Gets the 用户等级 格式：levelname_lowexperience_topexperience.
	 *
	 * @return the userLevel
	 */
	public String getUserLevel() {
		return userLevel;
	}
	
	/**
	 * Sets the 用户等级 格式：levelname_lowexperience_topexperience.
	 *
	 * @param userLevel the userLevel to set
	 */
	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	
	/**
	 * Gets the 用户下一等级 格式：levelname_lowexperience_topexperience.
	 *
	 * @return the nextLevel
	 */
	public String getNextLevel() {
		return nextLevel;
	}
	
	/**
	 * Sets the 用户下一等级 格式：levelname_lowexperience_topexperience.
	 *
	 * @param nextLevel the nextLevel to set
	 */
	public void setNextLevel(String nextLevel) {
		this.nextLevel = nextLevel;
	}
	
	/**
	 * Gets the 返回状态码.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the 返回状态码.
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets the 天翼用户开放平台id.
	 *
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * Sets the 天翼用户开放平台id.
	 *
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Gets the 登录后获得的acctoken.
	 *
	 * @return the 登录后获得的acctoken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Sets the 登录后获得的acctoken.
	 *
	 * @param accessToken the new 登录后获得的acctoken
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	/**
	 * Gets the 刷新acctoken.
	 *
	 * @return the 刷新acctoken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	/**
	 * Sets the 刷新acctoken.
	 *
	 * @param refreshToken the new 刷新acctoken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	/**
	 * Gets the 后台用户对应.
	 *
	 * @return the 后台用户对应
	 */
	public String getLeyueUserId() {
		return leyueUserId;
	}
	
	/**
	 * Sets the 后台用户对应.
	 *
	 * @param leyueUserId the new 后台用户对应
	 */
	public void setLeyueUserId(String leyueUserId) {
		this.leyueUserId = leyueUserId;
	}
	
}
