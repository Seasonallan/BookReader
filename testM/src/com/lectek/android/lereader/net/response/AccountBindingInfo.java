package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  账号绑定HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class AccountBindingInfo extends BaseDao {

	
	
	@Json(name = "id")
	public String id; //绑定id
	
	@Json(name = "uid")
	public String uid; //用户id
	
	@Json(name = "sourceId")
	public String source; //账号来源
	
	@Json(name = "password")
	public String password; //账号密码
	
	@Json(name = "account")
	public String account; //账号
	
	@Json(name = "createTime")  //创建时间
	public String createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "AccountBindingInfo [id=" + id + ", uid=" + uid + ", source="
				+ source + ", password=" + password + ", account=" + account
				+ ", createTime=" + createTime + "]";
	}
	

	

}
