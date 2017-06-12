package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;
/**
 * 书城专题返回信息
 * @author yyl
 * @date 2014-7-3
 */
public class SubjectResultInfo extends BaseDao{
	
	@Json(name = "subjectId")
	public int subjectId;//专题ID
	
	@Json(name = "subjectName")
	public String subjectName;//专题名称
	
	@Json(name = "subjectIntro")
	public String subjectIntro;//专题介绍
	
	@Json(name = "type")
	public int type;//专题类型（1：单本书籍，2：列表书籍，3：url形式,4:包月，5：限免，6：栏目，7：包月栏目）
	
	@Json(name = "module")
	public String module;//
	
	@Json(name = "subjectPic")
	public String subjectPic;//专题图片
	
	@Json(name = "bookNum")
	public int bookNum;//书籍数量
	
	@Json(name = "sequence")
	public int sequence;//排序
	
	@Json(name = "memo")
	public String memo;
	@Json(name = "status")
	public String status;//是否上线（0：下线，1：上线）
	
	@Json(name = "createTime")
	public String createTime;//创建时间
	
	@Json(name = "updateTime")
	public String updateTime;//更新日期

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectIntro() {
		return subjectIntro;
	}

	public void setSubjectIntro(String subjectIntro) {
		this.subjectIntro = subjectIntro;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getSubjectPic() {
		return subjectPic;
	}

	public void setSubjectPic(String subjectPic) {
		this.subjectPic = subjectPic;
	}

	public int getBookNum() {
		return bookNum;
	}

	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "BookCitySubjectClassifyResultInfo [subjectId=" + subjectId
				+ ", subjectName=" + subjectName + ", subjectIntro="
				+ subjectIntro + ", type=" + type + ", module=" + module
				+ ", subjectPic=" + subjectPic + ", bookNum=" + bookNum
				+ ", sequence=" + sequence + ", status=" + status
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}
	
}
