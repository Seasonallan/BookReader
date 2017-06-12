/*
 * ========================================================
 * ClassName:VelocityRenderHtmlConst.java* 
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
 * 2013-8-27     chendt          #00000       create
 */
package com.lectek.android.lereader.lib;


/**
 * @description
 *  乐阅（分销。自主...） 新增管理常量类
	<br/>Include：BS模式 WebView 渲染处理中，一些通用常量。
 * @author chendt
 * @date 2013-8-27
 * @Version 1.0
 */
public class LeyueConst {
	/**用于区分打乐阅还是单本书的开关*/
	public static final boolean isLeyueVersion = true; 
	/**输出LOG到文件*/
//	public static final boolean IS_OUT_LOG = true;
	/**是否输出log*/
	public static final boolean IS_DEBUG = true;
	/**微信平台下载乐阅的应用宝地址：*/ 
	/**微信appid*/
	public static final String YX_APP_ID = "yx73ebbc468dd9483eb1c28ec992eaaf1f";//single -keystore
	public static final String WX_APP_ID = "wx065610663e616353";//single -keystore
	public static String wxResonseStr = "";
	/**360渠道*/
	public static final String CHANNEL_360 = "m360";
	/**游客ID*/
	public static final String TOURIST_USER_ID = "_000000";
	/**下订单--来源 --乐阅平台-0*/
	public static final String SOURCE_TYPE = "0";//乐阅平台
	/**客户端内置密码*/
	public static final String KEY = "lereaderV1.0secretkey123";
	
	/**升级用客户端名称*/
	public static final String CLIENT_NAME = "leyuereader";
	
	/**SD卡存储文件夹名称*/
	public static final String FILENAME_SDCARD = CLIENT_NAME;
	
	/**编码格式：utf-8*/
	public static final String UTF8 = "UTF-8";
	/**文件类型：html*/
	public static final String TYPE_HTML = "text/html";
	/**访问assert文件夹路径*/
	public static final String ASSERT_FOLDER_PATH = "file:///android_asset/";
	/**获取相同界面对应主题名称的 分隔符:<br/> "#"*/
	public static final String GET_TITLE_TAG = "#";
	/**获取过滤后的包月专区product Id 分隔符:<br/> "英文 ,"*/
	public static final String GET_MONTHLY_FILTER_PRODUCTID_TAG1 = ",";
	/**获取过滤后的包月专区product Id 分隔符:<br/> "中文 ，"*/
	public static final String GET_MONTHLY_FILTER_PRODUCTID_TAG2 = "，";
	
	/**获取包月专区是否订购   —— 未订购返回码 10024"*/
	public static final String GET_MONTHLY_IS_ORDER_FAIL_CODE = "10024";
	/**获取包月专区是否订购   —— 已订购返回码 10141"*/
	public static final String GET_MONTHLY_IS_ORDER_SUCCESS_CODE = "10141";
	
	/**搜索列表历史记录内容上限*/
	public static final int SEARCH_RECORD_MAX_LIMIT = 5;
	
	
	/**书城推荐页_乐阅*/
	public static final String BOOK_CITY_RECOMMEND_HTML = "html/bookRecommend/bookCityRecommend.html";
	
	/**书城特价页_乐阅*/
	public static final String BOOK_CITY_SPECIAL_HTML = "html/special/bookCitySpecial.html";
	
	/**书城榜单页_乐阅*/
	public static final String BOOK_CITY_SALE_HTML = "html/bookRank/ranks.html";
	
	/**书城分类页_乐阅*/
	public static final String BOOK_CITY_CLASSIFY_HTML = "html/bookType/bookType.html";
	
	/**书城_书籍列表_乐阅*/
	public static final String BOOK_CITY_BOOK_LIST_HTML = "html/common/bookList.html";
	
	/**书城_专题详情_乐阅*/
	public static final String BOOK_CITY_SUBJECT_DETAIL_HTML = "html/bookSubject/detail.html";
	
	/**搜索列表_乐阅*/
	public static final String SEARCH_LIST_HTML = "html/search/searchList.html";
	
	/**进入专题详情*/
	public static final String GOTO_SUBJECT_DETAIL_TAG = "goto_subject_detail_tag";
	
	/**进入书籍列表*/
	public static final String GOTO_BOOK_LIST_TAG = "goto_book_list_tag";
	
	/**进入书籍详情_天翼*/
	public static final String GOTO_BOOK_DETAIL_HTML = "html/common/bookDetail.html";
	
	/**进入书籍详情
	 * 书籍来自乐阅服务器*/
	public static final String GOTO_BOOK_DETAIL_LEYUE_HTML = "html/common/bookDetail_leyue.html";
	
	/**进入乐阅书籍详情TAG
	 * 书籍来自乐阅服务器*/
	public static final String GOTO_LEYUE_BOOK_DETAIL_TAG = "goto_leyue_book_detail_tag";
	
	/**
	 * 是否是天翼阅读书籍详情
	 */
	public static final String EXTRA_BOOLEAN_IS_SURFINGREADER = "extra_boolean_is_surfingreader";
    /**
     * 天翼阅读书籍详情对应乐阅书籍ID
     */
    public static final String EXTRA_LE_BOOKID = "extra_string_lebook_id";
	
	/**进入外链界面_url*/
	public static final String GOTO_THIRD_PARTY_URL_HTML = "html/third_party/url.html";
	
	/**进入包月专区_天翼*/
	public static final String GOTO_MONTHLY_PAYMENT_ZONE_HTML = "html/surfing/monthly_payment_zone.html";
	
	/**获取包月专区图片分辨率_天翼*/
	public static final String GET_MONTHLY_COVER_SIZE_TAG = "2";
	
	/**进入包月专区_TAG*/
	public static final String GOTO_MONTHLY_PAYMENT_ZONE_TAG = "goto_monthly_payment_zone_tag";
	
	/**进入第三方网页_TAG*/
	public static final String GOTO_THIRD_PARTY_URL_TAG = "goto_third_party_url_tag";
	
	/**搜索参数格式_乐阅
	 * --默认只搜书名范围*/
	public static final String SEARCH_LIST_URL_FORMAT_TAG = "?url=book/search&field=bookName&word=";
	
	/**
	 * 进入包月专区内容列表Tag*/
	public static final String GOTO_MONTHLY_PRODUCT_CONTENT_TAG = "goto_monthly_product_content_tag";
	
	/**
	 * 进入书籍目录列表Tag*/
	public static final String GOTO_BOOK_CATALOG_LIST_TAG = "goto_book_catalog_list_tag";
	
	
	public static boolean isShowUserInfoHelper = false;



    /** 书城_专题栏目 */
    public static final String BOOK_CITY_SUBJECT_CATALOG_HTML = "html/bookSubject/zhuantilanmu.html";
	
}
