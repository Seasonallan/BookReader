package com.lectek.android.lereader.net.response;

import java.io.Serializable;
import java.util.ArrayList;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

public class SubjectDetailResultInfo extends BaseDao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3182935470697117650L;
	
	@Json(name = "subjectId")
	public int subjectId;
	
	@Json(name = "subjectName")
	public String subjectName;
	
	@Json(name = "subjectIntro")
	public String subjectIntro;
	
	@Json(name = "type")
	public int type;
	
	@Json(name = "subjectPic")
	public String subjectPic;
	
	@Json(name = "bookList",className=ContentInfoLeyue.class)
	public ArrayList<ContentInfoLeyue> bookList;
	
	public int getSubjectId(){
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

	public String getSubjectPic() {
		return subjectPic;
	}

	public void setSubjectPic(String subjectPic) {
		this.subjectPic = subjectPic;
	}

	public ArrayList<ContentInfoLeyue> getBookList(){
		return bookList;
	}
	
	public void setBookList(ArrayList<ContentInfoLeyue> list){
		this.bookList = list;
	}
}
