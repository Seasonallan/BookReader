/*
 * ========================================================
 * ClassName:UserInfoLeyue.java* 
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
 * 2013-9-26     chendt          #00000       create
 */
package com.lectek.android.lereader.storage.dbase;

import android.database.sqlite.SQLiteDatabase;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.IDbHelper;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Column;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Table;

/**
 * The Class UserInfoLeyue.
 *
 * @author chendt
 * @description 	用户个人信息
 * @date 2013-12-25
 * @Version 1.0
 */
@Table(name = "user_leyue_info_record", isOrderBy = true)
public class UserInfoLeyue extends BaseDao{
	
	/** The Constant TYPE_EMAIL. */
	public static final String TYPE_EMAIL = "11";
	
	/** The Constant TYPE_QQ. */
	public static final String TYPE_QQ = "21";
	
	/** The Constant TYPE_SINA. */
	public static final String TYPE_SINA = "22";
	
	/** The Constant TYPE_TIANYI. */
	public static final String TYPE_TIANYI = "23";
	/** 设备id */
	public static final String TYPE_DEVICEID = "24";
	/** 用户id. */
	@Json(name = "id")
	@Column(name = "feature_userId")
	public String userId;  //用户id
	
	/** 登录账号. */
	@Json(name = "account")
	@Column(name = "feature_account",isPrimaryKey = true)
	public String account; //登录账号
	
	/** 昵称. */
	@Json(name = "nickname")
	@Column(name = "feature_nickName")
	public String nickName; //昵称
	
	/** 邮箱. */
	@Json(name = "email")
	@Column(name = "feature_email")
	public String email; //邮箱
	
	/** 手机. */
	@Json(name = "mobile")
	@Column(name = "feature_mobile")
	public String mobile; //手机
	
	/** 性别. */
	@Json(name = "sex") 
	public String sex;//性别
	
	/** 生日日期. */
	@Json(name = "birthday") 
	public String birthday;//出生日期
	
	/** 个性签名 */
	@Json(name = "signature")
	@Column(name = "feature_signature")
	public String signature;//个性签名
	
	/** 头像id. */
	@Json(name = "photoId")
	@Column(name = "feature_photoId")
	public String photoId; //头像id
	
	/** 头像url. */
	@Json(name = "photoUrl")
	@Column(name = "feature_photoUrl")
	public String photoUrl; //头像url
	
	/** 总额. */
	@Json(name = "totalMoney")
	@Column(name = "feature_totalMoney")
	public String totalMoney; //总额
	
	/** 余额. */
	@Json(name = "balance")
	@Column(name = "feature_balance")
	public String balance; //余额
	
	/** 可用积分. */
	@Json(name = "score")
	@Column(name = "feature_score")
	public String score; 
	
	/** 总积分——用于等级评判. */
	@Json(name = "totalScore")
	@Column(name = "feature_totalScore")
	public String totalScore; 
	
	/** 状态. */
	@Json(name = "state")
	@Column(name = "feature_state")
	public String state; //状态
	
	/** 等级. */
	@Json(name = "grade")
	@Column(name = "feature_grade")
	public int grade; //等级
	
	/** 创建时间. */
	@Json(name = "createTime")
	@Column(name = "feature_createTime")
	public String createTime; //创建时间
	
	/** 最后登录时间. */
	@Json(name = "lastLoginTime")
	@Column(name = "feature_lastLogintime", isOrderDesc = true)
	public String lastLoginTime; //最后登录时间
	
	/** 用户来源（10：注册，11：邮箱，12：手机，20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）. */
	@Json(name = "source")
	@Column(name = "feature_source")
	public String source; //用户来源（邮箱登录,第三方登录）
	
	/** 是否删除. */
	@Json(name = "isDelete")
	@Column(name = "feature_isDelete")
	public String isDelete; //是否删除
	
