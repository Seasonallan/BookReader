package com.lectek.android.lereader.binding.model.bookcityrecommend;

import java.util.ArrayList;

import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;

public class BookCityRecommendData {

	/**
	 * 推荐专题
	 */
	ArrayList<SubjectResultInfo> mRecommendSubjectList;
	
	/**
	 * 小编推荐专题
	 */
	ArrayList<SubjectResultInfo> mEditorRecommendSubjectList;
	
	/**
	 * 重磅推荐书籍
	 */
	ArrayList<ContentInfoLeyue> mHeavyRecommendList;
	
	/**
	 * 新书抢先看
	 */
	ArrayList<ContentInfoLeyue> mNewBookRecommendList;
	
	/**
	 * 大家都在看
	 */
	ArrayList<ContentInfoLeyue> mAllLoveRecommendList;
	
}
