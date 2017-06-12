package com.lectek.android.lereader.binding.model.bookCityClassify;

import java.util.ArrayList;

import com.lectek.android.lereader.net.response.BookCityClassifyResultInfo;
import com.lectek.android.lereader.net.response.SubjectResultInfo;

public class BookCityClassifyData {

	/**
	  * 推荐专题
	 */
	ArrayList<SubjectResultInfo> mClassifySubjectList;
	
	/**
	 * 分类列表
	 */
	ArrayList<BookCityClassifyResultInfo> mClassifyInfoList;
	
}