	/** 产品来源（0：乐阅平台，1：天翼阅读，2：中信出版社，3：单本书，4：爱动漫）. */
	@Json(name = "sourceType")
	@Column(name = "feature_sourceType")
	public int sourceType; 

	/** 版本. */
	@Json(name = "version")
	@Column(name = "feature_version")
	public String version; 
	
	/** 发布渠道. */
	@Json(name = "releaseChannel")
	@Column(name = "feature_releaseChannel")
	public String releaseChannel; 
	
	/** 销售渠道. */
	@Json(name = "salesChannel")
	@Column(name = "feature_salesChannel")
	public String salesChannel; 
	
	/**  密码. */
	@Column(name = "feature_password")
	public String password;
	
	/**  第三方id. */
	@Column(name = "feature_thirdid")
	public String thirdid;

	
	@Override
	public IDbHelper newDatabaseHelper() { 
		return GlobalDBHelper.getDBHelper();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		if (oldVersion < 11) {  
			dropTable(db);
			createTable(db);
		}else if(oldVersion <= 13){
			if(!BaseDBHelper.contains(db, "user_leyue_info_record", "feature_photoUrl")){
                db.execSQL("ALTER TABLE user_leyue_info_record ADD COLUMN feature_photoUrl INTEGER;");
            }
			if(!BaseDBHelper.contains(db,  "user_leyue_info_record",  "feature_signature")){
                db.execSQL("ALTER TABLE user_leyue_info_record ADD COLUMN feature_signature INTEGER;");
            }
		}
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
	 * Gets the 登录账号.
	 *
	 * @return the 登录账号
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * Sets the 登录账号.
	 *
	 * @param account the new 登录账号
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * Gets the 昵称.
	 *
	 * @return the 昵称
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * Sets the 昵称.
	 *
	 * @param nickName the new 昵称
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * Gets the 邮箱.
	 *
	 * @return the 邮箱
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the 邮箱.
	 *
	 * @param email the new 邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the 手机.
	 *
	 * @return the 手机
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Sets the 手机.
	 *
	 * @param mobile the new 手机
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * Gets the 头像id.
	 *
	 * @return the 头像id
	 */
	public String getPhotoId() {
		return photoId;
	}

	/**
	 * Sets the 头像id.
	 *
	 * @param photoId the new 头像id
	 */
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	/**
	 * Gets the 总额.
	 *
	 * @return the 总额
	 */
	public String getTotalMoney() {
		return totalMoney;
	}

	/**
	 * Sets the 总额.
	 *
	 * @param totalMoney the new 总额
	 */
	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	/**
	 * Gets the 余额.
	 *
	 * @return the 余额
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * Sets the 余额.
	 *
	 * @param balance the new 余额
	 */
	public void setBalance(String balance) {
		this.balance = balance;
	}

	/**
	 * Gets the 可用积分.
	 *
	 * @return the 可用积分
	 */
	public String getScore() {
		return score;
	}

	/**
	 * Sets the 可用积分.
	 *
	 * @param score the new 可用积分
	 */
	public void setScore(String score) {
		this.score = score;
	}

	/**
	 * Gets the 状态.
	 *
	 * @return the 状态
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the 状态.
	 *
	 * @param state the new 状态
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Gets the 创建时间.
	 *
	 * @return the 创建时间
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * Sets the 创建时间.
	 *
	 * @param createTime the new 创建时间
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * Gets the 最后登录时间.
	 *
	 * @return the 最后登录时间
	 */
	public String getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * Sets the 最后登录时间.
	 *
	 * @param lastLoginTime the new 最后登录时间
	 */
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * Gets the 用户来源（10：注册，11：邮箱，12：手机，20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）.
	 *
	 * @return the 用户来源（10：注册，11：邮箱，12：手机，20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets the 用户来源（10：注册，11：邮箱，12：手机，20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）.
	 *
	 * @param source the new 用户来源（10：注册，11：邮箱，12：手机，20：第三方，21：腾讯， 22：新浪，23：天翼阅读，24：移动设备身份识别码，25：爱动漫，26：微信）
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Gets the 是否删除.
	 *
	 * @return the 是否删除
	 */
	public String getIsDelete() {
		return isDelete;
	}

	/**
	 * Sets the 是否删除.
	 *
	 * @param isDelete the new 是否删除
	 */
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * Gets the 总积分——用于等级评判.
	 *
	 * @return the 总积分——用于等级评判
	 */
	public String getTotalScore() {
		return totalScore;
	}

	/**
	 * Sets the 总积分——用于等级评判.
	 *
	 * @param totalScore the new 总积分——用于等级评判
	 */
	public void setTotalScore(String totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * Gets the 等级.
	 *
	 * @return the 等级
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * Sets the 等级.
	 *
	 * @param grade the new 等级
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

	/**
	 * Gets the 产品来源（0：乐阅平台，1：天翼阅读，2：中信出版社，3：单本书，4：爱动漫）.
	 *
	 * @return the 产品来源（0：乐阅平台，1：天翼阅读，2：中信出版社，3：单本书，4：爱动漫）
	 */
	public int getSourceType() {
		return sourceType;
	}

	/**
	 * Sets the 产品来源（0：乐阅平台，1：天翼阅读，2：中信出版社，3：单本书，4：爱动漫）.
	 *
	 * @param sourceType the new 产品来源（0：乐阅平台，1：天翼阅读，2：中信出版社，3：单本书，4：爱动漫）
	 */
	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * Gets the 版本.
	 *
	 * @return the 版本
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the 版本.
	 *
	 * @param version the new 版本
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the 发布渠道.
	 *
	 * @return the 发布渠道
	 */
	public String getReleaseChannel() {
		return releaseChannel;
	}

	/**
	 * Sets the 发布渠道.
	 *
	 * @param releaseChannel the new 发布渠道
	 */
	public void setReleaseChannel(String releaseChannel) {
		this.releaseChannel = releaseChannel;
	}

	/**
	 * Gets the 销售渠道.
	 *
	 * @return the 销售渠道
	 */
	public String getSalesChannel() {
		return salesChannel;
	}

	/**
	 * Sets the 销售渠道.
	 *
	 * @param salesChannel the new 销售渠道
	 */
	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	/**
	 * Gets the 性别.
	 *
	 * @return the 性别
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * Sets the 性别.
	 *
	 * @param sex the new 性别
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * Gets the 生日日期.
	 *
	 * @return the 生日日期
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * Sets the 生日日期.
	 *
	 * @param birthday the new 生日日期
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * Gets the 密码.
	 *
	 * @return the 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the 密码.
	 *
	 * @param password the new 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the 第三方id.
	 *
	 * @return the 第三方id
	 */
	public String getThirdid() {
		return thirdid;
	}

	/**
	 * Sets the 第三方id.
	 *
	 * @param thirdid the new 第三方id
	 */
	public void setThirdid(String thirdid) {
		this.thirdid = thirdid;
	}
	
	

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	@Override
	public String toString() {
		return "UserInfoLeyue [userId=" + userId + ", account=" + account
				+ ", nickName=" + nickName + ", email=" + email + ", mobile="
				+ mobile + ", sex=" + sex + ", birthday=" + birthday
				+ ", signature=" + signature + ", photoId=" + photoId
				+ ", photoUrl=" + photoUrl + ", totalMoney=" + totalMoney
				+ ", balance=" + balance + ", score=" + score + ", totalScore="
				+ totalScore + ", state=" + state + ", grade=" + grade
				+ ", createTime=" + createTime + ", lastLoginTime="
				+ lastLoginTime + ", source=" + source + ", isDelete="
				+ isDelete + ", sourceType=" + sourceType + ", version="
				+ version + ", releaseChannel=" + releaseChannel
				+ ", salesChannel=" + salesChannel + ", password=" + password
				+ ", thirdid=" + thirdid + "]";
	}
	
}
