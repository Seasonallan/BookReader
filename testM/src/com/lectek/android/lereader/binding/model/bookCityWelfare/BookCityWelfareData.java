package com.lectek.android.lereader.binding.model.bookCityWelfare;

import java.util.ArrayList;

import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.SubjectResultInfo;

public class BookCityWelfareData {

	/**
	 * 福利专题
	 */
	ArrayList<SubjectResultInfo> mWelfareSubjectList;
	
	/**
	 * 今日限免
	 */
	ArrayList<ContentInfoLeyue> mFreeLimitBooksList;
	
	/**
	 * 免费专区
	 */
	ArrayList<ContentInfoLeyue> mFreeBooksList;
	
	/**
	 * 最新特价
	 */
	ArrayList<ContentInfoLeyue> mLatestSpecialOfferList;
	
}
