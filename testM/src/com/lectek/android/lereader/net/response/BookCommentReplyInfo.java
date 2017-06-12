package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  评论回复列表HTTP数据结构
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class BookCommentReplyInfo extends BaseDao{

	@Json(name ="account")
	public String account;
	
	@Json(name ="replyUserId")
	public String replyUserId;

	@Json(name ="replyContent")
	public String replyContent;
	
	@Json(name ="createTime")
	public String createTime;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getReplyUserId() {
		return replyUserId;
	}
	public void setReplyUserId(String replyUserId) {
		this.replyUserId = replyUserId;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public String toString() {
		return "BookCommentReplyInfo [account=" + account + ", replyUserId=" + replyUserId + ", replyContent="
				+ replyContent + ", createTime=" + createTime + "]";
	}
}
